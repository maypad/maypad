package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RefreshWebhookHandlerTest {

    @MockBean
    private RepoService repoServiceMock;

    @Test
    public void testRefreshWebhookHandler() {
        RefreshWebhookHandler handler = new RefreshWebhookHandler(1, repoServiceMock);
        handler.handle();
        verify(repoServiceMock).refreshProject(1);
        verifyNoMoreInteractions(repoServiceMock);
    }
}
