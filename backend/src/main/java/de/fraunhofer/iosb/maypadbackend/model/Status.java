package de.fraunhofer.iosb.maypadbackend.model;

/**
 * Describes different states for an action (e.g. a build).
 *
 * @version 1.0
 */
public enum Status {

    /**
     * The process was completed successfully.
     */
    SUCCESS(2),
    /**
     * The process was completed incorrectly.
     */
    FAILED(3),
    /**
     * The process ended in an error.
     */
    ERROR(3),
    /**
     * The process has not been completed yet.
     */
    RUNNING(4),
    /**
     * The process was canceled.
     */
    CANCELED(0),
    /**
     * The status of the process is unknown.
     */
    UNKNOWN(0),
    /**
     * Currently create / init something.
     */
    INIT(1) {
        @Override
        public String getName() {
            return "Initialize";
        }
    },
    /**
     * The status of a process that has timed out.
     */
    TIMEOUT(0);


    private final int priority;

    Status(int priority) {
        this.priority = priority;
    }

    /**
     * Returns the priority of the status.
     *
     * @return the priority of the status
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the name of the status.
     *
     * @return the name of the status.
     */
    public String getName() {
        return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1);
    }
}
