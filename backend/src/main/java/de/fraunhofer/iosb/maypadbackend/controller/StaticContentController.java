package de.fraunhofer.iosb.maypadbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class StaticContentController {

    /**
     * Serves the index page.
     * @return index page
     */
    @RequestMapping("/")
    public String serveIndex() {
        return "index.html";
    }
}
