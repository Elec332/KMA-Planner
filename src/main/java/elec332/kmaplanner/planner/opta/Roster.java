package elec332.kmaplanner.planner.opta;

import com.google.common.collect.Lists;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Planner;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

/**
 * Created by Elec332 on 15-8-2019
 */
@PlanningSolution
@SuppressWarnings({"WeakerAccess", "unused"})
public class Roster {

    public Roster(Planner planner){
        this();
        this.planner = planner;
        persons.addAll(this.planner.getPersonManager().getPersons());
        planner.getEvents().stream().filter(e -> !e.everyone).forEach(event -> {
            for (int i = 0; i < event.requiredPersons; i++) {
                assignments.add(new Assignment(event));
            }
        });
    }

    public Roster(){
        this.assignments = Lists.newArrayList();
        this.persons = Lists.newArrayList();
    }

    protected Planner planner;

    private List<Person> persons;
    private List<Assignment> assignments;
    private HardSoftScore score;

    public Planner getPlanner() {
        return planner;
    }

    @ValueRangeProvider(id = "persons")
    @ProblemFactCollectionProperty
    public List<Person> getPersons() {
        return persons;
    }

    @PlanningEntityCollectionProperty
    public List<Assignment> getAssignments(){
        return assignments;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    public void print(){
        getPlanner().getPersonManager().getPersons().forEach(p -> p.events.clear());
        for (Assignment assignment : getAssignments()){
            assignment.person.events.add(assignment.event);
        }
        planner.getPersonManager().getPersons().forEach(p -> {
            System.out.println();
            System.out.println(p);
            p.events.forEach(System.out::println);
            System.out.println();
        });
        System.out.println();
        System.out.println(getScore());
        System.out.println();
        getAssignments().forEach(System.out::println);
    }

}
