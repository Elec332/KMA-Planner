package elec332.kmaplanner.planner.opta.solver.phase1;

import elec332.kmaplanner.planner.opta.solver.AbstractPhaseConfiguration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase1Configuration extends AbstractPhaseConfiguration {

    public Phase1Configuration(int end) {
        this.end = end;
    }

    private final int end;

    @Override
    protected void configure(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig, PlannerSettings settings) {
        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(end, Integer.MIN_VALUE, Integer.MIN_VALUE).toString());
        phase.getTerminationConfig().setUnimprovedSecondsSpentLimit(6 * 60L);
    }

}
