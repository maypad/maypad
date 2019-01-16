package de.fraunhofer.iosb.maypadbackend.config.server;

import de.fraunhofer.iosb.maypadbackend.config.factories.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class,
                value = "classpath:maypad.yml", name = "server-config")
public class ServerConfig {
    @Value("${webServerPort:${MAYPAD_WEBSERVER_PORT:-1}}")
    private int webServerPort;

    @Value("${reloadRepositoriesSeconds:${MAYPAD_RELOAD_REPOSITORIES_SECONDS:900}}")
    private int reloadRepositoriesSeconds;

    @Value("${maximumRefreshRequests.enabled:${MAYPAD_MAXIMUM_REFRESH_REQUESTS_ENABLED:0}}")
    private boolean maximumRefreshRequestsEnabled;

    @Value("${maximumRefreshRequests.seconds:${MAYPAD_MAXIMUM_REFRESH_REQUESTS_SECONDS:900}}")
    private int maximumRefreshRequestsSeconds;

    @Value("${maximumRefreshRequests.maximumRequests:${MAYPAD_MAXIMUM_REFRESH_REQUESTS:100}}")
    private int maximumRefreshRequests;

    @Value("${logLevel:${MAYPAD_LOG_LEVEL:INFO}}")
    private String logLevel;

    @Value("${repositoryStoragePath:${MAYPAD_REPOSITORY_STORAGE_PATH:not set}}")
    private String repositoryStoragePath;

    @Value("${mysql.user:${MAYPAD_DB_USER:not set}}")
    private String dbUser;

    @Value("${mysql.password:${MAYPAD_DB_PASSWORD:not set}}")
    private String dbPassword;

    @Value("${mysql.database:${MAYPAD_DB_DATABASE:not set}}")
    private String dbDatabase;

    @Value("${mysql.host:${MAYPAD_DB_HOST:not set}}")
    private String dbHost;

    @Value("${mysql.port:${MAYPAD_DB_PORT:-1}}")
    private int dbPort;
}
