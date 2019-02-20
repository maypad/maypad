package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private SseService sseService;

    @Mock
    private WebhookService webhookService;

    @Mock
    private DependencyBuildHelper dependencyBuildHelper;

    @Spy
    Collection<BuildTypeExecutor> executors = new ArrayList<>();

    @InjectMocks
    private BuildService buildService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void buildBranchValid() throws Exception {
        Branch branch = BranchBuilder.create()
                .name("master")
                .builds(new ArrayList<>())
                .dependencies(new ArrayList<>())
                .buildType(new WebhookBuild())
                .lastCommit(new Commit())
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][] {
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));
        Project project = ProjectBuilder.create()
                .id(1)
                .repository(repository)
                .build();

        executors.add(new WebhookBuildExecutor(webhookService, buildService));
        when(projectService.getBranch(1, "master")).thenReturn(branch);
        when(projectService.getProject(1)).thenReturn(project);
        when(projectService.saveProject(project)).thenReturn(project);
        when(dependencyBuildHelper.runBuildWithDependencies(1, "master")).thenReturn(new Tuple<>(true, null));
        when(webhookService.call(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(new ResponseEntity(HttpStatus.OK)));

        BuildRequest request = BuildRequestBuilder.create().withDependencies(true).build();

        buildService.initBuildTypeMappings();

        Thread startBuild = new Thread(
                () -> {
                    try {
                        buildService.buildBranch(1, "master", request, null).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        fail();
                    }
                }
        );
        startBuild.start();


        verify(sseService, after(2000)).push(any(EventData.class)); //wait for build to be running
        buildService.signalStatus(1, "master", Status.SUCCESS); //report build as successful
        startBuild.join();

        verify(sseService, times(2)).push(any(EventData.class));
        assertThat(branch.getBuilds().size()).isEqualTo(1);
        assertThat(branch.getBuilds().get(0).getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    public void getLatestBuildInvalid() {
        Branch branch = BranchBuilder.create()
                .builds(new ArrayList<>())
                .build();

        expectedException.expect(NotFoundException.class);
        buildService.getLatestBuild(branch);
    }

    @Test
    public void initInvalid() {
        BuildTypeExecutor executor = mock(BuildTypeExecutor.class);
        executors.add(executor);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(startsWith("No BuildTypeExecutor found for "));

        buildService.initBuildTypeMappings();
    }

    @Test
    public void initProxy() {
        Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), Arrays.array(BuildTypeExecutor.class),
                (o, method, objects) -> null);
        executors.add((BuildTypeExecutor) proxy);

        expectedException.expect(RuntimeException.class);

        buildService.initBuildTypeMappings();
    }
}
