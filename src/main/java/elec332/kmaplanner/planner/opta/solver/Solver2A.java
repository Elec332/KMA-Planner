package elec332.kmaplanner.planner.opta.solver;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterScoreCalculator;
import elec332.kmaplanner.planner.opta.solver.phase2.Phase2BConfiguration;
import elec332.kmaplanner.planner.opta.solver.phase2.Phase2Configuration;
import elec332.kmaplanner.planner.opta.solver.phase4.Phase4AConfiguration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 * Created by Elec332 on 14-9-2019
 */
public class Solver2A implements ISolverConfiguration {

    public Solver2A() {
        this(false);
    }

    protected Solver2A(boolean last) {
        this.last = last;
    }

    protected final boolean last;

    @Override
    public void preSolve(Roster roster, PlannerSettings settings) {
        Preconditions.checkNotNull(roster);
        roster.noSoft = true;
        HardMediumSoftScore score = RosterScoreCalculator.calculateScore(roster, false);
        if (score.getMediumScore() > 0) {
            roster.groupOffset /= 2;
        }
    }

    @Override
    public void configureSolver(SolverFactory<Roster> factory, Roster roster, PlannerSettings settings) {
        SolverConfigurator.configureSolver(factory, settings,
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                //new Phase2AConfiguration(),
                new Phase2BConfiguration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                //new Phase2AConfiguration(),
                new Phase2BConfiguration(),
                new Phase2Configuration(true),
                new Phase4AConfiguration(),
                //new Phase2AConfiguration(),
                new Phase2BConfiguration(),
                new Phase2Configuration(!last, !last));
    }

    @Override
    public void postSolve(Roster roster, PlannerSettings settings) {
        roster.noSoft = false;
    }

}
