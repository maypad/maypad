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

@Component
public class WebhookDeploymentExecutor implements DeploymentTypeExecutor {

    private WebhookService webhookService;
    private DeploymentService deploymentService;

    /**
     * Constructor for WebhookService.
     * @param webhookService the WebhookService used to call webhooks
     */
    @Autowired
    @Lazy
    public WebhookDeploymentExecutor(WebhookService webhookService, DeploymentService deploymentService) {
        this.webhookService = webhookService;
        this.deploymentService = deploymentService;
    }

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
            } catch (ExecutionException | InterruptedException ex) {
                deploymentService.signalStatus(id, ref, Status.FAILED);
            }
        }
    }
}
