package elec332.kmaplanner.planner.opta.solver.phase3;

import com.google.common.collect.Lists;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.CompoundMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import elec332.kmaplanner.util.PersonGroupHelper;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 30-8-2019
 */
public class SmartTimeMoveListFactory implements MoveListFactory<Roster> {

    @Override
    public List<? extends Move<Roster>> createMoveList(Roster roster) {
        roster.plannerApply();
        Map<Group, Set<Person>> groups = PersonGroupHelper.sortByGroup(roster.getPlanner().getPersonManager().getObjects(), roster.getPlanner().getGroupManager());
        List<AbstractRosterMove> moves = Lists.newArrayList();
        List<Group> sortedGroup = groups.keySet().stream()
                .sorted(Comparator.comparingLong(g -> g.getAverageSoftTime(roster)))
                .collect(Collectors.toList());
        int siz = Math.floorDiv(sortedGroup.size(), 2);
        int gs = sortedGroup.size();
        List<Group> min = sortedGroup.subList(0, siz);
        List<Group> max = sortedGroup.subList(gs - siz, gs);
        max.sort(Comparator.comparingLong(g -> -g.getAverageSoftTime(roster)));

        System.out.println(min);
        System.out.println(max);

        for (int i = 0; i < min.size(); i++) {
            Group minG = min.get(i);
            Group maxG = max.get(i);
            long minT = minG.getAverageSoftTime(roster);
            long maxT = maxG.getAverageSoftTime(roster);
            if (maxT - minT < roster.getPlanner().getSettings().timeDiffThreshold / 2) {
                continue;
            }
            Collection<Event> minGE = groups.get(minG).stream().flatMap(p -> p.getPlannerData().getEvents().stream()).collect(Collectors.toList());
            Collection<Event> maxGE = groups.get(maxG).stream().flatMap(p -> p.getPlannerData().getEvents().stream()).collect(Collectors.toList());
            {
                Collection<Event> cop = Lists.newArrayList(minGE);
                minGE.removeAll(maxGE);
                maxGE.removeAll(cop);
            }
            Event maxL = maxGE.stream().max(Comparator.comparingLong(Event::getDuration)).orElse(null);
            Event minS = minGE.stream().min(Comparator.comparingLong(Event::getDuration)).orElse(null);
            if (maxL == null || minS == null) {
                continue;
            }

            Set<Person> minP = groups.get(minG);
            Set<Person> maxP = groups.get(maxG);

            System.out.println("-----------------");
            System.out.println(minS + " " + minS.getDuration() + "    " + maxL + " " + maxL.getDuration());
            System.out.println(minG + ": " + minT + "  " + maxG + ": " + maxT);
            System.out.println(minP.stream().filter(p -> p.getPlannerData().getEvents().contains(minS)).collect(Collectors.toList()));
            System.out.println(maxP.stream().filter(p -> p.getPlannerData().getEvents().contains(maxL)).collect(Collectors.toList()));
            System.out.println("-----------------");

            Iterator<Assignment> minA = minP.stream().map(p -> roster.getAssignments().stream().filter(a -> a.person.equals(p) && a.event.equals(minS)).findFirst().orElse(null)).filter(Objects::nonNull).iterator();
            Iterator<Assignment> maxA = maxP.stream().map(p -> roster.getAssignments().stream().filter(a -> a.person.equals(p) && a.event.equals(maxL)).findFirst().orElse(null)).filter(Objects::nonNull).iterator();

            while (minA.hasNext() && maxA.hasNext()) {
                Assignment a1 = minA.next();
                Assignment a2 = maxA.next();
                //System.out.println(a1 + " " + a2);
                a1.groupFilter = g -> true;
                a2.groupFilter = g -> true;
                //moves.add(new RosterSwapMove(a1, a2));
            }
            Iterator<Person> minI = minG.getPersonIterator();
            Iterator<Person> maxI = maxG.getPersonIterator();
            while (minA.hasNext()) {
                Assignment a = minA.next();
                a.groupFilter = g -> true;
                System.out.println(a);
                moves.add(new RosterChangeMove(a, maxI.next()));
            }
            while (maxA.hasNext()) {
                Assignment a = maxA.next();
                a.groupFilter = g -> true;
                System.out.println(a);
                moves.add(new RosterChangeMove(a, minI.next()));
            }
        }
        System.out.println("RET");
        return Collections.singletonList(new CompoundMove(moves));
    }

}
