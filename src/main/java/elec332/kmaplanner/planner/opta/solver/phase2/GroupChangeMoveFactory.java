package elec332.kmaplanner.planner.opta.solver.phase2;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import elec332.kmaplanner.planner.opta.solver.move.factory.AbstractChangeMoveListFactory;

/**
 * Created by Elec332 on 16-9-2019
 */
public class GroupChangeMoveFactory extends AbstractChangeMoveListFactory {

    @Override
    protected AbstractRosterMove createMove(Assignment a1, Person p, Roster roster) {
        if (a1.person.getPlannerData().getMainGroup() == p.getPlannerData().getMainGroup()) {
            return null;
        }
        return new RosterChangeMove(a1, p);
    }

}