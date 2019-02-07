package de.fraunhofer.iosb.maypadbackend.services.deployment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for mapping a DeploymentTypeExecutor to a DeploymentType.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeploymentTypeExec {
    /**
     * Returns the executor associated with the annotated class.
     *
     * @return the executor
     */
    Class<? extends DeploymentTypeExecutor> executor();
}
