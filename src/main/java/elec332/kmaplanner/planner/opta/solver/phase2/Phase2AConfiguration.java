package elec332.kmaplanner.planner.opta.solver.phase2;

import com.google.common.collect.Lists;
import elec332.kmaplanner.planner.opta.solver.IPhaseConfiguration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 31-8-2019
 * <p>
 * TODO: Rework or remove
 */
public class Phase2AConfiguration implements IPhaseConfiguration<LocalSearchPhaseConfig> {

    @Override
    public LocalSearchPhaseConfig createPhase(PlannerSettings settings) {
        LocalSearchPhaseConfig phase = new LocalSearchPhaseConfig();

        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedSecondsSpentLimit(30L);
        phase.getTerminationConfig().setUnimprovedStepCountLimit(settings.unimprovedSteps / 3);
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, 0, Integer.MIN_VALUE).toString());

        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        MoveListFactoryConfig swapMoveListFactoryConfig = new MoveListFactoryConfig();
        swapMoveListFactoryConfig.setMoveListFactoryClass(Phase2AChangeMoveListFactory.class);
        MoveListFactoryConfig changeMoveListFactoryConfig = new MoveListFactoryConfig();
        changeMoveListFactoryConfig.setMoveListFactoryClass(Phase2ASwapMoveListFactory.class);
        unionMoveSelectorConfig.setMoveSelectorConfigList(Lists.newArrayList(swapMoveListFactoryConfig, changeMoveListFactoryConfig));
        phase.setMoveSelectorConfig(unionMoveSelectorConfig);

        return phase;
    }

}
