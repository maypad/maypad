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
    private String readme;
    private int[] maypadDependencyIds;
    private String[] members;
    private String buildWebhook;
    private String deploymentWebhook;
    private String buildSuccessUrl;
    private String buildFailureUrl;
    private String[] mails;
    private Status buildStatus;
    private ServiceAccountResponse serviceAccount;
    private CommitResponse lastCommit;
    private BuildResponse[] builds;
    private TagResponse[] tags;
}
