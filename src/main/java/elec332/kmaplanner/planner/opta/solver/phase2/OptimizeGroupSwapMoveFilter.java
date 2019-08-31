package elec332.kmaplanner.planner.opta.solver.phase2;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.filter.AbstractSwapMoveFilter;

/**
 * Created by Elec332 on 30-8-2019
 */
public class OptimizeGroupSwapMoveFilter extends AbstractSwapMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment a1, Assignment a2) {
        return a1.person.getPlannerData().getMainGroup() != a2.person.getPlannerData().getMainGroup();
    }

}
