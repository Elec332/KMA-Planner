package elec332.kmaplanner.util;

import java.io.File;
import java.io.IOException;

/**
 * Created by Elec332 on 4-9-2019
 */
public interface IObjectPrinter<O> {

    void printObject(File file, O object) throws IOException;

    default String getDefaultFileExtension() {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    static <O> void print(File location, String baseName, O object, IObjectPrinter<? super O>... printers) {
        for (IObjectPrinter<? super O> printer : printers) {
            try {
                printer.printObject(new File(location, baseName + "." + printer.getDefaultFileExtension()), object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
