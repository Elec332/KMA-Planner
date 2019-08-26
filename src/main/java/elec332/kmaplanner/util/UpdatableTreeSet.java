package elec332.kmaplanner.util;

import java.util.TreeSet;

/**
 * Created by Elec332 on 14-8-2019
 */
public class UpdatableTreeSet<T> extends TreeSet<T> {

    public void markObjectUpdated(T obj) {
        if (remove(obj)) {
            add(obj);
        }
    }

}
