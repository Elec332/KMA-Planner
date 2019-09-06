package elec332.kmaplanner.planner.opta.solver;

import com.google.common.collect.Lists;
import elec332.kmaplanner.planner.opta.solver.filter.DefaultChangeMoveFilter;
import elec332.kmaplanner.planner.opta.solver.filter.DefaultSwapMoveFilter;
import elec332.kmaplanner.project.PlannerSettings;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 30-8-2019
 */
public abstract class AbstractPhaseConfiguration implements IPhaseConfiguration<LocalSearchPhaseConfig> {

    public LocalSearchPhaseConfig createPhase(PlannerSettings settings) {
        LocalSearchPhaseConfig ret = new LocalSearchPhaseConfig();
        configurePhase(ret, settings);
        return ret;
    }

    private void configurePhase(LocalSearchPhaseConfig phase, PlannerSettings settings) {
        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        SwapMoveSelectorConfig swapMoveSelectorConfig = new SwapMoveSelectorConfig();
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorConfigList(Lists.newArrayList(changeMoveSelectorConfig, swapMoveSelectorConfig));
        swapMoveSelectorConfig.setFilterClassList(Lists.newArrayList(DefaultSwapMoveFilter.class));
        changeMoveSelectorConfig.setFilterClassList(Lists.newArrayList(DefaultChangeMoveFilter.class));
        phase.setTerminationConfig(new TerminationConfig() {

            @Override
            public Long calculateUnimprovedTimeMillisSpentLimit() {
                return null;
            }

        });
        configure(phase, swapMoveSelectorConfig, changeMoveSelectorConfig, settings);
        phase.setMoveSelectorConfig(unionMoveSelectorConfig);
    }

    protected abstract void configure(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig, PlannerSettings settings);

}
