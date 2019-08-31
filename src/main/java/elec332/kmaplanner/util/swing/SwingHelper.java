package elec332.kmaplanner.util.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 13-8-2019
 */
public class SwingHelper {

    public static void closeWindow(JPanelBase component) {
        if (component.dialog == null) {
            component.close = true;
            return;
        }
        component.dialog.dispose();
    }

    public static <P extends JPanelBase> P openPanelAsDialog(final P panel, final String title, JFrame window) {
        if (panel.close) {
            return panel;
        }
        JDialog frame = new JDialog(window, title, true);
        panel.setOpaque(true);
        panel.dialog = frame;
        frame.setContentPane(panel);
        frame.setLocationRelativeTo(window);
        frame.pack();
        frame.setVisible(true);
        return panel;
    }

    public static void setEnabledAll(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            for (Component c : ((Container) component).getComponents()) {
                setEnabledAll(c, enabled);
            }
        }
    }

}
