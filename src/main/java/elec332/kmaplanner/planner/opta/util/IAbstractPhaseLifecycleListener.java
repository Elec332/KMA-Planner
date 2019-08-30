package elec332.kmaplanner.planner.opta.util;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Created by Elec332 on 30-8-2019
 */
public interface IAbstractPhaseLifecycleListener<T> extends PhaseLifecycleListener<T> {

    @Override
    default void phaseStarted(AbstractPhaseScope<T> phaseScope) {
    }

    @Override
    default void stepStarted(AbstractStepScope<T> stepScope) {
    }

    @Override
    default void stepEnded(AbstractStepScope<T> stepScope) {
    }

    @Override
    default void phaseEnded(AbstractPhaseScope<T> phaseScope) {
    }

    @Override
    default void solvingStarted(DefaultSolverScope<T> solverScope) {
    }

    @Override
    default void solvingEnded(DefaultSolverScope<T> solverScope) {
    }

}
