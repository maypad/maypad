package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * A webhook provided by Maypad.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class InternalWebhook extends Webhook {

    @Column
    private String token;
    @Enumerated(EnumType.STRING)
    private WebhookType type;

    /**
     * Constructor for InternalWebhook.
     * @param url the URL of the webhook.
     * @param token the token that identifies the webhook in maypad
     * @param type the type of the webhook
     */
    public InternalWebhook(String url, String token, WebhookType type) {
        super(url);
        this.token = token;
        this.type = type;
    }
}
