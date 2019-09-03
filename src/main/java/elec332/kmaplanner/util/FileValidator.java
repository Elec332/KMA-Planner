package elec332.kmaplanner.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Created by Elec332 on 2-9-2019
 */
public class FileValidator {

    @Nullable
    public static File checkFileSave(File projFile, String extension, boolean mayExist) {
        return checkFile(projFile, false, mayExist, extension);
    }

    @Nullable
    public static File checkFileLoad(File projFile, String extension) {
        return checkFile(projFile, true, true, extension);
    }

    @Nullable
    private static File checkFile(File projFile, boolean load, boolean mayExist, String extension) {
        if (load && !mayExist) {
            throw new IllegalArgumentException("Opening non-existing files is a great idea...");
        }
        if (projFile == null) {
            return null;
        }
        Preconditions.checkNotNull(extension);
        String fName = projFile.getAbsolutePath();
        int fsl = fName.lastIndexOf(File.separatorChar);
        String pureFileName = fName.substring(fsl + 1);
        String preFile = fName.substring(0, fsl);
        int dot = pureFileName.indexOf('.');
        if (dot >= 0) {
            pureFileName = pureFileName.substring(0, dot);
        }
        if (Strings.isNullOrEmpty(pureFileName)) {
            DialogHelper.showErrorMessageDialog("Cannot enter empty file name!", "Failed to " + (load ? "open" : "save") + " Project");
            return null;
        }
        if (!pureFileName.endsWith(extension)) {
            pureFileName += extension;
        }
        projFile = new File(preFile + File.separator + pureFileName);
        if ((!mayExist && projFile.exists()) || (load && !projFile.exists())) {
            String s2 = load ? " doesn't exist..." : " already exists...";
            DialogHelper.showErrorMessageDialog("File " + projFile + s2, "Failed to open Project");
            return null;
        }
        return projFile;
    }

}
