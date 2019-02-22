package de.fraunhofer.iosb.maypadbackend.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A Tuple.
 *
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class Tuple<K, V> {
    private K key;
    private V value;
}


