package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 * A Build with its metadata
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Build {

    private int id;
    private Date timestamp;
    private Commit commit;
    private Status status;

}
