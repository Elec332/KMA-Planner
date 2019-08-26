package elec332.kmaplanner.filters;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import elec332.kmaplanner.filters.impl.NeverFilter;
import elec332.kmaplanner.filters.impl.TimeFilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 24-8-2019
 */
public enum FilterManager {

    INSTANCE;

    FilterManager(){
        registry = new HashMap<>();
    }

    private final HashMap<String, Supplier<AbstractFilter>> registry;

    public boolean registerFilter(Supplier<AbstractFilter> filterFunction, final String shortName){
        if (Strings.isNullOrEmpty(shortName)){
            throw new IllegalArgumentException();
        }
        if (registry.containsKey(shortName)){
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
        return true;
    }

    public Collection<Supplier<AbstractFilter>> getFilters(){
        return registry.values();
    }

    static {
        INSTANCE.registerFilter(() -> NeverFilter.INSTANCE, "Never participate");
        INSTANCE.registerFilter(TimeFilter::new, "Unavailable during");
    }

}
