package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;

/**
 *  Data transfer object for transferring information about a Branch entity.
 *
 * @author Max Willich
 */
@Data
public class BranchResponse {
    private String name;
    private String description;
    private String readme;
    private CommitResponse lastCommit;
    private String[] members;
    private String[] mails;
    private String buildWebhook;
    private String deployment;
    private String[] dependencies;
    private Status buildStatus;
    private String buildSuccessUrl;
    private String buildFailureUrl;
}
