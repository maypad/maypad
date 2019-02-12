package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.InvalidTokenException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectRepository;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebhookServiceTest {

    @MockBean
    private SseService sseServiceMock;

    @MockBean
    private ProjectRepository projectRepositoryMock;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        Mockito.reset(sseServiceMock, projectRepositoryMock);
    }

    @Test
    public void generateSuccessWebhook() {
        InternalWebhook generatedWebhook = webhookService.generateSuccessWebhook(new Tuple<>(1, "master"));

        assertThat(generatedWebhook).isNotNull();
        assertThat(generatedWebhook.getToken()).isNotNull().isNotEqualTo("");
        assertThat(webhookService.isMapped(generatedWebhook.getToken())).isTrue();
        assertThat(generatedWebhook.getType()).isEqualTo(WebhookType.UPDATEBUILD);
        assertThat(generatedWebhook.getBaseUrl()).isEqualTo(serverConfig.getDomain());
        assertThat(generatedWebhook.getUrl()).startsWith(serverConfig.getDomain() + "/hooks/");
    }

    @Test
    public void generateFailureWebhook() {
        InternalWebhook generatedWebhook = webhookService.generateFailWebhook(new Tuple<>(1, "master"));

        assertThat(generatedWebhook).isNotNull();
        assertThat(generatedWebhook.getToken()).isNotNull().isNotEqualTo("");
        assertThat(webhookService.isMapped(generatedWebhook.getToken())).isTrue();
        assertThat(generatedWebhook.getType()).isEqualTo(WebhookType.UPDATEBUILD);
        assertThat(generatedWebhook.getBaseUrl()).isEqualTo(serverConfig.getDomain());
        assertThat(generatedWebhook.getUrl()).startsWith(serverConfig.getDomain() + "/hooks/");
    }

    @Test
    public void generateRefreshWebhook() {
        InternalWebhook generatedWebhook = webhookService.generateRefreshWebhook(1);

        assertThat(generatedWebhook).isNotNull();
        assertThat(generatedWebhook.getToken()).isNotNull().isNotEqualTo("");
        assertThat(webhookService.isMapped(generatedWebhook.getToken())).isTrue();
        assertThat(generatedWebhook.getType()).isEqualTo(WebhookType.REFRESH);
        assertThat(generatedWebhook.getBaseUrl()).isEqualTo(serverConfig.getDomain());
        assertThat(generatedWebhook.getUrl()).startsWith(serverConfig.getDomain() + "/hooks/");
    }

    @Test
    public void removeWebhook() {
        InternalWebhook generatedWebhook = webhookService.generateRefreshWebhook(1);
        assertThat(webhookService.isMapped(generatedWebhook.getToken())).isTrue();
        webhookService.removeWebhook(generatedWebhook);
        assertThat(webhookService.isMapped(generatedWebhook.getToken())).isFalse();
    }

    @Test
    public void handleValid() {
        RefreshWebhookHandler handlerMock = mock(RefreshWebhookHandler.class);
        webhookService.setMappedHooks(Stream.of(new Object[][] {
                {"token", handlerMock}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (WebhookHandler) d[1])));
        webhookService.handle("token");
        verify(handlerMock).handle();
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    public void handleInvalidToken() {
        expectedException.expect(InvalidTokenException.class);
        webhookService.handle("invalid token");
    }

    @Test
    public void callValid() throws Exception {
        mockServer.expect(requestTo("http://localhost:8080"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));

        webhookService.call(new ExternalWebhook("http://localhost:8080"),
                HttpMethod.POST, new HttpEntity<String>(""), String.class).get();

        mockServer.verify();
    }

    @Test
    public void testInit() {
        Branch branch = BranchBuilder.create()
                .buildFailureWebhook(
                        new InternalWebhook("baseurl", "url", "token1", WebhookType.UPDATEBUILD))
                .buildSuccessWebhook(
                        new InternalWebhook("baseurl", "url", "token2", WebhookType.UPDATEBUILD))
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][] {
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));
        Project project = ProjectBuilder.create()
                .refreshWebhook(new InternalWebhook("baseurl", "url", "token3", WebhookType.UPDATEBUILD))
                .repository(repository)
                .build();
        when(projectRepositoryMock.findAll()).thenReturn(Arrays.asList(project));
        webhookService.initMapping();

        assertThat(webhookService.isMapped("token1")).isTrue();
        assertThat(webhookService.isMapped("token2")).isTrue();
        assertThat(webhookService.isMapped("token3")).isTrue();
    }

}
