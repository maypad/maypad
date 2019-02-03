package de.fraunhofer.iosb.maypadbackend.util.datastructures;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Remove an entry of the map, if ttl expired.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@AllArgsConstructor
public class ExpiredElementRemover implements Runnable {

    private final List<ExpiringElement> elements;

    @Override
    public void run() {
        elements.removeIf(ExpiringElement::isExpired);
    }

}
