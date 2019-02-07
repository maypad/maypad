package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;

import java.util.Date;

/**
 * Data transfer object for transferring information about a Build entity.
 *
 * @author Max Willich
 */
@Data
public class BuildResponse {
    private Date timestamp;
    private Status status;
    private CommitResponse commit;
}
