package de.fraunhofer.iosb.maypadbackend.model.deployment;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Generalized method for a deployment
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class DeploymentType {

    private int id;
    private String name;

}
