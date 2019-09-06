package elec332.kmaplanner.util;

import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Roster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 28-8-2019
 */
@SuppressWarnings("unused")
public class PersonGroupHelper {

    public static long getAverageSoftTime(Collection<Person> people, Roster roster) {
        long l = people.stream()
                .mapToLong(p -> p.getPlannerData().getSoftDuration(roster))
                .sum();
        return l / people.size();
    }

    public static Set<Person> sortByGroups(Collection<Person> persons, GroupManager groupManager) {
        return groupManager.stream()
                .filter(Group::isMainGroup)
                .sorted()
                .flatMap(group -> persons.stream()
                        .filter(group::containsPerson)
                        .sorted())
                .collect(Collectors.toSet());
    }

    public static Map<Group, Set<Person>> sortByGroup(Collection<Person> persons, GroupManager groupManager) {
        return groupManager.stream()
                .filter(Group::isMainGroup)
                .filter(g -> persons.stream().anyMatch(g::containsPerson))
                .sorted()
                .collect(Collectors.toMap(g -> g, g -> persons.stream()
                        .filter(g::containsPerson)
                        .sorted()
                        .collect(Collectors.toCollection(TreeSet::new)), (a, b) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", a));
                }, TreeMap::new));
    }

}
