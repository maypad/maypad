package de.fraunhofer.iosb.maypadbackend.model;

import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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

    @Column
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;


    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;

    //repository
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Repository repository;
    @Column
    private String repositoryUrl;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ServiceAccount serviceAccount;
    @Enumerated(EnumType.STRING)
    private Status repositoryStatus;

    //webhooks
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
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
        this.repositoryStatus = Status.INIT;
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
        this(new Date(), Status.INIT, new Repository(RepositoryType.NONE), repoUrl, serviceAccount, null);
    }

    /**
     * Get the name of this project. If this project is in error, so return the error as name.
     *
     * @return Name of this project
     */
    public String getName() {
        if (repositoryStatus != null && repositoryStatus != Status.SUCCESS) {
            return repositoryStatus.getName();
        }
        return name;
    }

    /**
     * Updates the status of this project.
     * @return the new status
     */
    public Status updateStatus() {
        if (repository != null) {
            repository.getBranches().forEach((key, value) -> value.updateStatus());
            Optional<Map.Entry<String, Branch>> maxPrioBranchEntry =
                    repository.getBranches().entrySet().stream()
                            .max(Comparator.comparing(e -> e.getValue().getBuildStatus().getPriority()));
            buildStatus = maxPrioBranchEntry.isPresent()
                    ? maxPrioBranchEntry.get().getValue().getBuildStatus() : buildStatus;
        }
        return buildStatus;
    }
}
