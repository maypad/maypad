package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Webhook, which is a URL for triggering an event
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class Webhook {

    private int id;
    private String url;

}
