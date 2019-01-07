package de.fraunhofer.iosb.maypadbackend.config;

import lombok.Data;

import java.io.File;

/**
 *  Configuration class to access configuration parameters written in
 *  the server-configuration-YAML-file.
 *
 * @author Max Willich
 */
@Data
public class ServerConfig {
    private int webServerPort;
    private long updateInterval;
    private int schedulerThreadNum;
    private boolean enableMaximumRefresh;
    private long maximumRefreshInterval;
    private int maximumRefresh;
    private File repositoryStorage;
    private String dbUser;
    private String dbPassword;
    private String dbHost;
    private int dbPort;
}
