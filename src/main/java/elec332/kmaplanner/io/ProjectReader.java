package elec332.kmaplanner.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.util.UpdatableTreeSet;

import java.io.*;
import java.util.Set;

/**
 * Created by Elec332 on 14-6-2019
 */
public class ProjectReader {

    public ProjectReader(File file, ProjectData data) {
        this.projFile = Preconditions.checkNotNull(file);
        this.projectData = Preconditions.checkNotNull(data);
        this.persons = Sets.newHashSet();
        this.groups = Sets.newHashSet();
    }

    public ProjectReader(File data) {
        this.projFile = Preconditions.checkNotNull(data);
    }

    private final File projFile;
    private ProjectData projectData;
    private Set<Person> persons;
    private Set<Group> groups;
    private Set<Event> events;

    @SuppressWarnings("unchecked")
    public ProjectReader read() throws IOException, ClassNotFoundException {
        if (!projFile.exists()) {
            throw new RuntimeException();
        }
        FileInputStream fis = new FileInputStream(projFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        projectData = (ProjectData) ois.readObject();
        persons = (Set<Person>) ois.readObject();
        groups = (Set<Group>) ois.readObject();
        events = (Set<Event>) ois.readObject();
        ois.close();
        return this;
    }

    public void write(PersonManager personManager, GroupManager groupManager, UpdatableTreeSet<Event> events) throws IOException {
        FileOutputStream fos = new FileOutputStream(projFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this.projectData);
        oos.writeObject(this.persons = personManager.getPersons());
        oos.writeObject(this.groups = groupManager.getGroups());
        oos.writeObject(this.events = Sets.newHashSet(events));
        oos.close();
    }

    public ProjectData getProjectData() {
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
