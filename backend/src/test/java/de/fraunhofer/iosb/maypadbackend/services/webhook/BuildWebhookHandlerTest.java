package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildWebhookHandlerTest {
    @MockBean
    private BuildService buildServiceMock;

    @Test
    public void testBuildWebhookHandler() {
        BuildWebhookHandler handler = new BuildWebhookHandler(new Tuple<>(1, "master"),
                Status.SUCCESS, buildServiceMock);
        handler.handle();
        verify(buildServiceMock).signalStatus(1, "master", Status.SUCCESS);
        verifyNoMoreInteractions(buildServiceMock);
    }
}
