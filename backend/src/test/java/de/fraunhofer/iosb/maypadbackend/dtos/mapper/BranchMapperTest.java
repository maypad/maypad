package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.BranchResponse;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BranchMapperTest {
    @Autowired
    private BranchMapper branchMapper;

    private Branch testBranch;


    /**
     * Setup test resources.
     */
    @Before
    public void setup() {
        DependencyDescriptor branchDependency = new DependencyDescriptor();
        branchDependency.setBranchName("master");
        branchDependency.setProjectId(12);
        List<DependencyDescriptor> dependencies = new ArrayList<>();
        dependencies.add(branchDependency);
        WebhookBuild webhookBuild = new WebhookBuild(new ExternalWebhook("https://buildProject.com/1234"));
        webhookBuild.setName("Webhook Build #1");
        WebhookDeployment webhookDeployment = new WebhookDeployment(new ExternalWebhook("https://deployProject.com/123"));
        webhookDeployment.setName("Webhook Deployment #1");

        testBranch = new Branch();
        testBranch.setName("testBranch");
        testBranch.setReadme("Test Readme");
        testBranch.setDependencies(dependencies);
        testBranch.setMembers(new ArrayList<>(Collections.singletonList(
                new Person("Max Mustermann"))));
        testBranch.setBuildType(webhookBuild);
        testBranch.setDeploymentType(webhookDeployment);
        testBranch.setBuildSuccessWebhook(new InternalWebhook("https://maypad.de/hook/12345",
                "12345", WebhookType.UPDATEBUILD));
        testBranch.setBuildFailureWebhook(new InternalWebhook("https://maypad.de/hook/123456",
                "123456", WebhookType.UPDATEBUILD));
        testBranch.setMails(new ArrayList<>(Collections.singletonList(
                new Mail("max.mustermann@maypad.de"))));
        testBranch.setBuildStatus(Status.SUCCESS);
        testBranch.setLastCommit(new Commit());
        testBranch.setBuilds(new ArrayList<>(Collections.singletonList(new Build())));
        testBranch.setDeployments(new ArrayList<>(Collections.singletonList(new Deployment())));
    }

    @Test
    public void mapBuildToBuildResponse() {
        BranchResponse response = branchMapper.toResponse(testBranch);

        String[] dependencies = {"12:master"};
        String[] members = {"Max Mustermann"};
        String[] mails = {"max.mustermann@maypad.de"};

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("testBranch");
        assertThat(response.getReadme()).isEqualTo("Test Readme");
        assertThat(response.getDependencies()).isEqualTo(dependencies);
        assertThat(response.getMembers()).isEqualTo(members);
        assertThat(response.getBuildWebhook()).isEqualTo("https://buildProject.com/1234");
        assertThat(response.getDeployment()).isEqualTo("https://deployProject.com/123");
        assertThat(response.getBuildSuccessUrl()).isEqualTo("https://maypad.de/hook/12345");
        assertThat(response.getBuildFailureUrl()).isEqualTo("https://maypad.de/hook/123456");
        assertThat(response.getMails()).isEqualTo(mails);
        assertThat(response.getBuildStatus()).isEqualTo(Status.SUCCESS);
        assertThat(response.getLastCommit()).isNotNull();
    }
}
