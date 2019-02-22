package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.services.deployment.DeploymentTypeExec;
import de.fraunhofer.iosb.maypadbackend.services.deployment.WebhookDeploymentExecutor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

/**
 * Deployment where a webhook should be called.
 *
 * @version 1.0
 */
@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DeploymentTypeExec(executor = WebhookDeploymentExecutor.class)
public class WebhookDeployment extends DeploymentType {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ExternalWebhook deploymentWebhook;
    @Enumerated(EnumType.STRING)
    private HttpMethod method;
    @Column(columnDefinition = "longblob")
    private HttpHeaders headers;
    @Column(columnDefinition = "TEXT")
    private String body;


    /**
     * Constructor for WebhookDeployment.
     *
     * @param name              Name of the deployment
     * @param deploymentWebhook the webhook used for deploying
     * @param method            the HTTP method (POST, GET, etc)
     * @param headers           the HttpHeaders
     * @param body              the RequestBody
     */
    public WebhookDeployment(String name, ExternalWebhook deploymentWebhook, HttpMethod method,
                             HttpHeaders headers, String body) {
        super(name);
        this.deploymentWebhook = deploymentWebhook;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        return deploymentWebhook.getUrl();
    }
}
