package elec332.kmaplanner.planner.opta.solver.move;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Elec332 on 30-8-2019
 */
public class RosterChangeMove extends AbstractRosterMove {

    public RosterChangeMove(Assignment assignment, Person person) {
        this.assignment = assignment;
        this.person = person;
    }

    private Assignment assignment;
    private Person person;

    @Override
    public AbstractRosterMove createUndoMove(ScoreDirector<Roster> scoreDirector) {
        return new RosterChangeMove(assignment, assignment.getPerson());
    }

    @Override
    public void doMoveOnGenuineVariables(ScoreDirector<Roster> scoreDirector) {
        RosterMoveHelper.movePerson(scoreDirector, assignment, person);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Roster> scoreDirector) {
        return !assignment.person.equals(person);
    }

    @Override
    public AbstractRosterMove rebase(ScoreDirector<Roster> destinationScoreDirector) {
        return new RosterChangeMove(destinationScoreDirector.lookUpWorkingObject(assignment), destinationScoreDirector.lookUpWorkingObject(person));
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Collections.singletonList(assignment);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Collections.singletonList(person);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof RosterChangeMove && ((RosterChangeMove) obj).person.equals(person) && ((RosterChangeMove) obj).assignment.equals(assignment));
    }

    @Override
    public int hashCode() {
        return person.hashCode() + assignment.hashCode() * 31;
    }

}
