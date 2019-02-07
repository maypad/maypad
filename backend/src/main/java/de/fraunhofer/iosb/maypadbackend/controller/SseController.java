package de.fraunhofer.iosb.maypadbackend.controller;

import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

/**
 * Controller for SSE Events.
 */
@Controller
public class SseController {
    private SseService sseService;

    /**
     * Constructor for server sent event.
     *
     * @param sseService the SseService
     */
    @Autowired
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    /**
     * Start the notify for new events.
     *
     * @return Event-Data as Flux
     */
    @GetMapping("/sse")
    public Flux<ServerSentEvent<EventData>> sse() {
        return sseService.get();
    }

}
