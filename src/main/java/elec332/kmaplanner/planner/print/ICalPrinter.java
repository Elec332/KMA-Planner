package elec332.kmaplanner.planner.print;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.util.DateHelper;
import elec332.kmaplanner.util.IObjectPrinter;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Elec332 on 29-8-2019
 */
public class ICalPrinter implements IObjectPrinter<Person> {

    private static final TzId UTC = new TzId("UTC");

    @Override
    public void printObject(File file, Person person) throws IOException {
        if (person.getPrintableEvents().isEmpty()) {
            return;
        }
        CalendarOutputter co = new CalendarOutputter();
        FileOutputStream fos = new FileOutputStream(Preconditions.checkNotNull(file));
        net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
        fillCalender(cal, person);
        co.output(cal, fos);
        fos.close();
    }

    @Override
    public String getDefaultFileExtension() {
        return "ics";
    }

    private void fillCalender(net.fortuna.ical4j.model.Calendar calendar, Person person) {
        System.out.println("Filling calender for: " + person + ", group: " + person.getMainGroup());
        person.getPrintableEvents().forEach(event -> calendar.getComponents().add(createEvent(event.start, event.end, event.name)));
        calendar.getProperties().add(new ProdId("-//KMA Event Calendar//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
    }

    private VEvent createEvent(Date start, Date end, String desc) {
        return createEvent(DateHelper.getCalendar(start), DateHelper.getCalendar(end), desc);
    }

    private VEvent createEvent(Calendar start, Calendar end, String desc) {
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

}
