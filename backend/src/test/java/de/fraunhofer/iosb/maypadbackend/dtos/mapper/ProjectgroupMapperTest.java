package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectgroupResponse;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectgroupMapperTest {
    private ProjectgroupMapper projectgroupMapper;
    private Projectgroup testProjectgroup;

    private static final String groupName = "Test Projectgroup";
    private static final int groupId = 4;
    private static final Status buildStatus = Status.SUCCESS;

    /**
     * Setup test resources.
     */
    @Before
    public void setup() {
        projectgroupMapper = Mappers.getMapper(ProjectgroupMapper.class);
        testProjectgroup = new Projectgroup(groupName, buildStatus);
        testProjectgroup.setId(groupId);
    }

    @Test
    public void mapCommitToCommitResponse() {
        ProjectgroupResponse response = projectgroupMapper.toResponse(testProjectgroup);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(groupId);
        assertThat(response.getName()).isEqualTo(groupName);
        assertThat(response.getBuildStatus()).isEqualTo(buildStatus);
    }
}
