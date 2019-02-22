package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;

/**
 * Data transfer object for transferring information about a Projectgroup entity.
 */
@Data
public class ProjectgroupResponse {
    private int id;
    private String name;
    private Status buildStatus;
}
