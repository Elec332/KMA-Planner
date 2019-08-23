package elec332.kmaplanner.persons;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.IEventFilter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Person implements Serializable, Comparable, IEventFilter {

    public Person(String fn, String ln) {
        this.firstName = Preconditions.checkNotNull(fn);
        this.lastName = Preconditions.checkNotNull(ln);
        this.groups = Sets.newHashSet();
        this.events = Sets.newTreeSet();
    }

    private String firstName, lastName;
    private Set<Group> groups;
    public transient Set<Event> events;

    public long getDuration(){
        return events.stream().mapToLong(Event::getDuration).sum();
    }

    public void setName(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person addToGroup(Group group) {
        Preconditions.checkNotNull(group);
        if (groups.contains(group)) {
            return this;
        }
        groups.add(group);
        groupInjector.accept(group, this);
        return this;
    }

    public void removeFromGroup(Group group) {
        if (!groups.contains(group)) {
            return;
        }
        groups.remove(group);
        groupRemover.accept(group, this);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    @Override
    public boolean canParticipateIn(Event event){
        return getGroups().stream().allMatch(g -> g.canParticipateIn(event));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Person && firstName.equals(((Person) obj).firstName) && lastName.equals(((Person) obj).lastName);
    }

    void postRead(GroupManager groupManager){
        this.events = Sets.newTreeSet();
        Set<Group> groupz = Sets.newHashSet(groups);
        groups.clear();
        groupz.forEach(g -> addToGroup(groupManager.getGroup(g.getName())));
    }

    /*@Override
    public int hashCode() {
        return firstName.hashCode() + 31 * lastName.hashCode() + 31 * groups.hashCode() + 7 * events.hashCode();
    }*/

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public int compareTo(@Nonnull Object o) {
        if (o instanceof Person) {
            return toString().compareTo(o.toString());
        }
        return -1;
    }

    public static BiConsumer<Group, Person> groupInjector, groupRemover;

}
