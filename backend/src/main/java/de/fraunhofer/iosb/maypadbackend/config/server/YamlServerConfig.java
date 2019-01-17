package de.fraunhofer.iosb.maypadbackend.config.server;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
@Data
public class YamlServerConfig implements ServerConfig {

    private static Logger logger = LoggerFactory.getLogger(YamlServerConfig.class);

    @Value("${MAYPAD_HOME:/usr/share/maypad}")
    private static String maypadHome;

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

    /**
     * Bean for easy access to properties in maypad.yaml
     *
     * @return A Placeholder for Properties, so that @Value works
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer
                = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        File configFile = new File(maypadHome + "/config.yaml");
        if (!configFile.exists()) {
            logger.error(configFile.getAbsolutePath() + " does not exist!");
            yaml.setResources(new ClassPathResource("maypad.yaml"));
        } else {
            yaml.setResources(new FileSystemResource(configFile));
        }
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }
}
