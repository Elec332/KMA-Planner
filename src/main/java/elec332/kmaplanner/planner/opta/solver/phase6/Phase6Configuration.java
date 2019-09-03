package elec332.kmaplanner.planner.opta.solver.phase6;

import com.google.common.collect.Lists;
import elec332.kmaplanner.planner.opta.solver.IPhaseConfiguration;
import elec332.kmaplanner.planner.opta.solver.phase3.SmartTimeMoveListFactoryV2;
import elec332.kmaplanner.planner.opta.solver.phase4.SmartTimeChangeMoveFactory;
import elec332.kmaplanner.project.ProjectSettings;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase6Configuration implements IPhaseConfiguration<LocalSearchPhaseConfig> {

    @Override
    public LocalSearchPhaseConfig createPhase(ProjectSettings settings) {
        LocalSearchPhaseConfig phase = new LocalSearchPhaseConfig();

        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedStepCountLimit(settings.unimprovedSteps);

        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        MoveListFactoryConfig swapMoveListFactoryConfig = new MoveListFactoryConfig();
        swapMoveListFactoryConfig.setMoveListFactoryClass(SmartTimeMoveListFactoryV2.class);
        MoveListFactoryConfig changeMoveListFactoryConfig = new MoveListFactoryConfig();
        changeMoveListFactoryConfig.setMoveListFactoryClass(SmartTimeChangeMoveFactory.class);
        unionMoveSelectorConfig.setMoveSelectorConfigList(Lists.newArrayList(swapMoveListFactoryConfig, changeMoveListFactoryConfig));
        phase.setMoveSelectorConfig(unionMoveSelectorConfig);

        return phase;
    }

}
