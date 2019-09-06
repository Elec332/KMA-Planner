package elec332.kmaplanner.project;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.events.EventManager;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Planner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    KMAPlannerProject(@Nonnull PersonManager personManager, @Nonnull GroupManager groupManager, @Nonnull EventManager events, @Nonnull PlannerSettings settings, @Nonnull UUID id, @Nonnull ProjectSettings projSettings, File file) {
        this.personManager = Preconditions.checkNotNull(personManager);
        this.groupManager = Preconditions.checkNotNull(groupManager);
        this.events = Preconditions.checkNotNull(events);
        this.settings = Preconditions.checkNotNull(settings);
        this.id = Preconditions.checkNotNull(id);
        this.projSettings = Preconditions.checkNotNull(projSettings);
        this.file = file;
        this.dirty = false;
        init();
    }

    private void init() {
        getPersonManager().addCallback(this::markDirty);
        getGroupManager().addCallback(this::markDirty);
        getEventManager().addCallback(this::markDirty);

        //Todo: Properly implement planner
        planner = new Planner(this);
    }

    @Nonnull
    private final PersonManager personManager;
    @Nonnull
    private final GroupManager groupManager;
    @Nonnull
    private final EventManager events;
    @Nonnull
    private final PlannerSettings settings;
    @Nonnull
    private final UUID id;
    @Nonnull
    private final ProjectSettings projSettings;

    private Planner planner;
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
    public EventManager getEventManager() {
        return events;
    }

    @Nonnull
    public PlannerSettings getPlannerSettings() {
        return settings;
    }

    @Nonnull
    public ProjectSettings getProjectSettings() {
        return projSettings;
    }

    @Nonnull
    public UUID getUuid() {
        return id;
    }

    @Nonnull
    public Optional<Planner> getPlanner() {
        return Optional.ofNullable(planner);
    }

    public void setPlanner(@Nullable Planner planner) {
        this.planner = planner;
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
