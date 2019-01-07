package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * A webhook provided by Maypad
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class InternalWebhook extends Webhook {

    @Column
    private String token;
    @Enumerated(EnumType.STRING)
    private WebhookType type;

}
