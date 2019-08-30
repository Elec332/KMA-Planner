package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Created by Elec332 on 30-8-2019
 */
public abstract class AbstractSwapMoveFilter implements SelectionFilter<Roster, SwapMove<Roster>> {

    @Override
    public boolean accept(ScoreDirector<Roster> scoreDirector, SwapMove<Roster> selection) {
        Assignment a1 = (Assignment) selection.getLeftEntity();
        Assignment a2 = (Assignment) selection.getRightEntity();
        Roster roster = scoreDirector.getWorkingSolution();
        return accept(roster, a1, a2);
    }

    protected abstract boolean accept(Roster roster, Assignment a1, Assignment a2);

}
