package elec332.kmaplanner.util.swing;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
        return openFileProjectChooser(parent, null, null);
    }

    public static File openFileProjectChooser(Component parent, File selected, String title) {
        return openFileChooser(parent, new FileNameExtensionFilter("KMAPlanner files (*.kp)", "kp"), selected, title);
    }

    public static File openFileChooser(Component parent, FileNameExtensionFilter kpFilter, String title) {
        return openFileChooser(parent, kpFilter, null, title);
    }

    @SuppressWarnings("WeakerAccess")
    public static File openFileChooser(Component parent, FileNameExtensionFilter kpFilter, File selected, String title) {
        JFileChooser fc = new JFileChooser();
        if (!Strings.isNullOrEmpty(title)) {
            fc.setDialogTitle(title);
            fc.setApproveButtonText(title);
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(FileHelper.getExecFolder());
        fc.addChoosableFileFilter(kpFilter);
        fc.setFileFilter(kpFilter);
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        if (selected != null) {
            fc.setSelectedFile(selected);
        }
        int ret = fc.showOpenDialog(parent);
        if (ret == JFileChooser.APPROVE_OPTION) {
            return Preconditions.checkNotNull(fc.getSelectedFile());
        }
        return null;
    }

}
