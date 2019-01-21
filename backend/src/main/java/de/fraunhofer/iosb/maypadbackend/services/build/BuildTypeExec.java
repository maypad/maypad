package de.fraunhofer.iosb.maypadbackend.services.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation used for mapping a BuildTypeExecutor to a BuildType.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BuildTypeExec {
    /**
     * Returns the executor associated with annotated class.
     * @return the executor
     */
    Class<? extends BuildTypeExecutor> executor();
}
