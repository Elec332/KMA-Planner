package elec332.kmaplanner.project;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.util.UpdatableTreeSet;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 2-9-2019
 */
public class KMAPlannerProject {

    KMAPlannerProject(@Nonnull PersonManager personManager, @Nonnull GroupManager groupManager, @Nonnull UpdatableTreeSet<Event> events, @Nonnull ProjectSettings settings, @Nonnull UUID id, File file) {
        this.personManager = Preconditions.checkNotNull(personManager);
        this.groupManager = Preconditions.checkNotNull(groupManager);
        this.events = Preconditions.checkNotNull(events);
        this.settings = Preconditions.checkNotNull(settings);
        this.id = Preconditions.checkNotNull(id);
        this.file = file;
        this.dirty = false;
        init();
    }

    private void init() {
        getPersonManager().addCallback(this::markDirty);
        getGroupManager().addCallback(this::markDirty);
    }

    @Nonnull
    private final PersonManager personManager;
    @Nonnull
    private final GroupManager groupManager;
    @Nonnull
    private final UpdatableTreeSet<Event> events;
    @Nonnull
    private final ProjectSettings settings;
    @Nonnull
    private final UUID id;

    private File file;
    private boolean dirty;

    @Nonnull
    public PersonManager getPersonManager() {
        return personManager;
    }

    @Nonnull
    public GroupManager getGroupManager() {
        return groupManager;
    }

    @Nonnull
    public UpdatableTreeSet<Event> getEvents() {
        return events;
    }

    @Nonnull
    public ProjectSettings getSettings() {
        return settings;
    }

    @Nonnull
    public UUID getUuid() {
        return id;
    }

    public Optional<Path> getSaveLocation() {
        return Optional.ofNullable(file).map(File::toPath);
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void saveIfPossible() {
        if (file != null) {
            try {
                save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(Supplier<File> noFile) throws IOException {
        if (file == null) {
            file = noFile.get();
        }
        //File can still be null
        save(file);
    }

    public void save(File file) throws IOException {
        ProjectManager.write(this, file);
        this.dirty = false;
    }

}
