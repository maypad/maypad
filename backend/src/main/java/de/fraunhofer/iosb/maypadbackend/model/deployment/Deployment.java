package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * A deployment with its metadata.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class Deployment {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Build build;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DeploymentType type;


    /**
     * Constructor for Deployment.
     * @param timestamp the exact time of the deployment start
     * @param build the build that is deployed
     */
    public Deployment(Date timestamp, Build build) {
        this.timestamp = timestamp;
        this.build = build;
    }
}
