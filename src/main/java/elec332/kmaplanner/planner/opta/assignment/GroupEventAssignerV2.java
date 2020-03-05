package elec332.kmaplanner.planner.opta.assignment;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Assignment;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Elec332 on 28-8-2019
 * <p>
 * Warning, very shite...
 */
public class GroupEventAssignerV2<T> extends AbstractGroupEventAssigner<T> {

    public GroupEventAssignerV2(IInitialEventAssigner<T> def) {
        super(def);
    }

    @Override
    protected void assignPersons(List<Assignment> assignments, Event event, List<Person> persons, Data<T> data, Planner planner, List<Group> groups) {
        List<Group> groupFilter = Lists.newArrayList(groups);
        System.out.println(event + "  " + groups);
        Random rand = new Random(planner.getSettings().seed);
        int toDo = event.requiredPersons;
        Iterator<Assignment> it = assignments.iterator();
        int persPerGroup = event.requiredPersons / groups.size();
        Iterator<Group> gIt = groups.iterator();
        while (gIt.hasNext()) {
            Group group = gIt.next();
            Set<Person> check = Sets.newHashSet();
            boolean last = !gIt.hasNext();
            for (int j = 0; j < (last ? toDo : persPerGroup); j++) {
                Person person = null;
                while (person == null) {
                    person = group.getRandomPerson(rand);
                    if (event.isDuring(person.getPrintableEvents()) && rand.nextDouble() > 0.01) {
                        person = null;
                        continue;
                    }
                    if ((!event.canPersonParticipate(person) || !person.canParticipateIn(event)) && rand.nextDouble() > 0.05) {
                        person = null;
                        continue;
                    }
                    if (!group.containsPerson(person) || (event.isDuring(person.getPrintableEvents()) && rand.nextDouble() > 0.2)) {
                        person = null;
                        continue;
                    }
                    if (!check.add(person)) {
                        person = null;
                    }
                }
                Assignment assignment = it.next();
                assignment.person = person;
                assignment.person.getPrintableEvents().add(event);
                assignment.groupFilter = groupFilter::contains; //groupFilter.size() <= 2 ? groupFilter::contains : null;
                if (!last) {
                    toDo--;
                }
            }
        }
        if (it.hasNext()) {
            throw new RuntimeException(event.requiredPersons + " " + groups + " " + persPerGroup + " " + toDo);
        }
    }

}
