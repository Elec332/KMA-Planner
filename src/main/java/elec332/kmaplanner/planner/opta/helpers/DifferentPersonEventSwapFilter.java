package elec332.kmaplanner.planner.opta.helpers;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Created by Elec332 on 29-8-2019
 */
public class DifferentPersonEventSwapFilter implements SelectionFilter<Roster, SwapMove<Roster>> {

    @Override
    public boolean accept(ScoreDirector<Roster> scoreDirector, SwapMove<Roster> selection) {
        Assignment a1 = (Assignment) selection.getLeftEntity();
        Assignment a2 = (Assignment) selection.getRightEntity();
        if (a1.person.getPlannerEvents().contains(a2.event) || a2.person.getPlannerEvents().contains(a1.event)) {
            return false;
        }
        return !a1.person.equals(a2.person) && !a1.event.equals(a2.event);
    }

}
