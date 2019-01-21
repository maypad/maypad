package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

/**
 *  Data transfer object to store a Project-Change-request as a POJO.
 *
 * @author Max Willich
 */
@Data
public class ChangeProjectRequest {
    private ServiceAccountRequest serviceAccountRequest;
}
