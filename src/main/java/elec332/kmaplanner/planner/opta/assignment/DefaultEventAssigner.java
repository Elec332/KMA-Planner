package elec332.kmaplanner.planner.opta.assignment;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Assignment;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 28-8-2019
 */
public class DefaultEventAssigner implements IInitialEventAssigner<Integer> {

    @Override
    public Integer assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, Integer data, Planner planner) {
        int i = data;
        for (Assignment assignment : assignments) {
            Person p = null;
            for (int j = 0; j < 6; j++) {
                p = persons.get(i);
                i++;
                if (i >= persons.size()) {
                    i = 0;
                }
                if (event.isDuring(p.getPrintableEvents())) {
                    continue;
                }
                break;
            }
            assignment.person = Preconditions.checkNotNull(p);
            assignment.person.getPrintableEvents().add(event);
        }
        return i;
    }

    @Override
    public Integer createInitialData(List<Person> persons, Set<Event> events, Planner planner) {
        return 0;
    }

}
