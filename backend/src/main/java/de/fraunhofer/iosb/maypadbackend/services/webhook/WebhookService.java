package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.webhook.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service managing webhooks.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Service
public class WebhookService {

    //TODO

    @Autowired
    public WebhookService() {
    }

    public void call(Webhook webhook) {}
}
