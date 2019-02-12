package de.fraunhofer.iosb.maypadbackend.controller;

import de.fraunhofer.iosb.maypadbackend.controller.api.RestExceptionHandler;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.InvalidTokenException;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebhookControllerTest {

    @MockBean
    private WebhookService webhookServiceMock;

    @SpyBean
    private RestExceptionHandler restExceptionHandlerSpy;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        Mockito.reset(webhookServiceMock);
    }

    @Test
    public void handleWebhookValid() throws Exception {
        mockMvc.perform(get("/hooks/{token}", "validToken"))
                .andExpect(status().isOk());

        verify(webhookServiceMock).handle("validToken");
        verifyNoMoreInteractions(webhookServiceMock);
    }

    @Test
    public void handleWebhookInvalid() throws Exception {
        doThrow(InvalidTokenException.class).when(webhookServiceMock).handle(anyString());

        mockMvc.perform(get("/hooks/{token}", "invalidToken"))
                .andExpect(status().isForbidden());

        verify(webhookServiceMock).handle("invalidToken");
        verify(restExceptionHandlerSpy).handleInvalidTokenException(any());
        verifyNoMoreInteractions(webhookServiceMock);
        verifyNoMoreInteractions(webhookServiceMock);
    }
}
