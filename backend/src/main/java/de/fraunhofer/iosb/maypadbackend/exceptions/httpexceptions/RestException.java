package de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * General exception for rest with an (internal) errormessage.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public abstract class RestException extends RuntimeException {

    @Getter
    private String error;

    /**
     * Constructor with an errormessage.
     *
     * @param error errormessage for this exception
     */
    public RestException(String error) {
        super();
        this.error = error;
    }

    /**
     * Constructor with an errormessage and message.
     *
     * @param error   errormessage for this exception
     * @param message message of this exception
     */
    public RestException(String error, String message) {
        super(message);
        this.error = error;
    }

    /**
     * Get the specific error code.
     *
     * @return a http errorcode, e.g. 404
     */
    public abstract HttpStatus getStatus();

}
