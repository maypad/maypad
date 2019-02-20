package de.fraunhofer.iosb.maypadbackend.model.build;

/**
 * Provides additional information about the build status.
 */
public enum BuildReason {

    /**
     * Used when the build of a dependency failed.
     */
    DEPENDENCY_BUILD_FAILED,

    /**
     * Used when the build failed.
     */
    BUILD_FAILED,

    /**
     * Used when the build cannot be started (e.g. buildurl not valid).
     */
    BUILD_NOT_STARTED
}
