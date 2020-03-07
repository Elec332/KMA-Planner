package elec332.kmaplanner.planner.opta;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.assignment.IInitialEventAssigner;
import elec332.kmaplanner.util.FileValidator;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.io.*;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 3-9-2019
 */
public class RosterIO {

    private static final int SAVE_VERSION = 2;

    @Nullable
    public static Roster readRoster(File file, Planner planner) throws IOException {
        file = FileValidator.checkFileLoad(file, ".kpa");
        if (file == null) {
            return null;
        }

        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);

        VersionedReader<RosterData> reader = createReader(planner);
        RosterData ld = dis.readVersionedObject(reader);

        dis.close();
        if (ld == null) {
            DialogHelper.showErrorMessageDialog("The roster data is not compatible with this project!", "Wrong project");
            return null;
        }
        return new Roster(ld, planner);
    }

    public static void writeRoster(Roster roster, File file) throws IOException {
        file = FileValidator.checkFileSave(file, ".kpa", true);
        if (file == null) {
            return;
        }

        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeObject(RosterIO::write, new RosterData(Preconditions.checkNotNull(roster)));

        dos.close();
    }

    private static VersionedReader<RosterData> createReader(Planner planner) {
        return new VersionedReader<>(stream -> read(stream, planner));
    }

    private static void write(IByteArrayDataOutputStream dos, RosterData data) {
        dos.setVersion(SAVE_VERSION);
        dos.writeUUID(data.projectUuid);
        dos.writeObjects(RosterIO::writeAssignment, data.assignments);
        dos.writeInt(data.getPOffset());
    }

    private static RosterData read(IByteArrayDataInputStream dis, Planner planner) {
        Map<String, Person> names = planner.getPersonManager().makeNameMap();
        UUID uuid = dis.readUUID();
        if (!planner.getProjectUuid().equals(uuid)) {
            return null;
        }
        Map<UUID, Event> eventMap = planner.getEventManager().stream().collect(Collectors.toMap(Event::getUuid, Function.identity()));
        ObjectReference<Boolean> noPers = new ObjectReference<>(false);
        List<Assignment> assignments = dis.readObjects(stream -> readAssignment(stream, names, eventMap, noPers));
        if (noPers.get()) {
            DialogHelper.showErrorMessageDialog("It seems that some persons have been removed. \nIt is advisable to disregard this save and plan a new roster, as that will probably be quicker.", "Missing persons");
        }
        int po = 0;
        if (dis.availableBytes() > 0) {
            po = dis.readInt();
        }
        return new RosterData(assignments, uuid, po).checkAssignments(planner);
    }

    private static void writeAssignment(IByteArrayDataOutputStream stream, Assignment assignment) {
        stream.writeUTF(assignment.person.toString());
        stream.writeUUID(assignment.event.getUuid());
    }

    private static Assignment readAssignment(IByteArrayDataInputStream stream, Map<String, Person> names, Map<UUID, Event> eventMap, ObjectReference<Boolean> noPers) {
        String name = stream.readUTF();
        Person p = names.get(name);
        if (p == null) {
            p = PersonManager.NULL_PERSON;
            noPers.set(true);
        }
        Assignment ret = new Assignment(Preconditions.checkNotNull(eventMap.get(stream.readUUID())));
        ret.person = p;
        return ret;
    }

    @SuppressWarnings("WeakerAccess")
    public static class RosterData {

        private RosterData(Roster roster) {
            this(roster.getAssignments(), roster.getProjectUuid(), roster.groupOffset);
            this.init = true;
        }

        private RosterData(@Nonnull List<Assignment> assignments, @Nonnull UUID projectUuid, int pOffset) {
            this.assignments = Preconditions.checkNotNull(assignments);
            this.projectUuid = Preconditions.checkNotNull(projectUuid);
            this.init = false;
            this.pOffset = Math.min(pOffset, 1);
        }

        private final List<Assignment> assignments;
        private final UUID projectUuid;
        private final int pOffset;

        private boolean init;

        @Nonnull
        public List<Assignment> getAssignments() {
            checkInit();
            return Preconditions.checkNotNull(assignments);
        }

        @Nonnull
        public UUID getProjectUuid() {
            checkInit();
            return projectUuid;
        }

        public int getPOffset() {
            return pOffset;
        }

        private void checkInit() {
            if (!init) {
                throw new IllegalStateException();
            }
        }

        private <T> RosterData checkAssignments(Planner planner) {
            if (init) {
                throw new IllegalStateException();
            }
            if (!planner.getProjectUuid().equals(projectUuid)) {
                return null;
            }

            Collection<Person> persons = planner.getPersonManager().getObjects();
            final List<Person> persons_ = Collections.unmodifiableList(Lists.newArrayList(persons));

            @SuppressWarnings("unchecked")
            IInitialEventAssigner<T> assigner = (IInitialEventAssigner<T>) planner.getSettings().sortingType.createEventAssigner();
            ObjectReference<T> data = new ObjectReference<>(assigner.createInitialData(Lists.newArrayList(persons), Sets.newHashSet(planner.getEventManager().getObjects()), planner));

            Set<Event> events = planner.getEventManager().stream()
                    .filter(e -> !e.everyone)
                    .collect(Collectors.toSet());

            Multimap<Event, Assignment> map = HashMultimap.create();
            assignments.forEach(a -> map.put(a.event, a));
            final List<Assignment> assignments = Lists.newArrayList();

            events.forEach(event -> {
                int required = event.requiredPersons;
                Collection<Assignment> al1 = map.get(event);
                if (al1 == null) {
                    List<Assignment> eventAssignments = Lists.newArrayList();
                    for (int i = 0; i < required; i++) {
                        eventAssignments.add(new Assignment(event));
                    }
                    data.use(d -> assigner.assignPersonsTo(Collections.unmodifiableList(eventAssignments), event, persons_, d, planner));
                    assignments.addAll(eventAssignments);
                } else if (required >= al1.size()) {
                    assignments.addAll(al1);
                    if (required > al1.size()) {
                        List<Assignment> eventAssignments = Lists.newArrayList();
                        for (int i = 0; i < required - al1.size(); i++) {
                            eventAssignments.add(new Assignment(event));
                        }
                        data.use(d -> assigner.assignPersonsTo(Collections.unmodifiableList(eventAssignments), event, persons_, d, planner));
                        assignments.addAll(eventAssignments);
                    }
                } else {
                    Iterator<Assignment> it = al1.iterator();
                    for (int i = 0; i < required; i++) {
                        assignments.add(it.next());
                    }
                }
            });
            RosterData ret = new RosterData(assignments, projectUuid, pOffset);
            ret.init = true;
            return ret;
        }

    }

}
