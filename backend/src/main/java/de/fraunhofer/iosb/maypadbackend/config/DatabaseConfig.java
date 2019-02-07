package de.fraunhofer.iosb.maypadbackend.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuration for a connect to a database.
 */
@Configuration
public class DatabaseConfig {

    private ServerConfig yamlServerConfig;

    @Autowired
    public DatabaseConfig(ServerConfig yamlServerConfig) {
        this.yamlServerConfig = yamlServerConfig;
    }

    /**
     * Adds a DataSource object to the environment of Spring to access
     * maypad database.
     *
     * @return The DataSource object.
     */
    @Bean
    @Profile("prod")
    public DataSource dataSource() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser(yamlServerConfig.getDbUser());
        ds.setPassword(yamlServerConfig.getDbPassword());
        ds.setDatabaseName(yamlServerConfig.getDbDatabase());
        ds.setServerName(yamlServerConfig.getDbHost());
        ds.setPort(yamlServerConfig.getDbPort());
        return ds;
    }

}
