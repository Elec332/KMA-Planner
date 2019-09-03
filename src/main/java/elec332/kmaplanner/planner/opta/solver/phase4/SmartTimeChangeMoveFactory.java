package elec332.kmaplanner.planner.opta.solver.phase4;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;
import elec332.kmaplanner.planner.opta.solver.move.RosterChangeMove;
import elec332.kmaplanner.project.ProjectSettings;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 31-8-2019
 */
public class SmartTimeChangeMoveFactory implements MoveListFactory<Roster> {

    @Override
    public List<? extends Move<Roster>> createMoveList(Roster roster) {
        roster.plannerApply();
        List<AbstractRosterMove> moves = Lists.newArrayList();
        ProjectSettings settings = roster.getPlanner().getSettings();
        long avgEventDuration = roster.getPlanner().getEvents().stream().mapToLong(Event::getDuration).sum() / roster.getPlanner().getEvents().size();

        roster.getPlanner().getGroupManager().getMainGroups().stream()
                .filter(g -> g.getPersonIterator().hasNext()).forEach(group -> {
            List<Person> pi = Streams.stream(group.getPersonIterator())
                    .sorted(Comparator.comparingLong(p -> p.getPlannerData().getSoftDuration(roster)))
                    .collect(Collectors.toList());

            int siz = Math.floorDiv(pi.size(), 2);
            int gs = pi.size();
            List<Person> min = pi.subList(0, siz);
            List<Person> max = pi.subList(gs - siz, gs);
            max.sort(Comparator.comparingLong(p -> -p.getPlannerData().getSoftDuration(roster)));

            if (min.size() != max.size()) {
                throw new RuntimeException();
            }

            for (int i = 0; i < min.size(); i++) {
                Person p1 = min.get(i);
                Person p2 = max.get(i);
                long p1S = p1.getPlannerData().getSoftDuration(roster);
                long p2S = p2.getPlannerData().getSoftDuration(roster);
                if (Math.abs(p1S - p2S) < avgEventDuration / 2) {
                    continue;
                }

                Event p2Min = p2.getPlannerData().getEvents().stream()
                        .filter(e -> !p1.getPlannerData().getEvents().contains(e))
                        .min(Comparator.comparingLong(Event::getDuration))
                        .orElse(null);

                if (p2Min == null) {
                    continue;
                }

                Assignment aM = roster.getAssignments().stream().filter(a -> a.person.equals(p2) && a.event.equals(p2Min)).findFirst().orElseThrow(NullPointerException::new);

                moves.add(new RosterChangeMove(aM, p1));
            }

        });

        return moves;
    }

}
