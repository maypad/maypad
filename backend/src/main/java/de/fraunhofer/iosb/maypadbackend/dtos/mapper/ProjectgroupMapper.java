package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectgroupResponse;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import org.mapstruct.Mapper;

import java.util.List;


/**
 * Interface for mapping a Projectgroup-Entity to a Projectgroup-Response DTO.
 */
@Mapper(componentModel = "spring")
public interface ProjectgroupMapper {
    ProjectgroupResponse toResponse(Projectgroup projectgroup);

    List<ProjectgroupResponse> toResponseList(List<Projectgroup> projectgroups);

}
