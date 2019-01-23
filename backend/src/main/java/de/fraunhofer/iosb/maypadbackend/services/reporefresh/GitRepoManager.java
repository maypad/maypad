package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.ArrayList;
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
                e.printStackTrace();
                getLogger().error("Can't read local repo from " + getProjectRootDir().getAbsolutePath());
            }
        }
        return localGit;
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
        List<Ref> branches = getBranches(ListBranchCommand.ListMode.REMOTE);
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
     * Get all branches of the repository.
     *
     * @param mode Selects the mode where the branches should come from (for example, only remotely)
     * @return List of all branches
     */
    private List<Ref> getBranches(ListBranchCommand.ListMode mode) {
        Git git = getGit();
        if (getGit() == null) {
            return null;
        }

        List<Ref> branches = null;
        try {
            branches = git.branchList().setListMode(mode).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            getLogger().error("Can't read branches from local repo " + getProjectRootDir().getAbsolutePath());
        }
        return branches;
    }

    /**
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     * @return true, if the switch to other branch was successfully, else false
     */
    @Override
    public boolean switchBranch(String name) {
        //Git git = getGit();
        /*try {
            //git.checkout().setName(name).call(); //TODO
            git.checkout().setName(name);
            gitPull();
            return true;
        } catch (GitAPIException e) {
            getLogger().warn("Can't switch to branch " + name + " in repo " + getProject().getRepoUrl());
            e.printStackTrace();
        }*/
        //TODO!!!
        gitPull();
        return false;
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
            e.printStackTrace();
            return tags;
        }

        for (Ref ref : refs) {
            RevWalk revWalk = new RevWalk(git.getRepository());
            RevCommit commit = null;
            try {
                commit = revWalk.parseCommit(ref.getObjectId().toObjectId());
            } catch (IOException e) {
                e.printStackTrace();
            }

            tags.add(new Tag(ref.getName(), getCommit(commit)));
        }

        return tags;
    }

    /**
     * Get the last commit of all branches.
     *
     * @return The last commit
     */
    @Override
    public Commit getLastCommit() {
        List<Ref> branches = getBranches(ListBranchCommand.ListMode.REMOTE);
        if (branches == null) {
            return getDefaultCommit();
        }
        Git git = getGit();

        RevCommit revCommit = null;
        RevWalk walk = new RevWalk(git.getRepository());
        for (Ref branch : branches) {
            RevCommit commit = null;
            try {
                commit = walk.parseCommit(branch.getObjectId());
            } catch (IOException e) {
                e.printStackTrace();
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
        }

        return getCommit(revCommit);
    }

    /**
     * Get the last Commit of the current selected branch.
     *
     * @return Last commit
     */
    public Commit getLastCommitByBranch() {
        //TODO
        return getDefaultCommit();
    }

    /**
     * Get the last Commit of the current given branch.
     *
     * @param branch Branch of the last commit
     * @return Last commit
     */
    public Commit getLastCommitByBranch(String branch) {
        switchBranch(branch);
        return getLastCommit();
    }


    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    @Override
    protected boolean cloneRepository() {
        try {
            Git git = Git.cloneRepository().setURI(getProject().getRepoUrl()).setDirectory(getProject().getRepository()
                    .getRootFolder()).call();
        } catch (GitAPIException e) {
            getLogger().warn("Can't access to repo " + getProject().getRepoUrl());
            e.printStackTrace();
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
        commit.setCommitMessage(revCommit.getShortMessage());
        commit.setCommitIdentifier(revCommit.getName());
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
     * "git pull".
     */
    private void gitPull() {
        Git git = getGit();
        git.pull();
    }

}
