package de.fraunhofer.iosb.maypadbackend.services.scheduler;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.ProjectBuilder;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {

    private static ServerConfig serverConfig;

    @Mock
    private ThreadPoolTaskScheduler scheduler;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private SchedulerService schedulerService;


    /**
     * Setup server config.
     */
    @BeforeClass
    public static void setupConfig() {
        serverConfig = mock(ServerConfig.class);
        when(serverConfig.getReloadRepositoriesSeconds()).thenReturn(1);
    }

    @Test
    public void scheduleRepoRefresh() {
        when(scheduler.scheduleWithFixedDelay(any(RefreshTask.class), any(Instant.class), any(Duration.class)))
                .thenReturn(mock(ScheduledFuture.class));

        schedulerService.scheduleRepoRefresh(5);

        verify(scheduler)
                .scheduleWithFixedDelay(any(RefreshTask.class), any(Instant.class),
                        eq(Duration.ofSeconds(1)));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void unscheduleRepoRefresh() {
        ScheduledFuture sf = mock(ScheduledFuture.class);

        when(scheduler.scheduleWithFixedDelay(any(RefreshTask.class), any(Instant.class), any(Duration.class)))
                .thenReturn(sf);

        schedulerService.scheduleRepoRefresh(5);
        verify(scheduler)
                .scheduleWithFixedDelay(any(RefreshTask.class), any(Instant.class),
                        eq(Duration.ofSeconds(1)));
        schedulerService.unscheduleRepoRefresh(5);
        verify(sf).cancel(false);
        verifyNoMoreInteractions(scheduler);
        verifyNoMoreInteractions(sf);
    }

    @Test
    public void initTest() {
        Project project = ProjectBuilder.create()
                .id(5)
                .build();

        when(projectRepository.findAll()).thenReturn(Arrays.asList(project));
        when(scheduler.scheduleWithFixedDelay(any(RefreshTask.class), anyLong()))
                .thenReturn(mock(ScheduledFuture.class));

        schedulerService.init();

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(scheduler).scheduleWithFixedDelay(any(RefreshTask.class), captor.capture());
        assertThat(captor.getValue()).isEqualTo(serverConfig.getReloadRepositoriesSeconds() * 1000);
        verifyNoMoreInteractions(scheduler);
    }
}
