package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.project.PlannerSettings;

/**
 * Created by Elec332 on 5-3-2020
 */
public class Solver3 extends Solver3Pre {

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        super.preSolve(roster, settings);
        roster.forceGrouping = true;
    }

}
