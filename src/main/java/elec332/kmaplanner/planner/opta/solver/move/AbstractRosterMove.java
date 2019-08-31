package elec332.kmaplanner.planner.opta.solver.move;

import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Elec332 on 31-8-2019
 */
public abstract class AbstractRosterMove extends AbstractMove<Roster> {

    @Override
    public abstract AbstractRosterMove createUndoMove(ScoreDirector<Roster> scoreDirector);

    @Override
    public abstract void doMoveOnGenuineVariables(ScoreDirector<Roster> scoreDirector);

    @Override
    public abstract AbstractRosterMove rebase(ScoreDirector<Roster> destinationScoreDirector);

    @Override
    public abstract Collection<?> getPlanningEntities();

    @Override
    public abstract Collection<?> getPlanningValues();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();


}
