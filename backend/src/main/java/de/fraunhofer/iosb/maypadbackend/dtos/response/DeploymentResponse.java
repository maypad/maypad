package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;

import java.util.Date;

/**
 * Data transfer object for transferring information about a Deployment entity.
 */
@Data
public class DeploymentResponse {
    private Date timestamp;
    private String type;
    private Status status;
}
