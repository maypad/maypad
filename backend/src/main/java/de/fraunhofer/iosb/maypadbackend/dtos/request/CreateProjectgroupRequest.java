package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

/**
 * Data transfer object to store a Create-Projectgroup-request as a POJO.
 */
@Data
public class CreateProjectgroupRequest {
    private String name;
}
