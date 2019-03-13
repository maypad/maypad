package de.fraunhofer.iosb.maypadbackend.config.server;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Serverconfiguration as YAML File.
 */
@Configuration
@Data
public class YamlServerConfig implements ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(YamlServerConfig.class);

    @Value("${MAYPAD_WEB_SERVER_PORT:${webServerPort:-1}}")
    private int webServerPort;
    @Value("${MAYPAD_RELOAD_REPOSITORIES_SECONDS:${reloadRepositoriesSeconds:900}}")
    private int reloadRepositoriesSeconds;
    @Value("${MAYPAD_MAXIMUM_REFRESH_REQUESTS_ENABLED:${maximumRefreshRequests.enabled:0}}")
    private boolean maximumRefreshRequestsEnabled;
    @Value("${MAYPAD_MAXIMUM_REFRESH_REQUESTS_SECONDS:${maximumRefreshRequests.seconds:900}}")
    private int maximumRefreshRequestsSeconds;
    @Value("${MAYPAD_MAXIMUM_REFRESH_REQUESTS_MAXIMUM_REQUESTS:${maximumRefreshRequests.maximumRequests:100}}")
    private int maximumRefreshRequests;
    @Value("${MAYPAD_LOG_LEVEL:${logLevel:INFO}}")
    private String logLevel;
    @Value("${MAYPAD_REPOSITORY_STORAGE_PATH:${repositoryStoragePath:not set}}")
    private String repositoryStoragePath;
    @Value("${MAYPAD_MYSQL_USER:${mysql.user:not set}}")
    private String dbUser;
    @Value("${MAYPAD_MYSQL_PASSWORD:${mysql.password:not set}}")
    private String dbPassword;
    @Value("${MAYPAD_MYSQL_DATABASE:${mysql.database:not set}}")
    private String dbDatabase;
    @Value("${MAYPAD_MYSQL_HOST:${mysql.host:not set}}")
    private String dbHost;
    @Value("${MAYPAD_MYSQL_PORT:${mysql.port:-1}}")
    private int dbPort;
    @Value("${MAYPAD_SCHEDULER_POOL_SIZE:${scheduler.poolSize:2}}")
    private int schedulerPoolSize;
    @Value("${MAYPAD_WEBHOOK_TOKEN_LENGTH:${webhook.tokenLength:20}}")
    private int webhookTokenLength;
    @Value("${MAYPAD_DOMAIN:${domain:not set}}")
    private String domain;

    @PostConstruct
    private void init() {
        logger.info("Active configuration:\n"
                    + "webServerPort: {}\n"
                    + "reloadRepositoriesSeconds: {}\n"
                    + "scheduler.reloadRepositoriesSeconds: {}\n"
                    + "webhook.tokenLength: {}\n"
                    + "maximumRefreshRequests.enabled: {}\n"
                    + "maximumRefreshRequests.seconds: {}\n"
                    + "maximumRefreshRequests.maxiumumRequests: {}\n"
                    + "logLevel: {}\n"
                    + "repositoryStoragePath: {}\n"
                    + "mysql.user: {}\n"
                    + "mysql.password: {}\n"
                    + "mysql.database: {}\n"
                    + "mysql.host: {}\n"
                    + "mysql.port: {}",
                    webServerPort, reloadRepositoriesSeconds, reloadRepositoriesSeconds, webhookTokenLength,
                    maximumRefreshRequestsEnabled, maximumRefreshRequestsSeconds, maximumRefreshRequests,
                    logLevel, repositoryStoragePath, dbUser, dbPassword, dbDatabase, dbHost, dbPort);
    }

    /**
     * Bean for easy access to properties.
     *
     * @return A Placeholder for Properties, so that @Value works
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer
                = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        String maypadHomePath = System.getenv("MAYPAD_HOME");
        maypadHomePath = maypadHomePath == null ? "/usr/share/maypad" : maypadHomePath;
        File maypadHome = new File(maypadHomePath);
        File configFile = new File(maypadHome.getAbsolutePath() + "/config.yaml");
        if (maypadHome.isDirectory()) {
            if (configFile.exists()) {
                logger.info("Serverconfig found.");
                yaml.setResources(new FileSystemResource(configFile));
            } else {
                logger.error("No config found in " + maypadHome.getAbsolutePath());
                throw new RuntimeException("No config found in " + maypadHome.getAbsolutePath());
            }
        } else {
            logger.error("MAYPAD_HOME is not set properly.");
            throw new RuntimeException("MAYPAD_HOME ist not set properly");
        }
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }
}
