package elec332.kmaplanner.planner.opta;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.print.DaySheetPrinter;
import elec332.kmaplanner.planner.print.ExportPrinter;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.helpers.IInitialEventAssigner;
import elec332.kmaplanner.util.IOHelper;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.io.DataInputStream;
import elec332.kmaplanner.util.io.DataOutputStream;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 15-8-2019
 */
@PlanningSolution
@SuppressWarnings({"WeakerAccess", "unused"})
public class Roster {

    public static <T> Roster readRoster(InputStream is, Planner planner) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        AssignmentIOR io = new AssignmentIOR(planner);
        List<Assignment> ioAss = dis.readObjects(io);

        Roster roster = new Roster(planner);

        Collection<Person> persons = planner.getPersonManager().getPersons();
        final List<Person> persons_ = Collections.unmodifiableList(Lists.newArrayList(persons));

        @SuppressWarnings("unchecked")
        IInitialEventAssigner<T> assigner = (IInitialEventAssigner<T>) planner.getSettings().sortingType.createEventAssigner();
        ObjectReference<T> data = new ObjectReference<>(assigner.createInitialData(Lists.newArrayList(persons), Sets.newHashSet(planner.getEvents()), planner.getSettings()));

        Set<Event> events = planner.getEvents().stream()
                .filter(e -> !e.everyone)
                .collect(Collectors.toSet());

        Multimap<Event, Assignment> map = HashMultimap.create();
        ioAss.forEach(a -> map.put(a.event, a));

        events.forEach(event -> {
            int required = event.requiredPersons;
            Collection<Assignment> al1 = map.get(event);
            if (required >= al1.size()){
                roster.assignments.addAll(al1);
                if (required > al1.size()){
                    System.out.println("Adding extra");
                    List<Assignment> eventAssignments = Lists.newArrayList();
                    for (int i = 0; i < required - al1.size(); i++) {
                        eventAssignments.add(new Assignment(event));
                    }
                    data.use(d -> assigner.assignPersonsTo(Collections.unmodifiableList(eventAssignments), event, persons_, d, planner.getSettings()));
                    roster.assignments.addAll(eventAssignments);
                }
            } else {
                System.out.println("Removing some...");
                Iterator<Assignment> it = al1.iterator();
                for (int i = 0; i < required; i++) {
                    roster.assignments.add(it.next());
                }
            }
        });

        return roster;
    }

    public <T> Roster(final Planner planner, IInitialEventAssigner<T> assigner) {
        this(planner);
        Collection<Person> persons = planner.getPersonManager().getPersons();
        ObjectReference<T> data = new ObjectReference<>(assigner.createInitialData(Lists.newArrayList(persons), Sets.newHashSet(planner.getEvents()), planner.getSettings()));
        final List<Person> persons_ = Collections.unmodifiableList(Lists.newArrayList(persons));
        planner.getEvents().stream().filter(e -> !e.everyone).forEach(event -> {
            List<Assignment> eventAssignments = Lists.newArrayList();
            for (int i = 0; i < event.requiredPersons; i++) {
                eventAssignments.add(new Assignment(event));
            }
            data.use(d -> assigner.assignPersonsTo(Collections.unmodifiableList(eventAssignments), event, persons_, d, planner.getSettings()));
            assignments.addAll(eventAssignments);
        });

    }

    private Roster(Planner planner){
        this();
        this.planner = planner;
        persons.addAll(this.planner.getPersonManager().getPersons());
        persons.add(PersonManager.NULL_PERSON);
    }

    private Roster() {
        this.assignments = Lists.newArrayList();
        this.persons = Lists.newArrayList();
    }

    private Planner planner;

    private List<Person> persons;
    private List<Assignment> assignments;
    private HardMediumSoftScore score;

    public Planner getPlanner() {
        return planner;
    }

    @ValueRangeProvider(id = "persons")
    @ProblemFactCollectionProperty
    public List<Person> getPersons() {
        return persons;
    }

    @PlanningEntityCollectionProperty
    public List<Assignment> getAssignments() {
        return assignments;
    }

    @PlanningScore
    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    public void apply() {
        getPlanner().getPersonManager().getPersons().forEach(p -> p.events.clear());
        for (Assignment assignment : getAssignments()) {
            assignment.person.events.add(assignment.event);
        }
        planner.getEvents().stream()
                .filter(e -> e.everyone)
                .forEach(e -> planner.getPersonManager().getPersons().forEach(p -> p.events.add(e)));

    }

    public void print() {
        apply();
        debugPrint();
        try {
            Workbook workbook = new XSSFWorkbook();
            ExportPrinter.printRoster(this, workbook);
            File f = new File(IOHelper.getExecFolder(), "export.xlsx");
            FileOutputStream fos = new FileOutputStream(f);
            workbook.write(fos);
            fos.close();

            workbook = new XSSFWorkbook();
            DaySheetPrinter.printRoster(this, workbook);
            f = new File(IOHelper.getExecFolder(), "days.xlsx");
            fos = new FileOutputStream(f);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "Failed to export planner.", "Export failed!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void debugPrint() {
        planner.getPersonManager().getPersons().forEach(p -> {
            System.out.println();
            System.out.println(p);
            p.events.forEach(System.out::println);
            System.out.println();
        });
        System.out.println();
        System.out.println(getScore());
        System.out.println();
        getAssignments().forEach(System.out::println);
        System.out.println();
        System.out.println("Average: " + RosterScoreCalculator.calculateAverage(this));
        System.out.println("Score: " + getScore());
    }

    public void write(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        BiConsumer<IByteArrayDataOutputStream, Assignment> writer = (stream, assignment) -> {
            stream.writeUTF(assignment.person.toString());
            stream.writeUUID(assignment.event.getUuid());
        };
        dos.writeObjects(writer, assignments);
    }

    private static class AssignmentIOR implements Function<IByteArrayDataInputStream, Assignment> {

        private AssignmentIOR(Planner planner){
            names = planner.getPersonManager().makeNameMap();
            events = planner.getEvents().stream().collect(Collectors.toMap(Event::getUuid, Function.identity()));
        }

        private final Map<String, Person> names;
        private final Map<UUID, Event> events;

        @Override
        public Assignment apply(IByteArrayDataInputStream stream) {
            Person p = names.get(stream.readUTF());
            Assignment ret = new Assignment(Preconditions.checkNotNull(events.get(stream.readUUID())));
            ret.person = p;
            return ret;
        }

    }

}
