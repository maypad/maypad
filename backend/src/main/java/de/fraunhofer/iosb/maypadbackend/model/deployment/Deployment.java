package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * A deployment with its metadata
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Build build;
    @ManyToOne
    private Branch branch;

}
