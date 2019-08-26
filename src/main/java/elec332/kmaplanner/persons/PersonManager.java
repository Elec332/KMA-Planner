package elec332.kmaplanner.persons;

import com.google.common.collect.Sets;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.io.ProjectReader;
import elec332.kmaplanner.planner.Event;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 14-6-2019
 */
public final class PersonManager {

    public PersonManager(GroupManager groupManager) {
        this.persons = Sets.newTreeSet();
        this.persons_ = Collections.unmodifiableSet(persons);
        this.groupManager = groupManager;
    }

    public static final Person NULL_PERSON = new Person("No", "Person") {

        @Override
        public boolean canParticipateIn(Event event) {
            return true;
        }

    };
    private final GroupManager groupManager;
    private final Set<Person> persons, persons_;

    public void load(ProjectReader projectReader) {
        projectReader.getPersons().forEach(this::addPerson);
        persons_.forEach(p -> p.postRead(groupManager));
    }

    public void updatePerson(Person person, Consumer<Person> consumer) {
        if (persons.remove(person)) {
            consumer.accept(person);
            persons.add(person);
        }
    }

    public void addPerson(Person person) {
        if (!addPersonNice(person)) {
            System.out.println("waa");
            System.out.println(persons.size());
            System.out.println(persons);
            System.out.println(person);
            throw new IllegalArgumentException();
        }
    }

    public boolean addPersonNice(Person person) {
        return persons.add(person);
    }

    public void removePerson(Person person) {
        if (!persons.contains(person)) {
            throw new IllegalArgumentException();
        }
        persons.remove(person);
        Sets.newHashSet(person.getGroups()).forEach(person::removeFromGroup);
    }

    @Nonnull
    public Set<Person> getPersons() {
        return this.persons_;
    }

}
