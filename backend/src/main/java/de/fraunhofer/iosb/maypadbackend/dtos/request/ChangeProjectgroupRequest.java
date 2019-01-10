package de.fraunhofer.iosb.maypadbackend.dtos.request;

import lombok.Data;

/**
 *  Data transfer object to store a Projectgroup-Change-Request as a POJO.
 *
 * @author Max Willich
 */
@Data
public class ChangeProjectgroupRequest {
    private String name;
}
