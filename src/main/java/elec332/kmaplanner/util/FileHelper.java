package elec332.kmaplanner.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Created by Elec332 on 13-8-2019
 */
public class FileHelper {

    public static File getExecFolder() {
        try {
            return new File(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteDir(File dir) throws IOException {
        if (dir == null || !dir.exists()) {
            return;
        }
        Path directory = dir.toPath();
        if (dir.isFile()) {
            Files.delete(directory);
            return;
        }
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public static List<File> getFilesRecursively(File file) {
        List<File> ret = Lists.newArrayList();
        getFilesRecursively(file, ret);
        return ret;
    }

    public static void getFilesRecursively(File file, @Nonnull List<File> list) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            list.add(file);
        }
        if (file.isDirectory()) {
            for (File sub : Preconditions.checkNotNull(file.listFiles())) {
                getFilesRecursively(sub, list);
            }
        }
    }

}
