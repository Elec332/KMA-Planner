package elec332.kmaplanner.project;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.util.FileValidator;
import elec332.kmaplanner.util.UpdatableTreeSet;
import elec332.kmaplanner.util.io.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Elec332 on 2-9-2019
 */
public class ProjectManager {

    private static final int SAVE_VERSION = 2;
    private static final VersionedReader<LoadData> reader = new VersionedReader<>(ProjectManager::read);

    public static KMAPlannerProject createNewProject() {
        GroupManager g = new GroupManager();
        return new KMAPlannerProject(new PersonManager(g), g, new UpdatableTreeSet<>(), new ProjectSettings(), UUID.randomUUID(), null);
    }

    @Nullable
    public static KMAPlannerProject loadProject(File projFile) throws IOException {
        LoadData ld = loadFile(projFile);
        if (ld == null) {
            return null;
        }

        GroupManager groupManager = new GroupManager();
        PersonManager personManager = new PersonManager(groupManager);
        UpdatableTreeSet<Event> events = new UpdatableTreeSet<>();
        groupManager.load(ld);
        personManager.load(ld);
        events.addAll(ld.getEvents());

        return new KMAPlannerProject(personManager, groupManager, events, ld.getSettings(), ld.getUuid(), projFile);
    }

    @Nullable
    public static LoadData loadFile(File projFile) throws IOException {
        projFile = FileValidator.checkFileLoad(projFile, ".kp");
        if (projFile == null) {
            return null;
        }

        FileInputStream fis = new FileInputStream(projFile);
        DataInputStream dis = new DataInputStream(fis);

        LoadData ld = Preconditions.checkNotNull(dis.readVersionedObject(reader));

        dis.close();
        return ld;
    }

    static void write(KMAPlannerProject project, File projFile) throws IOException {
        projFile = FileValidator.checkFileSave(projFile, ".kp", true);
        if (projFile == null) {
            return;
        }

        FileOutputStream fos = new FileOutputStream(projFile);
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeObject(ProjectManager::write, new LoadData(
                project.getSettings(),
                project.getPersonManager().getPersons(),
                project.getGroupManager().getGroups(),
                project.getEvents(),
                project.getUuid()));

        dos.close();
    }

    private static void write(IByteArrayDataOutputStream dos, LoadData data) {
        Preconditions.checkNotNull(data);
        dos.setVersion(SAVE_VERSION);
        dos.writeObject(data.getSettings());
        dos.writeObjects(data.getPersons());
        dos.writeObjects(data.getGroups());
        dos.writeObjects(data.getEvents());
        dos.writeUUID(data.getUuid());
    }

    private static LoadData read(IByteArrayDataInputStream dis) {
        int version = dis.getVersion();
        System.out.println("Reading file version " + version);
        ProjectSettings projectData = dis.readObject(new ProjectSettings());
        Set<Person> persons = Sets.newHashSet(dis.readObjects(() -> new Person("", "")));
        Set<Group> groups = Sets.newHashSet(dis.readObjects(() -> new Group("")));
        Set<Event> events = Sets.newHashSet(dis.readObjects(() -> new Event("", new Date(), new Date(), -1)));
        UUID id;
        if (dis.availableBytes() > 0) {
            id = dis.readUUID();
        } else {
            id = UUID.randomUUID();
        }
        return new LoadData(projectData, persons, groups, events, id);
    }

    public static class LoadData {

        private LoadData(ProjectSettings projectData, Set<Person> persons, Set<Group> groups, Set<Event> events, UUID id) {
            this.projectData = projectData;
            this.persons = persons;
            this.groups = groups;
            this.events = events;
            this.id = id;
        }

        private final ProjectSettings projectData;
        private final Set<Person> persons;
        private final Set<Group> groups;
        private final Set<Event> events;
        private final UUID id;

        public ProjectSettings getSettings() {
            return projectData;
        }

        public Set<Person> getPersons() {
            return persons;
        }

        public Set<Group> getGroups() {
            return groups;
        }

        public Set<Event> getEvents() {
            return events;
        }

        public UUID getUuid() {
            return id;
        }

    }

}
