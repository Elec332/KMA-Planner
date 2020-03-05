package elec332.kmaplanner.project;

import elec332.kmaplanner.planner.opta.assignment.PersonSortingType;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;

import java.util.Random;

/**
 * Created by Elec332 on 14-6-2019
 */
public final class PlannerSettings implements IDataSerializable {

    public long seed = new Random().nextLong();
    public PersonSortingType sortingType = PersonSortingType.GROUP;
    public int unimprovedSteps = 500;
    public int timeDiffThreshold = 45;
    public int mainGroupFactor = 5;

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeLong(seed);
        stream.writeByte(sortingType.ordinal());
        stream.writeInt(unimprovedSteps);
        stream.writeInt(timeDiffThreshold);
        stream.writeInt(mainGroupFactor);
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        this.seed = stream.readLong();
        sortingType = PersonSortingType.values()[stream.readByte()];
        unimprovedSteps = stream.readInt();
        timeDiffThreshold = stream.readInt();
        mainGroupFactor = stream.readInt();
    }

}
