package elec332.kmaplanner.filters;

import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.IEventFilter;

/**
 * Created by Elec332 on 15-8-2019
 */
public abstract class AbstractFilter implements IEventFilter {

    public AbstractFilter(String name) {
        this.name = name;
    }

    private final String name;

    public abstract String getDescription();

    public abstract boolean canParticipateIn(Event event);

    @Override
    public String toString() {
        return "Filter " + name + " Description: "+ getDescription();
    }

}
