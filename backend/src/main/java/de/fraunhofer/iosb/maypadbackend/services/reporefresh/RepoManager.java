package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;

import java.util.List;

/**
 * Provides specific methods to interact with a version control system.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public abstract class RepoManager {

    private Project project;

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
     */
    public abstract void switchBranch(String name);

    /**
     * Get the readme file of branch.
     *
     * @return Input of readme file
     */
    public abstract String getReadme();

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
     * Check if there are changes in the repository and update the project accordingly.
     *
     * @param project Project for which the (local) repository should be managed
     */
    public void refreshRepository(Project project) {

    }

    /**
     * Clones the repository using the repository URL stored in the project.
     */
    protected abstract void cloneRepository();

    /**
     * Returns a configuration that contains all values of the project configuration from the repository.
     *
     * @return Projectconfig of the project
     */
    protected abstract ProjectConfig getProjectConfig();


}
