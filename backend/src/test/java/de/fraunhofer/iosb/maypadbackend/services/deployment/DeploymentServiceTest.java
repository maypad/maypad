package de.fraunhofer.iosb.maypadbackend.services.deployment;

import de.fraunhofer.iosb.maypadbackend.dtos.request.DeploymentRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.DeploymentRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildBuilder;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.assertj.core.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentServiceTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private BuildService buildService;

    @Mock
    private SseService sseService;

    @Mock
    private WebhookService webhookService;

    @Spy
    Collection<DeploymentTypeExecutor> executors = new ArrayList<>();

    @InjectMocks
    private DeploymentService deploymentService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void deployBuildValid() throws Exception {
        Build build = BuildBuilder.create()
                .id(1)
                .status(Status.SUCCESS)
                .timestamp(new Date())
                .commit(new Commit())
                .build();
        Branch branch = BranchBuilder.create()
                .name("master")
                .deployments(new ArrayList<>())
                .builds(Stream.of(build).collect(Collectors.toList()))
                .deploymentType(new WebhookDeployment())
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][] {
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));
        Project project = ProjectBuilder.create()
                .id(1)
                .repository(repository)
                .build();

        executors.add(new WebhookDeploymentExecutor(webhookService, deploymentService));
        when(buildService.buildBranch(1, "master", true, null))
                .thenReturn(CompletableFuture.completedFuture(Status.SUCCESS));
        when(buildService.getLatestBuild(branch)).thenReturn(build);
        when(projectService.saveProject(project)).thenReturn(project);
        when(projectService.getProject(1)).thenReturn(project);
        when(webhookService.call(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(new ResponseEntity(HttpStatus.OK)));

        DeploymentRequest request = DeploymentRequestBuilder.create()
                .withDependencies(true)
                .withBuild(true)
                .build();

        deploymentService.initDeploymentTypeMappings();
        Status status = deploymentService.deployBuild(1, "master", request, "").get();

        assertThat(status).isEqualTo(Status.SUCCESS);
        verify(buildService).buildBranch(1, "master", true, null);
        verify(sseService, times(1)).push(any(EventData.class));
        assertThat(branch.getDeployments().size()).isEqualTo(1);
    }

    @Test
    public void deployBuildInvalidBranch() throws Exception {
        Project project = ProjectBuilder.create()
                .id(1)
                .repository(new Repository())
                .build();

        expectedException.expect(NotFoundException.class);

        executors.add(new WebhookDeploymentExecutor(webhookService, deploymentService));
        when(projectService.getProject(1)).thenReturn(project);

        deploymentService.initDeploymentTypeMappings();
        deploymentService.deployBuild(1, "master", true, true, "").get();
    }

    @Test
    public void initInvalid() {
        DeploymentTypeExecutor executor = mock(DeploymentTypeExecutor.class);
        executors.add(executor);

        expectedException.expect(RuntimeException.class);

        deploymentService.initDeploymentTypeMappings();
    }

    @Test
    public void initProxy() {
        Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), Arrays.array(DeploymentTypeExecutor.class),
                (o, method, objects) -> null);
        executors.add((DeploymentTypeExecutor) proxy);

        expectedException.expect(RuntimeException.class);

        deploymentService.initDeploymentTypeMappings();
    }
}
