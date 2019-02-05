package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class BuildProperty {
    private String type;
    private HttpMethod method;
    private String url;
    private HeaderProperty[] headers;
    private String body;
}
