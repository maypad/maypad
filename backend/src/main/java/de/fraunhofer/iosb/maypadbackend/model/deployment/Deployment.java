package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import lombok.Data;

import javax.persistence.Entity;

/**
 * A deployment with its metadata
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Deployment {

    private int id;
    private Data timestamp;
    private Build build;

}
