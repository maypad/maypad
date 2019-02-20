package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepoServiceTest {

    @MockBean
    private ProjectService projectServiceMock;

    @SpyBean
    private ServerConfig serverConfig;

    @Autowired
    private RepoService repoService;

    @MockBean
    private WebhookService webhookServiceMock;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder(); //have to be public for junit

    @Before
    public void setup() {
        Mockito.reset(projectServiceMock, serverConfig, webhookServiceMock);

    }

    @Test
    public void invalidGitRepoUrl() throws Exception {
        Project project = ProjectBuilder.create().id(1).repositoryUrl("").repository(new Repository(RepositoryType.GIT)).build();
        when(projectServiceMock.getProject(1)).thenReturn(project);
        when(projectServiceMock.saveProject(any())).thenReturn(project);
        doReturn(folder.getRoot().getAbsolutePath()).when(serverConfig).getRepositoryStoragePath();
        when(projectServiceMock.getRepoDir(1)).thenReturn(new File(folder.getRoot().getAbsolutePath() + File.separator + "1"));

        repoService.initProject(1).get();
        assertThat(project.getRepositoryStatus()).isEqualTo(Status.ERROR);
    }

    @Test
    public void invalidSvnRepoUrl() throws Exception {
        Project project = ProjectBuilder.create().id(1).repositoryUrl("").repository(new Repository(RepositoryType.SVN)).build();
        when(projectServiceMock.getProject(1)).thenReturn(project);
        when(projectServiceMock.saveProject(any())).thenReturn(project);
        doReturn(folder.getRoot().getAbsolutePath()).when(serverConfig).getRepositoryStoragePath();
        when(projectServiceMock.getRepoDir(1)).thenReturn(new File(folder.getRoot().getAbsolutePath() + File.separator + "1"));

        repoService.initProject(1).get();
        assertThat(project.getRepositoryStatus()).isEqualTo(Status.ERROR);
    }

    @Test
    public void localGitRepoUrl() throws Exception {
        Project project = initLocalGitTestRepo();

        assertThat(project.getRepositoryStatus()).isEqualTo(Status.SUCCESS);
        assertThat(project.getName()).isEqualTo("Testrepo");
        assertThat(project.getDescription()).isEqualTo("TestDescription");
        assertThat(project.getRepository().getBranches().get("master")).isNotNull();

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
        assertThat(commit).isNotNull();
        assertThat(commit.getMessage()).isEqualTo("Create maypadfile");
        assertThat(commit.getTimestamp()).isEqualTo("2019-02-12T23:16:28.000");
        assertThat(commit.getIdentifier()).isEqualTo("ef890414b4c4da0a3d60fdda8cfae8c818837ed9");

        //Readme
        assertThat(branch.getReadme()).isEqualTo("Testreadme\n");

        verify(webhookServiceMock).generateSuccessWebhook(new Tuple<>(1, branch.getName()));
        verify(webhookServiceMock).generateFailWebhook(new Tuple<>(1, branch.getName()));

    }

    @Test
    public void deleteLocalGitRepo() throws Exception {
        initLocalGitTestRepo();
        repoService.deleteProject(1).get();
        assertThat(new File(folder.getRoot().getAbsolutePath() + File.separator + "1")).doesNotExist();
        verify(webhookServiceMock, atLeast(2)).removeWebhook(any());
        verify(projectServiceMock).deleteProject(1);
    }

    private Project initLocalGitTestRepo() throws Exception {
        File ressourceRepo = ResourceUtils.getFile(this.getClass().getResource("/testrepo_git"));
        File testrepo = folder.newFolder();
        FileUtils.copyDirectory(ressourceRepo, testrepo);
        File gitFoler = new File(testrepo.getAbsolutePath() + File.separator + "git");
        gitFoler.renameTo(new File(testrepo.getAbsolutePath() + File.separator + ".git"));
        Project project = ProjectBuilder.create().id(1).repositoryUrl(testrepo.getAbsolutePath()
                .replace("\\", "/")).repository(new Repository(RepositoryType.GIT)).build();
        when(projectServiceMock.getProject(1)).thenReturn(project);
        when(projectServiceMock.saveProject(any())).thenReturn(project);
        doReturn(folder.getRoot().getAbsolutePath()).when(serverConfig).getRepositoryStoragePath();
        when(projectServiceMock.getRepoDir(1)).thenReturn(new File(folder.getRoot().getAbsolutePath() + File.separator + "1"));

        repoService.initProject(1).get();
        return project;
    }


}
