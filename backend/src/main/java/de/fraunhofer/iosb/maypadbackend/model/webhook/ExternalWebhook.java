package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * A webhook provided by another system.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class ExternalWebhook extends Webhook {

    /**
     * Constructor for Webhook.
     *
     * @param url the URL of the webhook.
     */
    public ExternalWebhook(String url) {
        super(url);
    }
}
