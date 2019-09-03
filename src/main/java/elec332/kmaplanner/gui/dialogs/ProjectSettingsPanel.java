package elec332.kmaplanner.gui.dialogs;

import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 3-9-2019
 */
public class ProjectSettingsPanel extends JPanel {

    public static void openDialog(KMAPlannerProject project, JFrame owner) {
        ProjectSettingsPanel panel = new ProjectSettingsPanel(project);
        if (DialogHelper.showDialog(owner, panel, "Project Settings")) {
            panel.apply();
        }
    }

    private ProjectSettingsPanel(KMAPlannerProject project) {
        super(new GridLayout(1, 1));

        JPanel uuid = new JPanel();
        uuid.add(new JLabel("Project UUID: "));
        JTextField textField = new JTextField(project.getUuid().toString());
        textField.setEditable(false);
        uuid.add(textField);
        add(uuid);
    }

    private void apply() {
        //Not yet
    }

}
