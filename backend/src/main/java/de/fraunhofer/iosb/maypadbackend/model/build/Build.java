package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * A Build with its metadata.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Build {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Commit commit;

    @Enumerated(EnumType.STRING)
    private Status status;

}
