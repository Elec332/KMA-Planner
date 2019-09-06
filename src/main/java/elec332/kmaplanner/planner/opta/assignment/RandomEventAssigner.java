package elec332.kmaplanner.planner.opta.assignment;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Assignment;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Elec332 on 28-8-2019
 * <p>
 * Dunno why you would use this...
 */
public class RandomEventAssigner implements IInitialEventAssigner<Random> {

    @Override
    public Random assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, Random data, Planner planner) {
        for (Assignment assignment : assignments) {
            assignment.person = persons.get(data.nextInt(persons.size()));
        }
        return data;
    }

    @Override
    public Random createInitialData(List<Person> persons, Set<Event> events, Planner planner) {
        return new Random(planner.getSettings().seed);
    }

}
