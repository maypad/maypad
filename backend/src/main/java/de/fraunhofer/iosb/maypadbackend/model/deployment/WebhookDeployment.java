package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Deployment where a webhook should be called.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class WebhookDeployment extends DeploymentType {

    @OneToOne(cascade = CascadeType.ALL)
    private ExternalWebhook deploymentWebhook;

}
