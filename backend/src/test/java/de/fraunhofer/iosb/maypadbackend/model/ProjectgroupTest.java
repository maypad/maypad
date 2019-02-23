package de.fraunhofer.iosb.maypadbackend.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectgroupTest {

    @Test
    public void updateStatusToFailed() {
        Projectgroup projectgroup = ProjectgroupBuilder.create().projects(new ArrayList<>()).build();

        projectgroup.getProjects().add(ProjectBuilder.create().buildStatus(Status.SUCCESS).build());
        projectgroup.getProjects().add(ProjectBuilder.create().buildStatus(Status.FAILED).build());

        assertThat(projectgroup.updateStatus()).isEqualTo(Status.FAILED);
    }

    @Test
    public void updateStatusToSuccess() {
        Projectgroup projectgroup = ProjectgroupBuilder.create().projects(new ArrayList<>()).build();

        projectgroup.getProjects().add(ProjectBuilder.create().buildStatus(Status.SUCCESS).build());
        projectgroup.getProjects().add(ProjectBuilder.create().buildStatus(Status.SUCCESS).build());

        assertThat(projectgroup.updateStatus()).isEqualTo(Status.SUCCESS);
    }
}