package elec332.kmaplanner.group;

import com.google.common.collect.Sets;
import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.filters.IFilterable;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.IEventFilter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Group implements Comparable<Group>, IEventFilter, Serializable, IFilterable {

    public static final long serialVersionUID = -3746563900204761046L;

    public Group(String name) {
        this.name = name;
        this.persons = Sets.newHashSet();
        this.persons_ = Collections.unmodifiableSet(persons);
        this.main = false;
        this.filters = Sets.newHashSet();
    }

    private String name;
    private boolean main;
    private Set<AbstractFilter> filters;
    //Higher weight is more important
    @SuppressWarnings("unused")
    private int weight;

    private transient Set<Person> persons, persons_;

    void postRead(){
        this.persons = Sets.newHashSet();
        this.persons_ = Collections.unmodifiableSet(persons);
    }

    @Override
    public boolean canParticipateIn(final Event event){
        return getFilters().stream().allMatch(f -> f.canParticipateIn(event));
    }

    public boolean containsPerson(Person person){
        return persons.contains(person);
    }

    public void setName(String name) {
        this.name = name;
    }

    Set<Person> getPersons() {
        return persons_;
    }

    public boolean isMainGroup(){
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public Iterator<Person> getPersonIterator(){
        return persons_.iterator();
    }

    @Override
    public int compareTo(@Nonnull Group o) {
        int ret = weight - o.weight;
        if (ret == 0) {
            return name.compareTo(o.name);
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Group && name.equals(((Group) obj).name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /*
    @Override
    public int hashCode() {
        return weight + 31 * name.hashCode();
    }*/

    static {
        Person.groupInjector = (group, person) -> {
            if (!group.persons.add(person)) {
                throw new IllegalArgumentException("Person \"" + person + "\" is already in group \"" + group + "\"");
            }
        };
        Person.groupRemover = (group, person) -> {
            if (!group.persons.remove(person)) {
                throw new IllegalArgumentException("Person \"" + person + "\"  is not in group \"" + group + "\"");
            }
        };
    }

    @Override
    public Set<AbstractFilter> getFilters() {
        return filters;
    }

}
