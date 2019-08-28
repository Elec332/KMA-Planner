package elec332.kmaplanner.planner.print;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.util.PersonGroupSorter;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 28-8-2019
 */
public class DaySheetPrinter {

    public static Workbook printRoster(Roster roster, Workbook workbook) {
        roster.apply();
        Multimap<Integer, Event> eventDayMap = HashMultimap.create();

        List<Event> events = Lists.newArrayList();
        roster.getPlanner().getEvents().stream()
                .filter(e -> !e.everyone)
                .forEach(events::add);
        Date day = roster.getPlanner().getFirstDate();
        int i = 1;
        while (!events.isEmpty()){
            Date day2 = roster.getPlanner().getLastDate();
            for (Iterator<Event> it = events.iterator(); it.hasNext();){
                Event event = it.next();
                if (sameDay(day, event.start())){
                    it.remove();
                    eventDayMap.put(i, event);
                } else if (event.start().before(day2)){
                    day2 = event.start();
                }
            }
            day = day2;
            i++;
        }

        for (int j : eventDayMap.keySet()){
            Sheet sheet = workbook.createSheet("Day " + j);
            writeDay(sheet, eventDayMap.get(j), roster);
        }

        return workbook;
    }

    private static void writeDay(Sheet sheet, Collection<Event> events, Roster roster){
        int i = 0;
        for (Event event : Sets.newTreeSet(events)) {
            writeEvent(sheet, i, event, roster);
            i += 5;
        }
        getCell(getOrCreateRow(sheet, 0), i + 2).setCellValue("");
    }

    private static void writeEvent(Sheet sheet, int colStart, Event event, Roster roster){
        Row row = getOrCreateRow(sheet, 0);
        Cell cell = getCell(row, colStart);
        cell.setCellValue(event.name + "   " + event.requiredPersons + " pax");
        Set<Person> pst = roster.getAssignments().stream()
                .filter(a -> a.event == event)
                .map(a -> a.person).collect(Collectors.toSet());
        Map<Group, Set<Person>> pse = PersonGroupSorter.sortByGroup(pst, roster.getPlanner().getGroupManager());
        int r = 2;
        int u = 1;
        for (Group g : pse.keySet()){
            row = getOrCreateRow(sheet, r);
            getCell(row, colStart).setCellValue(g.getName());
            r++;
            for (Person p : pse.get(g)){
                row = getOrCreateRow(sheet, r);
                getCell(row, colStart).setCellValue("" + u);
                getCell(row, colStart + 1).setCellValue(p.getFirstName() + " | " + p.getLastName());
                u++;
                r++;
            }
            r++;
        }
    }

    private static Row getOrCreateRow(Sheet sheet, int row){
        Row ret = sheet.getRow(row);
        if (ret == null) {
            ret = sheet.createRow(row);
        }
        return ret;
    }

    private static Cell getCell(Row row, int cell){
        return row.getCell(cell, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    private static boolean sameDay(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

}
