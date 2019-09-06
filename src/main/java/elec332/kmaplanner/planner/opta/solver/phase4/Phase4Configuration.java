package elec332.kmaplanner.planner.opta.solver.phase4;

import elec332.kmaplanner.planner.opta.solver.IPhaseConfiguration;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase4Configuration implements IPhaseConfiguration<LocalSearchPhaseConfig> {

    @Override
    public LocalSearchPhaseConfig createPhase(PlannerSettings settings) {
        LocalSearchPhaseConfig phase = new LocalSearchPhaseConfig();

        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedStepCountLimit(settings.unimprovedSteps);

        MoveListFactoryConfig moveListFactoryConfig = new MoveListFactoryConfig();
        moveListFactoryConfig.setMoveListFactoryClass(SmartTimeChangeMoveFactory.class);
        phase.setMoveSelectorConfig(moveListFactoryConfig);

        return phase;
    }

}
