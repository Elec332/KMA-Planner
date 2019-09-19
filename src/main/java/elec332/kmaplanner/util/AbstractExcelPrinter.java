package elec332.kmaplanner.util;

import com.google.common.base.Preconditions;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Elec332 on 29-8-2019
 */
public abstract class AbstractExcelPrinter<O> implements IObjectPrinter<O> {

    @Override
    public void printObject(@Nonnull File file, @Nonnull O object) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        printObject(Preconditions.checkNotNull(object), workbook);
        FileOutputStream fos = new FileOutputStream(Preconditions.checkNotNull(file));
        workbook.getProperties().getCoreProperties().setCreator(System.getProperty("user.name"));
        workbook.getProperties().getExtendedProperties().getUnderlyingProperties().setApplication(UIStrings.UI_NAME);
        workbook.write(fos);
        fos.close();
    }

    @Override
    public String getDefaultFileExtension() {
        return "xlsx";
    }

    protected abstract void printObject(O object, Workbook workbook);

    protected Row getOrCreateRow(Sheet sheet, int row) {
        Row ret = sheet.getRow(row);
        if (ret == null) {
            ret = sheet.createRow(row);
        }
        return ret;
    }

    protected Cell getCell(Row row, int cell) {
        return row.getCell(cell, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

}
