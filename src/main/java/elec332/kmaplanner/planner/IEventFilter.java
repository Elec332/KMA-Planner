package elec332.kmaplanner.planner;

import elec332.kmaplanner.events.Event;

/**
 * Created by Elec332 on 15-8-2019
 */
public interface IEventFilter {

    boolean canParticipateIn(Event event);

    default IEventFilter and(final IEventFilter filter) {
        return e -> canParticipateIn(e) && filter.canParticipateIn(e);
    }

}
