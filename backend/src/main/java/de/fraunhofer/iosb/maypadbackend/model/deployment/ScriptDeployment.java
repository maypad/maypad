package de.fraunhofer.iosb.maypadbackend.model.deployment;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.File;

/**
 * Deployment where a script should be executed.
 *
 * @version 1.0
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ScriptDeployment extends DeploymentType {

    @Column
    private File script;

    /**
     * Constructor for Script-Deployment.
     *
     * @param script File to the script
     */
    public ScriptDeployment(File script) {
        this.script = script;
    }

    /**
     * Constructor for Script-Deployment.
     *
     * @param script File to the script
     * @param name   Name of the deployment
     */
    public ScriptDeployment(File script, String name) {
        super(name);
        this.script = script;
    }
}
