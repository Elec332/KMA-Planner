package elec332.kmaplanner.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Created by Elec332 on 28-8-2019
 */
public class ObjectReference<T> implements Consumer<T>, Supplier<T> {

    public ObjectReference(){
        this(null);
    }

    public ObjectReference(T t){
        this.object = t;
    }

    private T object;

    public void use(UnaryOperator<T> func){
        object = func.apply(object);
    }

    @Override
    public void accept(T t) {
        object = t;
    }

    @Override
    public T get() {
        return object;
    }

}
