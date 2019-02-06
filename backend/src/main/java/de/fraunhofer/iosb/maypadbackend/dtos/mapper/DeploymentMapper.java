package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.DeploymentResponse;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


/**
 * Interface for mapping a Deployment-Entity to a Deployment-Response DTO.
 */
@Mapper(componentModel = "spring", uses = {BuildMapper.class})
public interface DeploymentMapper {
    @Mappings({
            @Mapping(source = "type.name", target = "type")
    })
    public DeploymentResponse toResponse(Deployment deployment);

    public List<DeploymentResponse> toResponseList(List<Deployment> deployments);
}
