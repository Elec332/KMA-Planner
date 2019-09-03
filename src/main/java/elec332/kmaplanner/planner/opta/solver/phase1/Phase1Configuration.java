package elec332.kmaplanner.planner.opta.solver.phase1;

import elec332.kmaplanner.planner.opta.solver.AbstractPhaseConfiguration;
import elec332.kmaplanner.project.ProjectSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase1Configuration extends AbstractPhaseConfiguration {

    @Override
    protected void configure(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig, ProjectSettings settings) {
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, Integer.MIN_VALUE, Integer.MIN_VALUE).toString());
    }

}
