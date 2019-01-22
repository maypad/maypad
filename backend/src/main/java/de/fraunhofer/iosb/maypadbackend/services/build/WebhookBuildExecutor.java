package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class WebhookBuildExecutor implements BuildTypeExecutor {

    private WebhookService webhookService;

    @Autowired
    public WebhookBuildExecutor(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void build(BuildType buildType) {
        if (buildType instanceof WebhookBuild) {
            WebhookBuild webhookBuild = (WebhookBuild) buildType;
            webhookService.call(webhookBuild.getBuildWebhook());
        }
    }
}
