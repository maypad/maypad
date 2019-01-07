package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * A build that is triggered by calling a webhook.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class WebhookBuild extends BuildType {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ExternalWebhook buildWebhook;

}
