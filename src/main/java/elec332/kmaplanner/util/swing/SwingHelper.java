package elec332.kmaplanner.util.swing;

import javax.swing.*;

/**
 * Created by Elec332 on 13-8-2019
 */
public class SwingHelper {

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

    public static <P extends JPanelBase> P openPanelAsDialog(final P panel, final String title, JFrame owner) {
        if (panel.noOpen) {
            return panel;
        }
        JDialog frame = new JDialog(owner, title, true);
        //JFrame frame = owner;
        panel.d = frame;
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        panel.setOpaque(true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        return panel;
    }

}
