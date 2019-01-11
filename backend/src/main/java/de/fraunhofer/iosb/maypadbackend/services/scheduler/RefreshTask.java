package de.fraunhofer.iosb.maypadbackend.services.scheduler;

import de.fraunhofer.iosb.maypadbackend.model.Project;

/**
 * Runnable that refreshes the given Project.
 * TODO:
 * - Add RepoService Attribute
 * - Implement run
 */
public class RefreshTask implements Runnable {
    private Project project;


    @Override
    public void run() {
        //Implementation requires RepoService
    }
}
