package elec332.kmaplanner.planner;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.util.ITimeSpan;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Elec332 on 13-8-2019
 */
public class Event implements IDataSerializable, Comparable<Event>, Cloneable, ITimeSpan {

    public Event(String name, Date start, Date end, int requiredPersons) {
        this.name = Preconditions.checkNotNull(name);
        if (Preconditions.checkNotNull(start).after(Preconditions.checkNotNull(end))) {
            Date rem = start;
            start = end;
            end = rem;
        }
        this.start = start;
        this.end = end;
        this.requiredPersons = requiredPersons;
        this.uuid = UUID.randomUUID();
    }

    public String name;
    public Date start, end;
    public int requiredPersons;
    public boolean everyone;
    @PlanningId
    private UUID uuid;

    public long getDuration() {
        return getDuration(TimeUnit.MINUTES);
    }

    public int getRequiredPersons() {
        return requiredPersons;
    }

    public boolean canPersonParticipate(Person person) {
        return true;
    }

    @Override
    public int compareTo(Event o) {
        int ret = start.compareTo(o.start);
        if (ret == 0) {
            int ret2 = end.compareTo(o.end);
            if (ret2 == 0) {
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
            Event ret = (Event) super.clone();
            ret.uuid = uuid; //maybe?
            return ret;
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

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeUTF(name);
        stream.writeLong(start.getTime());
        stream.writeLong(end.getTime());
        stream.writeInt(requiredPersons);
        stream.writeBoolean(everyone);
        stream.writeUUID(uuid);
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        name = stream.readUTF();
        start = new Date(stream.readLong());
        end = new Date(stream.readLong());
        requiredPersons = stream.readInt();
        everyone = stream.readBoolean();
        if (stream.availableBytes() > 0) {
            uuid = stream.readUUID();
        }
    }

}
