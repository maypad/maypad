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
        Branch branchDependency = new Branch();
        branchDependency.setId(4);
        WebhookBuild webhookBuild = new WebhookBuild();
        webhookBuild.setName("https://buildProject.com/4372434");
        WebhookDeployment webhookDeployment = new WebhookDeployment();
        webhookDeployment.setName("https://deployProject.com/3742493");


        testBranch = new Branch();
        testBranch.setName("testBranch");
        testBranch.setReadme("Test Readme");
        testBranch.setDependencies(new ArrayList<>(Collections.singletonList(branchDependency)));
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

        int[] dependencies = {4};
        String[] members = {"Max Mustermann"};
        String[] mails = {"max.mustermann@maypad.de"};

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("testBranch");
        assertThat(response.getReadme()).isEqualTo("Test Readme");
        assertThat(response.getDependencies()).isEqualTo(dependencies);
        assertThat(response.getMembers()).isEqualTo(members);
        assertThat(response.getBuildWebhook()).isEqualTo("https://buildProject.com/4372434");
        assertThat(response.getDeploymentWebhook()).isEqualTo("https://deployProject.com/3742493");
        assertThat(response.getBuildSuccessUrl()).isEqualTo("https://maypad.de/hook/12345");
        assertThat(response.getBuildFailureUrl()).isEqualTo("https://maypad.de/hook/123456");
        assertThat(response.getMails()).isEqualTo(mails);
        assertThat(response.getBuildStatus()).isEqualTo(Status.SUCCESS);
        assertThat(response.getLastCommit()).isNotNull();
    }
}
