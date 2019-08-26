package elec332.kmaplanner.planner;

/**
 * Created by Elec332 on 15-8-2019
 */
public interface IEventFilter {

    boolean canParticipateIn(Event event);

    default public IEventFilter and(final IEventFilter filter) {
        return e -> canParticipateIn(e) && filter.canParticipateIn(e);
    }

}
