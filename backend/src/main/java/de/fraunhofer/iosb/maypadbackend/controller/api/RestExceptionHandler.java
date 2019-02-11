package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.BuildRunningException;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.DeploymentRunningException;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.InvalidTokenException;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handles exceptions for the rest-api.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * Handle {@link NotFoundException}.
     * @param ex the exception
     * @return {@link ResponseEntity} containing the HttpStatus and the error message.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        logger.debug(ex.getError());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle {@link BuildRunningException}.
     * @param ex the exception
     * @return {@link ResponseEntity} containing the HttpStatus and the error message.
     */
    @ExceptionHandler(BuildRunningException.class)
    public ResponseEntity<String> handleBuildRunningException(BuildRunningException ex) {
        logger.debug(ex.getError());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link DeploymentRunningException}.
     * @param ex the exception
     * @return {@link ResponseEntity} containing the HttpStatus and the error message.
     */
    @ExceptionHandler(DeploymentRunningException.class)
    public ResponseEntity<String> handleDeploymentRunningException(DeploymentRunningException ex) {
        logger.debug(ex.getError());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link InvalidTokenException}.
     * @param ex the exception
     * @return {@link ResponseEntity} containing the HttpStatus and the error message.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        logger.debug(ex.getError());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

}
