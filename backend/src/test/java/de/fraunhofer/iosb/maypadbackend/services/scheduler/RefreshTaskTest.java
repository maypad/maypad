package de.fraunhofer.iosb.maypadbackend.services.scheduler;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class RefreshTaskTest {

    @Test
    public void runTest() {
        RepoService repoService = mock(RepoService.class);
        new RefreshTask(2, repoService).run();
        verify(repoService).refreshProject(2);
        verifyNoMoreInteractions(repoService);

    }
}
