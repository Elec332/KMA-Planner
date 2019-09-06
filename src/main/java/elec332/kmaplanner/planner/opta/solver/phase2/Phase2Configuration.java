package elec332.kmaplanner.planner.opta.solver.phase2;

import com.google.common.collect.Lists;
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

    @Override
    protected void configure(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig, PlannerSettings settings) {
        swapMoveSelectorConfig.getFilterClassList().add(OptimizeGroupSwapMoveFilter.class);
        changeMoveSelectorConfig.getFilterClassList().add(OptimizeGroupChangeMoveFilter.class);

        TerminationConfig c1 = new TerminationConfig();
        c1.setBestScoreLimit(HardMediumSoftScore.of(0, 0, Integer.MIN_VALUE).toString());

        //TerminationConfig c2 = new TerminationConfig();
        //c2.setTerminationCompositionStyle(TerminationCompositionStyle.AND);
        //c2.withTerminationConfigList(Lists.newArrayList(new TerminationConfig().withUnimprovedStepCountLimit(1000), new TerminationConfig().withBestScoreLimit(HardMediumSoftScore.of(0, 25, Integer.MIN_VALUE).toString())));

        phase.setTerminationConfig(new TerminationConfig().withTerminationConfigList(Lists.newArrayList(c1/*, c2*/)));

    }

}
