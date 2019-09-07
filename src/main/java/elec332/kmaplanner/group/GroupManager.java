package elec332.kmaplanner.group;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.project.ProjectManager;
import elec332.kmaplanner.util.IObjectManager;
import elec332.kmaplanner.util.WeakCallbackHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 14-6-2019
 */
public class GroupManager implements IObjectManager<Group, ProjectManager.LoadData> {

    public GroupManager() {
        this.groups = Sets.newTreeSet();
        this.groups_ = Collections.unmodifiableSet(groups);
        this.reverseLookup = Maps.newTreeMap();
        this.callbacks = new WeakCallbackHandler();
    }

    public static final Group EVERYONE = new Group("Everyone") {

        @Override
        public boolean containsPerson(Person person) {
            return true;
        }

        @Override
        public int compareTo(@Nonnull Group o) {
            if (o == EVERYONE) {
                return 0;
            }
            return -1;
        }

    };

    private final Set<Group> groups, groups_;
    private final Map<String, Group> reverseLookup;
    private final WeakCallbackHandler callbacks;

//    @Override
//    public void addCallback(Runnable runnable) {
//        callbacks.add(runnable);
//    }


    @Override
    public void addCallback(Object weakKey, Runnable runnable) {
        callbacks.addCallback(weakKey, runnable);
    }

    @Override
    public void load(ProjectManager.LoadData projectReader) {
        projectReader.getGroups().forEach(this::addObject);
        groups_.forEach(Group::postRead);
    }

    @Override
    public boolean addObjectNice(Group group) {
        if (reverseLookup.containsKey(group.getName()) || !groups.add(group)) {
            return false;
        }
        reverseLookup.put(group.getName(), group);
        callbacks.runCallbacks();
        return true;
    }

    @Override
    public void removeObject(Group group) {
        if (!reverseLookup.containsKey(group.getName()) || !groups.contains(group)) {
            throw new IllegalArgumentException();
        }
        reverseLookup.remove(group.getName());
        groups.remove(group);
        Sets.newHashSet(group.getPersons()).forEach(p -> p.removeFromGroup(group));
        callbacks.runCallbacks();
    }

    @Override
    public void updateObject(Group group, Consumer<Group> consumer) {
        if (!reverseLookup.containsKey(group.getName())) {
            throw new IllegalArgumentException();
        }
        reverseLookup.remove(group.getName());
        if (groups.remove(group)) {
            consumer.accept(group);
            addObject(group);
        }
        callbacks.runCallbacks();
    }

    @Nonnull
    @Override
    public Set<Group> getObjects() {
        return this.groups_;
    }

    public Set<Group> getMainGroups() {
        return stream()
                .filter(Group::isMainGroup)
                .collect(Collectors.toSet());
    }

    public Group getGroup(String name) {
        return reverseLookup.get(name);
    }

    public Group getOrCreate(String name) {
        Group group = getGroup(name);
        if (group == null) {
            addObject(group = new Group(name));
        }
        return group;
    }

}
