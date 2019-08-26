package elec332.kmaplanner.util;

import com.google.common.base.Preconditions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Created by Elec332 on 24-8-2019
 */
public class ProjectFileChooser {

    public static File openFileChooser(Component parent){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(IOUtils.getExecFolder());
        FileNameExtensionFilter kpFilter = new FileNameExtensionFilter("KMAPlanner files (*.kp)", "kp");
        fc.addChoosableFileFilter(kpFilter);
        fc.setFileFilter(kpFilter);
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        int ret = fc.showOpenDialog(parent);
        if (ret == JFileChooser.APPROVE_OPTION) {
            return Preconditions.checkNotNull(fc.getSelectedFile());
        }
        return null;
    }

}
