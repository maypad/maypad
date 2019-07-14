package de.fraunhofer.iosb.maypadbackend.services.sse;

/**
 * Contains messages used in events.
 */
public class SseMessages {

    //fallback value
    public static final String MISSING_MESSAGE = "missing_message";

    //updated project
    public static final String SERVICEACCOUNT_CHANGED = "serviceaccount_changed";

    //refreshed repository
    public static final String PROJECT_REFRESH_SUCCESSFUL = "refresh_successful";
    public static final String PROJECT_INIT_SUCCESSFUL = "init_successful";

    //repomanger
    public static final String REPO_MANAGER_MISSING_CONFIG = "config_missing";

    //repomanager, git specific
    public static final String REPO_MANAGER_GIT_CLONE_FAILED = "git_clone_failed";
    public static final String REPO_MANAGER_GIT_NOT_AVAILABLE = "git_not_available";
    public static final String REPO_MANAGER_GIT_AUTH_FAILED = "git_auth_failed";

    //repomanager, svn specific
    public static final String REPO_MANAGER_SVN_BAD_URL = "svn_bad_url";
    public static final String REPO_MANAGER_SVN_CONNECTION_REFUSED = "svn_connection_refused";
    public static final String REPO_MANAGER_SVN_NOT_FOUND = "svn_not_found";
    public static final String REPO_MANAGER_SVN_AUTH_FAILED = "svn_auth_failed";
    public static final String REPO_MANAGER_SVN_UNKNOWN = "svn_unknown_reason";

    //build-reason
    public static final String BUILD_REASON_BUILD_NOT_STARTED = "build_not_started";
    public static final String BUILD_REASON_BUILD_FAILED = "build_failed";
    public static final String BUILD_REASON_DEPENDENCY_BUILD_FAILED = "dependency_build_failed";

    private SseMessages() {
    }
}
