package elec332.kmaplanner.gui;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.io.ProjectData;
import elec332.kmaplanner.util.IOUtils;
import elec332.kmaplanner.util.JPanelBase;
import elec332.kmaplanner.util.SwingUtils;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Random;

/**
 * Created by Elec332 on 14-6-2019
 */
public class ProjectInitializerGui extends JPanelBase {

    public ProjectInitializerGui() {
        this.projectData = new ProjectData();
        projectData.seed = new Random().nextLong(); //TODO
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
        } else {
            System.exit(0); //todo
        }
    }

    private File projFile;
    private ProjectData projectData;

    @Nonnull
    public ProjectData getProjectData() {
        return Preconditions.checkNotNull(projectData);
    }

    @Nonnull
    public File getProjectFile() {
        return Preconditions.checkNotNull(projFile);
    }

}
