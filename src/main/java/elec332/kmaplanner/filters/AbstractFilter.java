package elec332.kmaplanner.filters;

import com.google.common.base.Strings;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.IEventFilter;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-8-2019
 */
public abstract class AbstractFilter implements IEventFilter {

    public AbstractFilter() {
        this.name = null;
    }

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public abstract String getDescription();

    public abstract boolean canParticipateIn(Event event);

    @Nonnull
    public JPanel createConfigPanel(Consumer<Runnable> applyCallback) {
        JPanel ret = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        top.add(new JLabel("Filter Name: "));
        final JTextField textField = new JTextField(getName(), 15);
        top.add(textField);
        textField.addActionListener(a -> setName(textField.getText()));
        ret.add(top, BorderLayout.NORTH);
        ret.add(createCenterPanel(applyCallback), BorderLayout.CENTER);
        applyCallback.accept(() -> setName(textField.getText()));
        return ret;
    }

    protected JPanel createCenterPanel(Consumer<Runnable> applyCallback) {
        return new JPanel();
    }

    @Nonnull
    public abstract AbstractFilter copy();

    public boolean isValid() {
        return !Strings.isNullOrEmpty(getName());
    }

    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeUTF(getName());
    }

    public void readObject(IByteArrayDataInputStream stream) {
        setName(stream.readUTF());
    }

    @Override
    public String toString() {
        return name;
    }

}
