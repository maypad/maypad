package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.InvalidTokenException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.Webhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebhookService {

    private Map<String, WebhookHandler> mappedHooks;
    private ServerConfig serverConfig;
    private BuildService buildService;
    private RepoService repoService;
    private char[] buf;

    private static final String tokenChars
            = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom rnd = new SecureRandom();
    private static final String hookPath = "/hooks/";

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);



    /**
     * Constructor for WebhookService.
     * @param serverConfig the server configuration
     * @param buildService the BuildService used to build projects
     * @param repoService the RepoService used to update repositories
     */
    @Lazy
    @Autowired
    public WebhookService(ServerConfig serverConfig, BuildService buildService, RepoService repoService) {
        this.serverConfig = serverConfig;
        this.buildService = buildService;
        this.repoService = repoService;

        buf = new char[serverConfig.getWebhookTokenLength()];

        mappedHooks = new ConcurrentHashMap<>();
    }

    /**
     * Generates a new token used for identifying webhooks.
     * @return the generated token
     */
    private String generateToken() {
        int tokenLength = serverConfig.getWebhookTokenLength();
        do {
            for (int i = 0; i < tokenLength; i++) {
                buf[i] = tokenChars.charAt(rnd.nextInt(tokenChars.length()));
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
        mappedHooks.put(token, new BuildWebhookHandler(branch, Status.SUCCESS, buildService));
        return new InternalWebhook(serverConfig.getDomain() + hookPath + token, token, WebhookType.UPDATEBUILD);
    }

    /**
     * Generates a webhook, that signals that the last build on the given branch failed.
     * @param branch the branch, that should be updated
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateFailWebhook(Branch branch) {
        String token = generateToken();
        mappedHooks.put(token, new BuildWebhookHandler(branch, Status.FAILED, buildService));
        return new InternalWebhook(serverConfig.getDomain() + hookPath + token, token, WebhookType.UPDATEBUILD);
    }

    /**
     * Generates a webhook, that signals that the given project should be refreshed.
     * @param project the project that should be updated
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateRefreshWebhook(Project project) {
        String token = generateToken();
        mappedHooks.put(token, new RefreshWebhookHandler(project, repoService));
        return new InternalWebhook(serverConfig.getDomain() + hookPath + token, token, WebhookType.REFRESH);
    }

    /**
     * Removes the mapping for the given Webhook.
     * @param webhook the webhook that should be unmapped.
     */
    public void removeWebhook(InternalWebhook webhook) {
        mappedHooks.remove(webhook.getToken());
    }

    /**
     * Calls the given webhook and returns the ResponseEntity with the given type.
     * @param webhook the webhook that should be called
     * @param responseType the type of the ResponseEntity
     * @param uriVariables the variables to expand the url of the given webhook
     * @return Future of ResponseEntity
     */
    @Async
    public <T> CompletableFuture<ResponseEntity<T>> call(Webhook webhook, Class<T> responseType, Object... uriVariables) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.getForEntity(webhook.getUrl(), responseType, uriVariables);
        return CompletableFuture.completedFuture(response);
    }

    /**
     * Calls the given webhook and returns the ReponseEntity as String.
     * @param webhook the webhook that should be called
     * @return Future of ResponseEntity
     */
    @Async
    public CompletableFuture<ResponseEntity<String>> call(Webhook webhook) {
        return call(webhook, String.class);
    }

    /**
     * Handles a call to an webhook with the given token.
     * @param token the token of the called Webhook.
     */
    public void handle(String token) {
        if (mappedHooks.containsKey(token)) {
            mappedHooks.get(token).handle();
        } else {
            throw new InvalidTokenException("INVALID_TOKEN", String.format("The token %s is invalid.", token));
        }
    }
}
