package elec332.kmaplanner.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Elec332 on 26-8-2019
 */
public interface ITimeSpan {

    public Date start();

    public Date end();

    default public long getDuration(@Nonnull TimeUnit timeUnit) {
        return timeUnit.convert(Math.abs(end().getTime() - start().getTime()), TimeUnit.MILLISECONDS);
    }

    default public boolean isDuring(ITimeSpan other) {
        return other.start().before(end()) && !(other.end().before(start()) || other.end().equals(start()));
    }

    default public boolean isDuring(Collection<? extends ITimeSpan> other) {
        return other.stream().anyMatch(this::isDuring);
    }

}
