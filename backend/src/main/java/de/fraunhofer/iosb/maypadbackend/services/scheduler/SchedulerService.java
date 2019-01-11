package de.fraunhofer.iosb.maypadbackend.services.scheduler;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class SchedulerService {
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private Map<Integer, ScheduledFuture<?>> taskMapping;

    private static final int poolSize = 4; //read from config
    private static final long refreshInterval = 10000; //read from config

    /**
     * Initializes the threadPoolTaskScheduler and the taskMapping.
     */
    public SchedulerService() {
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        taskMapping = new HashMap<>();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        threadPoolTaskScheduler.initialize();
    }

    /**
     * Adds a project to the scheduling.
     * @param project the project that should be refreshed in the set interval.
     */
    public void scheduleRepoRefresh(Project project) {
        if (!taskMapping.containsKey(project.getId())) {
            RefreshTask task = new RefreshTask();
            ScheduledFuture<?> sf = threadPoolTaskScheduler.scheduleWithFixedDelay(task,
                    refreshInterval);
            taskMapping.put(project.getId(), sf);
        }
    }

    /**
     * Removes a project from the scheduling.
     * @param project the project that should be removed from the scheduling
     */
    public void unscheduleRepoRefresh(Project project) {
        if (taskMapping.containsKey(project.getId())) {
            ScheduledFuture<?> sf = taskMapping.get(project.getId());
            sf.cancel(false);
            taskMapping.remove(project.getId());
        }
    }
}
