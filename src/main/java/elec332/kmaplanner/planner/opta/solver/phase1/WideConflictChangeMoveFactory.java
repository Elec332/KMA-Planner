package elec332.kmaplanner.planner.opta.solver.phase1;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import elec332.kmaplanner.planner.opta.solver.move.factory.AbstractChangeMoveListFactory;

/**
 * Created by Elec332 on 15-9-2019
 */
public class WideConflictChangeMoveFactory extends AbstractChangeMoveListFactory {

    @Override
    protected AbstractRosterMove createMove(Assignment a1, Person p, Roster roster) {
        return new RosterChangeMove(a1, p);
    }

    @Override
    protected boolean skipAssignment(Assignment a1, Roster roster) {
        return a1.getPerson().canParticipateIn(a1.event);
    }

}