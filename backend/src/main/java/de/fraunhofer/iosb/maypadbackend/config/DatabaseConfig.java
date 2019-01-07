package de.fraunhofer.iosb.maypadbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

/**
 *  Configuration class to establish a connection to a database, which is used by
 *  Maypad, e.g. for the Java Persistence API.
 *
 * @author Max Willich
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource getDataSource() {
        return null;
    }

    @Bean
    public Properties getAdditionalProperties() {
        return null;
    }
}
