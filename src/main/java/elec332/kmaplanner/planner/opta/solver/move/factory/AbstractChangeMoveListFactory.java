package elec332.kmaplanner.planner.opta.solver.move.factory;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.filter.DefaultChangeMoveFilter;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;

import java.util.List;

/**
 * Created by Elec332 on 17-9-2019
 */
public abstract class AbstractChangeMoveListFactory extends AbstractMoveListFactory {

    @Override
    public final void fillMoves(List<AbstractRosterMove> moves, Roster roster) {
        for (Assignment a1 : roster.getAssignments()) {
            if (skipAssignment(a1, roster)) {
                continue;
            }
            for (Person p : roster.getPersons()) {
                if (p == PersonManager.NULL_PERSON) {
                    continue;
                }
                if (p == a1.person) {
                    continue;
                }
                if (!DefaultChangeMoveFilter.isValidSwap(a1, p)) {
                    continue;
                }
                AbstractRosterMove arm = createMove(a1, p, roster);
                if (arm != null) {
                    moves.add(arm);
                }
            }
        }
    }

    protected abstract AbstractRosterMove createMove(Assignment a1, Person p, Roster roster);

    protected boolean skipAssignment(Assignment a1, Roster roster) {
        return false;
    }

}