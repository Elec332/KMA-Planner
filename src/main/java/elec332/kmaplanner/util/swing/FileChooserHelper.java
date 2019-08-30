package elec332.kmaplanner.util.swing;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.util.FileHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Created by Elec332 on 24-8-2019
 */
public class FileChooserHelper {

    public static File openFileProjectChooser(Component parent) {
        return openFileChooser(parent, new FileNameExtensionFilter("KMAPlanner files (*.kp)", "kp"));
    }

    public static File openFileChooser(Component parent, FileNameExtensionFilter kpFilter) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(FileHelper.getExecFolder());
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
