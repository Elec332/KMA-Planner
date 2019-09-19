package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterScoreCalculator;
import elec332.kmaplanner.planner.opta.solver.phase1.Phase1AConfiguration;
import elec332.kmaplanner.planner.opta.solver.phase1.Phase1BConfiguration;
import elec332.kmaplanner.planner.opta.solver.phase1.Phase1CConfiguration;
import elec332.kmaplanner.planner.opta.solver.phase1.Phase1Configuration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 * Created by Elec332 on 14-9-2019
 */
public class Solver1 implements ISolverConfiguration {

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        roster.noSoft = true;
    }

    @Override
    public void configureSolver(SolverFactory<Roster> factory, Roster roster, PlannerSettings settings) {
        HardMediumSoftScore score = RosterScoreCalculator.calculateScore(roster, false);
        float hs = score.getHardScore();
        SolverConfigurator.configureSolver(factory, settings,
                new Phase1Configuration((int) (hs * 0.5f)),
                new Phase1AConfiguration(),
                new Phase1BConfiguration(),
                new Phase1Configuration((int) (hs * 0.1f)),
                new Phase1AConfiguration(),
                new Phase1BConfiguration(),
                new Phase1Configuration((int) (hs * 0.05f)),
                new Phase1AConfiguration(),
                new Phase1BConfiguration(),
                new Phase1CConfiguration(),
                new Phase1Configuration((int) (hs * 0.02f)),
                new Phase1AConfiguration(),
                new Phase1BConfiguration(),
                new Phase1CConfiguration(),
                new Phase1Configuration((int) (hs * 0.01f)),
                new Phase1AConfiguration(),
                new Phase1BConfiguration(),
                new Phase1CConfiguration(),
                new Phase1Configuration(0));
    }

    @Override
    public void postSolve(Roster roster, PlannerSettings settings) {
        roster.noSoft = false;
    }

}
