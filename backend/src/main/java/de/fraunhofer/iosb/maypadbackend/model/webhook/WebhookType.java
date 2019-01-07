package de.fraunhofer.iosb.maypadbackend.model.webhook;

/**
 * Functionality provided by a internal webhook.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public enum WebhookType {

    /**
     * Notify Maypad that a build is complete.
     */
    UPDATEBUILD,
    /**
     * Notify Maypad that something has changed on the repository.
     */
    REFRESH;

}
