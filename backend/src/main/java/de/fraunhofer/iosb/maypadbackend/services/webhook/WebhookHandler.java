package de.fraunhofer.iosb.maypadbackend.services.webhook;

/**
 * WebhookHandlers define the action that is executed when a mapped webhook is called.
 */
public interface WebhookHandler {
    /**
     * Executes the defined action for a specific WebhookHandler.
     */
    public void handle();
}
