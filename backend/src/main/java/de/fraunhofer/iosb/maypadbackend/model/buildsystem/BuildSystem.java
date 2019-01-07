package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.BuildSystemManager;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Buildsystem, which is managed by a {@link BuildSystemManager}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BuildSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Dependency> dependencies;


    /**
     * Get an instance of a {@link BuildSystemManager} which manage the respective build system.
     *
     * @return An instance of a BuildSystemManager
     */
    public abstract BuildSystemManager getManager();


}
