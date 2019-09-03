package elec332.kmaplanner.planner.opta.solver.phase2;

import com.google.common.collect.Lists;
import elec332.kmaplanner.planner.opta.solver.IPhaseConfiguration;
import elec332.kmaplanner.project.ProjectSettings;
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
    public LocalSearchPhaseConfig createPhase(ProjectSettings settings) {
        LocalSearchPhaseConfig phase = new LocalSearchPhaseConfig();

        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedStepCountLimit(500);

        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        MoveListFactoryConfig swapMoveListFactoryConfig = new MoveListFactoryConfig();
        swapMoveListFactoryConfig.setMoveListFactoryClass(Phase2ASwapMoveListFactory.class);
        MoveListFactoryConfig changeMoveListFactoryConfig = new MoveListFactoryConfig();
        changeMoveListFactoryConfig.setMoveListFactoryClass(Phase2AChangeMoveListFactory.class);
        unionMoveSelectorConfig.setMoveSelectorConfigList(Lists.newArrayList(swapMoveListFactoryConfig, changeMoveListFactoryConfig));
        phase.setMoveSelectorConfig(unionMoveSelectorConfig);

        return phase;
    }

}
