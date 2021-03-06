package elec332.kmaplanner.planner.opta.solver.filter;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;

/**
 * Created by Elec332 on 30-8-2019
 */
public class DefaultSwapMoveFilter extends AbstractSwapMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment a1, Assignment a2) {
        return isValidSwap(a1, a2);
    }

    public static boolean isValidSwap(Assignment a1, Assignment a2) {
        if (a1 == a2) {
            return false;
        }
        if (a1.person.getPlannerData().getEvents().contains(a2.event) || a2.person.getPlannerData().getEvents().contains(a1.event)) {
            return false;
        }
        if (a1.event.isDuring(a2.person.getPlannerData().getEvents())) {
            return false;
        }
        if (a2.event.isDuring(a1.person.getPlannerData().getEvents())) {
            return false;
        }
        if (!a1.isValidGroup(a2.person.getPlannerData().getMainGroup())) {
            return false;
        }
        if (!a2.isValidGroup(a1.person.getPlannerData().getMainGroup())) {
            return false;
        }
        if (a1.person.equals(a2.person)) {
            return false;
        }
        return !a1.event.equals(a2.event);
    }

}
