package de.fraunhofer.iosb.maypadbackend.model;

import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectTest {

    @Test
    public void updateStatusToFailed() {
        Project project = ProjectBuilder.create().repository(new Repository(RepositoryType.GIT)).build();

        project.getRepository().getBranches().put("master", BranchBuilder.create().buildStatus(Status.SUCCESS).build());
        project.getRepository().getBranches().put("testbranch", BranchBuilder.create().buildStatus(Status.FAILED).build());

        assertThat(project.updateStatus()).isEqualTo(Status.FAILED);
    }

    @Test
    public void updateStatusToSuccess() {
        Project project = ProjectBuilder.create().repository(new Repository(RepositoryType.GIT)).build();

        project.getRepository().getBranches().put("master", BranchBuilder.create().buildStatus(Status.SUCCESS).build());
        project.getRepository().getBranches().put("testbranch", BranchBuilder.create().buildStatus(Status.SUCCESS).build());

        assertThat(project.updateStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    public void getNameNormal() {
        final String projectname = "testrepo";
        Project project = ProjectBuilder.create().name(projectname).repositoryStatus(Status.SUCCESS).build();
        assertThat(project.getName()).isEqualTo(projectname);
    }

    @Test
    public void getNameWhileInit() {
        final String projectname = "testrepo";
        Project project = ProjectBuilder.create().name(projectname).repositoryStatus(Status.INIT).build();
        assertThat(project.getName()).isEqualTo(Status.INIT.getName());
    }
}