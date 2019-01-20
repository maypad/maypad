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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    //repository
    @Column
    private String name;
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
    private List<Branch> dependencies;

    //build
    @OneToOne(cascade = CascadeType.ALL)
    private BuildType buildType;
    @OneToMany(cascade = CascadeType.ALL)
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

}
