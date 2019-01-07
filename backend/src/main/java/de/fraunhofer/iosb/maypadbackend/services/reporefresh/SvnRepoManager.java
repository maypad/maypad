package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.model.Project;

/**
 * Manager for a svn repository.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class SvnRepoManager extends RepoManager {

    /**
     * Constructor, prepare the SvnRepoManager.
     *
     * @param project Project for which the svn-repository is to be managed
     */
    public SvnRepoManager(Project project) {
        super(project);
    }
}
