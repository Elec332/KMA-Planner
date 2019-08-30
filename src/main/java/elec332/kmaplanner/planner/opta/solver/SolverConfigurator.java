package elec332.kmaplanner.planner.opta.solver;

import com.google.common.collect.Lists;
import elec332.kmaplanner.planner.opta.solver.phase2.OptimizeGroupChangeMoveFilter;
import elec332.kmaplanner.planner.opta.solver.phase2.OptimizeGroupSwapMoveFilter;
import elec332.kmaplanner.planner.opta.solver.phase3.OptimizeTimeChangeMoveFilter;
import elec332.kmaplanner.planner.opta.solver.phase3.OptimizeTimeSwapMoveFilter;
import elec332.kmaplanner.util.TriConsumer;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Created by Elec332 on 30-8-2019
 */
public class SolverConfigurator {

    public static void configureSolver(SolverFactory<?> factory) {
        LocalSearchPhaseConfig phase1 = new LocalSearchPhaseConfig();
        LocalSearchPhaseConfig phase2 = new LocalSearchPhaseConfig();
        LocalSearchPhaseConfig phase3 = new LocalSearchPhaseConfig();

        makePhase(phase1, SolverConfigurator::configurePhase1);
        makePhase(phase2, SolverConfigurator::configurePhase2);
        makePhase(phase3, SolverConfigurator::configurePhase3);

        factory.getSolverConfig().withPhases(phase1, phase2, phase3);

        //finally
        /*

        swapMoveSelectorConfig.setFilterClassList(Lists.newArrayList(DifferentPersonEventSwapFilter.class));
        changeMoveSelectorConfig.setFilterClassList(Lists.newArrayList(DifferentPersonChangeFilter.class));

        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        changeMoveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        valueSelectorConfig.setCacheType(SelectionCacheType.STEP);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        valueSelectorConfig.setSorterComparatorClass(PersonSorter.class);

        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        changeMoveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        entitySelectorConfig.setSorterComparatorClass(AssignmentSorter.class);*/
    }

    private static void configurePhase1(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig) {
        //phase.getTerminationConfig().setBestScoreFeasible(true);
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, Integer.MIN_VALUE, Integer.MIN_VALUE).toString());
    }

    private static void configurePhase2(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig) {
        swapMoveSelectorConfig.getFilterClassList().add(OptimizeGroupSwapMoveFilter.class);
        changeMoveSelectorConfig.getFilterClassList().add(OptimizeGroupChangeMoveFilter.class);

        //phase.getTerminationConfig().setBestScoreFeasible(true);
        phase.getTerminationConfig().setBestScoreLimit(HardMediumSoftScore.of(0, 0, Integer.MIN_VALUE).toString());
    }

    private static void configurePhase3(LocalSearchPhaseConfig phase, SwapMoveSelectorConfig swapMoveSelectorConfig, ChangeMoveSelectorConfig changeMoveSelectorConfig) {
        swapMoveSelectorConfig.getFilterClassList().add(OptimizeTimeSwapMoveFilter.class);
        changeMoveSelectorConfig.getFilterClassList().add(OptimizeTimeChangeMoveFilter.class);
        phase.setTerminationConfig(new TerminationConfig());
        phase.getTerminationConfig().setUnimprovedSecondsSpentLimit(60L);
    }

    private static void makePhase(LocalSearchPhaseConfig phase, TriConsumer<LocalSearchPhaseConfig, SwapMoveSelectorConfig, ChangeMoveSelectorConfig> config) {
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
        config.accept(phase, swapMoveSelectorConfig, changeMoveSelectorConfig);
        phase.setMoveSelectorConfig(unionMoveSelectorConfig);
    }

}
