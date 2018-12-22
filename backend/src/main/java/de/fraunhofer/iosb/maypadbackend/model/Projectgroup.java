package de.fraunhofer.iosb.maypadbackend.model;

import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

/**
 * A projectgroup in which can have {@link Project}s
 *
 * @author Lukas Brosch
 * @version 1.0
 */

@Data
@Entity
public class Projectgroup {

    private int id;
    private String name;
    private Status buildStatus;
    private List<Project> projects;

}
