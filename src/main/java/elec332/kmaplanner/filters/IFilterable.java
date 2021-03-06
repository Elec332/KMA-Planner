package elec332.kmaplanner.filters;

import java.util.Set;

/**
 * Created by Elec332 on 26-8-2019
 */
public interface IFilterable {

    Set<AbstractFilter> getFilters();

    default Set<AbstractFilter> getModifiableFilters() {
        return getFilters();
    }

}
