package elec332.kmaplanner.planner.opta;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.util.AssignmentDifficultyComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 15-8-2019
 */
@PlanningEntity(difficultyComparatorClass = AssignmentDifficultyComparator.class)
@SuppressWarnings("unused")
public class Assignment {

    private Assignment() {
    }

    public Assignment(Event event) {
        this.event = event;
        this.person = PersonManager.NULL_PERSON;
        this.identifier = UUID.randomUUID();
        this.groupFilter = event::canGroupParticipate;
    }

    public Event event;
    @PlanningVariable(valueRangeProviderRefs = {"persons"})
    public Person person;

    public Predicate<Group> groupFilter;

    @PlanningId
    @SuppressWarnings("FieldCanBeLocal")
    private UUID identifier;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidGroup(Group group) {
        return groupFilter == null || groupFilter.test(group);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Assignment && ((Assignment) obj).event.equals(event) && ((Assignment) obj).person.equals(person);
    }

    @Override
    public String toString() {
        return " Event:  " + event + "    Person: " + person;
    }

    @Override
    public int hashCode() {
        return event.hashCode() + person.hashCode() * 31;
    }

}
