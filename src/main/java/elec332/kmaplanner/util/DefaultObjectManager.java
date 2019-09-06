package elec332.kmaplanner.util;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 4-9-2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class DefaultObjectManager<O, R> extends AbstractObjectManager<O, R> {

    @Override
    public abstract void load(R reader);

    @Override
    public boolean addObjectNice(O object) {
        boolean ret = objects.add(object);
        if (ret) {
            postAddPerson(object);
            runCallbacks();
        }
        return ret;
    }

    protected void postAddPerson(O object) {
    }

    @Override
    public void removeObject(O object) {
        if (!objects.contains(object)) {
            throw new IllegalArgumentException();
        }
        objects.remove(object);
        postRemoveObject(object);
        runCallbacks();
    }

    protected void postRemoveObject(O object) {
    }

    @Override
    public void updateObject(O object, Consumer<O> consumer) {
        if (objects.remove(object)) {
            consumer.accept(object);
            objects.add(object);
            postUpdateObject(object);
            runCallbacks();
        }
    }

    protected void postUpdateObject(O object) {
    }

}
