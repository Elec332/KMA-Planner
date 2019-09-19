package elec332.kmaplanner.planner.opta.solver.phase4;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import elec332.kmaplanner.planner.opta.solver.move.factory.AbstractTimeChangeMoveListFactory;

import java.util.Comparator;

/**
 * Created by Elec332 on 31-8-2019
 */
public class SmartTimeChangeMoveFactory extends AbstractTimeChangeMoveListFactory {

    @Override
    protected AbstractRosterMove createMove(Person p1, Person p2, Roster roster, long avgEventDuration) {
        long p1S = p1.getPlannerData().getSoftDuration(roster);
        long p2S = p2.getPlannerData().getSoftDuration(roster);
        if (Math.abs(p1S - p2S) < avgEventDuration / 2) {
            return null;
        }

        Event p2Min = p2.getPlannerData().getEvents().stream()
                .filter(e -> !p1.getPlannerData().getEvents().contains(e))
                .min(Comparator.comparingLong(Event::getDuration))
                .orElse(null);

        if (p2Min == null) {
            return null;
        }

        Assignment aM = roster.getAssignments().stream().filter(a -> a.person.equals(p2) && a.event.equals(p2Min)).findFirst().orElseThrow(NullPointerException::new);

        return new RosterChangeMove(aM, p1);
    }

}
