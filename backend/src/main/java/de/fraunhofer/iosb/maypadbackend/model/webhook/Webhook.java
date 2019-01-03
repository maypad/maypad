package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.*;

/**
 * Webhook, which is a URL for triggering an event
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Basic
    private String url;

}
