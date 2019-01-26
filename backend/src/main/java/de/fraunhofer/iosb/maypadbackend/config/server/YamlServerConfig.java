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

import java.io.File;

/**
 * Serverconfiguration as YAML File.
 */
@Configuration
@Data
public class YamlServerConfig implements ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(YamlServerConfig.class);

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
    @Value("${scheduler.poolSize:${MAYPAD_SCHEDULER_POOL_SIZE:2}}")
    private int schedulerPoolSize;
    @Value("${webhook.tokenLength:${MAYPAD_WEBHOOK_TOKEN_LENGTH:20}}")
    private int webhookTokenLength;
    @Value("${domain:${MAYPAD_DOMAIN:not set}}")
    private String domain;

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
