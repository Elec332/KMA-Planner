package elec332.kmaplanner.planner;

import java.util.Date;

/**
 * Created by Elec332 on 29-8-2019
 */
public interface ISoftDuration {

    long getSoftDuration(long duration, long avg, Date start, Date end);

}
