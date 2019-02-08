package de.fraunhofer.iosb.maypadbackend.util.datastructures;

import java.util.concurrent.TimeUnit;

/**
 * A general element with TTL.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class ExpiringElement {

    private final long expirationTimestamp;

    /**
     * Constructor for ExpiringElement.
     *
     * @param ttl      TTL
     * @param timeUnit unit for ttl
     */
    public ExpiringElement(long ttl, TimeUnit timeUnit) {
        expirationTimestamp = System.currentTimeMillis() + timeUnit.toMillis(ttl);
    }

    /**
     * Check, if this element expired.
     *
     * @return true, if expired, else false
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTimestamp;
    }

}
