package elec332.kmaplanner.filters.impl;

import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.util.DateChooserPanel;
import elec332.kmaplanner.util.ITimeSpan;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 26-8-2019
 */
public class TimeFilter extends AbstractFilter implements ITimeSpan {

    public TimeFilter(){
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

}
