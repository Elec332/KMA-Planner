package elec332.kmaplanner.planner.print;

import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.Roster;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 24-8-2019
 */
public class ExportPrinter {

    private static final int NAME_EVENT_OFFSET = 3;

    public static Workbook printRoster(Roster roster, Workbook workbook) {
        roster.apply();
        Set<Group> groups = roster.getPersons().stream()
                .map(Person::getGroups)
                .flatMap(Set::stream)
                .filter(Group::isMainGroup)
                .collect(Collectors.toSet());

        groups.forEach(group -> {
            Sheet sheet = workbook.createSheet(group.getName());
            writeGroup(group, roster.getPlanner().getEvents(), sheet);
        });

        return workbook;
    }

    private static void writeGroup(Group group, Set<Event> eventz, Sheet sheet) {
        int i = 2;
        Event[] events = eventz.toArray(new Event[0]);
        Row row_ = sheet.getRow(0);
        if (row_ == null) {
            row_ = sheet.createRow(0);
        }
        int j;
        for (j = 0; j < events.length; j++) {
            Event event = events[j];
            row_.getCell(j + NAME_EVENT_OFFSET, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(event.name);
            //sheet.setColumnWidth(j + NAME_EVENT_OFFSET, 20 * 256); //20 characters
            sheet.autoSizeColumn(j + NAME_EVENT_OFFSET);
        }
        row_.getCell(j + NAME_EVENT_OFFSET + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("Minutes (NE)");
        for (Iterator<Person> it = group.getPersonIterator(); it.hasNext(); ) {
            Person person = it.next();
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            writePerson(row, person, events);
            i++;
        }
    }

    private static void writePerson(Row row, Person person, Event[] eventIndex) {
        row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(person.getFirstName() + " " + person.getLastName());
        int i;
        for (i = 0; i < eventIndex.length; i++) {
            if (person.events.contains(eventIndex[i])) {
                row.getCell(i + NAME_EVENT_OFFSET, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("XXX");
            }
        }
        row.getCell(i + NAME_EVENT_OFFSET + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("" + person.getDuration(false));
    }

}
