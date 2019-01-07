package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;

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
}
