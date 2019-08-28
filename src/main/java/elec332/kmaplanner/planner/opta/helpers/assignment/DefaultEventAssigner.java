package elec332.kmaplanner.planner.opta.helpers.assignment;

import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.helpers.IInitialEventAssigner;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 28-8-2019
 */
public class DefaultEventAssigner implements IInitialEventAssigner<Integer> {

    @Override
    public Integer assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, Integer data, ProjectSettings settings) {
        int i = data;
        for (Assignment assignment : assignments){
            assignment.person = persons.get(i);
            i++;
            if (i >= persons.size()){
                i = 0;
            }
        }
        return i;
    }

    @Override
    public Integer createInitialData(List<Person> persons, Set<Event> events, ProjectSettings settings) {
        return 0;
    }

}
