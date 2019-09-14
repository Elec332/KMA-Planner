package elec332.kmaplanner.planner.opta.solver.phase5;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.filter.AbstractChangeMoveFilter;
import elec332.kmaplanner.project.PlannerSettings;

/**
 * Created by Elec332 on 30-8-2019
 */
public class OptimizeTimeChangeMoveFilter extends AbstractChangeMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment a, Person p) {
        PlannerSettings settings = roster.getPlanner().getSettings();
        long dur = roster.getAveragePersonTimeSoft(false);
        dur *= 1.1f;
        long cPDur = a.person.getPlannerData().getSoftDuration(dur, roster.getStartDate(), roster.getEndDate());
        if (Math.abs(dur - cPDur) < settings.timeDiffThreshold) {
            return false;
        }
        long nPDur = p.getPlannerData().getSoftDuration(dur, roster.getStartDate(), roster.getEndDate());
        if (nPDur > cPDur) {
            return false;
        }
        long diff = Math.abs(dur - nPDur);
        return diff > settings.timeDiffThreshold / 2;
    }

}
