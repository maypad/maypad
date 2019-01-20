package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;

import java.util.List;

/**
 * Dummy for an unknown type of a repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class NullRepoManager extends RepoManager {

    /**
     * Constructor, prepare the NullRepoManager.
     *
     * @param project        Project for which this null-repository is to be managed
     * @param projectRootDir The root directory for the repo files
     */
    public NullRepoManager(Project project, File projectRootDir) {
        super(project, projectRootDir);
    }

    /**
     * Constructor, prepare the NullRepoManager.
     *
     * @param project Project for which this null-repository is to be managed
     */
    public NullRepoManager(Project project) {
        super(project);
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
     * Switches the branch of a repository .
     *
     * @param name Name of the branch
     */
    @Override
    public void switchBranch(String name) {

    }

    /**
     * Get the readme file of branch.
     *
     * @return Input of readme file
     */
    @Override
    public String getReadme() {
        return null;
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
     */
    @Override
    protected void cloneRepository() {

    }

    /**
     * Returns a configuration that contains all values of the project configuration from the repository.
     *
     * @return Projectconfig of the project
     */
    @Override
    protected ProjectConfig getProjectConfig() {
        return null;
    }
}
