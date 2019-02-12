package de.fraunhofer.iosb.maypadbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.maypadbackend.dtos.mapper.request.BuildRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.mapper.request.DeploymentRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.DeploymentRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildBuilder;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentBuilder;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.BranchBuilder;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.services.deployment.DeploymentService;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BranchControllerTest {

    @MockBean
    private ProjectService projectServiceMock;

    @MockBean
    private BuildService buildServiceMock;

    @MockBean
    private DeploymentService deploymentServiceMock;

    @MockBean
    private RepoService repoServiceMock;

    @Autowired
    private MockMvc mockMvc;


    @Before
    public void setup() {
        Mockito.reset(projectServiceMock, buildServiceMock, deploymentServiceMock);
    }

    @Test
    public void getBranches() throws Exception {
        Branch first = BranchBuilder.create()
                .id(1)
                .build();
        Branch second = BranchBuilder.create()
                .id(2)
                .build();

        when(projectServiceMock.getBranches(1)).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/projects/{id}/branches", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].id", is(2)));

        verify(projectServiceMock, times(1)).getBranches(1);
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void getBranchValid() throws Exception {
        Branch response = BranchBuilder.create()
                .id(3)
                .name("master")
                .build();

        when(projectServiceMock.getBranch(2, "master")).thenReturn(response);

        mockMvc.perform(get("/api/projects/{id}/branches/{ref}", 2, "master"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("master")));

        verify(projectServiceMock, times(1)).getBranch(2, "master");
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void getBranchInvalid() throws Exception {

        when(projectServiceMock.getBranch(2, "master")).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/projects/{id}/branches/{ref}", 2, "master"))
                .andExpect(status().isNotFound());

        verify(projectServiceMock, times(1)).getBranch(2, "master");
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void getBuilds() throws Exception {
        Build first = BuildBuilder.create()
                .status(Status.FAILED)
                .build();
        Build second = BuildBuilder.create()
                .status(Status.SUCCESS)
                .build();

        Branch branchMock = mock(Branch.class);

        when(projectServiceMock.getBranch(2, "master")).thenReturn(branchMock);
        when(branchMock.getBuilds()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/projects/{id}/branches/{ref}/builds", 2, "master"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("FAILED")))
                .andExpect(jsonPath("$[1].status", is("SUCCESS")));

        verify(projectServiceMock, times(1)).getBranch(2, "master");
        verify(branchMock, times(1)).getBuilds();
        verifyNoMoreInteractions(projectServiceMock);
        verifyNoMoreInteractions(branchMock);
    }

    @Test
    public void getDeployments() throws Exception {
        Deployment first = DeploymentBuilder.create()
                .status(Status.FAILED)
                .build();
        Deployment second = DeploymentBuilder.create()
                .status(Status.SUCCESS)
                .build();

        Branch branchMock = mock(Branch.class);

        when(projectServiceMock.getBranch(2, "master")).thenReturn(branchMock);
        when(branchMock.getDeployments()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/projects/{id}/branches/{ref}/deployments", 2, "master"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("FAILED")))
                .andExpect(jsonPath("$[1].status", is("SUCCESS")));

        verify(projectServiceMock, times(1)).getBranch(2, "master");
        verify(branchMock, times(1)).getDeployments();
        verifyNoMoreInteractions(projectServiceMock);
        verifyNoMoreInteractions(branchMock);
    }

    @Test
    public void triggerBuild() throws Exception {
        BuildRequest request = BuildRequestBuilder.create()
                .withDependencies(true)
                .build();

        when(projectServiceMock.getBranch(2, "master")).thenReturn(BranchBuilder.create().build());

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/api/projects/{id}/branches/{ref}/builds", 2, "master")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk());

        verify(buildServiceMock, times(1)).buildBranch(2, "master", request, null);
        verifyNoMoreInteractions(buildServiceMock);
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void triggerDeployment() throws Exception {
        DeploymentRequest request = DeploymentRequestBuilder.create()
                .withDependencies(true)
                .build();

        when(projectServiceMock.getBranch(2, "master")).thenReturn(BranchBuilder.create().build());

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/api/projects/{id}/branches/{ref}/deployments", 2, "master")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk());

        verify(deploymentServiceMock, times(1)).deployBuild(2, "master", request, null);
        verifyNoMoreInteractions(deploymentServiceMock);
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void notifyRepoUpdate() throws Exception {
        when(projectServiceMock.getBranch(2, "master")).thenReturn(BranchBuilder.create().build());

        mockMvc.perform(post("/api/projects/{id}/branches/{ref}/push?token=123", 2, "master"))
                .andExpect(status().isOk());

        verify(repoServiceMock, times(1)).refreshProject(2);
        verifyNoMoreInteractions(projectServiceMock);
        verifyNoMoreInteractions(repoServiceMock);
    }

    @Test
    public void notifyBuildSuccess() throws Exception {
        when(projectServiceMock.getBranch(2, "master")).thenReturn(BranchBuilder.create().build());

        mockMvc.perform(post("/api/projects/{id}/branches/{ref}/builds/success?token=123", 2, "master"))
                .andExpect(status().isOk());

        verify(buildServiceMock, times(1)).signalStatus(2, "master", Status.SUCCESS);
        verifyNoMoreInteractions(projectServiceMock);
        verifyNoMoreInteractions(repoServiceMock);
    }

    @Test
    public void notifyBuildFailure() throws Exception {
        when(projectServiceMock.getBranch(2, "master")).thenReturn(BranchBuilder.create().build());

        mockMvc.perform(post("/api/projects/{id}/branches/{ref}/builds/fail?token=123", 2, "master"))
                .andExpect(status().isOk());

        verify(buildServiceMock, times(1)).signalStatus(2, "master", Status.FAILED);
        verifyNoMoreInteractions(projectServiceMock);
        verifyNoMoreInteractions(repoServiceMock);
    }

}
