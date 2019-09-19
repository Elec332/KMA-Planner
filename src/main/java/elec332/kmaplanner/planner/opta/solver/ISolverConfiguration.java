package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 * Created by Elec332 on 14-9-2019
 */
public interface ISolverConfiguration {

    default void preSolve(Roster roster, PlannerSettings settings) {
    }

    void configureSolver(SolverFactory<Roster> factory, Roster roster, PlannerSettings settings);

    default void postSolve(Roster roster, PlannerSettings settings) {
    }

}
