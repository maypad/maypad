package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

import java.util.Date;

/**
 *  Data transfer object for transferring information about a Deployment entity.
 *
 * @author Max Willich
 */
@Data
public class DeploymentResponse {
    private Date timestamp;
    private BuildResponse build;
}
