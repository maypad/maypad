package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


/**
 * Interface for mapping a Project-Entity to a Project-Response DTO.
 */
@Mapper(componentModel = "spring", uses = CommitMapper.class)
public interface ProjectMapper {
    @Mappings({
            @Mapping(source = "refreshWebhook.url", target = "refreshUrl"),
            @Mapping(source = "repository.tags", target = "tags")
    })
    public ProjectResponse toResponse(Project project);

    public List<ProjectResponse> toResponseList(List<Project> projects);

    default String map(Person person) {
        return person.getName();
    }
}
