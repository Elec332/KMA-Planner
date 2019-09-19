package elec332.kmaplanner.planner.opta.solver.phase2;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterSwapMove;
import elec332.kmaplanner.planner.opta.solver.move.factory.AbstractSwapMoveListFactory;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase2ASwapMoveListFactory extends AbstractSwapMoveListFactory {

    @Override
    protected AbstractRosterMove createMove(Assignment a1, Assignment a2, Roster roster) {
        if (a1.person.getPlannerData().getMainGroup() == a2.person.getPlannerData().getMainGroup()) {
            return null;
        }
        return new RosterSwapMove(a1, a2);
    }

}
