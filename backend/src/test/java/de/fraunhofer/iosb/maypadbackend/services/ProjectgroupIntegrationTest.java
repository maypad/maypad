package de.fraunhofer.iosb.maypadbackend.services;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectgroupIntegrationTest {

    @Autowired
    private ProjectgroupService projectgroupService;

    @Test
    public void createProjectgroup() {
        CreateProjectgroupRequest request = new CreateProjectgroupRequest();
        final String groupname = "Testgroup";
        request.setName(groupname);
        Projectgroup projectgroup = projectgroupService.create(request);
        assertThat(projectgroup.getName()).isEqualTo(groupname);
    }

    @Test
    public void renameProjectgroup() {
        Projectgroup projectgroup = projectgroupService.create("Testgroup");
        ChangeProjectgroupRequest request = new ChangeProjectgroupRequest();
        final String renamedGroupName = "Renamedgroup";
        request.setName(renamedGroupName);
        Projectgroup renamedProjectgroup = projectgroupService.changeProjectgroup(projectgroup.getId(), request);
        assertThat(renamedProjectgroup.getName()).isEqualTo(renamedGroupName);
    }

    @Test(expected = NotFoundException.class)
    public void getProjectgroupWithInvalidId() {
        projectgroupService.getProjectgroup(-1);
    }

    @Test
    public void getAllProjectgroups() {
        final String group1 = "Testgroup";
        final String group2 = "One more group";
        projectgroupService.create(group1);
        projectgroupService.create(group2);
        List<Projectgroup> projectgroups = projectgroupService.getProjectgroups();
        assertThat(projectgroups.size()).isEqualTo(2);
        List<String> groupNames = new ArrayList<>();
        for (Projectgroup projectgroup : projectgroups) {
            groupNames.add(projectgroup.getName());
        }
        assertThat(groupNames).contains(group1);
        assertThat(groupNames).contains(group2);
    }

    @Test
    public void getProjects() {
        Projectgroup projectgroup = projectgroupService.create("Testgroup");
        final String repoUrl = "http://maypadrepo.de";
        projectgroup.getProjects().add(new Project(repoUrl));
        projectgroup = projectgroupService.saveProjectgroup(projectgroup);

        List<Project> projects = projectgroupService.getProjects(projectgroup.getId());
        assertThat(projects.size()).isEqualTo(1);
        assertThat(projects.get(0).getRepositoryUrl()).isEqualTo(repoUrl);
    }

    @Test
    public void getProjectgroupByProject() {
        Projectgroup projectgroup = projectgroupService.create("Testgroup");
        final String repoUrl = "http://maypadrepo.de";
        projectgroup.getProjects().add(new Project(repoUrl));
        projectgroup = projectgroupService.saveProjectgroup(projectgroup);
        Project project = projectgroup.getProjects().get(0);

        assertThat(projectgroupService.getProjectrgroupByProject(project).getId()).isEqualTo(projectgroup.getId());
    }


    @Test
    public void updateStatus() {
        Projectgroup projectgroup = projectgroupService.create("Testgroup");
        final String repoUrl = "http://maypadrepo.de";
        Project project = new Project(repoUrl);
        project.setBuildStatus(Status.SUCCESS);
        projectgroup.getProjects().add(project);
        projectgroup = projectgroupService.saveProjectgroup(projectgroup);
        assertThat(projectgroup.getBuildStatus()).isEqualTo(Status.UNKNOWN);
        projectgroupService.updateStatus(projectgroup.getId());
        assertThat(projectgroupService.getProjectgroup(projectgroup.getId()).getBuildStatus()).isEqualTo(Status.SUCCESS);
    }


}
