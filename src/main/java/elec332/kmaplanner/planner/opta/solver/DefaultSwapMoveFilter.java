package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;

/**
 * Created by Elec332 on 30-8-2019
 */
public class DefaultSwapMoveFilter extends AbstractSwapMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment a1, Assignment a2) {
        if (a1.person.getPlannerData().getEvents().contains(a2.event) || a2.person.getPlannerData().getEvents().contains(a1.event)) {
            return false;
        }
        if (a1.event.isDuring(a2.person.getPlannerData().getEvents())) {
            return false;
        }
        if (a2.event.isDuring(a1.person.getPlannerData().getEvents())) {
            return false;
        }
        if (!a1.groupFilter.test(a2.person.getPlannerData().getMainGroup())) {
            return false;
        }
        if (!a2.groupFilter.test(a1.person.getPlannerData().getMainGroup())) {
            return false;
        }
        return !a1.person.equals(a2.person) && !a1.event.equals(a2.event);
    }

}
