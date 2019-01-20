package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


/**
 *  Interface for mapping a Project-Entity to a Project-Response DTO.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mappings({
            @Mapping(source = "refreshWebhook.url", target = "refreshUrl")
    })
    public ProjectResponse toResponse(Project project);

    public List<ProjectResponse> toResponseList(List<Project> projects);
}
