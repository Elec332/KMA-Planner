package elec332.kmaplanner.util;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 4-9-2019
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractObjectManager<O, R> implements IObjectManager<O, R> {

    public AbstractObjectManager() {
        this.objects = Sets.newTreeSet(getComparator());
        this.objects_ = Collections.unmodifiableSet(objects);
        this.callbacks = new WeakCallbackHandler();
    }

    protected final SortedSet<O> objects;
    protected final Set<O> objects_;
    protected final WeakCallbackHandler callbacks;

    @Override
    public void addCallback(Object weakKey, Runnable runnable) {
        callbacks.addCallback(weakKey, runnable);
    }

    @Override
    public abstract void load(R reader);

    @Override
    public abstract boolean addObjectNice(O object);

    @Override
    public abstract void removeObject(O object);

    @Override
    public abstract void updateObject(O object, Consumer<O> consumer);

    @Nonnull
    @Override
    public Set<O> getObjects() {
        return objects_;
    }

    protected Comparator<O> getComparator() {
        return Comparator.comparingInt(Object::hashCode);
    }

}
