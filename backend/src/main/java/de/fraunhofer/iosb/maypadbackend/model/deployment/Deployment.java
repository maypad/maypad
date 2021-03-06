package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.Status;
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
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class Deployment {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Build build;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DeploymentType type;
    @Column
    private Status status;


    /**
     * Constructor for Deployment.
     *
     * @param timestamp the exact time of the deployment start
     * @param build     the build that is deployed
     * @param status    the status of the deployment
     */
    public Deployment(Date timestamp, Build build, Status status) {
        this.timestamp = timestamp;
        this.build = build;
        this.status = status;
    }
}
