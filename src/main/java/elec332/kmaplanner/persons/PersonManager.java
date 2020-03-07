package elec332.kmaplanner.persons;

import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.project.ProjectManager;
import elec332.kmaplanner.util.DefaultObjectManager;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 14-6-2019
 */
public final class PersonManager extends DefaultObjectManager<Person, ProjectManager.LoadData> {

    public PersonManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    private static final Group NULL_GROUP = new Group("NULL_GROUP");

    public static final Person NULL_PERSON = new Person("No", "Person") {

        @Override
        public boolean canParticipateIn(Event event) {
            return true;
        }

        @Override
        public Group getMainGroup() {
            return NULL_GROUP;
        }

    };

    private final GroupManager groupManager;

    @Override
    public void load(ProjectManager.LoadData projectReader) {
        projectReader.getPersons().forEach(this::addObject);
        objects_.forEach(p -> p.postRead(groupManager));
    }

    @Override
    protected void postRemoveObject(Person person) {
        Sets.newHashSet(person.getGroups()).forEach(person::removeFromGroup);
    }

    public Map<String, Person> makeNameMap() {
        return stream().collect(Collectors.toMap(Person::toString, Function.identity()));
    }

}
