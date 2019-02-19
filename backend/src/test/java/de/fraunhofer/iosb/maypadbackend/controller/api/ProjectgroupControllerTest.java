package de.fraunhofer.iosb.maypadbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.ProjectgroupBuilder;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.services.ProjectgroupService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectgroupControllerTest {

    @MockBean
    private ProjectgroupService projectgroupServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        Mockito.reset(projectgroupServiceMock);
    }

    @Test
    public void createProjectgroupValid() throws Exception {
        CreateProjectgroupRequest request = CreateProjectgroupRequestBuilder.create()
                .name("Test")
                .build();
        Projectgroup response = ProjectgroupBuilder.create()
                .id(1)
                .name("Test")
                .buildStatus(Status.UNKNOWN)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        when(projectgroupServiceMock.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/projectgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test")))
                .andExpect(jsonPath("$.buildStatus", is("UNKNOWN")));

        verify(projectgroupServiceMock, times(1)).create(request);
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

    @Test
    public void listProjectGroups() throws Exception {
        Projectgroup first = ProjectgroupBuilder.create()
                .id(1)
                .name("Testgroup")
                .buildStatus(Status.SUCCESS)
                .build();
        Projectgroup second = ProjectgroupBuilder.create()
                .id(2)
                .name("ü Gruppe !$")
                .buildStatus(Status.FAILED)
                .build();

        when(projectgroupServiceMock.getProjectgroups()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/projectgroups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Testgroup")))
                .andExpect(jsonPath("$[0].buildStatus", is("SUCCESS")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("ü Gruppe !$")))
                .andExpect(jsonPath("$[1].buildStatus", is("FAILED")));

        verify(projectgroupServiceMock, times(1)).getProjectgroups();
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

    @Test
    public void getProjectgroupValid() throws Exception {
        Projectgroup project = ProjectgroupBuilder.create()
                .id(1)
                .name("Testgroup")
                .buildStatus(Status.SUCCESS)
                .build();

        when(projectgroupServiceMock.getProjectgroup(1)).thenReturn(project);

        mockMvc.perform(get("/api/projectgroups/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Testgroup")))
                .andExpect(jsonPath("$.buildStatus", is("SUCCESS")));

        verify(projectgroupServiceMock, times(1)).getProjectgroup(1);
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

    @Test
    public void getProjectgroupInvalid() throws Exception {
        when(projectgroupServiceMock.getProjectgroup(5)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/projectgroups/{id}", 5))
                .andExpect(status().isNotFound());
        verify(projectgroupServiceMock, times(1)).getProjectgroup(5);
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

    @Test
    public void changeProjectValid() throws Exception {
        ChangeProjectgroupRequest request = ChangeProjectgroupRequestBuilder.create()
                .name("new name")
                .build();
        Projectgroup respose = ProjectgroupBuilder.create()
                .name("new name")
                .build();

        ObjectMapper mapper = new ObjectMapper();

        when(projectgroupServiceMock.changeProjectgroup(1, request)).thenReturn(respose);

        mockMvc.perform(put("/api/projectgroups/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name", is("new name")));

        verify(projectgroupServiceMock, times(1)).changeProjectgroup(1, request);
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

    @Test
    public void deleteProjectValid() throws Exception {
        mockMvc.perform(delete("/api/projectgroups/{id}", 1))
                .andExpect(status().isOk());

        verify(projectgroupServiceMock, times(1)).deleteProjectgroup(1);
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

    @Test
    public void getProjectsValid() throws Exception {
        Project first = ProjectBuilder.create().id(1).build();
        Project second = ProjectBuilder.create().id(2).build();

        when(projectgroupServiceMock.getProjects(1)).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/projectgroups/{id}/projects", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(projectgroupServiceMock, times(1)).getProjects(1);
        verifyNoMoreInteractions(projectgroupServiceMock);
    }

}
