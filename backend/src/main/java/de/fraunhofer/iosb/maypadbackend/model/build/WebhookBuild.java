package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildTypeExec;
import de.fraunhofer.iosb.maypadbackend.services.build.WebhookBuildExecutor;
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
 * A build that is triggered by calling a webhook.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@BuildTypeExec(executor = WebhookBuildExecutor.class)
public class WebhookBuild extends BuildType {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ExternalWebhook buildWebhook;
    @Enumerated(EnumType.STRING)
    private HttpMethod method;
    @Column(columnDefinition = "longblob")
    private HttpHeaders headers;
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * Constructor for WebhookBuild.
     * @param buildWebhook the url that should be called
     * @param method the HTTP method (POST, GET, etc)
     * @param headers the HttpHeaders
     * @param body the RequestBody
     */
    public WebhookBuild(ExternalWebhook buildWebhook, HttpMethod method, HttpHeaders headers, String body) {
        this.buildWebhook = buildWebhook;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        return buildWebhook.getUrl();
    }
}
