package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.project.PlannerSettings;

/**
 * Created by Elec332 on 3-3-2020
 */
public class Solver2D extends Solver2A {

    public Solver2D() {
        super(true);
    }

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        super.preSolve(roster, settings);
        roster.getAssignments().forEach(assignment -> assignment.groupFilter = assignment.event::canGroupParticipate);
    }

}
