package elec332.kmaplanner.planner.print;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.util.DateHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by Elec332 on 29-8-2019
 */
public class PersonPrinter extends AbstractPrinter {

    private static final int OFFSET = 2;
    private static final int OFFSET_2 = 2;

    public static void printRoster(Person person, Workbook workbook) {
        Sheet sheet = workbook.createSheet(person.toString());
        Row r1 = getOrCreateRow(sheet, 0);
        getCell(r1, 0).setCellValue("Rooster voor: " + person.toString());
        getCell(r1, OFFSET).setCellValue("Event");
        sheet.setColumnWidth(OFFSET + OFFSET_2 - 1, 2 * 256);
        getCell(r1, OFFSET + OFFSET_2).setCellValue("Time (Start)");
        getCell(r1, OFFSET + OFFSET_2 + 1).setCellValue("Time (End)");

        int c = OFFSET;
        for (Event event : person.getPrintableEvents()) {
            Row row = getOrCreateRow(sheet, c);
            getCell(row, OFFSET).setCellValue(event.name);
            getCell(row, OFFSET + OFFSET_2).setCellValue(DateHelper.getNiceString(event.start));
            getCell(row, OFFSET + OFFSET_2 + 1).setCellValue(DateHelper.getNiceString(event.end));
            c++;
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(OFFSET);
        sheet.autoSizeColumn(OFFSET + OFFSET_2);
        sheet.autoSizeColumn(OFFSET + OFFSET_2 + 1);
    }

}
