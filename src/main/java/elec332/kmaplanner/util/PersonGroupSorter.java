package elec332.kmaplanner.util;

import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 28-8-2019
 */
public class PersonGroupSorter {

    public static Set<Person> sortByGroups(Collection<Person> persons, GroupManager groupManager) {
        return groupManager.getGroups().stream()
                .filter(Group::isMainGroup)
                .sorted()
                .flatMap(group -> persons.stream()
                        .filter(group::containsPerson)
                        .sorted())
                .collect(Collectors.toSet());
    }

    public static Map<Group, Set<Person>> sortByGroup(Collection<Person> persons, GroupManager groupManager) {
        return groupManager.getGroups().stream()
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
