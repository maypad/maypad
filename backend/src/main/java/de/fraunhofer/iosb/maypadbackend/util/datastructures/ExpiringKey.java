package de.fraunhofer.iosb.maypadbackend.util.datastructures;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * A key to expire.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class ExpiringKey<E> extends ExpiringElement {

    @Getter
    private final E element;

    /**
     * Constructor for ExpiringKey.
     *
     * @param element  key to expire
     * @param ttl      TTL
     * @param timeUnit timeunit for ttl
     */
    public ExpiringKey(E element, long ttl, TimeUnit timeUnit) {
        super(ttl, timeUnit);
        this.element = element;
    }
}
