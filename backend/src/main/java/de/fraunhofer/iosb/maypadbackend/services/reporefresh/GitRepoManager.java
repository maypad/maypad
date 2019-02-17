package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import de.fraunhofer.iosb.maypadbackend.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Manager for a git repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class GitRepoManager extends RepoManager {

    private Git localGit = null;

    /**
     * "Singleton" of a local git instance.
     *
     * @return Git instance
     */
    private Git getGit() {
        if (localGit == null) {
            try {
                localGit = Git.open(getProjectRootDir());
            } catch (IOException e) {
                getLogger().error("Can't read local repo from " + getProjectRootDir().getAbsolutePath());
                cloneRepository();
            }
        }
        return localGit;
    }

    /**
     * Constructor, prepare the GitRepoManager.
     *
     * @param project        Project for which the git-repository is to be managed
     * @param projectRootDir The root directory for the repo files
     */
    public GitRepoManager(Project project, File projectRootDir) {
        super(project, projectRootDir);
    }

    /**
     * Constructor, prepare the GitRepoManager.
     *
     * @param project Project for which the git-repository is to be managed
     */
    public GitRepoManager(Project project) {
        super(project);
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    @Override
    public List<String> getBranchNames() {
        Collection<Ref> branches = null;
        try {
            branches = ((LsRemoteCommand) getAuth(getGit().lsRemote().setHeads(true))).call();
        } catch (GitAPIException e) {
            getLogger().error("Can't get remote branches.");
            return new ArrayList<>();
        }
        List<String> branchNames = new ArrayList<>();

        if (branches == null) {
            return branchNames;
        }
        for (Ref branch : branches) {
            branchNames.add(branch.getName().substring(branch.getName().lastIndexOf("/") + 1, branch.getName().length()));
        }
        return branchNames;
    }

    /**
     * Get the name of the main branch.
     *
     * @return Name of the main branch
     */
    @Override
    public String getMainBranchName() {
        return "master";
    }

    /**
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     * @return true, if the switch to other branch was successfully, else false
     */
    @Override
    public boolean switchBranch(String name) {
        if (getGit() == null || getGit().getRepository() == null) {
            getLogger().error("Can't detect current (local) branch");
            return false;
        }
        String currentBranch = getCurrentBranch();
        if (currentBranch != null && currentBranch.equals(name)) {
            //remove locale changes
            try {
                getGit().reset().setMode(ResetCommand.ResetType.HARD).call();
            } catch (GitAPIException e) {
                getLogger().error("Can't reset branch " + name + ". Perhaps data were deleted or permissions were changed.");
            }
            gitPull();
            return true;
        }

        Git git = getGit();
        boolean createBranch = true;
        Ref ref = null;
        try {
            ref = git.getRepository().exactRef("refs/heads/" + name);
        } catch (IOException e) {
            getLogger().error("Can't get possible local branch with name " + name + " from " + getProjectRootDir().getAbsolutePath());
            return false;
        }
        if (ref != null) {
            createBranch = false;
        }

        try {
            git.checkout().setName(name).setCreateBranch(createBranch).call();
            gitPull();
            return true;
        } catch (GitAPIException e) {
            getLogger().warn("Can't switch to branch " + name + " in repo " + getProject().getRepositoryUrl());
        }

        return false;
    }

    /**
     * Prepare the repomanager for the refresh.
     */
    @Override
    public void prepareRefresh() {
        switchBranch(getMainBranchName());
    }

    /**
     * Get all tags of a branch.
     *
     * @return List with tags
     */
    @Override
    public List<Tag> getTags() {
        Git git = getGit();
        List<Tag> tags = new ArrayList<>();
        List<Ref> refs;
        try {
            refs = git.tagList().call();
        } catch (GitAPIException e) {
            getLogger().error("Can't get tags from " + getProjectRootDir().getAbsolutePath());
            return tags;
        }

        for (Ref ref : refs) {
            RevWalk revWalk = new RevWalk(git.getRepository());
            RevCommit commit = null;
            try {
                commit = revWalk.parseCommit(ref.getObjectId().toObjectId());
            } catch (IOException e) {
                getLogger().warn("Can't get tag of branch " + ref.getName() + " in " + getProjectRootDir().getAbsolutePath());
                continue;
            }

            tags.add(new Tag(ref.getName().replace("refs/tags/", ""), getCommit(commit)));
        }

        return tags;
    }

    /**
     * Get the last commit of all branches.
     *
     * @return The last commit
     */
    public Commit getGlobalLastCommit() {
        return getLastCommitByBranch(null);
    }


    /**
     * Get the last Commit of the current selected branch.
     *
     * @return Last commit
     */
    @Override
    public Commit getLastCommit() {
        String currentBranch = getCurrentBranch();
        if (currentBranch == null) {
            return getDefaultCommit();
        }
        return getLastCommitByBranch(currentBranch);
    }

    /**
     * Clean the RepoManager after usage.
     */
    @Override
    public void cleanUp() {
        if (localGit != null) {
            localGit.close();
        }
        localGit = null;
    }

    /**
     * Get the last Commit of the current given branch.
     *
     * @param branchname Branch of the last commit. If null, all branches were checked.
     * @return Last commit
     */
    public Commit getLastCommitByBranch(String branchname) {
        boolean allBranches = branchname == null;
        Collection<Ref> branches = null;
        Git git = getGit();
        try {
            branches = ((LsRemoteCommand) getAuth(git.lsRemote().setHeads(true))).call();
        } catch (GitAPIException e) {
            getLogger().error("Can't get remote branches.");
            return getDefaultCommit();
        }
        if (branches == null) {
            return getDefaultCommit();
        }

        RevCommit revCommit = null;
        RevWalk walk = new RevWalk(git.getRepository());
        for (Ref branch : branches) {
            if (!allBranches && !branch.getName().equals("refs/heads/" + branchname)) {
                continue;
            }
            RevCommit commit = null;
            try {
                commit = walk.parseCommit(branch.getObjectId());
            } catch (IOException e) {
                getLogger().warn("Can't get last commit of branch " + branch.getName() + " in " + getProjectRootDir().getAbsolutePath());
            }
            if (commit == null) {
                continue;
            }
            if (revCommit == null) {
                revCommit = commit;
            }
            if (commit.getAuthorIdent().getWhen().compareTo(revCommit.getAuthorIdent().getWhen()) > 0) {
                revCommit = commit;
            }
            //branch was found, so we don't need to check other Branches, if we won't check all branches
            if (!allBranches) {
                break;
            }
        }

        return getCommit(revCommit);
    }

    /**
     * Get the current (local) branch.
     *
     * @return Name of the local branch
     */
    private String getCurrentBranch() {
        try {
            return getGit().getRepository().getBranch();
        } catch (IOException e) {
            getLogger().error("Can't detect current (local) branch");
        }
        return null;
    }


    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    @Override
    protected boolean cloneRepository() {
        if (!FileUtil.isDirectoryEmpty(getProjectRootDir())) {
            getLogger().error("Folder " + getProjectRootDir().getAbsolutePath() + " isn't empty, so can't clone.");
            return false;
        }
        if (localGit != null) {
            cleanUp();
        }
        try {
            localGit = (Git) getAuth(Git.cloneRepository().setURI(getProject().getRepositoryUrl())
                    .setDirectory(getProjectRootDir())).call();
        } catch (GitAPIException | JGitInternalException e) {
            cleanUp();

            getLogger().warn("Can't access to repo " + getProject().getRepositoryUrl() + " or an internal error occurred.");
            try {
                FileUtils.deleteDirectory(getProjectRootDir());
            } catch (IOException e1) {
                getLogger().warn("Can't delete folder at " + getProjectRootDir().getAbsolutePath());
            }
            return false;
        }
        return true;
    }

    /**
     * Convert a RevCommit object to a {@link Commit} object.
     *
     * @param revCommit RevCommit object
     * @return Commit object
     */
    private Commit getCommit(RevCommit revCommit) {
        Commit commit = getDefaultCommit();
        if (revCommit == null) {
            return commit;
        }
        commit.setMessage(revCommit.getShortMessage());
        commit.setIdentifier(revCommit.getName());
        commit.setAuthor(new Author(revCommit.getAuthorIdent().getName(), new Mail(revCommit.getAuthorIdent().getEmailAddress())));
        commit.setTimestamp(revCommit.getAuthorIdent().getWhen());

        return commit;
    }

    /**
     * Get a dummy commit.
     *
     * @return Dummy commit
     */
    private Commit getDefaultCommit() {
        return new Commit("", "", new Date(0), new Author("", new Mail("")));
    }

    /**
     * Do a "git pull".
     */
    private void gitPull() {
        Git git = getGit();
        try {
            getAuth(git.pull().setStrategy(MergeStrategy.THEIRS)).call();
        } catch (GitAPIException e) {
            git.close();
            getLogger().error("Can't pull project with id " + getProject().getId());
        }
    }

    /**
     * Add the correct Auth to the git command.
     *
     * @param command git command
     * @return (git) command with added Auth
     */
    private TransportCommand getAuth(TransportCommand command) {
        ServiceAccount serviceAccount = getProject().getServiceAccount();
        if (serviceAccount != null) {
            if (serviceAccount instanceof UserServiceAccount) {
                UserServiceAccount userServiceAccount = (UserServiceAccount) serviceAccount;
                command.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userServiceAccount.getUsername(),
                        userServiceAccount.getPassword()));
            } else if (serviceAccount instanceof KeyServiceAccount) {
                command.setTransportConfigCallback(new TransportConfigCallback() {
                    @Override
                    public void configure(Transport transport) {
                        SshTransport sshTransport = (SshTransport) transport;
                        sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
                            @Override
                            protected void configure(OpenSshConfig.Host host, Session session) {
                                session.setConfig("StrictHostKeyChecking", "no");
                            }

                            @Override
                            protected JSch createDefaultJSch(FS fs) throws JSchException {
                                JSch defaultJSch = super.createDefaultJSch(fs);
                                defaultJSch.addIdentity(getKeyFileManager().getSshFile().getAbsolutePath());
                                return defaultJSch;
                            }
                        });
                    }
                });
            }
        }
        return command;
    }

}
