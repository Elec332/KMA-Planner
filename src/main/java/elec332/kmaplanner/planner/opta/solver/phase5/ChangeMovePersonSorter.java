package elec332.kmaplanner.planner.opta.solver.phase5;

import elec332.kmaplanner.persons.Person;

import java.util.Comparator;

/**
 * Created by Elec332 on 30-8-2019
 */
public class ChangeMovePersonSorter implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        return (int) (o2.getPlannerData().getDuration() - o1.getPlannerData().getDuration());
    }

}
