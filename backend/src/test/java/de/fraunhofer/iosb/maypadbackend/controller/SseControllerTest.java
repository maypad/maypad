package de.fraunhofer.iosb.maypadbackend.controller;

import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SseControllerTest {
    @MockBean
    private SseService sseServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        Mockito.reset(sseServiceMock);
    }

    @Test
    public void subscribe() throws Exception {
        mockMvc.perform(get("/sse"))
                .andExpect(status().isOk());

        verify(sseServiceMock).get();
        verifyNoMoreInteractions(sseServiceMock);
    }
}
