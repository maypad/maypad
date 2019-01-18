package de.fraunhofer.iosb.maypadbackend.config.server;

public interface ServerConfig {

    public int getWebServerPort();

    public int getReloadRepositoriesSeconds();

    public boolean isMaximumRefreshRequestsEnabled();

    public int getMaximumRefreshRequestsSeconds();

    public int getMaximumRefreshRequests();

    public String getLogLevel();

    public String getRepositoryStoragePath();

    public String getDbUser();

    public String getDbPassword();

    public String getDbDatabase();

    public String getDbHost();

    public int getDbPort();

    public int getSchedulerPoolSize();

    public int getWebhookTokenLength();

}
