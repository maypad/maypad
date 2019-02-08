package de.fraunhofer.iosb.maypadbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Threadpool executor for reporefresh.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Configuration
public class AsyncConfig {

    /**
     * Get a executor.
     *
     * @return Executor with limited poolsize.
     */
    @Bean(name = "repoRefreshPool")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        return executor;
    }

}
