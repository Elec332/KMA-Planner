package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.phase1.Phase1Configuration;
import elec332.kmaplanner.planner.opta.solver.phase2.Phase2Configuration;
import elec332.kmaplanner.planner.opta.solver.phase4.Phase4AConfiguration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 * Created by Elec332 on 14-9-2019
 */
public class Solver1 implements ISolverConfiguration {

    @Override
    public void configureSolver(SolverFactory<Roster> factory, PlannerSettings settings) {
        SolverConfigurator.configureSolver(factory, settings,
                new Phase1Configuration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                new Phase2Configuration(true, true));
    }

}
