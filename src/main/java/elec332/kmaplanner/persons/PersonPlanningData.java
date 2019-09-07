package elec332.kmaplanner.persons;

import com.google.common.collect.Sets;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.planner.opta.Roster;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Created by Elec332 on 30-8-2019
 */
public class PersonPlanningData {

    PersonPlanningData(Person person) {
        this.person = person;
        this.events = Sets.newHashSet();
        this.events_ = Collections.unmodifiableSet(events);
        this.checkEvents = Sets.newHashSet();
    }

    private final Person person;

    private Set<Event> events, events_, checkEvents;
    private Group mainGroup;
    private Long duration, durationPublic, durationSoft;

    public Group getMainGroup() {
        if (mainGroup == null) {
            mainGroup = person.getMainGroup();
        }
        return mainGroup;
    }

    public Set<Event> getCheckEvents() {
        return checkEvents;
    }

    public void clearEvents() {
        events.clear();
        checkEvents.clear();
        eventsChanged();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addEvent(Event event) {
        boolean ret = events.add(event);
        eventsChanged();
        return ret;
    }

    public void importEvents() {
        events.clear();
        events.addAll(person.getPrintableEvents());
        eventsChanged();
    }

    private void eventsChanged() {
        duration = durationPublic = durationSoft = null;
    }

    public Set<Event> getEvents() {
        return events_;
    }

    public long getSoftDuration(Roster roster) {
        return getSoftDuration(roster.getAveragePersonTimeReal(), roster.getStartDate(), roster.getEndDate());
    }

    public long getSoftDuration(long avg, Date start, Date end) {
        if (durationSoft == null) {
            long duration = getDuration();
            durationSoft = duration + person.getGroups().stream()
                    .mapToLong(g -> g.getSoftDuration(duration, avg, start, end))
                    .sum() + person.getFilters().stream()
                    .mapToLong(f -> f.getSoftDuration(duration, avg, start, end))
                    .sum();
        }
        return durationSoft;
    }

    public long getDuration() {
        return getDuration(false);
    }

    public long getDuration(boolean publicE) {
        if (publicE && durationPublic == null) {
            durationPublic = person.getDuration(events, true);
        }
        if (!publicE && duration == null) {
            duration = person.getDuration(events, false);
        }
        return publicE ? durationPublic : duration;
    }

}
