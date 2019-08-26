package elec332.kmaplanner.planner;

import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.opta.Roster;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 24-8-2019
 */
public class EventPrinter {

    public static Workbook printRoster(Roster roster, Workbook workbook){
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

    private static void writeGroup(Group group, Set<Event> eventz, Sheet sheet){
        int i = 2;
        Event[] events = eventz.toArray(new Event[0]);
        Row row_ = sheet.getRow(0);
        for (int j = 0; j < events.length; j++) {
            Event event = events[j];
            row_.getCell(j + 2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(event.name);
        }
        for (Iterator<Person> it = group.getPersonIterator(); it.hasNext(); ) {
            Person person = it.next();
            Row row = sheet.getRow(i);
            writePerson(row, person, events);
            i++;
        }
    }

    private static void writePerson(Row row, Person person, Event[] eventIndex){
        row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(person.getFirstName() + " " + person.getLastName());
        int i;
        for (i = 0; i < eventIndex.length; i++) {
            if (person.events.contains(eventIndex[i])){
                row.getCell(i + 2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("XXX");
            }
        }
        row.getCell(i + 4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("" + person.getDuration(false));
    }

}
