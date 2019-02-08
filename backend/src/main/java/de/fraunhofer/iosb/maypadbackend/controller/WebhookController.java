package de.fraunhofer.iosb.maypadbackend.controller;

import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for webhooks.
 */
@Controller
@RequestMapping("/hooks")
public class WebhookController {

    private WebhookService webhookService;

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    /**
     * Constructor for WebhookController.
     *
     * @param webhookService the WebhookService used to handle webhooks
     */
    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Handle a called webhook.
     *
     * @param token Webhook token
     */
    @GetMapping("/{token}")
    public void handleWebhook(@PathVariable String token) {
        webhookService.handle(token);
    }


}
