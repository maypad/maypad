package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.buildsystem.BuildSystem;
import de.fraunhofer.iosb.maypadbackend.model.buildsystem.Dependency;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

/**
 * A branch within a {@link Repository}
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Branch {

    private int id;

    //repository
    private String readme;
    private Commit lastCommit;

    //buildsystem
    private BuildSystem buildSystem;

    //maypad-data
    private List<Person> members;
    private List<Mail> mails;
    private List<Dependency> dependencies;

    //build
    private BuildType buildType;
    private List<Build> builds;
    private Status buildStatus;

    //deployment
    private DeploymentType deploymentType;
    private List<Deployment> deployments;

    //webhooks
    private InternalWebhook buildSuccessWebhook;
    private InternalWebhook buildFailureWebhook;

}
