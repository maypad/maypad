package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class InternalWebhook extends Webhook {

    @Column
    private String token;
    @Enumerated(EnumType.STRING)
    private WebhookType type;
    @Column
    private String baseUrl;

    /**
     * Constructor for InternalWebhook.
     *
     * @param baseUrl the domain of the webhook
     * @param url     the relative URL of the webhook.
     * @param token   the token that identifies the webhook in maypad
     * @param type    the type of the webhook
     */
    public InternalWebhook(String baseUrl, String url, String token, WebhookType type) {
        super(url);
        this.baseUrl = baseUrl;
        this.token = token;
        this.type = type;
    }

    @Override
    public String getUrl() {
        return baseUrl + super.getUrl();
    }
}
