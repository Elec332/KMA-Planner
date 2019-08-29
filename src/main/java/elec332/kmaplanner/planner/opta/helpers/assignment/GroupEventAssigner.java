package elec332.kmaplanner.planner.opta.helpers.assignment;

import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.helpers.IInitialEventAssigner;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Elec332 on 28-8-2019
 * <p>
 * Warning, very shite...
 */
public class GroupEventAssigner<T> implements IInitialEventAssigner<T> {

    public GroupEventAssigner(IInitialEventAssigner<T> def) {
        defaultAssigner = def;
    }

    private final IInitialEventAssigner<T> defaultAssigner;

    @Override
    public T assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, T data, ProjectSettings settings) {
        if (event.requiredPersons > settings.mainGroupFactor * 10) {
            return defaultAssigner.assignPersonsTo(assignments, event, persons, data, settings);
        } else {
            int groups = Math.max((int) Math.floor(event.requiredPersons / (settings.mainGroupFactor * 2.5f)), 1);
            int toDo = event.requiredPersons;
            Random rand = new Random((settings.seed / toDo) * groups);
            if (event.requiredPersons != assignments.size()) {
                throw new IllegalArgumentException();
            }
            Iterator<Assignment> it = assignments.iterator();
            int persPerGroup = event.requiredPersons / groups;
            for (int i = 0; i < groups; i++) {
                boolean last = i == (groups - 1);
                Group group = persons.get(rand.nextInt(persons.size())).getGroups().stream()
                        .filter(Group::isMainGroup)
                        .findFirst()
                        .orElseThrow(NullPointerException::new);
                for (int j = 0; j < (last ? toDo : persPerGroup); j++) {
                    Person person = null;
                    while (person == null) {
                        person = persons.get(rand.nextInt(persons.size()));
                        if (!group.containsPerson(person)) {
                            person = null;
                        }
                    }
                    it.next().person = person;
                    if (!last) {
                        toDo--;
                    }
                }
            }
            if (it.hasNext()) {
                throw new RuntimeException(event.requiredPersons + " " + groups + " " + persPerGroup + " " + toDo);
            }
            return data;
        }
    }

    @Override
    public T createInitialData(List<Person> persons, Set<Event> events, ProjectSettings settings) {
        return defaultAssigner.createInitialData(persons, events, settings);
    }

}
