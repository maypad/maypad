package de.fraunhofer.iosb.maypadbackend.model;

import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
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
 * A project in maypad which has a {@link Repository}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class Project {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Basic
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;

    //repository
    @OneToOne(cascade = CascadeType.ALL)
    private Repository repository;
    @Column
    private String repositoryUrl;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ServiceAccount serviceAccount;

    //webhooks
    @OneToOne(cascade = CascadeType.ALL)
    private InternalWebhook refreshWebhook;

    /**
     * Constructor for Project.
     *
     * @param lastUpdate     the exact time of the last repository update
     * @param buildStatus    the build status of the project
     * @param repository     the repository
     * @param repoUrl        the repository URL
     * @param serviceAccount the serviceaccount
     * @param refreshWebhook the webhook for refreshing the project
     */
    public Project(Date lastUpdate, Status buildStatus, Repository repository, String repoUrl,
                   ServiceAccount serviceAccount, InternalWebhook refreshWebhook) {
        this.lastUpdate = lastUpdate;
        this.buildStatus = buildStatus;
        this.repository = repository;
        this.repositoryUrl = repoUrl;
        this.serviceAccount = serviceAccount;
        this.refreshWebhook = refreshWebhook;
    }

    /**
     * Constructor for Project.
     *
     * @param repoUrl the repository URL
     */
    public Project(String repoUrl) {
        this(repoUrl, null);
    }

    /**
     * Constructor for Project.
     *
     * @param repoUrl        the repository URL
     * @param serviceAccount the serviceaccount
     */
    public Project(String repoUrl, ServiceAccount serviceAccount) {
        this(new Date(), Status.UNKNOWN, null, repoUrl, serviceAccount, null);
    }
}
