package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.BuildResponse;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
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
public class BuildMapperTest {

    @Autowired
    private BuildMapper buildMapper;

    private Build testBuild;

    private static final Date buildDate = new Date();
    private static final Status buildStatus = Status.SUCCESS;
    private static final String commitMessage = "Commit Message";
    private static final String commitIdentifier = "Commit Identifier";
    private static final Date commitTimestamp = new Date();
    private static final Author commitAuthor = new Author("Max Musermann",
            new Mail("musterman@maypad.de"));
    private static final Commit buildCommit = new Commit(commitMessage, commitIdentifier,
            commitTimestamp, commitAuthor);

    /**
     * Setup test resources.
     */
    @Before
    public void setup() {
        testBuild = new Build(buildDate, buildCommit, buildStatus);
    }

    @Test
    public void mapBuildToBuildResponse() {
        BuildResponse response = buildMapper.toResponse(testBuild);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(buildStatus);
        assertThat(response.getTimestamp()).isEqualTo(buildDate);
        assertThat(response.getCommit().getTimestamp()).isEqualTo(commitTimestamp);
        assertThat(response.getCommit().getCommitMessage()).isEqualTo(commitMessage);
        assertThat(response.getCommit().getCommitIdentifier()).isEqualTo(commitIdentifier);
        assertThat(response.getCommit().getAuthor()).isEqualTo(
                commitAuthor.getMail().getMailAddress());
        assertThat(response.getCommit().getTimestamp()).isEqualTo(commitTimestamp);
    }
}
