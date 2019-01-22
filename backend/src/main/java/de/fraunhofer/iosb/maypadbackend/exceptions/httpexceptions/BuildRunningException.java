package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import org.springframework.http.HttpStatus;

public class BuildRunningException extends RestException {

    /**
     * Constructor with a message for the exception and the (internal) errormessage.
     *
     * @param error   errormessage for this exception
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
