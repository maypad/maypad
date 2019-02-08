package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Maypad-Dependencies between project and branches.
 */
@Data
@Entity
@NoArgsConstructor
public class DependencyDescriptor {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Basic
    private int projectId;

    @Basic
    private String branchName;

    /**
     * Constructor for a DependencyDescriptor.
     *
     * @param projectId  id of the project
     * @param branchName name of the branch
     */
    public DependencyDescriptor(int projectId, String branchName) {
        this.projectId = projectId;
        this.branchName = branchName;
    }
}
