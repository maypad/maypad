package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidTokenException extends RestException {
    /**
     * Constructor with a message for the exception and the (internal) errormessage.
     *
     * @param error   errormessage for this exception
     * @param message message of this exception
     */
    public InvalidTokenException(String error, String message) {
        super(error, message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
