package elec332.kmaplanner.io;

import elec332.kmaplanner.util.swing.DialogHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Elec332 on 14-6-2019
 */
@SuppressWarnings("WeakerAccess")
public class ExcelReader {

    @Nullable
    public static Workbook openExcelFile(File file) {
        if (!file.exists()) {
            DialogHelper.showErrorMessageDialog("File does not exist!", "Failed to open Excel file");
            return null;
        }
        String name = file.getAbsolutePath();
        if (!name.endsWith(".xls") && !name.endsWith(".xlsx")) {
            DialogHelper.showErrorMessageDialog("Invalid file name!", "Failed to open Excel file");
            return null;
        }
        Workbook workbook;
        try {
            FileInputStream fis = new FileInputStream(file);
            if (name.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                workbook = new XSSFWorkbook(fis);
            }
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return workbook;
    }

}
