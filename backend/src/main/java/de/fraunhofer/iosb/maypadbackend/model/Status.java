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
    FAILED {
        @Override
        public String getName() {
            return "Error";
        }
    },
    /**
     * The process ended in an error.
     */
    ERROR,
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
     * Currently create / init something.
     */
    CREATING,
    /**
     * The status of a process that has timed out.
     */
    TIMEOUT;

    public String getName() {
        return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1);
    }
}
