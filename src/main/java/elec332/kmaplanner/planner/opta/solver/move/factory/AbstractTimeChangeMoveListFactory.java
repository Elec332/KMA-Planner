package elec332.kmaplanner.planner.opta.solver.move.factory;

import com.google.common.collect.Streams;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.move.AbstractRosterMove;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 17-9-2019
 */
public abstract class AbstractTimeChangeMoveListFactory extends AbstractMoveListFactory {

    @Override
    public final void fillMoves(List<AbstractRosterMove> moves, Roster roster) {
        final long avgEventDuration = roster.getPlanner().getEventManager().stream().mapToLong(Event::getDuration).sum() / roster.getPlanner().getEventManager().getObjects().size();

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
                AbstractRosterMove arm = createMove(p1, p2, roster, avgEventDuration);
                if (arm != null) {
                    moves.add(arm);
                }
            }

        });
    }

    protected abstract AbstractRosterMove createMove(Person minP, Person maxP, Roster roster, long avgEventDuration);

}
