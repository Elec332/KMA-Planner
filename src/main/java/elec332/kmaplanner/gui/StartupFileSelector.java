package elec332.kmaplanner.gui;

import elec332.kmaplanner.util.JPanelBase;
import elec332.kmaplanner.util.ProjectFileChooser;
import elec332.kmaplanner.util.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Elec332 on 14-6-2019
 */
public class StartupFileSelector extends JPanelBase implements ActionListener {

    public StartupFileSelector() {
        JButton b = new JButton("Start new project");
        b.setActionCommand(NEW);
        b.addActionListener(this);
        add(b);
        b = new JButton("Open existing project");
        b.addActionListener(this);
        add(b);
    }

    private static final String NEW = "new";
    private File projFile;
    private boolean didSomething;

    public File getProjectFile() {
        return projFile;
    }

    public boolean shouldExit() {
        return !didSomething;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        didSomething = true;
        if (e.getActionCommand().equals(NEW)) {
            projFile = null;
            SwingUtils.closeWindow(this);
        } else {
            File file = ProjectFileChooser.openFileChooser(this);
            if (file != null) {
                projFile = file;
                SwingUtils.closeWindow(this);
            }
        }
    }

}
