package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.YamlProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.util.FileUtil;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


/**
 * Provides specific methods to interact with a version control system.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public abstract class RepoManager {

    private Project project;
    private Logger logger = LoggerFactory.getLogger(RepoManager.class);
    @Setter
    @Getter(AccessLevel.PROTECTED)
    private File projectRootDir;

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
     * Get the names of all existing branches of the repository.
     *
     * @return List of all branchnames
     */
    public abstract List<String> getBranchNames();

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
     * Create a SSH pem File.
     *
     * @return SSH File with key
     */
    protected File getSshFile() {
        File keyDir = new File(projectRootDir.getAbsolutePath() + File.separator + "keys");
        if (!keyDir.isDirectory() && !keyDir.exists()) {
            logger.info("Folder for keys doesn't exists. So, create it.");
            if (!keyDir.mkdirs()) {
                logger.error("Can't create folder " + keyDir.getAbsolutePath());
                return null;
            }
        }
        File keyFile = new File(keyDir.getAbsolutePath() + File.separator + project.getId());
        //
        if (keyFile.exists()) {
            return keyFile;
        }
        //Create new keyfile
        if (!(project.getServiceAccount() instanceof KeyServiceAccount)) {
            logger.warn("Project with id " + project.getId() + " hasn't an serviceaccount with a key.");
            return null;
        }
        KeyServiceAccount serviceAccount = (KeyServiceAccount) project.getServiceAccount();
        List<String> lines = Arrays.asList("-----BEGIN RSA PRIVATE KEY-----", serviceAccount.getKey(), "-----END RSA PRIVATE KEY-----");
        Path file = Paths.get(keyFile.getAbsolutePath());
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            logger.error("Can't write key file for project with id " + project.getId());
            return null;
        }
        return keyFile;
    }

    /**
     * Get the location root dir of the current branch.
     *
     * @return File to the location
     */
    public File getCurrentBranchLocation() {
        return projectRootDir;
    }

    /**
     * Delete the SSH key file, if exists.
     */
    protected void deleteSshFile() {
        File keyFile = new File(projectRootDir.getAbsolutePath() + File.separator + "keys" + File.separator + project.getId());
        if (keyFile.exists()) {
            if (!keyFile.delete()) {
                logger.error("Can't delete key file for project with id " + project.getId());
            }
        }
    }

}
