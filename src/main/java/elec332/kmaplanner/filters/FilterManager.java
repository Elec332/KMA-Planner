package elec332.kmaplanner.filters;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import elec332.kmaplanner.filters.impl.LessTimeFilter;
import elec332.kmaplanner.filters.impl.NeverFilter;
import elec332.kmaplanner.filters.impl.TimeFilter;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 24-8-2019
 */
public enum FilterManager implements Function<IByteArrayDataInputStream, AbstractFilter>, BiConsumer<IByteArrayDataOutputStream, AbstractFilter> {

    INSTANCE;

    FilterManager() {
        registry = new HashMap<>();
        reverseLookup = new HashMap<>();
        descriptionGetter = new HashMap<>();
    }

    private final HashMap<String, Supplier<AbstractFilter>> registry;
    private final HashMap<Class, String> reverseLookup;
    private final HashMap<String, String> descriptionGetter;

    @SuppressWarnings("UnusedReturnValue")
    public boolean registerFilter(Supplier<AbstractFilter> filterFunction, final String shortName) {
        if (Strings.isNullOrEmpty(shortName)) {
            throw new IllegalArgumentException();
        }
        if (registry.containsKey(shortName)) {
            return false;
        }
        AbstractFilter filter = filterFunction.get();
        Class type = filter.getClass();
        if (reverseLookup.containsKey(type)) {
            return false;
        }
        Preconditions.checkNotNull(filterFunction);
        registry.put(shortName, new Supplier<AbstractFilter>() {

            @Override
            public AbstractFilter get() {
                return filterFunction.get();
            }


            @Override
            public String toString() {
                return shortName;
            }

        });
        reverseLookup.put(type, shortName);
        descriptionGetter.put(shortName, filter.getDescription());
        return true;
    }

    public Collection<Supplier<AbstractFilter>> getFilters() {
        return registry.values();
    }

    public String getDescription(String name) {
        return descriptionGetter.get(name);
    }

    static {
        INSTANCE.registerFilter(() -> NeverFilter.INSTANCE, "Never participate");
        INSTANCE.registerFilter(TimeFilter::new, "Unavailable during");
        INSTANCE.registerFilter(LessTimeFilter::new, "Efficiency");
    }

    @Override
    public void accept(IByteArrayDataOutputStream stream, AbstractFilter filter) {
        String name = reverseLookup.get(filter.getClass());
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException();
        }
        stream.writeUTF(name);
        filter.writeObject(stream);
    }

    @Override
    public AbstractFilter apply(IByteArrayDataInputStream stream) {
        Supplier<AbstractFilter> sup = registry.get(stream.readUTF());
        if (sup == null) {
            return null;
        }
        AbstractFilter ret = sup.get();
        if (ret == null) {
            return null;
        }
        ret.readObject(stream);
        return ret;
    }

}
