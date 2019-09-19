package elec332.kmaplanner.planner.opta.solver.move.factory;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.filter.DefaultSwapMoveFilter;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;

import java.util.List;

/**
 * Created by Elec332 on 17-9-2019
 */
public abstract class AbstractSwapMoveListFactory extends AbstractMoveListFactory {

    @Override
    public final void fillMoves(List<AbstractRosterMove> moves, Roster roster) {
        for (Assignment a1 : roster.getAssignments()) {
            for (Assignment a2 : roster.getAssignments()) {
                if (a1 == a2 || !DefaultSwapMoveFilter.isValidSwap(a1, a2)) {
                    continue;
                }
                AbstractRosterMove arm = createMove(a1, a2, roster);
                if (arm != null) {
                    moves.add(arm);
                }
            }
        }
    }

    protected abstract AbstractRosterMove createMove(Assignment a1, Assignment a2, Roster roster);

}
