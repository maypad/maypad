package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * Class to store deployment properties as listed in Maypad YAML-File.
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
