package elec332.kmaplanner.util;

import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Elec332 on 29-8-2019
 */
public class ZipHelper {

    public static void zip(File source, File dest) throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        zip(source, fos);
        fos.close();
    }

    public static void zip(File source, OutputStream dest) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(dest);
        zip(source, zos);
        zos.close();
    }

    public static void zip(File source, ZipOutputStream dest) throws IOException {
        List<File> files = Lists.newArrayList();
        FileHelper.getFilesRecursively(source, files);
        if (files.isEmpty()) {
            return;
        }
        String rem = source.getAbsolutePath() + File.separator;
        for (File file : FileHelper.getFilesRecursively(source)) {
            ZipEntry ze = new ZipEntry(file.getAbsolutePath().replace(rem, ""));
            dest.putNextEntry(ze);
            Files.copy(file.toPath(), dest);
            dest.closeEntry();
        }
    }

}
