package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception for an non existing element.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class NotFoundException extends RestException {

    /**
     * Constructor with a message for the exception and the (internal) errormessage.
     *
     * @param error   errormessage for this exception
     * @param message message of this exception
     */
    public NotFoundException(String error, String message) {
        super(error, message);
    }

    /**
     * Return the HTTP-Status for an non found element.
     *
     * @return HTTP-Statuscode
     */
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;

    }

}
