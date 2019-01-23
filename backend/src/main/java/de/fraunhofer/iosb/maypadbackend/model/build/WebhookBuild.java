package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import lombok.AllArgsConstructor;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildTypeExec;
import de.fraunhofer.iosb.maypadbackend.services.build.WebhookBuildExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@BuildTypeExec(executor = WebhookBuildExecutor.class)
public class WebhookBuild extends BuildType {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ExternalWebhook buildWebhook;

}
