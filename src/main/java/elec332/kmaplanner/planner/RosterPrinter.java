package elec332.kmaplanner.planner;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.ical.ICalPrinter;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.print.PersonPrinter;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.ZipHelper;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Created by Elec332 on 30-8-2019
 */
public class RosterPrinter {

    public static void printRoster(Roster roster) {
        roster.print();
        roster.apply();
        File tempFolder = new File(FileHelper.getExecFolder(), UUID.randomUUID().toString());
        if (!tempFolder.mkdirs()) {
            throw new RuntimeException(new IOException()); //No, this doesn't exist, look away please....
        }
        for (Person p : roster.getPersons()) {
            try {
                File personFile = new File(tempFolder, p.getLastName() + " - " + p.getFirstName() + ".xlsx");
                Workbook workbook = new XSSFWorkbook();
                PersonPrinter.printRoster(p, workbook);
                FileOutputStream fos = new FileOutputStream(personFile);
                workbook.write(fos);
                fos.close();

                if (p.getPrintableEvents().isEmpty()) {
                    continue;
                }

                File calFile = new File(tempFolder, p.getLastName() + " - " + p.getFirstName() + ".ics");
                CalendarOutputter co = new CalendarOutputter();
                fos = new FileOutputStream(calFile);
                Calendar cal = new Calendar();
                ICalPrinter.fillCalender(cal, p);
                co.output(cal, fos);
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            File dest = new File(FileHelper.getExecFolder(), "user_progs.zip");
            Files.deleteIfExists(dest.toPath());
            ZipHelper.zip(tempFolder, dest);
            FileHelper.deleteDir(tempFolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
