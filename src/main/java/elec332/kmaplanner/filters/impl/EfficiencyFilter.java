package elec332.kmaplanner.filters.impl;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 29-8-2019
 */
public class EfficiencyFilter extends AbstractFilter {

    public EfficiencyFilter() {
        desc = "Person will be assigned x% less (or more) assignments.";
    }

    private final String desc;
    private float efficiency;

    @Nonnull
    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    protected JPanel createCenterPanel(Consumer<Runnable> applyCallback) {
        JPanel center = new JPanel();
        center.add(new JLabel("Time efficiency: "));
        JTextField eff = new JTextField("" + efficiency, 5);
        center.add(eff);

        applyCallback.accept(() -> {
            Float e;
            try {
                e = Float.parseFloat(eff.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
                DialogHelper.showErrorMessageDialog(center, "Failed to parse efficiency.", "Failed to apply");
                return;
            }
            EfficiencyFilter.this.efficiency = e;
        });
        return center;
    }

    @Override
    public boolean canParticipateIn(Event event) {
        return true;
    }

    @Override
    public long getSoftDuration(long duration, long avg, Date start, Date end) {
        return (long) (duration * (efficiency - 1));
    }

    @Nonnull
    @Override
    public AbstractFilter copy() {
        EfficiencyFilter ret = new EfficiencyFilter();
        ret.efficiency = efficiency;
        ret.setName(getName());
        return ret;
    }

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        super.writeObject(stream);
        stream.writeFloat(efficiency);
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        super.readObject(stream);
        efficiency = stream.readFloat();
    }

}
