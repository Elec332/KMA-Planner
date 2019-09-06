package elec332.kmaplanner.filters.impl;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.filters.AbstractFilter;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 24-8-2019
 */
public class NeverFilter extends AbstractFilter {

    public static final AbstractFilter INSTANCE = new NeverFilter();

    private NeverFilter() {
        setName("Never");
        desc = "Cannot participate in any event.";
    }

    private final String desc;

    @Nonnull
    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public boolean canParticipateIn(Event event) {
        return false;
    }

    @Nonnull
    @Override
    public JPanel createConfigPanel(Consumer<Runnable> applyCallback) {
        return new JPanel();
    }

    @Nonnull
    @Override
    public AbstractFilter copy() {
        return INSTANCE;
    }

    @Override
    public long getSoftDuration(long duration, long avg, Date start, Date end) {
        return avg;
    }

}
