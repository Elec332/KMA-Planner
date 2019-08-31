package elec332.kmaplanner.planner.opta.solver.phase2;

import com.google.common.collect.Lists;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.filter.DefaultChangeMoveFilter;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase2ASwapMoveListFactory implements MoveListFactory<Roster> {

    @Override
    public List<? extends Move<Roster>> createMoveList(Roster roster) {
        roster.plannerApply();
        List<AbstractRosterMove> moves = Lists.newArrayList();
        for (Assignment a1 : roster.getAssignments()) {
            for (Person p : roster.getPersons()) {
                if (p == PersonManager.NULL_PERSON || p == a1.person || !(new Random().nextDouble() < 0.2)) {
                    continue;
                }
                if (!DefaultChangeMoveFilter.isValidSwap(a1, p)) {
                    continue;
                }
                if (a1.person.getPlannerData().getMainGroup() == p.getPlannerData().getMainGroup()) {
                    continue;
                }
                moves.add(new RosterChangeMove(a1, p));
            }
        }

        return moves;
    }

}