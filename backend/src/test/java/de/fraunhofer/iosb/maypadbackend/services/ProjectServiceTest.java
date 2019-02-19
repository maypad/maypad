package de.fraunhofer.iosb.maypadbackend.services;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ServiceAccountRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.ProjectgroupBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectRepository;
import de.fraunhofer.iosb.maypadbackend.services.scheduler.SchedulerService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    @Mock
    private WebhookService webhookService;

    @Mock
    private ProjectgroupService projectgroupService;

    @Mock
    private SseService sseService;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ServerConfig serverConfig;

    @InjectMocks
    private ProjectService projectService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createValid() {
        CreateProjectRequest request = CreateProjectRequestBuilder.create()
                .repositoryUrl("url")
                .groupId(1)
                .versionControlSystem("git")
                .serviceAccount(ServiceAccountRequestBuilder.create()
                        .password(Optional.of("password"))
                        .username(Optional.of("user"))
                        .build())
                .build();

        Projectgroup projectgroup = ProjectgroupBuilder.create()
                .id(1)
                .projects(new ArrayList<>())
                .build();

        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> {
            Project givenProject = invocation.getArgument(0);
            givenProject.setId(1);
            return givenProject;
        });
        when(projectgroupService.getProjectgroup(1)).thenReturn(projectgroup);
        when(webhookService.generateRefreshWebhook(1))
                .thenReturn(new InternalWebhook("baseurl", "url", "token", WebhookType.REFRESH));

        Project project = projectService.create(request);

        assertThat(project.getRepository().getRepositoryType()).isEqualTo(RepositoryType.GIT);
        assertThat(project.getRefreshWebhook()).isNotNull();
        assertThat(project.getId()).isNotEqualTo(0);
        verify(projectRepository, atMost(2)).saveAndFlush(any(Project.class));
        verify(webhookService).generateRefreshWebhook(1);
        verify(projectgroupService).getProjectgroup(1);
        verify(projectgroupService, times(1)).saveProjectgroup(any(Projectgroup.class));
        verify(schedulerService).scheduleRepoRefresh(1);
        verifyNoMoreInteractions(schedulerService);
        verifyNoMoreInteractions(webhookService);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    public void createValidNoRequest() {
        Projectgroup projectgroup = ProjectgroupBuilder.create()
                .id(1)
                .projects(new ArrayList<>())
                .build();

        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> {
            Project givenProject = invocation.getArgument(0);
            givenProject.setId(1);
            return givenProject;
        });
        when(projectgroupService.getProjectgroup(1)).thenReturn(projectgroup);
        when(webhookService.generateRefreshWebhook(1))
                .thenReturn(new InternalWebhook("baseurl", "url", "token", WebhookType.REFRESH));

        Project project = projectService.create(1, "url", "git");

        assertThat(project.getRepository().getRepositoryType()).isEqualTo(RepositoryType.GIT);
        assertThat(project.getRefreshWebhook()).isNotNull();
        assertThat(project.getId()).isNotEqualTo(0);
        verify(projectRepository, atMost(2)).saveAndFlush(any(Project.class));
        verify(webhookService).generateRefreshWebhook(1);
        verify(projectgroupService).getProjectgroup(1);
        verify(projectgroupService, times(1)).saveProjectgroup(any(Projectgroup.class));
        verify(schedulerService).scheduleRepoRefresh(1);
        verifyNoMoreInteractions(schedulerService);
        verifyNoMoreInteractions(webhookService);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    public void createInvalidRepositoryType() {
        Projectgroup projectgroup = ProjectgroupBuilder.create()
                .id(1)
                .projects(new ArrayList<>())
                .build();

        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> {
            Project givenProject = invocation.getArgument(0);
            givenProject.setId(1);
            return givenProject;
        });
        when(projectgroupService.getProjectgroup(1)).thenReturn(projectgroup);
        when(webhookService.generateRefreshWebhook(1))
                .thenReturn(new InternalWebhook("baseurl", "url", "token", WebhookType.REFRESH));

        Project project = projectService.create(1, "url", "invalid");
        assertThat(project.getRepository().getRepositoryType()).isEqualTo(RepositoryType.NONE);
        assertThat(project.getRefreshWebhook()).isNotNull();
        assertThat(project.getId()).isNotEqualTo(0);
    }

    @Test
    public void createInvalidRepositoryUrl() {
        Projectgroup projectgroup = ProjectgroupBuilder.create()
                .id(1)
                .projects(new ArrayList<>())
                .build();

        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> {
            Project givenProject = invocation.getArgument(0);
            givenProject.setId(1);
            return givenProject;
        });
        when(projectgroupService.getProjectgroup(1)).thenReturn(projectgroup);
        when(webhookService.generateRefreshWebhook(1))
                .thenReturn(new InternalWebhook("baseurl", "url", "token", WebhookType.REFRESH));

        Project project = projectService.create(1, null, "git");
        assertThat(project.getRepository().getRepositoryType()).isEqualTo(RepositoryType.NONE);
        assertThat(project.getRefreshWebhook()).isNotNull();
        assertThat(project.getId()).isNotEqualTo(0);
    }

    @Test
    public void getBranches() {
        Branch branch = BranchBuilder.create()
                .name("master")
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][]{
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));
        Project project = ProjectBuilder.create()
                .id(1)
                .repository(repository)
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        assertThat(projectService.getBranches(1)).containsExactly(branch);
    }

    @Test
    public void getProjects() {
        Project first = ProjectBuilder.create()
                .id(1)
                .build();
        Project second = ProjectBuilder.create()
                .id(2)
                .build();

        when(projectRepository.findAll()).thenReturn(Arrays.asList(first, second));

        assertThat(projectService.getProjects()).containsExactly(first, second);
    }

    @Test
    public void getBranche() {
        Branch branch = BranchBuilder.create()
                .name("master")
                .build();
        Repository repository = new Repository();
        repository.setBranches(Stream.of(new Object[][]{
                {"master", branch}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Branch) d[1])));
        Project project = ProjectBuilder.create()
                .id(1)
                .repository(repository)
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        assertThat(projectService.getBranch(1, "master")).isEqualTo(branch);
    }

    @Test
    public void getRepoDir() {
        Project project = ProjectBuilder.create()
                .id(1)
                .build();

        when(serverConfig.getRepositoryStoragePath()).thenReturn("path");

        assertThat(projectService.getRepoDir(project)).isEqualTo(new File("path" + File.separator + 1));
    }

    @Test
    public void getRepoDirInvalid() {
        assertThat(projectService.getRepoDir(null)).isNull();
    }

    @Test
    public void changeProject() {
        Project project = ProjectBuilder.create()
                .id(1)
                .build();
        ChangeProjectRequest request = ChangeProjectRequestBuilder.create()
                .serviceAccount(ServiceAccountRequestBuilder.create()
                        .username(Optional.of("user"))
                        .password(Optional.of("password"))
                        .build())
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.changeProject(1, request);

        verify(sseService).push(any(EventData.class));
        verify(projectRepository).saveAndFlush(any(Project.class));
    }

    @Test
    public void deleteProject() {
        Project project = ProjectBuilder.create()
                .id(1)
                .refreshWebhook(new InternalWebhook("baseurl", "url", "token", WebhookType.REFRESH))
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.deleteProject(1);

        verify(webhookService).removeWebhook(new InternalWebhook("baseurl", "url", "token", WebhookType.REFRESH));
        verify(schedulerService).unscheduleRepoRefresh(1);
        verify(projectRepository).deleteById(1);
    }

    @Test
    public void statusPropagation() {
        Project project = ProjectBuilder.create()
                .id(1)
                .build();
        Projectgroup projectgroup = ProjectgroupBuilder.create()
                .id(1)
                .build();

        when(projectgroupService.getProjectrgroupByProject(project)).thenReturn(projectgroup);
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.statusPropagation(1);

        verify(projectgroupService).updateStatus(1);
    }

    @Test
    public void getProjectInvalid() {
        expectedException.expect(NotFoundException.class);

        projectService.getProject(1);
    }
}
