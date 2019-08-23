package elec332.kmaplanner.planner.opta;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

/**
 * Created by Elec332 on 15-8-2019
 */
public class RosterScoreCalculator implements EasyScoreCalculator<Roster> {

    @Override
    public Score calculateScore(Roster roster) {
        int hardScore = 0;
        int softScore = 0;
        roster.getPlanner().getPersonManager().getPersons().forEach(p -> p.events.clear());
        long totTime = roster.planner.getEvents().stream().mapToLong(e -> e.getDuration() * e.getRequiredPersons()).sum();
        long avg = totTime / roster.getPersons().size();
        for (Assignment assignment : roster.getAssignments()){

            if (assignment.person == null){
                hardScore -= 10;
                continue;
            }

            if (assignment.person.events.contains(assignment.event)){
                hardScore -= 50;
            }

            if (!assignment.person.canParticipateIn(assignment.event)){
                hardScore--;
            }

            if (!assignment.event.canPersonParticipate(assignment.person)){
                hardScore--;
            }

            for (Event e : assignment.person.events){
                if (assignment.event.isDuring(e)){
                    hardScore -= 10;
                }
            }
            assignment.person.events.add(assignment.event);
        }

        for (Person person : roster.getPersons()){
            long dur = person.getDuration();
            if (dur > avg + 60){
                softScore -= (dur - avg) / 10 - 3;
            }
        }

        return HardSoftScore.of(hardScore, softScore);
    }

}
