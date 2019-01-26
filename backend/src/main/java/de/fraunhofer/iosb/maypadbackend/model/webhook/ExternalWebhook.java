package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

/**
 * A webhook provided by another system.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ExternalWebhook extends Webhook {

    /**
     * Constructor for Webhook.
     */
    public ExternalWebhook() {

    }

    /**
     * Constructor for Webhook.
     *
     * @param url the URL of the webhook.
     */
    public ExternalWebhook(String url) {
        super(url);
    }
}
