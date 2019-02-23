package de.fraunhofer.iosb.maypadbackend.services.build;

import lombok.Getter;

import java.util.concurrent.Semaphore;

/**
 * Locks a build.
 */
@Getter
public class BuildLock extends Semaphore {
    private Integer buildId;

    /**
     * Constructor for BuildLock.
     *
     * @param buildId the id of the build that should be locked
     */
    public BuildLock(Integer buildId) {
        super(0);
        this.buildId = buildId;
    }
}
