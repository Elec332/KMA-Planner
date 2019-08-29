package elec332.kmaplanner.planner.print;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Created by Elec332 on 29-8-2019
 */
class AbstractPrinter {

    static Row getOrCreateRow(Sheet sheet, int row) {
        Row ret = sheet.getRow(row);
        if (ret == null) {
            ret = sheet.createRow(row);
        }
        return ret;
    }

    static Cell getCell(Row row, int cell) {
        return row.getCell(cell, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }


}
