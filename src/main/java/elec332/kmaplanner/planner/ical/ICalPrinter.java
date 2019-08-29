package elec332.kmaplanner.planner.ical;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.util.DateHelper;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Elec332 on 29-8-2019
 */
public class ICalPrinter {

    public static void fillCalender(net.fortuna.ical4j.model.Calendar calendar, Person person) {
        person.getPrintableEvents().forEach(event -> calendar.getComponents().add(createEvent(event.start, event.end, event.name)));
        calendar.getProperties().add(new ProdId("-//KMA Event Calendar//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
    }

    private static VEvent createEvent(Date start, Date end, String desc) {
        return createEvent(DateHelper.getCalendar(start), DateHelper.getCalendar(end), desc);
    }

    private static VEvent createEvent(Calendar start, Calendar end, String desc) {
        if (!start.getTimeZone().equals(end.getTimeZone())) {
            throw new IllegalArgumentException();
        }

        DateTime startT = new DateTime(true);
        startT.setTime(start.getTimeInMillis());
        DateTime endT = new DateTime(true);
        endT.setTime(end.getTimeInMillis());

        VEvent ret = new VEvent(startT, endT, desc);
        ret.getProperties().add(UTC);
        ret.getProperties().add(new Uid(UUID.randomUUID().toString()));
        return ret;
    }

    private static final TzId UTC = new TzId("UTC");

}
