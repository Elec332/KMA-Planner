package elec332.kmaplanner.planner.opta.helpers;

import elec332.kmaplanner.persons.Person;

import java.util.Comparator;

/**
 * Created by Elec332 on 30-8-2019
 */
public class PersonSorter implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        return (int) (o1.getDuration() - o2.getDuration());
    }

}
