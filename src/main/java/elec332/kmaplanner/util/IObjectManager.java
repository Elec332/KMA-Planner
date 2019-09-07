package elec332.kmaplanner.util;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 4-9-2019
 */
public interface IObjectManager<O, R> extends Iterable<O> {

    void addCallback(Object weakKey, Runnable runnable);

    void load(R reader);

    default void addObject(O object) {
        if (!addObjectNice(object)) {
            throw new IllegalArgumentException(object.toString());
        }
    }

    boolean addObjectNice(O object);

    void removeObject(O object);

    void updateObject(O object, Consumer<O> consumer);

    @Nonnull
    Set<O> getObjects();

    default Stream<O> stream() {
        return getObjects().stream();
    }

    @Nonnull
    @Override
    default Iterator<O> iterator() {
        return getObjects().iterator();
    }

}
