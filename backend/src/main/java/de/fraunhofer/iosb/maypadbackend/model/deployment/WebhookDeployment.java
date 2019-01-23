package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class WebhookDeployment extends DeploymentType {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ExternalWebhook deploymentWebhook;

    public WebhookDeployment(ExternalWebhook deploymentWebhook, String name) {
        super(name);
        this.deploymentWebhook = deploymentWebhook;
    }
}
