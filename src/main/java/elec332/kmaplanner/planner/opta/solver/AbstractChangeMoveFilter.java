package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Created by Elec332 on 30-8-2019
 */
public abstract class AbstractChangeMoveFilter implements SelectionFilter<Roster, ChangeMove<Roster>> {

    @Override
    public boolean accept(ScoreDirector<Roster> scoreDirector, ChangeMove<Roster> selection) {
        Assignment a = (Assignment) selection.getEntity();
        Person p = (Person) selection.getToPlanningValue();
        if (p == PersonManager.NULL_PERSON) {
            return false;
        }
        Roster roster = scoreDirector.getWorkingSolution();
        return accept(roster, a, p);
    }

    protected abstract boolean accept(Roster roster, Assignment entity, Person value);

}
