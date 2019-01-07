package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;

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
     * @param project Project for which this null-repository is to be managed
     */
    public NullRepoManager(Project project) {
        super(project);
    }
}
