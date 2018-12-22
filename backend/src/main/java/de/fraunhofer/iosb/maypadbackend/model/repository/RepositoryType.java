package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.GitRepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoManager;

/**
 * Types of a version control system
 *
 * @author Lukas Brosch
 * @version 1.0
 */

public enum RepositoryType {

    /**
     * Version control system git
     */
    GIT {
        @Override
        public GitRepoManager toRepoManager(Project project) {
            return new GitRepoManager(project);
        }
    },
    /**
     * Version control system SVN (Subversion)
     */
    SVN {
        @Override
        public GitRepoManager toRepoManager(Project project) {
            return new GitRepoManager(project);
        }
    },
    /**
     * Unknown version control system
     */
    NONE {
        @Override
        public GitRepoManager toRepoManager(Project project) {
            return new GitRepoManager(project);
        }
    };

    public abstract RepoManager toRepoManager(Project project);

}
