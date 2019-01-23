package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    //repository
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String readme;
    @OneToOne(cascade = CascadeType.ALL)
    private Commit lastCommit;

    //maypad-data
    @OneToMany(cascade = CascadeType.ALL)
    private List<Person> members;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Mail> mails;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<DependencyDescriptor> dependencies;

    //build
    @OneToOne(cascade = CascadeType.ALL)
    private BuildType buildType;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Build> builds;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;

    //deployment
    @OneToOne(cascade = CascadeType.ALL)
    private DeploymentType deploymentType;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Deployment> deployments;

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
        updateList(members, branch.getMembers());
        updateList(mails, branch.getMails());
        updateList(dependencies, branch.getDependencies());
        if (!buildType.equals(branch.getBuildType())) {
            buildType = branch.getBuildType();
        }
        if (!deploymentType.equals(branch.getDeploymentType())) {
            deploymentType = branch.getDeploymentType();
        }
    }

    /**
     * Update a list with data from a new list.
     *
     * @param oldList Current list
     * @param newList Data in List
     * @param <T>     Type in list
     */
    private <T> void updateList(List<T> oldList, List<T> newList) {
        oldList.retainAll(newList);
        for (T item : newList) {
            if (!oldList.contains(item)) {
                oldList.add(item);
            }
        }
    }


}
