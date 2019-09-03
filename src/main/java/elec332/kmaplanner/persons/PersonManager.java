package elec332.kmaplanner.persons;

import com.google.common.collect.Sets;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.project.ProjectManager;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 14-6-2019
 */
public final class PersonManager {

    public PersonManager(GroupManager groupManager) {
        this.persons = Sets.newTreeSet();
        this.persons_ = Collections.unmodifiableSet(persons);
        this.groupManager = groupManager;
        this.callbacks = Sets.newHashSet();
    }

    public static final Person NULL_PERSON = new Person("No", "Person") {

        @Override
        public boolean canParticipateIn(Event event) {
            return true;
        }

    };
    private final GroupManager groupManager;
    private final Set<Person> persons, persons_;
    private final Set<Runnable> callbacks;

    public void load(ProjectManager.LoadData projectReader) {
        projectReader.getPersons().forEach(this::addPerson);
        persons_.forEach(p -> p.postRead(groupManager));
    }

    public boolean addCallback(Runnable runnable) {
        return callbacks.add(runnable);
    }

    private void runCallbacks() {
        callbacks.forEach(Runnable::run);
    }

    public void updatePerson(Person person, Consumer<Person> consumer) {
        if (persons.remove(person)) {
            consumer.accept(person);
            persons.add(person);
        }
        runCallbacks();
    }

    public void addPerson(Person person) {
        if (!addPersonNice(person)) {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean addPersonNice(Person person) {
        boolean ret = persons.add(person);
        runCallbacks();
        return ret;
    }

    public void removePerson(Person person) {
        if (!persons.contains(person)) {
            throw new IllegalArgumentException();
        }
        persons.remove(person);
        Sets.newHashSet(person.getGroups()).forEach(person::removeFromGroup);
        runCallbacks();
    }

    @Nonnull
    public Set<Person> getPersons() {
        return this.persons_;
    }

    public Map<String, Person> makeNameMap() {
        return getPersons().stream().collect(Collectors.toMap(Person::toString, Function.identity()));
    }

}
