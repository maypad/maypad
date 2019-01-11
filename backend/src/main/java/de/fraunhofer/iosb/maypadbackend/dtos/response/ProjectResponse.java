package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;

import java.util.Date;

/**
 *  Data transfer object for transferring information about a Project entity.
 *
 * @author Max Willich
 */
@Data
public class ProjectResponse {
    private int id;
    private String repoUrl;
    private Date lastUpdate;
    private Status buildStatus;
    private String projectRefreshUrl;
}
