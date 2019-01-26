package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import de.fraunhofer.iosb.maypadbackend.util.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
import java.util.Set;

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
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String readme;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Commit lastCommit;

    //maypad-data
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Person> members;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Mail> mails;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<DependencyDescriptor> dependencies;

    //build
    @OneToOne(cascade = CascadeType.ALL)
    private BuildType buildType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Build> builds;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;

    //deployment
    @OneToOne(cascade = CascadeType.ALL)
    private DeploymentType deploymentType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Deployment> deployments;

    //webhooks
    @OneToOne(cascade = CascadeType.ALL)
    private InternalWebhook buildSuccessWebhook;
    @OneToOne(cascade = CascadeType.ALL)
    private InternalWebhook buildFailureWebhook;


    /**
     * Compare this current branch with an other branch and update different data.
     *
     * @param branch Other Branch
     */
    public void compareAndUpdate(Branch branch) {
        Util.updateSet(members, branch.getMembers());
        Util.updateSet(mails, branch.getMails());
        Util.updateSet(dependencies, branch.getDependencies());
        if (!buildType.equals(branch.getBuildType())) {
            buildType = branch.getBuildType();
        }
        if (!deploymentType.equals(branch.getDeploymentType())) {
            deploymentType = branch.getDeploymentType();
        }
    }


}
