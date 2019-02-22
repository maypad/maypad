package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

import java.util.Optional;

/**
 * Data transfer object to store a Service-Account-request as a POJO.
 */
@Data
public class ServiceAccountRequest {
    private Optional<String> username;
    private Optional<String> password;
    private Optional<String> sshKey;
}
