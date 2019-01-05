package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

/**
 *  Data transfer object for transferring information about a Tag entity.
 *
 * @author Max Willich
 */
@Data
public class TagResponse {
    private String name;
    private CommitResponse commit;
}
