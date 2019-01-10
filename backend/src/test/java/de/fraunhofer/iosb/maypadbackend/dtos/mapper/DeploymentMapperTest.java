package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.DeploymentResponse;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeploymentMapperTest {
    @Autowired
    private DeploymentMapper deploymentMapper;

    private Deployment testDeployment;

    private static final Date deployDate = new Date();
    private static final Date buildDate = new Date();
    private static final Status buildStatus = Status.SUCCESS;
    private static final String commitMessage = "Commit Message";
    private static final String commitIdentifier = "Commit Identifier";
    private static final Date commitTimestamp = new Date();
    private static final Author commitAuthor = new Author("Max Mustermann",
            new Mail("mustermann@maypad.de"));
    private static final Commit buildCommit = new Commit(commitMessage, commitIdentifier,
            commitTimestamp, commitAuthor);

    /**
     * Setup test resources.
     */
    @Before
    public void setup() {
        testDeployment = new Deployment(deployDate, new Build(buildDate, buildCommit, buildStatus));
    }

    @Test
    public void mapBuildToBuildResponse() {
        DeploymentResponse response = deploymentMapper.toResponse(testDeployment);

        assertThat(response).isNotNull();
        assertThat(response.getTimestamp()).isEqualTo(deployDate);
        assertThat(response.getBuild().getStatus()).isEqualTo(buildStatus);
        assertThat(response.getBuild().getTimestamp()).isEqualTo(buildDate);
        assertThat(response.getBuild().getTimestamp()).isEqualTo(commitTimestamp);
        assertThat(response.getBuild().getCommit().getCommitMessage()).isEqualTo(commitMessage);
        assertThat(response.getBuild().getCommit().getCommitIdentifier())
                .isEqualTo(commitIdentifier);
        assertThat(response.getBuild().getCommit().getAuthor()).isEqualTo(
                commitAuthor.getMail().getMailAddress());
        assertThat(response.getBuild().getCommit().getTimestamp()).isEqualTo(commitTimestamp);
    }
}
