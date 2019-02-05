package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * Property for a deployment.
 */
@Data
public class DeploymentProperty {
    private String name;
    private String type;
    private HttpMethod method;
    private String url;
    private HeaderProperty[] headers;
    private String body;
}
