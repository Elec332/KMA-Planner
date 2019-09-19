package elec332.kmaplanner.planner.opta.solver.phase3;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterSwapMove;
import elec332.kmaplanner.planner.opta.solver.move.factory.AbstractTimeChangeMoveListFactory;

import java.util.Comparator;

/**
 * Created by Elec332 on 31-8-2019
 */
public class SmartTimeMoveListFactoryV2 extends AbstractTimeChangeMoveListFactory {

    @Override
    protected AbstractRosterMove createMove(Person p1, Person p2, Roster roster, long avgEventDuration) {
        long p1S = p1.getPlannerData().getSoftDuration(roster);
        long p2S = p2.getPlannerData().getSoftDuration(roster);
        if (Math.abs(p1S - p2S) < roster.getPlanner().getSettings().timeDiffThreshold / 2) {
            return null;
        }

        Event p1Min = p1.getPlannerData().getEvents().stream()
                .filter(e -> !p2.getPlannerData().getEvents().contains(e))
                .min(Comparator.comparingLong(Event::getDuration))
                .orElse(null);
        Event p2Max = p2.getPlannerData().getEvents().stream()
                .filter(e -> !p1.getPlannerData().getEvents().contains(e))
                .max(Comparator.comparingLong(Event::getDuration))
                .orElse(null);

        if (p1Min == null || p2Max == null) {
            return null;
        }

        Assignment p1A = roster.getAssignments().stream().filter(a -> a.person.equals(p1) && a.event.equals(p1Min)).findFirst().orElseThrow(NullPointerException::new);
        Assignment p2A = roster.getAssignments().stream().filter(a -> a.person.equals(p2) && a.event.equals(p2Max)).findFirst().orElseThrow(NullPointerException::new);

        return new RosterSwapMove(p1A, p2A);
    }

}
