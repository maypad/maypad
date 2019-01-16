package de.fraunhofer.iosb.maypadbackend.config.server;

import lombok.Data;
import java.util.Map;

@Data
public class ServerConfigData {

    private int webServerPort;
    private int reloadRepositoriesSeconds;
    private Map<String, Object> maximumRefreshRequests;
    private String logLevel;
    private String repositoryStoragePath;
    private Map<String, Object> mysql;
}
