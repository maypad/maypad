package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * A Build with its metadata
 *
 * @author Lukas Brosch, Max Willich
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

    @ManyToOne
    private Branch branch;

}
