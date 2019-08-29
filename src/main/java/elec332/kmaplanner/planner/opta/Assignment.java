package elec332.kmaplanner.planner.opta;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.helpers.AssignmentDifficultyComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.UUID;

/**
 * Created by Elec332 on 15-8-2019
 */
@PlanningEntity(difficultyComparatorClass = AssignmentDifficultyComparator.class)
@SuppressWarnings({"WeakerAccess", "unused"})
public class Assignment {

    private Assignment() {
    }

    public Assignment(Event event) {
        this.event = event;
        this.person = PersonManager.NULL_PERSON;
        this.identifier = UUID.randomUUID();
    }

    public Event event;
    public Person person;

    @PlanningId
    @SuppressWarnings("FieldCanBeLocal")
    private UUID identifier;

    @PlanningVariable(valueRangeProviderRefs = {"persons"})
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Assignment && event == ((Assignment) obj).event && person == ((Assignment) obj).person;
    }

    @Override
    public String toString() {
        return " Event:  " + event + "    Person: " + person;
    }

}
