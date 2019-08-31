package elec332.kmaplanner.planner.opta.util;

import elec332.kmaplanner.planner.opta.Assignment;

import java.util.Comparator;

/**
 * Created by Elec332 on 27-8-2019
 */
public class AssignmentDifficultyComparator implements Comparator<Assignment> {

    @Override
    public int compare(Assignment o1, Assignment o2) {
        return (int) (o1.event.getDuration() - o2.event.getDuration());
    }

}
