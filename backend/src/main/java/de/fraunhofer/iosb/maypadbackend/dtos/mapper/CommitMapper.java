package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.CommitResponse;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


/**
 * Interface for mapping a Commit-Entity to a Commit-Response DTO.
 */
@Mapper(componentModel = "spring")
public interface CommitMapper {
    @Mappings({
            @Mapping(source = "author.name", target = "author")
    })
    CommitResponse toResponse(Commit commit);

    List<CommitResponse> toResponseList(List<Commit> commits);
}
