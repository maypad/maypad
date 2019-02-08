package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception if the same deployment is already running.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DeploymentRunningException extends RestException {
    /**
     * Constructor with a message for the exception and the (internal) error-message.
     *
     * @param error   error-message for this exception
     * @param message message of this exception
     */
    public DeploymentRunningException(String error, String message) {
        super(error, message);
    }


    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
