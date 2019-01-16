package de.fraunhofer.iosb.maypadbackend.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 *  Configuration class to establish a connection to a database, which is used by
 *  Maypad, e.g. for the Java Persistence API.
 *
 * @author Max Willich
 */
@Configuration
@PropertySource("classpath:application.properties")
@Data
public class DatabaseConfigOld {

    @Value("${database.user}")
    private String user;

    @Value("${database.password}")
    private String password;

    @Value("${database.database}")
    private String database;

    @Value("${database.host}")
    private String host;

    @Value("${database.port}")
    private int port;


    /**
     * Creates a DataSource-Object to access the Maypad database with
     * the connection details specified in the application.properties file.
     *
     * @return The DataSource-Object for connection purposes.
     */
    public DataSource getDataSource() {
        MysqlDataSource ret = new MysqlDataSource();
        ret.setUser(user);
        ret.setPassword(password);
        ret.setDatabaseName(database);
        ret.setUrl(host);
        ret.setPort(port);
        return ret;
    }
}
