package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.*;

/**
 * A webhook provided by Maypad
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class InternalWebhook extends Webhook {

    @Basic
    private String token;
    @Enumerated(EnumType.STRING)
    private WebhookType type;

}
