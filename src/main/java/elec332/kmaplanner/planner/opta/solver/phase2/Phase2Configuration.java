package elec332.kmaplanner.planner.opta.solver.phase2;

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
public class Phase2Configuration extends AbstractPhaseConfiguration {

    public Phase2Configuration(boolean step) {
        this(step, false);
    }

    public Phase2Configuration(boolean step, boolean usm) {
        this.me = step;
        this.usm = usm;
    }

    private final boolean me, usm;

    @Override
    protected void configure(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig, PlannerSettings settings) {
        swapMoveSelectorConfig.getFilterClassList().add(OptimizeGroupSwapMoveFilter.class);
        changeMoveSelectorConfig.getFilterClassList().add(OptimizeGroupChangeMoveFilter.class);

        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, 0, Integer.MIN_VALUE).toString());
        if (usm) {
            phase.getTerminationConfig().setUnimprovedSecondsSpentLimit(60L);
        }
        if (me) {
            int mul = 3;
            if (usm) {
                mul += 2;
            }
            phase.getTerminationConfig().setUnimprovedStepCountLimit(settings.unimprovedSteps * mul);
        }
        phase.getTerminationConfig().setUnimprovedMinutesSpentLimit(5L);
    }

}
