package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildTypeExec;
import de.fraunhofer.iosb.maypadbackend.services.build.WebhookBuildExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * A build that is triggered by calling a webhook.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@BuildTypeExec(executor = WebhookBuildExecutor.class)
public class WebhookBuild extends BuildType {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ExternalWebhook buildWebhook;


    public WebhookBuild(ExternalWebhook buildWebhook) {
        this.buildWebhook = buildWebhook;
    }

    @Override
    public String toString() {
        return buildWebhook.getUrl();
    }
}
