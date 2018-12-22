package de.fraunhofer.iosb.maypadbackend.model;

import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 * A project in maypad which has a {@link Repository}
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class Project {

    private int id;

    private Date lastUpdate;
    private Status buildStatus;

    //repository
    private Repository repository;
    private String repoURL;
    private ServiceAccount serviceAccount;

    //webhooks
    private InternalWebhook refreshWebhook;


}
