package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.GitRepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.NullRepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.SvnRepoManager;

/**
 * Types of a version control system.
 *
 * @version 1.0
 */

public enum RepositoryType {

    /**
     * Version control system git.
     */
    GIT {
        @Override
        public RepoManager toRepoManager(Project project) {
            return new GitRepoManager(project);
        }
    },

    /**
     * Version control system SVN (Subversion).
     */
    SVN {
        @Override
        public RepoManager toRepoManager(Project project) {
            return new SvnRepoManager(project);
        }
    },

    /**
     * Unknown version control system.
     */
    NONE {
        @Override
        public RepoManager toRepoManager(Project project) {
            return new NullRepoManager(project);
        }
    };

    /**
     * Get RepoManager of Repotype.
     *
     * @param project Project for the repomanager
     * @return Accompanying repomanager
     */
    public abstract RepoManager toRepoManager(Project project);


}
