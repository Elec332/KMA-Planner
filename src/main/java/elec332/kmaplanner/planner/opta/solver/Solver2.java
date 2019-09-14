package elec332.kmaplanner.planner.opta.solver;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.phase2.Phase2Configuration;
import elec332.kmaplanner.planner.opta.solver.phase3.Phase3Configuration;
import elec332.kmaplanner.planner.opta.solver.phase4.Phase4AConfiguration;
import elec332.kmaplanner.planner.opta.solver.phase4.Phase4Configuration;
import elec332.kmaplanner.planner.opta.solver.phase5.Phase5Configuration;
import elec332.kmaplanner.planner.opta.solver.phase6.Phase6Configuration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 * Created by Elec332 on 14-9-2019
 */
public class Solver2 implements ISolverConfiguration {

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        Preconditions.checkNotNull(roster);
        roster.getAssignments().forEach(assignment -> {
            if (!assignment.event.everyone && assignment.event.requiredPersons > settings.mainGroupFactor * 2.5f) {
                assignment.groupFilter = assignment.event::canGroupParticipate;
            }
        });
    }

    @Override
    public void configureSolver(SolverFactory<Roster> factory, PlannerSettings settings) {
        SolverConfigurator.configureSolver(factory, settings,
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                new Phase2Configuration(),
                new Phase3Configuration(),
                new Phase4Configuration(),
                new Phase5Configuration(),
                new Phase6Configuration(),
                new Phase3Configuration(),
                new Phase4Configuration(),
                new Phase5Configuration(),
                new Phase6Configuration(),
                new Phase4Configuration(),
                new Phase5Configuration(),
                new Phase6Configuration());
    }

}
