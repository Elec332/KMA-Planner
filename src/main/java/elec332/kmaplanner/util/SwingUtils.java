package elec332.kmaplanner.util;

import javax.swing.*;

/**
 * Created by Elec332 on 13-8-2019
 */
public class SwingUtils {

    public static void closeWindow(JPanelBase component) {
        /*component.show(false);
        component.setVisible(false);
        Window win = SwingUtilities.getWindowAncestor(component);
        while (win != null){
            win.dispose();
            win = SwingUtilities.getWindowAncestor(component);
        }*/
        if (component.d == null) {
            component.noOpen = true;
            return;
        }
        component.d.dispose();
    }

    public static <P extends JPanelBase> P openPanelAsDialog(final P panel, final String title) {
        if (panel.noOpen) {
            return panel;
        }
        JDialog frame = new JDialog(new JFrame(), title, true);
        panel.d = frame;
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        panel.setOpaque(true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        return panel;
    }

}
