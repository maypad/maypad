package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

/**
 * Depicts the used scope of a (Maven-)dependency.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public enum Scope {

    /**
     * No explicit scope has been set.
     */
    NONE,
    /**
     * The scope compile is used.
     */
    COMPILE,
    /**
     * The scope provided is used.
     */
    PRIVIDED,
    /**
     * The scope runtime is used.
     */
    RUNTIME,
    /**
     * The scope test is used.
     */
    TEST,
    /**
     * The scope system is used.
     */
    SYSTEM,
    /**
     * The scope import is used.
     */
    IMPORT;

}
