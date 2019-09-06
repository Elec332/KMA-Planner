package elec332.kmaplanner.events;

import elec332.kmaplanner.project.ProjectManager;
import elec332.kmaplanner.util.DefaultObjectManager;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Elec332 on 4-9-2019
 */
public class EventManager extends DefaultObjectManager<Event, ProjectManager.LoadData> {

    public EventManager() {
    }

    @Override
    public void load(ProjectManager.LoadData reader) {
        reader.getEvents().forEach(this::addObject);
    }

    public Date getFirstDate() {
        if (getObjects().isEmpty()) {
            return new Date();
        }
        return (Date) objects.first().start.clone();
    }

    public Date getLastDate() {
        if (getObjects().isEmpty()) {
            return new Date();
        }
        return (Date) objects.last().end.clone();
    }

    @Override
    protected Comparator<Event> getComparator() {
        return Comparator.naturalOrder();
    }

}
