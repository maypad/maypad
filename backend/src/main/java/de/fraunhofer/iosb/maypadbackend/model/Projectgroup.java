package de.fraunhofer.iosb.maypadbackend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A projectgroup in which can have {@link Project}s.
 *
 * @version 1.0
 */

@Data
@NoArgsConstructor
@Entity
public class Projectgroup {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Column(length = 1024)
    private String name;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Project> projects;

    /**
     * Constructor for Projectgorup.
     *
     * @param name        the projectgroup name
     * @param buildStatus the projectgroup BuildStatus
     */
    public Projectgroup(String name, Status buildStatus) {
        this.name = name;
        this.buildStatus = buildStatus;
        projects = new ArrayList<>();
    }

    /**
     * Constructor for Projectgorup.
     *
     * @param name the projectgroup name
     */
    public Projectgroup(String name) {
        this(name, Status.UNKNOWN);
    }

    /**
     * Updates the status of this branch.
     *
     * @return the new status
     */
    public Status updateStatus() {
        if (projects != null) {
            projects.forEach(Project::updateStatus);
            Optional<Project> maxPrioProject = projects.stream().max(Comparator.comparing(p -> {
                int currentPriority = p.getBuildStatus().getPriority();
                return (currentPriority != 4) ? currentPriority : (currentPriority - 3);
            }));
            buildStatus = maxPrioProject.isPresent()
                    ? maxPrioProject.get().getBuildStatus() : buildStatus;
        }
        return buildStatus;
    }

}
