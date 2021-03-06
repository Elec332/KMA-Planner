package elec332.kmaplanner.planner.opta;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.project.PlannerSettings;
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
        return calculateScore(roster, false);
    }

    public static HardMediumSoftScore calculateScore(Roster roster, boolean debug) {
        int hardScore = 0;
        int mediumScore = 0;
        int softScore = 0;
        roster.getPersons().forEach(p -> p.getPlannerData().clearEvents());
        roster.getAssignments().stream()
                .filter(assignment -> assignment.getPerson() != PersonManager.NULL_PERSON)
                .forEach(assignment -> assignment.person.getPlannerData().addEvent(assignment.event));
        Date start = roster.getPlanner().getEventManager().getFirstDate();
        Date end = roster.getPlanner().getEventManager().getLastDate();
        long avg = roster.getAveragePersonTimeSoft(true);
        long avgH = roster.getAveragePersonTimeReal();
        if (debug) {
            System.out.println("APTS: " + avg);
        }
        for (Assignment assignment : roster.getAssignments()) {

            if (assignment.event.everyone) {
                continue;
            }

            if (assignment.person == null || assignment.person == PersonManager.NULL_PERSON) {
                hardScore -= 10;
                continue;
            }

            if (assignment.person.getPlannerData().getCheckEvents().contains(assignment.event)) {
                hardScore -= 50;
                if (debug) {
                    System.out.println("P double assigned in e: " + assignment.person + ":" + assignment.person.getMainGroup() + "  " + assignment.event);
                }
            }

            if (!assignment.person.canParticipateIn(assignment.event)) {
                if (debug) {
                    System.out.println("P cannot in e: " + assignment.person + ":" + assignment.person.getMainGroup() + "  " + assignment.event);
                }
                hardScore--;
            }

            if (!assignment.event.canPersonParticipate(assignment.person)) {
                if (debug) {
                    System.out.println("e cannot p: " + assignment.person + ":" + assignment.person.getMainGroup() + "  " + assignment.event);
                }
                hardScore--;
            }

            //Force group filter if set by an EventAssigner
            if (!assignment.isValidGroup(assignment.getPerson().getPlannerData().getMainGroup())) {
                hardScore -= 50;
            }


            for (Event e : assignment.person.getPlannerData().getCheckEvents()) {
                if (!e.everyone && assignment.event.isDuring(e)) {
                    if (debug) {
                        System.out.println("PD: " + e + "  " + assignment.person + "  " + assignment.event);
                    }
                    hardScore -= 11;
                }
            }
            assignment.person.getPlannerData().getCheckEvents().add(assignment.event);
        }

        PlannerSettings settings = roster.getPlanner().getSettings();

        if (!roster.noSoft) {
            for (Person person : roster.getPersons()) {
                if (person == PersonManager.NULL_PERSON) {
                    continue;
                }
                long dur = person.getPlannerData().getSoftDuration(avgH, start, end);
                if (dur > (avg + settings.timeDiffThreshold)) {
                    softScore -= ((dur - avg) / 5);
                }
                if (dur > (avg + settings.timeDiffThreshold * 2)) {
                    softScore -= ((dur - avg) / 5) * 2;
                }
                if (dur < (avg - settings.timeDiffThreshold)) {
                    softScore -= (int) (((avg - dur) / 5f) * 1.5f);
                }
                if (dur < avg / 2) { //This one is doing waaaay to little...
                    softScore -= (avg - dur) * 5;
                }
            }
        }

        for (Group group : roster.getPlanner().getGroupManager().getMainGroups()) {
            if (!group.getPersonIterator().hasNext()) {
                continue;
            }
            long gAvg = group.getAverageSoftTime(roster);
            if (debug) {
                System.out.println("gaVG: " + gAvg);
            }
            if (gAvg > avg + settings.timeDiffThreshold / 1.5f || gAvg < avg - settings.timeDiffThreshold / 1.5f) {
                long diff = Math.abs(gAvg - avg) * 3;
                if (roster.forceGrouping) {
                    diff *= 5;
                }
                mediumScore -= diff;//Math.floorDiv(diff, 3);
            }
        }
        if (debug) {
            System.out.println("MS1: " + mediumScore);
        }
        if (settings.mainGroupFactor > 1) {
            Multimap<Event, Person> map = HashMultimap.create();
            roster.getAssignments().forEach(a -> map.put(a.event, a.person));
            Set<Group> mainGroups = roster.getPlanner().getGroupManager().stream()
                    .filter(Group::isMainGroup)
                    .filter(g -> g.getGroupSize() >= settings.mainGroupFactor * 1.2f)
                    .collect(Collectors.toSet());

            for (Map.Entry<Event, Collection<Person>> pc : map.asMap().entrySet()) {
                int groupF = Math.min(settings.mainGroupFactor - roster.getGroupOffset(), pc.getKey().requiredPersons);
                int mt = mainGroups.stream()
                        .mapToInt(g -> {
                            int ret = (int) pc.getValue().stream()
                                    .filter(g::containsPerson)
                                    .count();
                            if (ret >= Math.ceil(g.getGroupSize() / 2f)) {
                                ret = 0;
                            }
                            return ret;
                        })
                        .filter(i -> i > 0)
                        .filter(i -> i < groupF)
                        .reduce(0, (a, b) -> a + (groupF - b) * 10);
                mediumScore -= mt;
                if (debug && mt > 0) {
                    System.out.println(pc.getKey() + "  " + (-mt));
                }
            }
        }
        if (debug) {
            System.out.println("MS2: " + mediumScore);
        }
        if (roster.noSoft) {
            softScore = Integer.MIN_VALUE;
        }
        return HardMediumSoftScore.of(hardScore, mediumScore, softScore);
    }

}
