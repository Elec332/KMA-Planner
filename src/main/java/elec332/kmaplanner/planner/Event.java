package elec332.kmaplanner.planner;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.util.ITimeSpan;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Elec332 on 13-8-2019
 */
public class Event implements Serializable, Comparable<Event>, Cloneable, ITimeSpan {

    public static final long serialVersionUID = -3746563830174761046L;

    public Event(String name, Date start, Date end, int requiredPersons){
        this.name = Preconditions.checkNotNull(name);
        if (Preconditions.checkNotNull(start).after(Preconditions.checkNotNull(end))){
            Date rem = start;
            start = end;
            end = rem;
        }
        this.start = start;
        this.end = end;
        this.requiredPersons = requiredPersons;
    }

    public String name;
    public Date start, end;
    public int requiredPersons;
    public boolean everyone;

    public long getDuration(){
        return getDuration(TimeUnit.MINUTES);
    }

    public int getRequiredPersons() {
        return requiredPersons;
    }

    public boolean canPersonParticipate(Person person){
        return true;
    }

    @Override
    public int compareTo(Event o) {
        int ret = start.compareTo(o.start);
        if (ret == 0){
            int ret2 = end.compareTo(o.end);
            if (ret2 == 0){
                return name.compareTo(o.name);
            }
            return ret2;
        }
        return ret;
    }

    @Override
    public String toString() {
        return name + "  " + start + " -> " + end + "   Pers: " + (everyone ? "Everyone" : requiredPersons);
    }

    @Override
    public Event clone() {
        try {
            return (Event) super.clone();
        } catch (Exception e) {
            //Impossible
        }
        throw new RuntimeException();
    }

    @Override
    public Date start() {
        return start;
    }

    @Override
    public Date end() {
        return end;
    }

}