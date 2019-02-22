package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

/**
 * Data transfer object to store a Deployment-trigger-request as a POJO.
 */
@Data
public class DeploymentRequest {
    private boolean withDependencies;
    private boolean withBuild;
}
