package elec332.kmaplanner.util;

import java.io.File;

/**
 * Created by Elec332 on 13-8-2019
 */
public class IOUtils {

    public static File getExecFolder() {
        try {
            return new File(IOUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
