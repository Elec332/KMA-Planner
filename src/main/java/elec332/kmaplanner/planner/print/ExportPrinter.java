package elec332.kmaplanner.planner.print;

import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.util.AbstractExcelPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 24-8-2019
 */
public class ExportPrinter extends AbstractExcelPrinter<Roster> {

    private static final int NAME_EVENT_OFFSET = 3;


    @Override
    protected void printObject(Roster roster, Workbook workbook) {
        roster.apply();
        Set<Group> groups = roster.getPersons().stream()
                .map(Person::getGroups)
                .flatMap(Set::stream)
                .filter(Group::isMainGroup)
                .sorted()
                .collect(Collectors.toCollection(TreeSet::new));

        groups.forEach(group -> {
            Sheet sheet = workbook.createSheet(group.getName());
            writeGroup(group, roster.getPlanner().getEventManager().getObjects(), sheet);
        });
    }

    private void writeGroup(Group group, Set<Event> eventz, Sheet sheet) {
        int i = 2;
        Event[] events = eventz.toArray(new Event[0]);
        Row row_ = getOrCreateRow(sheet, 0);
        int j;
        for (j = 0; j < events.length; j++) {
            Event event = events[j];
            getCell(row_, j + NAME_EVENT_OFFSET).setCellValue(event.name);
            //sheet.setColumnWidth(j + NAME_EVENT_OFFSET, 20 * 256); //20 characters
            sheet.autoSizeColumn(j + NAME_EVENT_OFFSET);
        }
        getCell(row_, j + NAME_EVENT_OFFSET + 1).setCellValue("Minutes (NE)");
        for (Iterator<Person> it = group.getPersonIterator(); it.hasNext(); ) {
            Person person = it.next();
            Row row = getOrCreateRow(sheet, i);
            writePerson(row, person, events);
            i++;
        }
    }

    private void writePerson(Row row, Person person, Event[] eventIndex) {
        getCell(row, 0).setCellValue(person.getFirstName() + " " + person.getLastName());
        int i;
        for (i = 0; i < eventIndex.length; i++) {
            if (person.getPrintableEvents().contains(eventIndex[i])) {
                getCell(row, i + NAME_EVENT_OFFSET).setCellValue("XXX");
            }
        }
        getCell(row, i + NAME_EVENT_OFFSET + 1).setCellValue("" + person.getDuration(false));
    }

}
