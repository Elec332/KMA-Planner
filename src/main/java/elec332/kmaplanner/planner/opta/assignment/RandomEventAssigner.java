package elec332.kmaplanner.planner.opta.assignment;

import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
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
    public Random assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, Random data, ProjectSettings settings) {
        for (Assignment assignment : assignments) {
            assignment.person = persons.get(data.nextInt(persons.size()));
        }
        return data;
    }

    @Override
    public Random createInitialData(List<Person> persons, Set<Event> events, ProjectSettings settings) {
        return new Random(settings.seed);
    }

}
