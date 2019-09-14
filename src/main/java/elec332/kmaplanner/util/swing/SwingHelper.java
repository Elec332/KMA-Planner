package elec332.kmaplanner.util.swing;

import java.awt.*;

/**
 * Created by Elec332 on 13-8-2019
 */
public class SwingHelper {

    public static void setEnabledAll(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            for (Component c : ((Container) component).getComponents()) {
                setEnabledAll(c, enabled);
            }
        }
    }

}
