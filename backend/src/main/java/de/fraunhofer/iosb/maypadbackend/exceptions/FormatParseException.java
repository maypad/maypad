package de.fraunhofer.iosb.maypadbackend.exceptions;

import java.io.IOException;

/**
 * Exception if e.g. a file couldn't be parsed.
 *
 * @version 1.0
 */
public class FormatParseException extends IOException {

    /**
     * Constructor for FormatParseException.
     */
    public FormatParseException() {
        super();
    }

    /**
     * Constructor for FormatParseException with a message.
     *
     * @param message Message for exception
     */
    public FormatParseException(String message) {
        super(message);
    }
}
