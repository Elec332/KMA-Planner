package elec332.kmaplanner.util;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Elec332 on 7-9-2019
 */
public class WeakCallbackHandler {

    public WeakCallbackHandler() {
        callbacks = new WeakHashMap<>();
    }

    private final Map<Object, Runnable> callbacks;

    public void addCallback(Object weakKey, Runnable runnable) {
        callbacks.put(weakKey, runnable);
    }

    public void runCallbacks() {
        callbacks.values().forEach(Runnable::run);
    }

}
