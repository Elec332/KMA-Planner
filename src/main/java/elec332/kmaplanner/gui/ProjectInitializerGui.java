package elec332.kmaplanner.gui;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.util.FileChooserHelper;
import elec332.kmaplanner.util.JPanelBase;
import elec332.kmaplanner.util.SwingHelper;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Created by Elec332 on 14-6-2019
 */
public class ProjectInitializerGui extends JPanelBase {

    public ProjectInitializerGui() {
        this.projectData = new ProjectSettings();
        File file = FileChooserHelper.openFileProjectChooser(this);
        if (file != null) {
            projFile = file;
            SwingHelper.closeWindow(this);
        } else {
            System.exit(0);
        }
    }

    private File projFile;
    private ProjectSettings projectData;

    @Nonnull
    public ProjectSettings getProjectData() {
        return Preconditions.checkNotNull(projectData);
    }

    @Nonnull
    public File getProjectFile() {
        return Preconditions.checkNotNull(projFile);
    }

}
