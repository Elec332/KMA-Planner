package elec332.kmaplanner.planner.opta.solver.filter;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;

/**
 * Created by Elec332 on 30-8-2019
 */
public class DefaultChangeMoveFilter extends AbstractChangeMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment a, Person p) {
        return isValidSwap(a, p);
    }

    public static boolean isValidSwap(Assignment a, Person p) {
        if (p.getPlannerData().getEvents().contains(a.event)) {
            return false;
        }
        if (a.event.isDuring(p.getPlannerData().getEvents())) {
            return false;
        }
        if (!a.isValidGroup(p.getPlannerData().getMainGroup())) {
            return false;
        }
        return !a.person.equals(p);
    }

}
