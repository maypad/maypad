package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Component
public class WebhookBuildExecutor implements BuildTypeExecutor {

    private WebhookService webhookService;
    private BuildService buildService;

    @Autowired
    @Lazy
    public WebhookBuildExecutor(WebhookService webhookService, BuildService buildService) {
        this.webhookService = webhookService;
        this.buildService = buildService;
    }

    @Override
    @Async
    public void build(BuildType buildType, Branch branch) {
        if (buildType instanceof WebhookBuild) {
            WebhookBuild webhookBuild = (WebhookBuild) buildType;
            CompletableFuture<ResponseEntity<String>> response = webhookService.call(webhookBuild.getBuildWebhook());
            try {
                if (response.get().getStatusCode() == HttpStatus.OK) {
                    buildService.signalStatus(branch, Status.RUNNING);
                } else {
                    buildService.signalStatus(branch, Status.FAILED);
                }
            } catch (ExecutionException | InterruptedException ex) {
                buildService.signalStatus(branch, Status.FAILED);
            }
        }
    }
}
