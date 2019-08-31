package elec332.kmaplanner.planner.opta.solver.move;

import com.google.common.collect.Lists;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Collection;

/**
 * Created by Elec332 on 31-8-2019
 */
public class RosterSwapMove extends AbstractRosterMove {

    public RosterSwapMove(Assignment a1, Assignment a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    private Assignment a1, a2;

    @Override
    public AbstractRosterMove createUndoMove(ScoreDirector<Roster> scoreDirector) {
        return new RosterSwapMove(a2, a1);
    }

    @Override
    public AbstractRosterMove rebase(ScoreDirector<Roster> destinationScoreDirector) {
        return new RosterSwapMove(destinationScoreDirector.lookUpWorkingObject(a1), destinationScoreDirector.lookUpWorkingObject(a2));
    }

    @Override
    public void doMoveOnGenuineVariables(ScoreDirector<Roster> scoreDirector) {
        Person p1 = a1.person;
        Person p2 = a2.person;
        RosterMoveHelper.movePerson(scoreDirector, a1, p2);
        RosterMoveHelper.movePerson(scoreDirector, a2, p1);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Roster> scoreDirector) {
        return !a1.person.equals(a2.person);
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Lists.newArrayList(a1, a2);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Lists.newArrayList(a1.person, a2.person);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof RosterSwapMove && ((RosterSwapMove) obj).a1.equals(a1) && ((RosterSwapMove) obj).a2.equals(a2));
    }

    @Override
    public int hashCode() {
        return a1.hashCode() + a2.hashCode() * 31;
    }

}
