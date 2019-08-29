package elec332.kmaplanner.planner.opta;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 15-8-2019
 */
public class RosterScoreCalculator implements EasyScoreCalculator<Roster> {

    @Override
    public Score calculateScore(Roster roster) {
        int hardScore = 0;
        int mediumScore = 0;
        int softScore = 0;
        roster.getPersons().forEach(p -> p.getPlannerEvents().clear());
        Date start = roster.getPlanner().getFirstDate();
        Date end = roster.getPlanner().getLastDate();
        long avg = calculateAverage(roster);
        avg *= 1.1f;
        for (Assignment assignment : roster.getAssignments()) {

            if (assignment.person == null || assignment.person == PersonManager.NULL_PERSON) {
                hardScore -= 10;
                continue;
            }

            if (assignment.person.getPlannerEvents().contains(assignment.event)) {
                hardScore -= 50;
            }

            if (!assignment.person.canParticipateIn(assignment.event)) {
                hardScore--;
            }

            if (!assignment.event.canPersonParticipate(assignment.person)) {
                hardScore--;
            }

            for (Event e : assignment.person.getPlannerEvents()) {
                if (assignment.event.isDuring(e)) {
                    hardScore -= 11;
                }
            }
            assignment.person.getPlannerEvents().add(assignment.event);
        }

        ProjectSettings settings = roster.getPlanner().getSettings();

        for (Person person : roster.getPersons()) {
            if (person == PersonManager.NULL_PERSON) {
                continue;
            }
            long dur = person.getSoftDuration(avg, start, end);
            if (dur > (avg + settings.timeDiffThreshold)) {
                softScore -= ((dur - avg) / 5);
            }
            if (dur > (avg + settings.timeDiffThreshold * 2.5f)) {
                softScore -= ((dur - avg) / 5) * 2;
            }
            if (dur < (avg - settings.timeDiffThreshold)) {
                softScore -= (int) (((avg - dur) / 5f) * 1.5f);
            }
            if (dur < avg / 2) { //This one is doing waaaay to little...
                softScore -= (avg - dur) * 5;
            }
        }


        if (settings.mainGroupFactor > 1) {
            Multimap<Event, Person> map = HashMultimap.create();
            roster.getAssignments().forEach(a -> map.put(a.event, a.person));
            Set<Group> mainGroups = roster.getPlanner().getGroupManager().getGroups().stream()
                    .filter(Group::isMainGroup)
                    .filter(g -> g.getGroupSize() >= settings.mainGroupFactor * 1.2f)
                    .collect(Collectors.toSet());

            for (Map.Entry<Event, Collection<Person>> pc : map.asMap().entrySet()) {
                int groupF = Math.min(settings.mainGroupFactor, pc.getKey().requiredPersons);
                mediumScore -= mainGroups.stream()
                        .mapToInt(g -> (int) pc.getValue().stream()
                                .filter(g::containsPerson)
                                .count())
                        .filter(i -> i > 0)
                        .filter(i -> i < groupF)
                        .reduce(0, (a, b) -> a + (groupF - b) * 10);
            }
        }

        return HardMediumSoftScore.of(hardScore, mediumScore, softScore);
    }

    public static long calculateAverage(Roster roster) {
        long totTime = roster.getPlanner().getEvents().stream()
                .filter(e -> !e.everyone)
                .mapToLong(e -> e.getDuration() * e.getRequiredPersons())
                .sum();
        return totTime / roster.getPersons().size();
    }

}
