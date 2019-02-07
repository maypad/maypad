package de.fraunhofer.iosb.maypadbackend.config.server;

/**
 * Interface for classes that implement the parsing of a server configuration file.
 */
public interface ServerConfig {

    /**
     * Get the port under which the web-server should run.
     *
     * @return port of the server
     */
    int getWebServerPort();

    /**
     * Get if the maximum refresh cap is enabled or not.
     *
     * @return true, if enabled, else false.
     */
    boolean isMaximumRefreshRequestsEnabled();

    /**
     * Get the amount of seconds, until a refresh requests expires for the cap.
     *
     * @return amount of seconds, the refresh counts to the cap.
     */
    int getMaximumRefreshRequestsSeconds();

    /**
     * Get the amount of refresh requests which are allowed in X seconds (X defined in key maximumRefreshRequestsSeconds).
     *
     * @return the amount of refresh requests
     */
    int getMaximumRefreshRequests();

    /**
     * Get the time (in seconds) in which the scheduler should update the projects.
     *
     * @return the delay between two refreshes
     */
    int getReloadRepositoriesSeconds();

    /**
     * Get the size of the pool size, the scheduler will use.
     *
     * @return size of the threadpool
     */
    int getSchedulerPoolSize();

    /**
     * Get the loglevel of the server.
     *
     * @return log level of the server
     */
    String getLogLevel();

    /**
     * Get the directory where the repositories should be downloaded and stored.
     *
     * @return Path to the directory
     */
    String getRepositoryStoragePath();

    /**
     * Get the username for the database connection.
     *
     * @return the username
     */
    String getDbUser();

    /**
     * Get the password for the database connection.
     *
     * @return the password
     */
    String getDbPassword();

    /**
     * Get the name of the database.
     *
     * @return Name of the database.
     */
    String getDbDatabase();

    /**
     * Get the host (e.g. IP) for the database connection.
     *
     * @return the host.
     */
    String getDbHost();

    /**
     * Get the port for the database connection.
     *
     * @return the port
     */
    int getDbPort();

    /**
     * Get the length of the generated webhook tokens.
     *
     * @return the length of the tokens
     */
    int getWebhookTokenLength();

    /**
     * Get the domain or IP under which maypad can be called.
     *
     * @return the domain
     */
    String getDomain();

}
