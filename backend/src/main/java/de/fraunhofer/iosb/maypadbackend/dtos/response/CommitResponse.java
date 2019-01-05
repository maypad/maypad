package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

import java.util.Date;

/**
 *  Data transfer object for transferring information about a Commit entity.
 *
 * @author Max Willich
 */
@Data
public class CommitResponse {
    private String commitIdentifier;
    private String author;
    private String commitMessage;
    private Date timestamp;
}
