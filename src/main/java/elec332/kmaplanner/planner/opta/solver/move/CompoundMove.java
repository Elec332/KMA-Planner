package elec332.kmaplanner.planner.opta.solver.move;

import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 31-8-2019
 */
public class CompoundMove extends AbstractRosterMove {

    public CompoundMove(List<AbstractRosterMove> moves) {
        this.moves = moves;
    }

    private List<AbstractRosterMove> moves;

    @Override
    public AbstractRosterMove createUndoMove(ScoreDirector<Roster> scoreDirector) {
        return new CompoundMove(moves.stream().map(m -> m.createUndoMove(scoreDirector)).collect(Collectors.toList()));
    }

    @Override
    public void doMoveOnGenuineVariables(ScoreDirector<Roster> scoreDirector) {
        moves.forEach(m -> m.doMoveOnGenuineVariables(scoreDirector));
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Roster> scoreDirector) {
        return moves.stream().allMatch(m -> m.isMoveDoable(scoreDirector));
    }

    @Override
    public AbstractRosterMove rebase(ScoreDirector<Roster> destinationScoreDirector) {
        return new CompoundMove(moves.stream().map(m -> m.rebase(destinationScoreDirector)).collect(Collectors.toList()));
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return moves.stream().flatMap(m -> m.getPlanningEntities().stream()).collect(Collectors.toList());
    }

    @Override
    public Collection<?> getPlanningValues() {
        return moves.stream().flatMap(m -> m.getPlanningValues().stream()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof CompoundMove && ((CompoundMove) obj).moves.equals(moves));
    }

    @Override
    public int hashCode() {
        return moves.hashCode();
    }

}
