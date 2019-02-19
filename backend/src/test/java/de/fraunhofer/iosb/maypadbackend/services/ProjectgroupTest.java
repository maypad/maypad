package de.fraunhofer.iosb.maypadbackend.services;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectgroupRepository;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectgroupTest {

    @InjectMocks
    private ProjectgroupService projectgroupService;

    @Mock
    private ProjectgroupRepository projectgroupRepository;

    @Mock
    private RepoService repoService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getProjectgroups() {
        projectgroupService.getProjectgroups();
        verify(projectgroupRepository).findAll();
    }

    @Test
    public void updateStatus() {
        Projectgroup projectgroup = new Projectgroup("Testgroup");
        projectgroup.setId(1);
        when(projectgroupRepository.findById(1)).thenReturn(Optional.of(projectgroup));
        projectgroupService.updateStatus(1);
        verify(projectgroupRepository).saveAndFlush(projectgroup);
    }

    @Test
    public void createProjectgroup() {
        CreateProjectgroupRequest request = new CreateProjectgroupRequest();
        request.setName("Testgroup");
        projectgroupService.create(request);
        verify(projectgroupRepository).saveAndFlush(any());
    }

    @Test
    public void changeProjectgroup() {
        Projectgroup projectgroup = new Projectgroup("Testgroup");
        projectgroup.setId(1);
        when(projectgroupRepository.findById(1)).thenReturn(Optional.of(projectgroup));

        ChangeProjectgroupRequest request = new ChangeProjectgroupRequest();
        request.setName("Testgroup");
        projectgroupService.changeProjectgroup(1, request);
        verify(projectgroupRepository).saveAndFlush(any());
    }

    @Test
    public void deleteProjectgroup() throws Exception {
        Projectgroup projectgroup = new Projectgroup("Testgroup");
        projectgroup.setId(1);
        Project project = new Project("http://maypad.de/testrepo.git");
        project.setId(1);
        projectgroup.getProjects().add(project);
        when(projectgroupRepository.findById(1)).thenReturn(Optional.of(projectgroup));
        doAnswer((Answer<Void>) invocation -> null).when(projectgroupRepository).deleteById(1);
        projectgroupService.deleteProjectgroup(1).get();
    }


    @Test
    public void getProjectgroup() {
        Projectgroup projectgroup = new Projectgroup("Testgroup");
        projectgroup.setId(1);
        when(projectgroupRepository.findById(1)).thenReturn(Optional.of(projectgroup));

        projectgroupService.getProjectgroup(1);
        verify(projectgroupRepository).findById(1);
    }

    @Test
    public void getProjectgroupWithInvalidId() {
        expectedException.expect(NotFoundException.class);
        projectgroupService.getProjectgroup(1);
    }

    @Test
    public void getProjectrgroupByProject() {
        Project project = new Project("http://maypad.de/testrepo.git");

        Projectgroup projectgroup = new Projectgroup("Testgroup");
        projectgroup.setId(1);
        when(projectgroupRepository.findProjectgroupByProjectsContaining(project)).thenReturn(Optional.of(projectgroup));

        projectgroupService.getProjectrgroupByProject(project);
        verify(projectgroupRepository).findProjectgroupByProjectsContaining(project);
    }

    @Test
    public void getProjectrgroupByInvalidProject() {
        expectedException.expect(NotFoundException.class);
        Project project = new Project("http://maypad.de/testrepo.git");
        projectgroupService.getProjectrgroupByProject(project);
    }

    @Test
    public void getProjects() {
        Project project = new Project("http://maypad.de/testrepo.git");
        Projectgroup projectgroup = new Projectgroup("Testgroup");
        projectgroup.setId(1);
        projectgroup.getProjects().add(project);
        when(projectgroupRepository.findById(1)).thenReturn(Optional.of(projectgroup));
        assertThat(projectgroupService.getProjects(1).size()).isEqualTo(1);
        assertThat(projectgroupService.getProjects(1).get(0)).isEqualTo(project);
    }


}
