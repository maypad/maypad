package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.Webhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Map;

@Service
public class WebhookService {
    private Map<String, WebhookHandler> mappedHooks;

    private static final String baseUrl = "https://maypad.de/hook/"; //should be read from config
    private static final String tokenChars
            = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static int tokenLength = 20;
    private static final SecureRandom rnd = new SecureRandom();
    private final char[] buf = new char[tokenLength];

    /**
     * Generates a new token used for identifying webhooks.
     * @return the generated token
     */
    private String generateToken() {
        do {
            for (int i = 0; i < tokenLength; i++) {
                buf[i] = tokenChars.charAt(rnd.nextInt(tokenLength));
            }
        } while (mappedHooks.containsKey(new String(buf)));
        return new String(buf);
    }

    /**
     * Generates a webhook, that signals that the last build on the given branch was successful.
     * @param branch the branch, that should be updated
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateSuccessWebhook(Branch branch) {
        String token = generateToken();
        mappedHooks.put(token, new BuildWebhookHandler(branch, Status.SUCCESS));
        return new InternalWebhook(baseUrl + token, token, WebhookType.UPDATEBUILD);
    }

    /**
     * Generates a webhook, that signals that the last build on the given branch failed.
     * @param branch the branch, that should be updated
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateFailWebhook(Branch branch) {
        String token = generateToken();
        mappedHooks.put(token, new BuildWebhookHandler(branch, Status.FAILED));
        return new InternalWebhook(baseUrl + token, token, WebhookType.UPDATEBUILD);
    }

    /**
     * Generates a webhook, that signals that the given project should be refreshed.
     * @param project the project that should be updated
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateRefreshWebhook(Project project) {
        String token = generateToken();
        mappedHooks.put(token, new RefreshWebhookHandler(project));
        return new InternalWebhook(baseUrl + token, token, WebhookType.REFRESH);
    }

    /**
     * Removes the mapping for the given Webhook.
     * @param webhook the webhook that should be unmapped.
     */
    public void removeWebhook(InternalWebhook webhook) {
        mappedHooks.remove(webhook.getToken());
    }

    /**
     * Calls the given webhook.
     * @param webhook the webhook that should be called
     */
    @Async
    public void call(Webhook webhook) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(webhook.getUrl(), String.class);
    }

    /**
     * Handles a call to an webhook with the given token.
     * @param token the token of the called Webhook.
     */
    public void handle(String token) {
        if (mappedHooks.containsKey(token)) {
            mappedHooks.get(token).handle();
        }
    }
}
