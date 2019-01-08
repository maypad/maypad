package de.fraunhofer.iosb.maypadbackend.model;

import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
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
 * A project in maypad which has a {@link Repository}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;

    //repository
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Repository repository;
    @Column
    private String repoUrl;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ServiceAccount serviceAccount;

    //webhooks
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private InternalWebhook refreshWebhook;


}
