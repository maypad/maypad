package de.fraunhofer.iosb.maypadbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ServiceAccountRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.repository.Tag;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.ProjectgroupService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectgroupService projectgroupService;

    @Autowired
    private ProjectService projectService;

    @SpyBean
    private SseService sseService;

    @SpyBean
    private ServerConfig serverConfig;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void createProjectgroupValid() throws Exception {
        CreateProjectgroupRequest request = CreateProjectgroupRequestBuilder.create()
                .name("Test")
                .build();

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/api/projectgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test")))
                .andExpect(jsonPath("$.buildStatus", is("UNKNOWN")));

        assertThat(projectgroupService.getProjectgroups().size()).isEqualTo(1);
        Projectgroup projectgroup = projectgroupService.getProjectgroups().get(0);
        assertThat(projectgroup.getName()).isEqualTo("Test");
        assertThat(projectgroup.getBuildStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(projectgroup.getId()).isEqualTo(1);
        assertThat(projectgroup.getProjects().size()).isEqualTo(0);
    }

    @Test
    public void createProjectValid() throws Exception {

        when(serverConfig.getRepositoryStoragePath()).thenReturn(folder.getRoot().getAbsolutePath());

        CreateProjectgroupRequest projectGroupRequest = CreateProjectgroupRequestBuilder.create()
                .name("Test")
                .build();

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/api/projectgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectGroupRequest))
        );

        File gitRepo = initLocalGitTestRepo();
        final String repoUrl = gitRepo.getAbsolutePath().replace("\\", "/");
        CreateProjectRequest projectRequest = CreateProjectRequestBuilder.create()
                .groupId(1)
                .repositoryUrl(repoUrl)
                .versionControlSystem("git")
                .build();

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectRequest))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)));

        Mockito.verify(sseService, timeout(60000)).push(any(EventData.class));

        assertThat(projectgroupService.getProjectgroup(1).getProjects().size()).isEqualTo(1);
        assertThat(projectService.getProjects().size()).isEqualTo(1);
        Project project = projectService.getProject(1);
        assertThat(project.getServiceAccount()).isNull();
        checkLocalGitTestRepo(project, repoUrl);
    }

    @Test
    public void createProjectValidWithUserServiceaccount() throws Exception {

        when(serverConfig.getRepositoryStoragePath()).thenReturn(folder.getRoot().getAbsolutePath());

        CreateProjectgroupRequest projectGroupRequest = CreateProjectgroupRequestBuilder.create()
                .name("Test")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        mockMvc.perform(post("/api/projectgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectGroupRequest))
        );

        File gitRepo = initLocalGitTestRepo();
        final String repoUrl = gitRepo.getAbsolutePath().replace("\\", "/");
        final String username = "user";
        final String password = "password";

        CreateProjectRequest projectRequest = CreateProjectRequestBuilder.create()
                .groupId(1)
                .repositoryUrl(repoUrl)
                .versionControlSystem("git")
                .serviceAccount(ServiceAccountRequestBuilder.create()
                        .username(Optional.of(username))
                        .password(Optional.of(password))
                        .build())
                .build();

        System.out.println(mapper.writeValueAsString(projectRequest));
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectRequest))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)));

        Mockito.verify(sseService, timeout(60000)).push(any(EventData.class));

        assertThat(projectgroupService.getProjectgroup(1).getProjects().size()).isEqualTo(1);
        assertThat(projectService.getProjects().size()).isEqualTo(1);
        Project project = projectService.getProject(1);
        assertThat(project.getServiceAccount()).isNotNull();
        assertThat(project.getServiceAccount()).isExactlyInstanceOf(UserServiceAccount.class);
        assertThat(((UserServiceAccount) project.getServiceAccount()).getUsername()).isEqualTo(username);
        assertThat(((UserServiceAccount) project.getServiceAccount()).getPassword()).isEqualTo(password);
        checkLocalGitTestRepo(project, repoUrl);
    }

    private File initLocalGitTestRepo() throws Exception {
        File ressourceRepo = ResourceUtils.getFile(this.getClass().getResource("/testrepo_git"));
        File testrepo = folder.newFolder();
        FileUtils.copyDirectory(ressourceRepo, testrepo);
        File gitFoler = new File(testrepo.getAbsolutePath() + File.separator + "git");
        gitFoler.renameTo(new File(testrepo.getAbsolutePath() + File.separator + ".git"));
        return testrepo;
    }


    private void checkLocalGitTestRepo(Project project, String repoUrl) {
        assertThat(project.getRepositoryStatus()).isEqualTo(Status.SUCCESS);
        assertThat(project.getName()).isEqualTo("Testrepo");
        assertThat(project.getDescription()).isEqualTo("TestDescription");
        assertThat(project.getRepositoryUrl()).isEqualTo(repoUrl);
        assertThat(project.getBuildStatus()).isEqualTo(Status.UNKNOWN);

        //Repository
        Repository repository = project.getRepository();
        assertThat(repository.getRepositoryType()).isEqualTo(RepositoryType.GIT);
        assertThat(repository.getBranches().get("master")).isNotNull();

        //RefreshWebhook
        assertThat(project.getRefreshWebhook()).isNotNull();

        //Branch
        Branch branch = project.getRepository().getBranches().get("master");
        assertThat(branch.getName()).isEqualTo("master");
        assertThat(branch.getDescription()).isEqualTo("Lorem Ipsum");
        assertThat(branch.getMembers().size()).isEqualTo(1);
        assertThat(branch.getMembers().get(0).getName()).isEqualTo("Peter");
        assertThat(branch.getMails().size()).isEqualTo(1);
        assertThat(branch.getMails().get(0).getMailAddress()).isEqualTo("example.mail@gmail.com");

        //Build
        assertThat(branch.getBuildType()).isExactlyInstanceOf(WebhookBuild.class);
        WebhookBuild build = (WebhookBuild) branch.getBuildType();
        assertThat(build.getBuildWebhook().getUrl()).isEqualTo("https://greatBuild.com/12345abc");
        assertThat(build.getBody()).isEqualTo("{}");
        assertThat(build.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(branch.getBuildSuccessWebhook()).isNotNull();
        assertThat(branch.getBuildFailureWebhook()).isNotNull();

        //Deployment
        assertThat(branch.getDeploymentType()).isExactlyInstanceOf(WebhookDeployment.class);
        WebhookDeployment deployment = (WebhookDeployment) branch.getDeploymentType();
        assertThat(deployment.getDeploymentWebhook().getUrl()).isEqualTo("https://greatDeployment.com/54321abcd");
        assertThat(deployment.getBody()).isEqualTo("{}");
        assertThat(deployment.getMethod()).isEqualTo(HttpMethod.POST);

        //Depends on
        assertThat(branch.getDependencies().size()).isEqualTo(1);
        assertThat(branch.getDependencies().get(0).getProjectId()).isEqualTo(2);
        assertThat(branch.getDependencies().get(0).getBranchName()).isEqualTo("master");

        //Last commit
        Commit commit = branch.getLastCommit();
        assertThat(commit.getAuthor()).isNotNull();
        assertThat(commit.getMessage()).isEqualTo("Create maypadfile");
        assertThat(commit.getTimestamp().getTime()).isEqualTo(1550009788000L);
        assertThat(commit.getIdentifier()).isEqualTo("ef890414b4c4da0a3d60fdda8cfae8c818837ed9");

        //Tags
        assertThat(repository.getTags().size()).isEqualTo(1);
        Tag tag = repository.getTags().get(0);
        assertThat(tag.getName()).isEqualTo("testtag");
        assertThat(tag.getCommit().getIdentifier()).isEqualTo("ef890414b4c4da0a3d60fdda8cfae8c818837ed9");
        assertThat(tag.getCommit().getMessage()).isEqualTo("Create maypadfile");
        assertThat(tag.getCommit().getTimestamp().getTime()).isEqualTo(1550009788000L);
        assertThat(tag.getCommit().getAuthor()).isNotNull();

        //Readme
        assertThat(branch.getReadme()).isEqualTo("Testreadme\n");
    }


}
