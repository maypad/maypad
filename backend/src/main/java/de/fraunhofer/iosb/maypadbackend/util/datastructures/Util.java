package de.fraunhofer.iosb.maypadbackend.util.datastructures;

import lombok.NonNull;

import java.util.List;

/**
 * Add some util functions.
 *
 * @version 1.0
 */
public class Util {

    /**
     * Update a list with data from a new list.
     *
     * @param oldList Current list
     * @param newList Data in List
     * @param <T>     Type in list
     */
    public static <T> void updateList(@NonNull List<T> oldList, List<T> newList) {
        if (newList == null) {
            return;
        }
        oldList.retainAll(newList);
        for (T item : newList) {
            if (!oldList.contains(item)) {
                oldList.add(item);
            }
        }
    }


}
