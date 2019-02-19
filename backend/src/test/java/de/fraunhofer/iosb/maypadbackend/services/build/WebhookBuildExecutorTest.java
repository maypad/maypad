package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildReason;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebhookBuildExecutorTest {

    @Mock
    WebhookService webhookService;

    @Mock
    BuildService buildService;

    @InjectMocks
    WebhookBuildExecutor webhookBuildExecutor;

    @Test
    public void buildSuccessful() {
        ExternalWebhook webhook = new ExternalWebhook("url");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity("", headers);

        BuildType buildType = new WebhookBuild(webhook, HttpMethod.POST, headers, "");

        when(webhookService.call(webhook, HttpMethod.POST, entity, String.class))
                .thenReturn(CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.OK)));

        webhookBuildExecutor.build(buildType, 1, "master");

        verify(buildService).signalStatus(1, "master", Status.RUNNING);
        verifyNoMoreInteractions(buildService);
    }

    @Test
    public void buildFailure() {
        ExternalWebhook webhook = new ExternalWebhook("url");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity("", headers);

        BuildType buildType = new WebhookBuild(webhook, HttpMethod.POST, headers, "");

        when(webhookService.call(webhook, HttpMethod.POST, entity, String.class))
                .thenReturn(CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND)));

        webhookBuildExecutor.build(buildType, 1, "master");

        verify(buildService).signalStatus(1, "master", Status.FAILED, BuildReason.BUILD_NOT_STARTED, null);
        verifyNoMoreInteractions(buildService);
    }
}
