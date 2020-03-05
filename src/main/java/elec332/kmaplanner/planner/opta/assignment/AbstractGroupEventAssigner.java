package elec332.kmaplanner.planner.opta.assignment;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Assignment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 30-8-2019
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractGroupEventAssigner<T> implements IInitialEventAssigner<AbstractGroupEventAssigner.Data<T>> {

    public AbstractGroupEventAssigner(IInitialEventAssigner<T> def) {
        defaultAssigner = def;
    }

    private final IInitialEventAssigner<T> defaultAssigner;

    @Override
    public Data<T> assignPersonsTo(List<Assignment> assignments, Event event, List<Person> persons, Data<T> data, Planner planner) {
        if (event.requiredPersons > planner.getSettings().mainGroupFactor * 8) {
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
        if (data.time == null) {
            data.time = Maps.newHashMap();
            planner.getGroupManager().getMainGroups().forEach(group -> data.time.put(group, 0d));
        }
        int groupsInit = Math.max((int) Math.floor(event.requiredPersons / (planner.getSettings().mainGroupFactor * 2.5f)), 1) - 1;
        boolean retry = false;
        int groups = groupsInit;
        Set<Group> mainGroups = planner.getGroupManager().getMainGroups().stream()
                .filter(event::canGroupParticipate)
                .filter(g -> g.canParticipateIn(event))
                .filter(g -> {
                    int s = g.getGroupSize();
                    int al = 0;
                    for (Iterator<Person> it = g.getPersonIterator(); it.hasNext();) {
                        Person p = it.next();
                        if (p.canParticipateIn(event)) {
                            al++;
                        }
                    }
                    return al > (s / 2);
                })

                .collect(Collectors.toSet());
        Set<Group> allowed = Sets.newHashSet();
        while (groups > allowed.size() || groups <= 0) {
            groups++;
            float ppg = (float) event.requiredPersons / groups;
            allowed = mainGroups.stream().filter(g -> g.getGroupSize() / 1.8f >= ppg).collect(Collectors.toSet());
            allowed.removeAll(data.groups);
            if (ppg < planner.getSettings().mainGroupFactor * 1.75f && groups > 1) {
                if (!retry) {
                    retry = true;
                    allowed = Sets.newHashSet();
                    groups = groupsInit;
                    data.groups.clear();
                } else {
                    System.out.println("Failoe: " + event);
                    data.t = defaultAssigner.assignPersonsTo(assignments, event, persons, data.t, planner);
                    return;
                }
            }
        }
        List<Group> used = allowed.stream()
                //.sorted(Comparator.comparingInt(Group::getGroupSize))
                .sorted(Comparator.comparingDouble(group -> data.time.get(group)))
                .collect(Collectors.toList())
                .subList(0, groups);
        used.forEach(group -> data.time.put(group, data.time.get(group) + ((float) event.requiredPersons / used.size()) * event.getDuration()));
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
        private Map<Group, Double> time;

    }

}
