package de.fraunhofer.iosb.maypadbackend.config;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    private ServerConfig serverConfig;

    /**
     * Set the server config.
     *
     * @param serverConfig the server config.
     */
    @Autowired
    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * Return scheduler used in SchedulerService.
     * @return the scheduler
     */
    @Bean
    public ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(serverConfig.getSchedulerPoolSize());
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
