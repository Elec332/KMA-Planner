package elec332.kmaplanner.planner.opta.helpers;

import elec332.kmaplanner.planner.opta.Assignment;

import java.util.Comparator;

/**
 * Created by Elec332 on 30-8-2019
 */
public class AssignmentSorter implements Comparator<Assignment> {

    @Override
    public int compare(Assignment o1, Assignment o2) {
        return (int) (o2.event.getDuration() - o1.event.getDuration());
    }

}
