package elec332.kmaplanner.gui;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.io.ProjectData;
import elec332.kmaplanner.util.JPanelBase;
import elec332.kmaplanner.util.ProjectFileChooser;
import elec332.kmaplanner.util.SwingUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Random;

/**
 * Created by Elec332 on 14-6-2019
 */
public class ProjectInitializerGui extends JPanelBase {

    public ProjectInitializerGui() {
        this.projectData = new ProjectData();
        projectData.seed = new Random().nextLong(); //TODO
        File file = ProjectFileChooser.openFileChooser(this);
        if (file != null){
            projFile = file;
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
