package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
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
    private ProjectConfig projConfig;

    private static SvnRepoManager instance = null;

    private String projectRoot;

    @Autowired
    ServerConfig config;

    /**
     * Constructor, prepare the SvnRepoManager.
     *
     * @param project Project for which the svn-repository is to be managed
     */
    private SvnRepoManager(Project project) {
        super(project);
        logger.info("Cloned project into " + projectRoot);
        if (project.getServiceAccount() != null) {
            if (project.getServiceAccount() instanceof KeyServiceAccount) {
                KeyFileManager kfm = new KeyFileManager(this.getProjectRootDir(), this.getProject());
                KeyServiceAccount sA = (KeyServiceAccount)project.getServiceAccount();
                File sshFile = kfm.getSshFile();
                int sshPort = (this.getSshPort() == -1) ? 22 : getSshPort();
                authManager = new BasicAuthenticationManager("", sshFile, "", sshPort);
            } else if (project.getServiceAccount() instanceof UserServiceAccount) {
                UserServiceAccount sA = (UserServiceAccount)project.getServiceAccount();
                authManager = new BasicAuthenticationManager(sA.getUsername(), sA.getPassword());
            }
            svnClientManager.setAuthenticationManager(authManager);
        }
    }

    public static SvnRepoManager getInstance(Project project) {
        if (instance == null) {
            return (instance = new SvnRepoManager(project));
        }
        return instance;
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    @Override
    public List<String> getBranchNames() {
        File branchesFolder = new File(this.getProjectRootDir().getAbsolutePath() + "/branches/");
        String[] branchList = branchesFolder.list();
        if (branchList == null) {
            logger.info("Project contains no branches");
            return new ArrayList<String>(); }
        List<String> branches = new ArrayList<String>();
        for (String b : branchList) {
            logger.info("Found branch: " + b);
            if (new File(branchesFolder.getAbsolutePath() + "/" + b).isDirectory()) {
                branches.add(new File(b).getName());
            }
        }
        logger.info("Found " + branches.size() + " branches.");
        return branches;
    }

    /**
     * Get the name of the main branch.
     *
     * @return Name of the main branch
     */
    @Override
    public String getMainBranchName() {
        return "trunk";
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
            projectRoot = this.getProjectRootDir().getAbsolutePath() + "/" + trunkPath;
            return true;
        } else {
            String branchPath = projConfig.getSvnBranchDirectory();
            File branchFolder = new File(this.getProjectRootDir().getAbsolutePath() + "/" + branchPath + "/" + name);
            if (!branchFolder.isDirectory() || !branchFolder.exists()) {
                logger.error("Branch of name " + name + " doesn't exist");
                logger.error("Not found in directory " + branchFolder.getAbsolutePath());
                return false;
            }
            projectRoot = branchFolder.getAbsolutePath();
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
        if (tagList == null) {
            logger.info("Project contains no tags.");
            return new ArrayList<Tag>();
        }
        List<Tag> tags = new ArrayList<Tag>();
        for (String t : tagList) {
            if (new File(tagFolder.getAbsolutePath() + "/" + t).isDirectory()) {
                Tag _t = new Tag();
                _t.setName(new File(t).getName());
            }
        }
        logger.info("Found " + tags.size() + " tags.");
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
                    new File[] { this.getProjectRootDir() },
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
                    this.getProjectRootDir(), // Folder to clone into
                    SVNRevision.HEAD,
                    SVNRevision.HEAD, // What revision to clone (head == latest revision)
                    SVNDepth.INFINITY, // How far to clone the history tree
                    true
            );
            projConfig = this.getProjectConfig().getKey();
            switchBranch("trunk");
            return true;
        } catch (SVNException ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    @Override
    public File getCurrentBranchLocation() {
        return new File(projectRoot);
    }


    @Override
    public Tuple<ProjectConfig, File> getProjectConfig() {
        try {
            File f = new File(this.getProjectRootDir().getAbsolutePath() + "/maypad.yaml");
            logger.info("Searching for config at " + f.getAbsolutePath());
            return new Tuple<ProjectConfig, File>(new YamlProjectConfig(f), f);
        } catch (IOException ex) {
            logger.error("Can't find project configuration (maypad.yaml).");
            return null;
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
            commit = new Commit();
            commit.setAuthor(new Author(svnLogEntry.getAuthor(), null));
            commit.setTimestamp(svnLogEntry.getDate());
            commit.setIdentifier(null); // No SVN commit identifier
            commit.setMessage(svnLogEntry.getMessage());
        }

        public Commit getCommit() {
            return commit;
        }
    }

}
