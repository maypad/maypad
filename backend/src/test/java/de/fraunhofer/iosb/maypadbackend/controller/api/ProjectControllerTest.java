package de.fraunhofer.iosb.maypadbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import de.fraunhofer.iosb.maypadbackend.dtos.mapper.request.ChangeProjectRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.mapper.request.CreateProjectRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.mapper.request.ServiceAccountRequestBuilder;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import de.fraunhofer.iosb.maypadbackend.services.security.EncryptedText;
import de.fraunhofer.iosb.maypadbackend.services.security.EncryptionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(EncryptionService.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectServiceMock;

    @MockBean
    private RepoService repoServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        Mockito.reset(projectServiceMock, repoServiceMock);
    }

    @Test
    public void createProjectValid() throws Exception {
        CreateProjectRequest request = CreateProjectRequestBuilder.create()
                .groupId(1)
                .repositoryUrl("https://github.com/juliantodt/maypad.git")
                .build();
        Project response = ProjectBuilder.create()
                .id(1)
                .repositoryUrl("https://github.com/juliantodt/maypad.git")
                .build();

        when(projectServiceMock.create(request)).thenReturn(response);

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.repositoryUrl", is("https://github.com/juliantodt/maypad.git")));

        verify(projectServiceMock, times(1)).create(request);
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void getProjects() throws Exception {
        Project first = ProjectBuilder.create()
                .id(4)
                .build();
        Project second = ProjectBuilder.create()
                .id(5)
                .build();

        when(projectServiceMock.getProjects()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(4)))
                .andExpect(jsonPath("$[1].id", is(5)));

        verify(projectServiceMock, times(1)).getProjects();
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void getProjectValid() throws Exception {
        Project response = ProjectBuilder.create()
                .id(3)
                .build();

        when(projectServiceMock.getProject(1)).thenReturn(response);

        mockMvc.perform(get("/api/projects/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(3)));

        verify(projectServiceMock, times(1)).getProject(1);
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void getProjectInvalid() throws Exception {
        when(projectServiceMock.getProject(1)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/projects/{id}", 1))
                .andExpect(status().isNotFound());

        verify(projectServiceMock, times(1)).getProject(1);
        verifyNoMoreInteractions(projectServiceMock);
    }

    @Test
    public void changeProjectValid() throws Exception {
        mockStatic(EncryptionService.class);
        PowerMockito.when(EncryptionService.encryptText(anyString())).thenReturn(new EncryptedText("12345", ""));
        PowerMockito.when(EncryptionService.decryptText(anyString(), anyString())).thenReturn("12345");

        ChangeProjectRequest request = ChangeProjectRequestBuilder.create()
                .serviceAccount(ServiceAccountRequestBuilder.create()
                    .username(Optional.of("user"))
                    .password(Optional.of("12345"))
                    .sshKey(Optional.of(""))
                    .build())
                .build();

        Project response = ProjectBuilder.create()
                .id(1)
                .serviceAccount(new UserServiceAccount("user", "12345"))
                .build();

        when(projectServiceMock.changeProject(1, request)).thenReturn(response);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        mockMvc.perform(put("/api/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.serviceAccount.username").doesNotExist())
                .andExpect(jsonPath("$.serviceAccount.password").doesNotExist());

        verify(projectServiceMock, times(1)).changeProject(1, request);
        verifyNoMoreInteractions(projectServiceMock);
    }
}
