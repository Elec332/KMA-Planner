package elec332.kmaplanner.gui.dialogs;

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Elec332 on 4-9-2019
 */
@SuppressWarnings("WeakerAccess")
public class AbstractDialogPanel extends JPanel {

    public AbstractDialogPanel() {
        callbacks = Lists.newArrayList();
    }

    private final List<Runnable> callbacks;
    private int rows;

    protected void addPanel(Function<JPanel, Runnable> func) {
        JPanel ret = new JPanel();
        Runnable r = func.apply(ret);
        if (r != null) {
            callbacks.add(r);
        }
        rows++;
        setLayout(new GridLayout(rows, 1));
        add(ret);
    }

    protected void apply() {
        callbacks.forEach(Runnable::run);
    }

}
