package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.BuildResponse;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import org.mapstruct.Mapper;

import java.util.List;


/**
 * Interface for mapping a Build-Entity to a Build-Response DTO.
 */
@Mapper(componentModel = "spring", uses = {CommitMapper.class})
public interface BuildMapper {

    BuildResponse toResponse(Build build);

    List<BuildResponse> toResponseList(List<Build> builds);
}
