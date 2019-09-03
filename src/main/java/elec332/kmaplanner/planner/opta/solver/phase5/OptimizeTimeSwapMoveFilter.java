package elec332.kmaplanner.planner.opta.solver.phase5;

import elec332.kmaplanner.planner.opta.Assignment;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.solver.filter.AbstractSwapMoveFilter;
import elec332.kmaplanner.project.ProjectSettings;

/**
 * Created by Elec332 on 30-8-2019
 */
public class OptimizeTimeSwapMoveFilter extends AbstractSwapMoveFilter {

    @Override
    protected boolean accept(Roster roster, Assignment a1, Assignment a2) {
        long t1 = a1.event.getDuration();
        long t2 = a2.event.getDuration();
        if (t1 == t2) {
            return false;
        }
        long avg = (long) (roster.getAveragePersonTimeSoft() * 1.1f);
        long a1Dur = a1.person.getPlannerData().getSoftDuration(roster);
        long a2Dur = a2.person.getPlannerData().getSoftDuration(roster);
        if (Math.abs(a1Dur - a2Dur) < 10) {
            return false;
        }
        ProjectSettings settings = roster.getPlanner().getSettings();
        boolean h1 = a1Dur - (avg + settings.timeDiffThreshold) > 0;
        boolean h2 = a2Dur - (avg + settings.timeDiffThreshold) > 0;
        return !h1 && !h2; //todo: Expand
    }

}
