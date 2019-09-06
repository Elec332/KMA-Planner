package elec332.kmaplanner.persons;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.filters.FilterManager;
import elec332.kmaplanner.filters.IFilterable;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.planner.IEventFilter;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Person implements Comparable, IEventFilter, IFilterable, IDataSerializable {

    public Person(String fn, String ln) {
        this.firstName = Preconditions.checkNotNull(fn);
        this.lastName = Preconditions.checkNotNull(ln);
        this.groups = Sets.newHashSet();
        this.events = Sets.newTreeSet();
        this.filters = Sets.newHashSet();
        this.filters_ = Collections.unmodifiableSet(filters);
        this.plannerData = new ThreadLocal<>();
    }

    private String firstName, lastName;
    private Set<Group> groups;
    private Set<AbstractFilter> filters, filters_;
    private transient ThreadLocal<PersonPlanningData> plannerData;
    private transient Set<Event> events;

    public synchronized PersonPlanningData getPlannerData() {
        PersonPlanningData ret = plannerData.get();
        if (ret == null) {
            plannerData.set(ret = new PersonPlanningData(this));
        }
        return ret;
    }

    public void clearEvents() {
        events.clear();
        plannerData = new ThreadLocal<>();
    }

    //Not threadsafe version for printing stuff after the calculations are done
    public Set<Event> getPrintableEvents() {
        return events;
    }

    public long getDuration() {
        return getDuration(false);
    }

    public long getDuration(boolean publicE) {
        return getDuration(events, publicE);
    }

    long getDuration(Set<Event> events, final boolean publicE) {
        return events.stream()
                .filter(e -> publicE || !e.everyone)
                .mapToLong(Event::getDuration)
                .sum();
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person addToGroup(Group group) {
        Preconditions.checkNotNull(group);
        if (group.isMainGroup() && groups.stream().anyMatch(Group::isMainGroup)) {
            return this; //Nope
        }
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

    @SuppressWarnings("WeakerAccess")
    public Group getMainGroup() {
        return getGroups().stream()
                .filter(Group::isMainGroup)
                .findFirst()
                .orElseThrow(() -> new NullPointerException(toString()));
    }

    @Override
    public boolean canParticipateIn(final Event event) {
        return getGroups().stream().allMatch(g -> g.canParticipateIn(event)) && getFilters().stream().allMatch(f -> f.canParticipateIn(event));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Person && firstName.equals(((Person) obj).firstName) && lastName.equals(((Person) obj).lastName);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    void postRead(GroupManager groupManager) {
        this.events = Sets.newTreeSet();
        Set<Group> groupz = Sets.newHashSet(groups);
        groups.clear();
        groupz.forEach(g -> addToGroup(groupManager.getGroup(g.getName())));
    }

    /*@Override
    public int hashCode() {
        return firstName.hashCode() + 31 * lastName.hashCode() + 31 * groups.hashCode() + 7 * events.hashCode();
    }*/

    @PlanningId
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

    @Override
    public Set<AbstractFilter> getFilters() {
        return filters_;
    }

    public Set<AbstractFilter> getModifiableFilters() {
        return filters;
    }

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeUTF(firstName);
        stream.writeUTF(lastName);
        stream.writeObjects(groups);
        stream.writeObjects(FilterManager.INSTANCE, filters);
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        firstName = stream.readUTF();
        lastName = stream.readUTF();
        groups = Sets.newHashSet(stream.readObjects(() -> new Group("")));
        filters = Sets.newHashSet(stream.readObjects(FilterManager.INSTANCE));
    }

}
