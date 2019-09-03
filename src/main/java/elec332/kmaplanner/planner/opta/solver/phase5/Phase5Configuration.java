package elec332.kmaplanner.planner.opta.solver.phase5;

import elec332.kmaplanner.planner.opta.solver.AbstractPhaseConfiguration;
import elec332.kmaplanner.project.ProjectSettings;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 31-8-2019
 */
public class Phase5Configuration extends AbstractPhaseConfiguration {

    @Override
    protected void configure(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig, ProjectSettings settings) {
        swapMoveSelectorConfig.getFilterClassList().add(OptimizeTimeSwapMoveFilter.class);
        changeMoveSelectorConfig.getFilterClassList().add(OptimizeTimeChangeMoveFilter.class);
        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedStepCountLimit(settings.unimprovedSteps);

/*
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        changeMoveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        valueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        valueSelectorConfig.setSorterComparatorClass(ChangeMovePersonSorter.class);

        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        changeMoveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        entitySelectorConfig.setSorterComparatorClass(ChangeMoveAssignmentSorter.class);
        */
    }

}
