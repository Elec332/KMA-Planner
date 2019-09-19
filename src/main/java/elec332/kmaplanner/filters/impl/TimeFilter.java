package elec332.kmaplanner.filters.impl;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.util.ITimeSpan;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.swing.DateChooserPanel;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 26-8-2019
 */
public class TimeFilter extends AbstractFilter implements ITimeSpan {

    public TimeFilter() {
        start = new Date();
        end = new Date();
        desc = "Cannot participate in the given timespan.";
    }

    private Date start, end;
    private final String desc;

    @Nonnull
    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public boolean canParticipateIn(Event event) {
        return !event.isDuring(this);
    }

    @Override
    protected JPanel createCenterPanel(Consumer<Runnable> applyCallback) {
        JPanel center = new JPanel(new GridLayout(1, 3));
        DateChooserPanel start = new DateChooserPanel(this.start);
        DateChooserPanel end = new DateChooserPanel(this.end);
        JPanel cl = new JPanel();
        JPanel cr = new JPanel();
        cl.add(new JLabel("Start date: "));
        cl.add(start);
        cr.add(new JLabel("End date:"));
        cr.add(end);
        center.add(cl);
        center.add(new JPanel()); //spacer
        center.add(cr);

        applyCallback.accept(() -> {
            TimeFilter.this.start = start.getDate();
            TimeFilter.this.end = end.getDate();
        });

        return center;
    }

    @Nonnull
    @Override
    public AbstractFilter copy() {
        TimeFilter copy = new TimeFilter();
        copy.setName(getName());
        copy.start = (Date) start.clone();
        copy.end = (Date) end.clone();
        return copy;
    }

    @Override
    public Date start() {
        return start;
    }

    @Override
    public Date end() {
        return end;
    }

    @Override
    public long getSoftDuration(long duration, long avg, Date start, Date end) {
        long ed = end.getTime() - start.getTime();
        long fd = end().getTime() - Math.max(start().getTime(), start.getTime());
        ed /= 10;
        fd /= 10;
        float div = (fd * 1f) / ed;
        if (div < 0.33f) {
            return 0;
        }
        if (div < 0.66f) {
            return (long) (avg * (div * 0.66f));
        }
        return (long) (div * avg);
    }

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        super.writeObject(stream);
        stream.writeLong(start.getTime());
        stream.writeLong(end.getTime());
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        super.readObject(stream);
        start = new Date(stream.readLong());
        end = new Date(stream.readLong());
    }

}
