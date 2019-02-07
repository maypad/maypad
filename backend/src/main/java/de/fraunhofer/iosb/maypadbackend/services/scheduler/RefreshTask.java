package de.fraunhofer.iosb.maypadbackend.services.scheduler;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable that refreshes the given Project.
 */
public class RefreshTask implements Runnable {
    private int projectId;
    private RepoService repoService;

    private static final Logger logger = LoggerFactory.getLogger(RefreshTask.class);

    /**
     * Constructor for RefreshTask.
     *
     * @param projectId   the id of the project that should be updated
     * @param repoService the RepoService used to refresh a repository
     */
    public RefreshTask(int projectId, RepoService repoService) {
        this.projectId = projectId;
        this.repoService = repoService;
    }

    @Override
    public void run() {
        logger.debug("Running scheduled task for project " + projectId);
        repoService.refreshProject(projectId);
    }
}
