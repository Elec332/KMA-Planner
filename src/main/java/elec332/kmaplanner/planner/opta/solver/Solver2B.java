package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.project.PlannerSettings;

/**
 * Created by Elec332 on 16-9-2019
 */
public class Solver2B extends Solver2A {

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        super.preSolve(roster, settings);
        roster.getAssignments().forEach(assignment -> {
            if (!assignment.event.everyone && assignment.event.requiredPersons > settings.mainGroupFactor * 2.5f) {
                assignment.groupFilter = assignment.event::canGroupParticipate;
            }
        });
    }

}
