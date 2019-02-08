package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.InvalidTokenException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.Webhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectRepository;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for generating and handling webhooks.
 */
@Service
public class WebhookService {

    private Map<String, WebhookHandler> mappedHooks;
    private ServerConfig serverConfig;
    private BuildService buildService;
    private RepoService repoService;
    private ProjectRepository projectRepository;
    private char[] buf;

    private static final String tokenChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom rnd = new SecureRandom();
    private static final String HOOKS_PATH = "/hooks/";

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);


    /**
     * Constructor for WebhookService.
     *
     * @param serverConfig      the server configuration
     * @param buildService      the BuildService used to build projects
     * @param repoService       the RepoService used to update repositories
     * @param projectRepository the ProjectRepository used to access projects
     */
    @Lazy
    @Autowired
    public WebhookService(ServerConfig serverConfig, BuildService buildService, RepoService repoService,
                          ProjectRepository projectRepository) {
        this.serverConfig = serverConfig;
        this.buildService = buildService;
        this.repoService = repoService;
        this.projectRepository = projectRepository;

        buf = new char[serverConfig.getWebhookTokenLength()];

        mappedHooks = new ConcurrentHashMap<>();
    }

    /**
     * Generates a new token used for identifying webhooks.
     *
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
     *
     * @param branch the branch that should be updated as a pair of project id and branch name
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateSuccessWebhook(Tuple<Integer, String> branch) {
        String token = generateToken();
        mappedHooks.put(token, new BuildWebhookHandler(branch, Status.SUCCESS, buildService));
        return new InternalWebhook(serverConfig.getDomain(), HOOKS_PATH + token, token, WebhookType.UPDATEBUILD);
    }

    /**
     * Generates a webhook, that signals that the last build on the given branch failed.
     *
     * @param branch the branch that should be updated as a pair of project id and branch name
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateFailWebhook(Tuple<Integer, String> branch) {
        String token = generateToken();
        mappedHooks.put(token, new BuildWebhookHandler(branch, Status.FAILED, buildService));
        return new InternalWebhook(serverConfig.getDomain(), HOOKS_PATH + token, token, WebhookType.UPDATEBUILD);
    }

    /**
     * Generates a webhook, that signals that the given project should be refreshed.
     *
     * @param projectId the id of the project that should be updated
     * @return InternalWebhook for the generated webhook.
     */
    public InternalWebhook generateRefreshWebhook(int projectId) {
        String token = generateToken();
        mappedHooks.put(token, new RefreshWebhookHandler(projectId, repoService));
        return new InternalWebhook(serverConfig.getDomain(), HOOKS_PATH + token, token, WebhookType.REFRESH);
    }

    /**
     * Removes the mapping for the given Webhook.
     *
     * @param webhook the webhook that should be unmapped.
     */
    public void removeWebhook(InternalWebhook webhook) {
        if (webhook == null) {
            return;
        }
        mappedHooks.remove(webhook.getToken());
    }

    /**
     * Calls the webhook with the given method and returns the ResponseEntity with the given type.
     *
     * @param webhook       the webhook that should be called
     * @param method        the HTTP method (GET, POST, etc)
     * @param requestEntity the entity (headers and/or body) to write to the request may be null)
     * @param responseType  the type of the ResponseEntity
     * @param uriVariables  the variables to expand the url of the given webhook
     * @return Future of ResponseEntity
     */
    @Async
    public <T> CompletableFuture<ResponseEntity<T>> call(Webhook webhook, HttpMethod method, HttpEntity<?> requestEntity,
                                                         Class<T> responseType, Object... uriVariables) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.exchange(webhook.getUrl(), method, requestEntity, responseType,
                uriVariables);
        return CompletableFuture.completedFuture(response);
    }

    /**
     * Calls the webhook with the given method and returns the ResponseEntity as String.
     *
     * @param webhook the webhook that should be called
     * @param method  the HTTP method (GET, POST, etc)
     * @return Future of ResponseEntity
     */
    @Async
    public CompletableFuture<ResponseEntity<String>> call(Webhook webhook, HttpMethod method) {
        return call(webhook, method, null, String.class);
    }

    /**
     * Handles a call to an webhook with the given token.
     *
     * @param token the token of the called Webhook.
     */
    public void handle(String token) {
        if (mappedHooks.containsKey(token)) {
            mappedHooks.get(token).handle();
        } else {
            throw new InvalidTokenException("INVALID_TOKEN", String.format("The token %s is invalid.", token));
        }
    }

    /**
     * Init (after start) the mapping for webhooks.
     */
    @PostConstruct
    private void initMapping() {
        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            InternalWebhook refreshWebhook = project.getRefreshWebhook();
            if (refreshWebhook != null) {
                mappedHooks.put(refreshWebhook.getToken(), new RefreshWebhookHandler(project.getId(), repoService));
                refreshWebhook.setBaseUrl(serverConfig.getDomain());
            }
            if (project.getRepository() == null) {
                continue;
            }
            for (Map.Entry<String, Branch> entry : project.getRepository().getBranches().entrySet()) {
                InternalWebhook buildFailureWebhook = entry.getValue().getBuildFailureWebhook();
                if (buildFailureWebhook != null) {
                    mappedHooks.put(buildFailureWebhook.getToken(), new BuildWebhookHandler(
                            new Tuple<>(project.getId(), entry.getValue().getName()), Status.FAILED, buildService));
                    buildFailureWebhook.setBaseUrl(serverConfig.getDomain());

                }
                InternalWebhook buildSuccessWebhook = entry.getValue().getBuildSuccessWebhook();
                if (buildSuccessWebhook != null) {
                    mappedHooks.put(buildSuccessWebhook.getToken(), new BuildWebhookHandler(
                            new Tuple<>(project.getId(), entry.getValue().getName()), Status.SUCCESS, buildService));
                    buildSuccessWebhook.setBaseUrl(serverConfig.getDomain());
                }
            }
        }
        projectRepository.saveAll(projects);
        projectRepository.flush();
    }
}
