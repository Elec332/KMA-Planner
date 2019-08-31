package elec332.kmaplanner.planner.opta.solver.move;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Created by Elec332 on 30-8-2019
 */
public class RosterMoveHelper {

    public static void movePerson(ScoreDirector<Roster> scoreDirector, Assignment assignment, Person person) {
        scoreDirector.beforeVariableChanged(assignment, "person");
        assignment.setPerson(person);
        scoreDirector.afterVariableChanged(assignment, "person");
    }

}
