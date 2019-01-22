package de.fraunhofer.iosb.maypadbackend.services.deployment;

import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;

public interface DeploymentTypeExecutor {

    /**
     * Deploys a build with the given deploymentType.
     * @param deploymentType the type that specifies how the build should be deployed
     */
    public void deploy(DeploymentType deploymentType);
}
