package de.fraunhofer.iosb.maypadbackend.model.deployment;

import lombok.Data;

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
public class ScriptDeployment {

    private File script;

}
