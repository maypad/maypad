package de.fraunhofer.iosb.maypadbackend.dtos.mapper;

import de.fraunhofer.iosb.maypadbackend.dtos.response.DeploymentResponse;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;

import org.mapstruct.Mapper;

import java.util.List;



/**
 *  Interface for mapping a Deployment-Entity to a Deployment-Response DTO.
 */
@Mapper(componentModel = "spring", uses = {BuildMapper.class})
public interface DeploymentMapper {
    public DeploymentResponse toResponse(Deployment deployment);

    public List<DeploymentResponse> toResponseList(List<Deployment> deployments);
}
