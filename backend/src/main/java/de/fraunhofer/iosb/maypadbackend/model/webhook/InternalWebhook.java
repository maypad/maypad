package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.Entity;

/**
 * A webhook provided by Maypad
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class InternalWebhook {

    private String token;
    private WebhookType type;

}
