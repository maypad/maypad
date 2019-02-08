package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception if the same build is already running.
 */
public class BuildRunningException extends RestException {

    /**
     * Constructor with a message for the exception and the (internal) error-message.
     *
     * @param error   error-message for this exception
     * @param message message of this exception
     */
    public BuildRunningException(String error, String message) {
        super(error, message);
    }


    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
