package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.BuildSystemManager;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

/**
 * Buildsystem, which is managed by a {@link BuildSystemManager}
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class BuildSystem {

    private int id;
    private String name;
    private BuildSystemManager buildSystemManager;
    private List<Dependency> dependencies;

    /**
     * Get an instance of a {@link BuildSystemManager} which manage the respective build system
     *
     * @return An instance of a BuildSystemManager
     */
    public abstract BuildSystemManager getManager();


}
