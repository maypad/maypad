package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

/**
 * Data transfer object to store a Build-trigger-request as a POJO.
 */
@Data
public class BuildRequest {
    private boolean withDependencies;
}
