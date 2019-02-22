package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * General exception for rest with an (internal) error-message.
 *
 * @version 1.0
 */
public abstract class RestException extends RuntimeException {

    @Getter
    private String error;

    /**
     * Constructor with an error-message.
     *
     * @param error error-message for this exception
     */
    public RestException(String error) {
        super();
        this.error = error;
    }

    /**
     * Constructor with an error-message and message.
     *
     * @param error   error-message for this exception
     * @param message message of this exception
     */
    public RestException(String error, String message) {
        super(message);
        this.error = error;
    }

    /**
     * Get the specific error code.
     *
     * @return a http error-code, e.g. 404
     */
    public abstract HttpStatus getStatus();

}
