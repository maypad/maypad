package de.fraunhofer.iosb.maypadbackend.controller;

import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hooks")
public class WebhookController {

    private WebhookService webhookService;

    /**
     * Constructor for WebhookController.
     * @param webhookService the WebhookService used to handle webhooks
     */
    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @GetMapping("/{token}")
    public void handleWebhook(@PathVariable String token) {
        webhookService.handle(token);
    }


}
