package elec332.kmaplanner.planner.opta.solver.move.factory;

import com.google.common.collect.Lists;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.List;

/**
 * Created by Elec332 on 16-9-2019
 */
public abstract class AbstractMoveListFactory implements MoveListFactory<Roster> {

    @Override
    public final List<? extends Move<Roster>> createMoveList(Roster roster) {
        roster.plannerApply();
        List<AbstractRosterMove> moves = Lists.newArrayList();
        fillMoves(moves, roster);
        if (moves.isEmpty()) {
            Assignment a = roster.getAssignments().get(0);
            moves.add(new RosterChangeMove(a, a.person));
        }
        return moves;
    }

    public abstract void fillMoves(List<AbstractRosterMove> moves, Roster roster);

}
