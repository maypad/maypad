package de.fraunhofer.iosb.maypadbackend.model.webhook;

import lombok.Data;

import javax.persistence.Entity;

/**
 * A webhook provided by another system
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class ExternalWebhook extends Webhook {
}
