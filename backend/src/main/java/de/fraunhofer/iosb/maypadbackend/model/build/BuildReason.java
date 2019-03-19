package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.services.sse.SseMessages;

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
    BUILD_NOT_STARTED;

    /**
     * Returns the correct sse-message for this reason.
     *
     * @return sse-message for this reason
     */
    public String toSseMessage() {
        switch (this) {
            case BUILD_NOT_STARTED:
                return SseMessages.BUILD_REASON_BUILD_NOT_STARTED;
            case BUILD_FAILED:
                return SseMessages.BUILD_REASON_BUILD_FAILED;
            case DEPENDENCY_BUILD_FAILED:
                return SseMessages.BUILD_REASON_DEPENDENCY_BUILD_FAILED;
            default:
                return SseMessages.MISSING_MESSAGE;
        }
    }
}
