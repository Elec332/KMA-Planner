package elec332.kmaplanner.gui.dialogs;

import elec332.kmaplanner.Main;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;

/**
 * Created by Elec332 on 4-9-2019
 */
public class AboutPanel extends AbstractDialogPanel {

    public static void openDialog(JFrame owner) {
        AboutPanel panel = new AboutPanel();
        DialogHelper.showDialog(owner, panel, "About", JOptionPane.DEFAULT_OPTION);
    }

    private AboutPanel() {
        addPanel(panel -> {
            JTextArea text = new JTextArea();
            text.setText(Main.ABOUT_TEXT);
            text.setEditable(false);
            text.setBackground(AboutPanel.this.getBackground());
            panel.add(text);
            return null;
        });
    }

}
