package elec332.kmaplanner.gui.dialogs;

import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.project.ProjectSettings;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;

/**
 * Created by Elec332 on 3-9-2019
 */
public class ProjectSettingsPanel extends AbstractDialogPanel {

    public static void openDialog(KMAPlannerProject project, JFrame owner) {
        ProjectSettingsPanel panel = new ProjectSettingsPanel(project);
        if (DialogHelper.showDialog(owner, panel, "Project Settings")) {
            panel.apply();
            project.markDirty();
        }
    }

    private ProjectSettingsPanel(KMAPlannerProject project) {
        ProjectSettings settings = project.getProjectSettings();

        addPanel(uuid -> {
            uuid.add(new JLabel("Project UUID: "));
            JTextField textField = new JTextField(project.getUuid().toString());
            textField.setEditable(false);
            uuid.add(textField);
            return null;
        });

        addPanel(compression -> {
            compression.add(new JLabel("Enable compression: "));
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(settings.enableCompression);
            compression.add(checkBox);
            return () -> settings.enableCompression = checkBox.isSelected();
        });
    }

}
