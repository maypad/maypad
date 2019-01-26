package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.config.server.YamlServerConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for a svn repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class SvnRepoManager extends RepoManager {

    private SVNClientManager svnClientManager = SVNClientManager.newInstance();
    private ISVNAuthenticationManager authManager;
    private Logger logger = LoggerFactory.getLogger(SvnRepoManager.class);
    private YamlProjectConfig projConfig;

    private String projectRoot;

    @Autowired
    ServerConfig config;

    /**
     * Constructor, prepare the SvnRepoManager.
     *
     * @param project        Project for which the svn-repository is to be managed
     * @param projectRootDir The root directory for the repo files
     */
    public SvnRepoManager(Project project, File projectRootDir) {
        super(project, projectRootDir);
    }

    /**
     * Constructor, prepare the SvnRepoManager.
     *
     * @param project Project for which the svn-repository is to be managed
     */
    public SvnRepoManager(Project project) {
        super(project);
        projectRoot = project.getRepository().getRootFolder().getAbsolutePath();
        if (project.getServiceAccount() != null) {
            if (project.getServiceAccount() instanceof KeyServiceAccount) {
                KeyServiceAccount sA = (KeyServiceAccount)project.getServiceAccount();
                File sshFile = getSshFile();
                int sshPort = (getSshKey() == -1) ? 22 : getSshKey();
                authManager = new BasicAuthenticationManager("", sshFile, "", sshPort);
            } else if (project.getServiceAccount() instanceof UserServiceAccount) {
                UserServiceAccount sA = (UserServiceAccount)project.getServiceAccount();
                authManager = new BasicAuthenticationManager(sA.getUsername(), sA.getPassword());
            }
            svnClientManager.setAuthenticationManager(authManager);
        }
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    @Override
    public List<String> getBranchNames() {
        File branchesFolder = new File(projectRoot + "/branches/");
        String[] branchList = branchesFolder.list();
        List<String> branches = new ArrayList<String>();
        for (String b : branchList) {
            if (new File(b).isDirectory()) {
                branches.add(new File(b).getName());
            }
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
        if (name.equals("trunk")) {
            String trunkPath = projConfig.getSvnTrunkDirectory();
            this.getProject().getRepository().setRootFolder(new File(projectRoot + "/" + trunkPath));
            return true;
        } else {
            String branchPath = projConfig.getSvnBranchDirectory();
            File branchFolder = new File(projectRoot + "/" + branchPath + "/" + name);
            if (!branchFolder.isDirectory() || !branchFolder.exists()) {
                logger.error("Branch of name " + name + " doesn't exist");
                logger.error("Not found in directory " + branchFolder.getAbsolutePath());
                return false;
            }
            this.getProject().getRepository().setRootFolder(branchFolder);
            return true;
        }
    }


    /**
     * Get all tags of a branch.
     *
     * @return List with tags
     */
    @Override
    public List<Tag> getTags() {
        String tagPath = projConfig.getSvnTagsDirectory();
        File tagFolder = new File(projectRoot + "/" + tagPath);
        String[] tagList = tagFolder.list();
        List<Tag> tags = new ArrayList<Tag>();
        for (String t : tagList) {
            if (new File(t).isDirectory()) {
                Tag _t = new Tag();
                _t.setName(new File(t).getName());
            }
        }
        return tags;
    }

    /**
     * Get the last commit.
     *
     * @return The last commit
     */
    @Override
    public Commit getLastCommit() {
        try {
            RecentCommitHandler rcm = new RecentCommitHandler();
            svnClientManager.getLogClient().doLog(
                    new File[] { this.getProject().getRepository().getRootFolder() },
                    SVNRevision.HEAD,
                    SVNRevision.HEAD,
                    true,
                    false,
                    1,
                    rcm
            );
            return rcm.getCommit();
        } catch (SVNException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    @Override
    protected boolean cloneRepository() {
        try {
            SVNURL url = SVNURL.parseURIEncoded(this.getProject().getRepositoryUrl());
            svnClientManager.getUpdateClient().doCheckout(
                    url, // Clone URL
                    this.getProject().getRepository().getRootFolder(), // Folder to clone into
                    SVNRevision.HEAD,
                    SVNRevision.HEAD, // What revision to clone (head == latest revision)
                    SVNDepth.INFINITY, // How far to clone the history tree
                    true
            );
            try {
                projConfig = new YamlProjectConfig(this.getProject().getRepository().getRootFolder());
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
            switchBranch("trunk");
            return true;
        } catch (SVNException ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    private boolean checkFolderStructure() {
        File trunk = new File(projectRoot + "/trunk/");
        File tags = new File(projectRoot + "/tags");
        File branches = new File(projectRoot + "/branches/");
        return trunk.exists() && tags.exists() && branches.exists();
    }

    private class RecentCommitHandler implements ISVNLogEntryHandler {

        private Commit commit;

        @Override
        public void handleLogEntry(SVNLogEntry svnLogEntry) throws SVNException {
            commit.setAuthor(new Author(svnLogEntry.getAuthor(), null));
            commit.setTimestamp(svnLogEntry.getDate());
            commit.setCommitIdentifier(null); // No SVN commit identifier
            commit.setCommitMessage(svnLogEntry.getMessage());
        }

        public Commit getCommit() {
            return commit;
        }
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    @Override
    public List<String> getBranchNames() {
        return null;
    }

    /**
     * Get the name of the main branch.
     *
     * @return Name of the main branch
     */
    @Override
    public String getMainBranchName() {
        return "trunc";
    }

    /**
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     * @return true, if the switch to other branch was successfully, else false
     */
    @Override
    public boolean switchBranch(String name) {
        return true;
    }


    /**
     * Get all tags of a branch.
     *
     * @return List with tags
     */
    @Override
    public List<Tag> getTags() {
        return null;
    }

    /**
     * Get the last commit.
     *
     * @return The last commit
     */
    @Override
    public Commit getLastCommit() {
        return null;
    }

    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    @Override
    protected boolean cloneRepository() {
        return true;
    }

}
