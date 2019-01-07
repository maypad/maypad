package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Webhook, which is a URL for triggering an event.
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
    @Column
    private String url;

}
