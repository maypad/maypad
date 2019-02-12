package de.fraunhofer.iosb.maypadbackend.services.build;

import lombok.Getter;

import java.util.concurrent.Semaphore;

@Getter
public class BuildLock extends Semaphore {
    private Integer buildId;

    public BuildLock(Integer buildId) {
        super(1);
        this.buildId = buildId;
    }
}
