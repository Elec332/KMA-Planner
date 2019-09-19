package elec332.kmaplanner.planner.opta.solver.phase1;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;

/**
 * Created by Elec332 on 15-9-2019
 */
public class ConflictChangeMoveFactory extends GroupChangeMoveFactory {

    @Override
    protected boolean skipAssignment(Assignment a1, Roster roster) {
        return a1.getPerson().canParticipateIn(a1.event);
    }

}