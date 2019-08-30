package elec332.kmaplanner.planner.opta.assignment;

import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.Assignment;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 28-8-2019
 */
public interface IInitialEventAssigner<T> {

    public T assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, T data, ProjectSettings settings);

    public T createInitialData(List<Person> persons, Set<Event> events, ProjectSettings settings);

}
