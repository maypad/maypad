package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.GitRepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.NullRepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.SvnRepoManager;

/**
 * Types of a version control system.
 *
 * @author Lukas Brosch
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

        @Override
        public boolean isUrlBelongToRepotype(String url) {
            return url.endsWith(".git");
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

        @Override
        public boolean isUrlBelongToRepotype(String url) {
            return url.matches("(.*):\\/\\/(.*)\\/");

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

        @Override
        public boolean isUrlBelongToRepotype(String url) {
            return false;
        }
    };

    /**
     * Get RepoManager of Repotype.
     *
     * @param project Project for the repomanager
     * @return Accompanying repomanager
     */
    public abstract RepoManager toRepoManager(Project project);

    /**
     * Check whether a url (for a repo) corresponding to this repotype.
     *
     * @param url Url to the repository
     * @return True, if url belong to the corresponding repotype, else false
     */
    public abstract boolean isUrlBelongToRepotype(String url);


}
