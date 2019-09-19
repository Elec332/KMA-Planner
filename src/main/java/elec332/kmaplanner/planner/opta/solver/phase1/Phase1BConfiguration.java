package elec332.kmaplanner.planner.opta.solver.phase1;

import elec332.kmaplanner.planner.opta.solver.IPhaseConfiguration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 15-9-2019
 */
public class Phase1BConfiguration implements IPhaseConfiguration<LocalSearchPhaseConfig> {

    @Override
    public LocalSearchPhaseConfig createPhase(PlannerSettings settings) {
        LocalSearchPhaseConfig phase = new LocalSearchPhaseConfig();

        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedSecondsSpentLimit(30L);
        phase.getTerminationConfig().setUnimprovedStepCountLimit(settings.unimprovedSteps / 3);
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, Integer.MIN_VALUE, Integer.MIN_VALUE).toString());

        MoveListFactoryConfig moveListFactoryConfig = new MoveListFactoryConfig();
        moveListFactoryConfig.setMoveListFactoryClass(ConflictChangeMoveFactory.class);
        phase.setMoveSelectorConfig(moveListFactoryConfig);

        return phase;
    }

}
