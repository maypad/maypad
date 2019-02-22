package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

/**
 * Data transfer object for transferring information about a ServiceAccount entity.
 */
@Data
public class ServiceAccountResponse {
    private String username;
    private String key;
}
