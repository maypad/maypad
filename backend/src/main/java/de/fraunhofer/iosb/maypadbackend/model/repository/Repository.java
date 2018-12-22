package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;

import javax.persistence.Entity;
import java.io.File;
import java.util.List;

/**
 * Repository of a version management system
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Repository {

    private int id;
    private File rootFolder;
    private RepositoryType repositoryType;
    private List<Tag> tags;
    private List<Branch> branches;

}
