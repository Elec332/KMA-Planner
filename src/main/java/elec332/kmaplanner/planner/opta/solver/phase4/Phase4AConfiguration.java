package elec332.kmaplanner.planner.opta.solver.phase4;

import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;

/**
 * Created by Elec332 on 14-9-2019
 */
public class Phase4AConfiguration extends Phase4Configuration {

    @Override
    public LocalSearchPhaseConfig createPhase(PlannerSettings settings) {
        LocalSearchPhaseConfig ret = super.createPhase(settings);
        ret.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, 0, Integer.MIN_VALUE).toString());
        return ret;
    }

}
