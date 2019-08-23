package elec332.kmaplanner.gui;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.util.IOUtils;
import elec332.kmaplanner.util.JPanelBase;
import elec332.kmaplanner.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setCurrentDirectory(IOUtils.getExecFolder());
            FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("KMAPlanner files (*.kp)", "kp");
            fc.addChoosableFileFilter(xmlFilter);
            fc.setFileFilter(xmlFilter);
            fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
            int ret = fc.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                projFile = Preconditions.checkNotNull(fc.getSelectedFile());
                SwingUtils.closeWindow(this);
            }
        }
    }

}
