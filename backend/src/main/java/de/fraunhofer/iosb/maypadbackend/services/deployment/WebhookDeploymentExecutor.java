package de.fraunhofer.iosb.maypadbackend.services.deployment;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Executor for a deployment with a webhook.
 */
@Component
public class WebhookDeploymentExecutor implements DeploymentTypeExecutor {

    private WebhookService webhookService;
    private DeploymentService deploymentService;

    /**
     * Constructor for WebhookService.
     *
     * @param webhookService    the WebhookService used to call webhooks
     * @param deploymentService the deploymentservice
     */
    @Autowired
    @Lazy
    public WebhookDeploymentExecutor(WebhookService webhookService, DeploymentService deploymentService) {
        this.webhookService = webhookService;
        this.deploymentService = deploymentService;
    }

    /**
     * Deploy a branch.
     *
     * @param deploymentType the type that specifies how the build should be deployed
     * @param id             the id of the project
     * @param ref            the name of the branch
     */
    @Override
    public void deploy(DeploymentType deploymentType, int id, String ref) {
        if (deploymentType instanceof WebhookDeployment) {
            WebhookDeployment webhookDeployment = (WebhookDeployment) deploymentType;
            CompletableFuture<ResponseEntity<String>> response = webhookService.call(webhookDeployment.getDeploymentWebhook(),
                    webhookDeployment.getMethod(),
                    new HttpEntity<>(webhookDeployment.getBody(), webhookDeployment.getHeaders()), String.class);
            try {
                if (response.get().getStatusCode().is2xxSuccessful()) {
                    deploymentService.signalStatus(id, ref, Status.SUCCESS);
                } else {
                    deploymentService.signalStatus(id, ref, Status.FAILED);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                deploymentService.signalStatus(id, ref, Status.FAILED);
            } catch (ExecutionException ex) {
                deploymentService.signalStatus(id, ref, Status.FAILED);
            }
        }
    }
}
