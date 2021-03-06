package de.fraunhofer.iosb.maypadbackend.dtos.response;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import lombok.Data;

/**
 * Data transfer object for transferring information about a Project entity.
 */
@Data
public class ProjectResponse {
    private int id;
    private String name;
    private String description;
    private Status buildStatus;
    private String refreshUrl;
    private String repositoryUrl;
    private String repoWebsiteUrl;
    private ServiceAccountResponse serviceAccount;
    private TagResponse[] tags;
    private Commit lastCommit;
}
