package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

import java.util.Date;

/**
 * Data transfer object for transferring information about a Commit entity.
 */
@Data
public class CommitResponse {
    private String identifier;
    private String author;
    private String message;
    private Date timestamp;
}
