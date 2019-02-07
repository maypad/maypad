package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * Executor for a build with a webhook.
 */
@Component
public class WebhookBuildExecutor implements BuildTypeExecutor {

    private WebhookService webhookService;
    private BuildService buildService;

    /**
     * Constructor for the WebhookBuildExecutor.
     *
     * @param webhookService Service for webhooks
     * @param buildService   Service for builds
     */
    @Autowired
    @Lazy
    public WebhookBuildExecutor(WebhookService webhookService, BuildService buildService) {
        this.webhookService = webhookService;
        this.buildService = buildService;
    }

    /**
     * Start a build.
     *
     * @param buildType the type that specifies how the branch should be build
     * @param id        the id of the project
     * @param ref       the name of the Branch
     */
    @Override
    @Async
    public void build(BuildType buildType, int id, String ref) {
        if (buildType instanceof WebhookBuild) {
            WebhookBuild webhookBuild = (WebhookBuild) buildType;
            CompletableFuture<ResponseEntity<String>> response = webhookService.call(webhookBuild.getBuildWebhook(),
                    webhookBuild.getMethod(), new HttpEntity<>(webhookBuild.getBody(), webhookBuild.getHeaders()), String.class);
            try {
                if (response.get().getStatusCode().is2xxSuccessful()) {
                    buildService.signalStatus(id, ref, Status.RUNNING);
                } else {
                    buildService.signalStatus(id, ref, Status.FAILED);
                }
            } catch (ExecutionException | InterruptedException ex) {
                buildService.signalStatus(id, ref, Status.FAILED);
            }
        }
    }
}
