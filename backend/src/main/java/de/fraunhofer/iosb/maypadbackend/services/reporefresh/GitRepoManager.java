package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;

/**
 * Manager for a git repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class GitRepoManager extends RepoManager {

    /**
     * Constructor, prepare the GitRepoManager.
     *
     * @param project Project for which the git-repository is to be managed
     */
    public GitRepoManager(Project project) {
        super(project);
    }
}
