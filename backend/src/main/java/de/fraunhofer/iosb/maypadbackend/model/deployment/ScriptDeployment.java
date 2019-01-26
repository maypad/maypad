package de.fraunhofer.iosb.maypadbackend.model.deployment;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.File;

/**
 * Deployment where a script should be executed.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ScriptDeployment extends DeploymentType {

    @Column
    private File script;

}
