package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import java.util.List;
import java.util.Optional;

/**
 * A branch within a {@link Repository}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@Embeddable
public class Branch {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    //repository
    @Column(length = 1024)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String readme;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Commit lastCommit;

    //maypad-data
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Person> members;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Mail> mails;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<DependencyDescriptor> dependencies;

    //build
    @OneToOne(cascade = CascadeType.ALL)
    private BuildType buildType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy("id ASC")
    private List<Build> builds;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;

    //deployment
    @OneToOne(cascade = CascadeType.ALL)
    private DeploymentType deploymentType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Deployment> deployments;

    //webhooks
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private InternalWebhook buildSuccessWebhook;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private InternalWebhook buildFailureWebhook;


    /**
     * Compare this current branch with an other branch and update different data. Important: Only Data in maypad.yaml
     * will be compared!
     *
     * @param branch Other Branch
     */
    public void compareAndUpdate(Branch branch) {
        if (branch == null) {
            return;
        }

        if (name == null || !name.equals(branch.getName())) {
            setName(branch.getName());
        }

        if (description == null || !description.equals(branch.getDescription())) {
            setDescription(branch.getDescription());
        }

        if (branch.getMembers() != null) {
            Util.updateList(members, branch.getMembers());
        }

        if (branch.getMails() != null) {
            Util.updateList(mails, branch.getMails());
        }

        if (branch.getDependencies() != null) {
            Util.updateList(dependencies, branch.getDependencies());
        }

        if (buildType == null || !buildType.equals(branch.getBuildType())) {
            setBuildType(branch.getBuildType());
        }

        if (deploymentType == null || !deploymentType.equals(branch.getDeploymentType())) {
            setDeploymentType(branch.getDeploymentType());
        }

    }

    /**
     * Updates the status of this branch.
     *
     * @return the new status
     */
    public Status updateStatus() {
        if (builds != null) {
            Optional<Build> lastBuild = builds.stream().reduce((a, b) -> b);
            buildStatus = lastBuild.isPresent() ? lastBuild.get().getStatus() : buildStatus;
        }
        return buildStatus;
    }
}
