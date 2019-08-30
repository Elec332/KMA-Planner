package elec332.kmaplanner.planner.opta.solver.phase2;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.AbstractChangeMoveFilter;

/**
 * Created by Elec332 on 30-8-2019
 */
public class OptimizeGroupChangeMoveFilter extends AbstractChangeMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment entity, Person value) {
        return value.getPlannerData().getMainGroup() != entity.person.getPlannerData().getMainGroup();
    }

}
