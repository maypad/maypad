package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

/**
 *  Data transfer object for transferring information about a Deployment entity.
 *
 * @author Max Willich
 */
@Data
public class DeploymentResponse {
    private String name;
    private String url;
}
