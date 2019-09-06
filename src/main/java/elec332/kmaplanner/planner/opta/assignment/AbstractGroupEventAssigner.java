package elec332.kmaplanner.planner.opta.assignment;

import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Assignment;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 30-8-2019
 */
public abstract class AbstractGroupEventAssigner<T> implements IInitialEventAssigner<AbstractGroupEventAssigner.Data<T>> {

    public AbstractGroupEventAssigner(IInitialEventAssigner<T> def) {
        defaultAssigner = def;
    }

    private final IInitialEventAssigner<T> defaultAssigner;

    @Override
    public Data<T> assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, Data<T> data, Planner planner) {
        if (event.requiredPersons > planner.getSettings().mainGroupFactor * 10) {
            data.t = defaultAssigner.assignPersonsTo(assignments, event, persons, data.t, planner);
            return data;
        } else {
            assignPersons(assignments, event, persons, data, planner);
            return data;
        }
    }

    protected void assignPersons(List<Assignment> assignments, Event event, List<Person> persons, Data<T> data, Planner planner) {
        if (event.requiredPersons != assignments.size()) {
            throw new IllegalArgumentException();
        }
        int groupsInit = Math.max((int) Math.floor(event.requiredPersons / (planner.getSettings().mainGroupFactor * 2.5f)), 1) - 1;
        int groups = groupsInit;
        Set<Group> mainGroups = planner.getGroupManager().getMainGroups().stream()
                .filter(event::canGroupParticipate)
                .collect(Collectors.toSet());
        Set<Group> allowed = Sets.newHashSet();
        while (groups > allowed.size() || groups <= 0) {
            groups++;
            float ppg = (float) event.requiredPersons / groups;
            allowed = mainGroups.stream().filter(g -> g.getGroupSize() / 1.8f >= ppg).collect(Collectors.toSet());
            allowed.removeAll(data.groups);
            if (ppg < planner.getSettings().mainGroupFactor * 1.5f) {
                if (groupsInit >= 0) {
                    groups = groupsInit;
                    groupsInit = -1;
                    allowed = Sets.newHashSet();
                    data.groups.clear();
                } else {
                    data.t = defaultAssigner.assignPersonsTo(assignments, event, persons, data.t, planner);
                    return;
                }
            }
        }
        List<Group> used = allowed.stream().sorted(Comparator.comparingInt(Group::getGroupSize)).collect(Collectors.toList());
        System.out.println("groups: " + groups);
        used = used.subList(0, groups);
        data.groups.addAll(used);
        assignPersons(assignments, event, persons, data, planner, used);
    }

    protected abstract void assignPersons(List<Assignment> assignments, Event event, List<Person> persons, Data<T> data, Planner planner, List<Group> groups);

    @Override
    public Data<T> createInitialData(List<Person> persons, Set<Event> events, Planner planner) {
        return new Data<>(defaultAssigner.createInitialData(persons, events, planner));
    }

    static class Data<T> {

        private Data(T initial) {
            t = initial;
            groups = Sets.newHashSet();
        }

        private T t;
        private Set<Group> groups;

    }

}
