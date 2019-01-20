package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;

import javax.xml.ws.Service;
import java.util.Date;

/**
 *  Data transfer object for transferring information about a Project entity.
 *
 * @author Max Willich
 */
@Data
public class ProjectResponse {
    private int id;
    private String name;
    private Status buildStatus;
    private String refreshUrl;
    private String repositoryUrl;
    private ServiceAccountResponse serviceAccount;
}
