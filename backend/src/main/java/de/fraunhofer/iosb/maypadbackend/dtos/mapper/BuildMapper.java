package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.BuildResponse;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import org.mapstruct.Mapper;

import java.util.Set;


/**
 *  Interface for mapping a Build-Entity to a Build-Response DTO.
 */
@Mapper(componentModel = "spring", uses = {CommitMapper.class})
public interface BuildMapper {

    public BuildResponse toResponse(Build build);

    public Set<BuildResponse> toResponseList(Set<Build> builds);
}
