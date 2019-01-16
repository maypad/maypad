package de.fraunhofer.iosb.maypadbackend.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Autowired
    private ServerConfig yamlServerConfig;

    /**
     * Adds a DataSource object to the environment of Spring to access
     * maypad database.
     *
     * @return The DataSource object.
     */
    // @Bean
    public DataSource dataSource() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser(yamlServerConfig.getDbUser());
        ds.setPassword(yamlServerConfig.getDbPassword());
        ds.setDatabaseName(yamlServerConfig.getDbDatabase());
        ds.setUrl(yamlServerConfig.getDbHost());
        ds.setPort(yamlServerConfig.getDbPort());
        return ds;
    }

}
