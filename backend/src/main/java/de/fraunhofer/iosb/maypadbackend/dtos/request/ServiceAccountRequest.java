package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

import java.util.Optional;

/**
 *  Data transfer object to store a Service-Account-request as a POJO.
 *
 * @author Max Willich
 */
@Data
public class ServiceAccountRequest {
    private Optional<String> userName;
    private Optional<String> password;
    private Optional<String> key;
}
