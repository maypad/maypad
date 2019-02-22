package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildReason;
import lombok.Data;

import java.util.Date;

/**
 * Data transfer object for transferring information about a Build entity.
 */
@Data
public class BuildResponse {
    private Date timestamp;
    private Status status;
    private CommitResponse commit;
    private BuildReason reason;
    private String reasonDependency;

}
