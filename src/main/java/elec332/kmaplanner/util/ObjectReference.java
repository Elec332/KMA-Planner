package elec332.kmaplanner.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Created by Elec332 on 28-8-2019
 */
public class ObjectReference<T> implements Consumer<T>, Supplier<T> {

    public ObjectReference() {
        this(null);
    }

    public ObjectReference(T t) {
        this.object = t;
    }

    private T object;

    public void use(UnaryOperator<T> func) {
        object = func.apply(object);
    }

    public void set(T t) {
        accept(t);
    }

    public <Q extends T> Q put(Q q) {
        accept(q);
        return q;
    }

    @Override
    public void accept(T t) {
        object = t;
    }

    @Override
    public T get() {
        return object;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(object);
    }

}
