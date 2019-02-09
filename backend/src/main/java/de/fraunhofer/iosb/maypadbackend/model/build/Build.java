package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@NoArgsConstructor
@Entity
public class Build {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Commit commit;

    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Constructor for Build.
     *
     * @param timestamp the exact time of the build
     * @param commit    the last commit on the built branch
     * @param status    the status of the build
     */
    public Build(Date timestamp, Commit commit, Status status) {
        this.timestamp = timestamp;
        this.commit = commit;
        this.status = status;
    }
}
