package elec332.kmaplanner.planner.opta.helpers;

import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Created by Elec332 on 30-8-2019
 */
public class DifferentPersonChangeFilter implements SelectionFilter<Roster, ChangeMove<Roster>> {

    @Override
    public boolean accept(ScoreDirector<Roster> scoreDirector, ChangeMove<Roster> selection) {
        Assignment a = (Assignment) selection.getEntity();
        Person p = (Person) selection.getToPlanningValue();
        if (p.getPlannerEvents().contains(a.event)) {
            return false;
        }
        if (a.person.equals(p)) {
            return false;
        }
        Roster roster = scoreDirector.getWorkingSolution();
        if (roster.getScore().getMediumScore() > 0) {
            return true;
        }
        ProjectSettings settings = roster.getPlanner().getSettings();
        long dur = roster.getAveragePersonTime();
        dur *= 1.1f;
        long cPDur = a.person.getSoftDuration(dur, roster.getStartDate(), roster.getEndDate());
        if (Math.abs(dur - cPDur) < settings.timeDiffThreshold) {
            return false;
        }
        long nPDur = p.getSoftDuration(dur, roster.getStartDate(), roster.getEndDate());
        if (nPDur > cPDur) {
            return false;
        }
        long diff = Math.abs(dur - nPDur);
        return diff > settings.timeDiffThreshold / 2;
    }

}
