package elec332.kmaplanner.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.util.UpdatableTreeSet;
import elec332.kmaplanner.util.io.DataInputStream;
import elec332.kmaplanner.util.io.DataOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

/**
 * Created by Elec332 on 14-6-2019
 */
public class ProjectReader {

    public ProjectReader(File file, ProjectSettings data) {
        this.projFile = Preconditions.checkNotNull(file);
        this.projectData = Preconditions.checkNotNull(data);
        this.persons = Sets.newHashSet();
        this.groups = Sets.newHashSet();
    }

    public ProjectReader(File data) {
        this.projFile = Preconditions.checkNotNull(data);
    }

    private final File projFile;
    private ProjectSettings projectData;
    private Set<Person> persons;
    private Set<Group> groups;
    private Set<Event> events;

    public ProjectReader read() throws IOException {
        if (!projFile.exists()) {
            throw new RuntimeException();
        }
        FileInputStream fis = new FileInputStream(projFile);
        DataInputStream dis = new DataInputStream(fis);

        projectData = dis.readObject(new ProjectSettings());
        persons = Sets.newHashSet(dis.readObjects(() -> new Person("", "")));
        groups = Sets.newHashSet(dis.readObjects(() -> new Group("")));
        events = Sets.newHashSet(dis.readObjects(() -> new Event("", new Date(), new Date(), -1)));

        dis.close();
        return this;
    }

    public void write(PersonManager personManager, GroupManager groupManager, UpdatableTreeSet<Event> events) throws IOException {
        FileOutputStream fos = new FileOutputStream(projFile);
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeObject(this.projectData);
        dos.writeObjects(this.persons = personManager.getPersons());
        dos.writeObjects(this.groups = groupManager.getGroups());
        dos.writeObjects(this.events = Sets.newHashSet(events));

        dos.close();
    }

    public ProjectSettings getProjectData() {
        return Preconditions.checkNotNull(projectData);
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

}
