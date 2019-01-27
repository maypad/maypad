package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.CommitResponse;
import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitMapperTest {
    private CommitMapper commitMapper;
    private Commit testCommit;

    private static final String commitMessage = "Commit Message";
    private static final String commitIdentifier = "Commit Identifier";
    private static final Date commitTimestamp = new Date();
    private static final Author commitAuthor = new Author("Max Mustermann",
            new Mail("musterman@maypad.de"));

    /**
     * Setup test resources.
     */
    @Before
    public void setup() {
        commitMapper = Mappers.getMapper(CommitMapper.class);
        testCommit = new Commit(commitMessage, commitIdentifier, commitTimestamp, commitAuthor);
    }

    @Test
    public void mapCommitToCommitResponse() {
        CommitResponse response = commitMapper.toResponse(testCommit);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(commitMessage);
        assertThat(response.getIdentifier()).isEqualTo(commitIdentifier);
        assertThat(response.getAuthor()).isEqualTo(commitAuthor.getMail().getMailAddress());
        assertThat(response.getTimestamp()).isEqualTo(commitTimestamp);
    }
}
