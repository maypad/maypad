package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

/**
 * Data transfer object to store a Create-Project-request as a POJO.
 */
@Data
public class CreateProjectRequest {
    private int groupId;
    private String repositoryUrl;
    private ServiceAccountRequest serviceAccount;
    private String versionControlSystem;
}
