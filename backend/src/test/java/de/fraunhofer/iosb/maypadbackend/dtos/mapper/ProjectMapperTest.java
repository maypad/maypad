package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.model.webhook.WebhookType;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectMapperTest {
    private ProjectMapper projectMapper;
    private Project testProject;

    private static final String projectRefreshUrl = "https://maypad.de/hook/437284683242498";
    private static final String projectRepoUrl = "https://github.com/juliantodt/maypad.git";
    private static final Date lastUpdate = new Date();
    private static final int projectId = 8;
    private static final Status buildStatus = Status.SUCCESS;

    /**
     * Setup test resources.
     */
    @Before
    public void setup() {
        projectMapper = Mappers.getMapper(ProjectMapper.class);
        testProject = new Project(lastUpdate, buildStatus, new Repository(), projectRepoUrl,
                new KeyServiceAccount(), new InternalWebhook(projectRefreshUrl,
                "437284683242498", WebhookType.REFRESH));
        testProject.setId(projectId);
    }

    @Test
    public void mapCommitToCommitResponse() {
        ProjectResponse response = projectMapper.toResponse(testProject);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(projectId);
        assertThat(response.getRepoUrl()).isEqualTo(projectRepoUrl);
        assertThat(response.getLastUpdate()).isEqualTo(lastUpdate);
        assertThat(response.getBuildStatus()).isEqualTo(buildStatus);
        assertThat(response.getProjectRefreshUrl()).isEqualTo(projectRefreshUrl);
    }
}