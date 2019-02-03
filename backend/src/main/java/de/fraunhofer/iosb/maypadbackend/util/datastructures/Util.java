package de.fraunhofer.iosb.maypadbackend.util.datastructures;

import java.util.List;
import java.util.Set;

/**
 * Add some util functions.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
public class Util {

    /**
     * Update a set with data from a new set.
     *
     * @param oldList Current set
     * @param newList Data in set
     * @param <T>     Type in set
     */
    public static <T> void updateSet(Set<T> oldList, Set<T> newList) {
        oldList.retainAll(newList);
        oldList.addAll(newList);
    }

    /**
     * Update a list with data from a new list.
     *
     * @param oldList Current list
     * @param newList Data in List
     * @param <T>     Type in list
     */
    public static <T> void updateList(List<T> oldList, List<T> newList) {
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
