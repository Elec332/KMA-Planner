package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.phase3.Phase3Configuration;
import elec332.kmaplanner.planner.opta.solver.phase4.Phase4Configuration;
import elec332.kmaplanner.planner.opta.solver.phase5.Phase5Configuration;
import elec332.kmaplanner.planner.opta.solver.phase6.Phase6Configuration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 * Created by Elec332 on 16-9-2019
 */
public class Solver3Pre implements ISolverConfiguration {

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        roster.groupOffset = 0;
    }

    @Override
    public void configureSolver(SolverFactory<Roster> factory, Roster roster, PlannerSettings settings) {
        SolverConfigurator.configureSolver(factory, settings,
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
                new Phase6Configuration(),
                new Phase4Configuration(),
                new Phase5Configuration(),
                new Phase6Configuration(),
                new Phase4Configuration(),
                new Phase5Configuration(),
                new Phase6Configuration(),
                new Phase4Configuration(),
                new Phase5Configuration(),
                new Phase6Configuration());
    }

}
