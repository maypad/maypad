package de.fraunhofer.iosb.maypadbackend.model;

/**
 * Describes different states for an action (e.g. a build).
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public enum Status {

    /**
     * The process was completed successfully.
     */
    SUCCESS,
    /**
     * The process was completed incorrectly.
     */
    FAILED,
    /**
     * The process has not been completed yet.
     */
    RUNNING,
    /**
     * The process was canceled.
     */
    CANCELED,
    /**
     * The status of the process is unknown.
     */
    UNKNOWN,
    /**
     * The status of a process that has timed out.
     */
    TIMEOUT;

}
