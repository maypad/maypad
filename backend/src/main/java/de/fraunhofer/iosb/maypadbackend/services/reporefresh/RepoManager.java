package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.util.FileUtil;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
 * Provides specific methods to interact with a version control system.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public abstract class RepoManager {

    private Project project;
    private static final Logger logger = LoggerFactory.getLogger(RepoManager.class);
    @Getter(AccessLevel.PROTECTED)
    private File projectRootDir;
    @Getter(AccessLevel.PROTECTED)
    private KeyFileManager keyFileManager;

    /**
     * Constructor, prepare the RepoManager.
     *
     * @param project        Project for which the repository is to be managed
     * @param projectRootDir The root directory for the repo files
     */
    public RepoManager(Project project, File projectRootDir) {
        this.project = project;
        this.projectRootDir = projectRootDir;
    }

    /**
     * Constructor, prepare the RepoManager.
     *
     * @param project Project for which the repository is to be managed
     */
    public RepoManager(Project project) {
        this.project = project;
    }

    /**
     * Init the project repo manager.
     *
     * @param projectRootDir The root directory for the repo files
     */
    public void initRepoManager(File keyDir, File projectRootDir) {
        this.projectRootDir = projectRootDir;
        this.keyFileManager = new KeyFileManager(keyDir, project);
    }

    /**
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    public abstract List<String> getBranchNames();

    /**
     * Get the name of the main branch.
     *
     * @return Name of the main branch
     */
    public abstract String getMainBranchName();

    /**
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     * @return true, if the switch to other branch was successfully, else false
     */
    public abstract boolean switchBranch(String name);

    /**
     * Get all tags of a branch.
     *
     * @return List with tags
     */
    public abstract List<Tag> getTags();

    /**
     * Get the last commit.
     *
     * @return The last commit
     */
    public abstract Commit getLastCommit();

    /**
     * Clean the RepoManager after usage.
     */
    protected void cleanUp() {
        //default: nothing to do.
    }

    /**
     * Clones the repository using the repository URL stored in the project.
     *
     * @return True in success, else false
     */
    protected abstract boolean cloneRepository();

    /**
     * Get the project of this repomanager.
     *
     * @return Project of this repomanager
     */
    public Project getProject() {
        return project;
    }

    /**
     * Get the repomanager logger.
     *
     * @return Logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns a configuration with path that contains all values of the project configuration from the repository.
     *
     * @return Tuple of projectconfig and path to config
     */
    public Tuple<ProjectConfig, File> getProjectConfig() {
        File mapaydConfigPath = new File(getProjectRootDir().getAbsolutePath() + File.separator + "maypad.yaml");
        if (mapaydConfigPath.exists() && mapaydConfigPath.canRead()) {
            try {
                return new Tuple<>(new YamlProjectConfig(mapaydConfigPath), mapaydConfigPath);
            } catch (FileNotFoundException e) {
                logger.error("Projectconfiguration is missing at " + mapaydConfigPath.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Projectconfiguration at " + mapaydConfigPath.getAbsolutePath() + " hasn't valid YAML syntax");
            }
        }
        return null;
    }

    /**
     * Get the readme file of branch.
     *
     * @return Input of readme file
     */
    public String getReadme() {
        String content = FileUtil.getFileContent(new File(getProjectRootDir() + File.separator + "README.md"));
        return content == null ? "" : content;
    }

    /**
     * Get the location root dir of the current branch.
     *
     * @return File to the location
     */
    public File getCurrentBranchLocation() {
        return projectRootDir;
    }

    protected int getSshPort() {
        try {
            String[] spl = project.getRepositoryUrl().split(":");
            return Integer.parseInt(spl[spl.length - 1]);
        } catch (Exception ex) {
            logger.error("Could not get port from URL.");
            return -1;
        }
    }
}
