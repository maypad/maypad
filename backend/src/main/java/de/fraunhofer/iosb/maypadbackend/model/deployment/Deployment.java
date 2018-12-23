package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

}
