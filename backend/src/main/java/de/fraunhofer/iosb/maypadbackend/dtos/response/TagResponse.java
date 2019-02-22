package de.fraunhofer.iosb.maypadbackend.dtos.response;

import lombok.Data;

/**
 * Data transfer object for transferring information about a Tag entity.
 */
@Data
public class TagResponse {
    private String name;
    private CommitResponse commit;
}
