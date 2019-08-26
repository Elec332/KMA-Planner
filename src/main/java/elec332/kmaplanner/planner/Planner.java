package elec332.kmaplanner.planner;

import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.util.UpdatableTreeSet;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.util.Date;
import java.util.Random;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Planner {

    public Planner(PersonManager personManager, GroupManager groupManager, UpdatableTreeSet<Event> events, long seed) {
        this.personManager = personManager;
        this.groupManager = groupManager;
        this.events = events;
        this.seed = seed;
    }

    private final PersonManager personManager;
    private final GroupManager groupManager;
    private final UpdatableTreeSet<Event> events;
    private final long seed;

    public void initialize() {
    }

    public PersonManager getPersonManager() {
        return personManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public UpdatableTreeSet<Event> getEvents() {
        return events;
    }

    @SuppressWarnings("unused")
    public Date getFirstDate(){
        if (getEvents().isEmpty()){
            return new Date();
        }
        return (Date) getEvents().first().start.clone();
    }

    public Date getLastDate(){
        if (getEvents().isEmpty()){
            return new Date();
        }
        return (Date) getEvents().last().end.clone();
    }

    public void plan(){
        initialize();
        getPersonManager().getPersons().forEach(p -> p.events.clear());
        plan_();
    }

    private void plan_(){
        if (getPersonManager().getPersons().isEmpty() || getEvents().isEmpty()){
            return;
        }
        Roster roster = new Roster(this);
        SolverFactory<Roster> factory = SolverFactory.createFromXmlResource("config.xml");
        factory.getSolverConfig().getTerminationConfig().setUnimprovedSecondsSpentLimit(20L);
        factory.getSolverConfig().setRandomSeed(seed);
        Solver<Roster> solver = factory.buildSolver();
        Roster solution = solver.solve(roster);
        solution.print();
    }

}
