package elec332.kmaplanner.io;

import elec332.kmaplanner.util.PersonSortingType;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;

import java.util.Random;

/**
 * Created by Elec332 on 14-6-2019
 */
public final class ProjectSettings implements IDataSerializable {

    public long seed = new Random().nextLong();
    public PersonSortingType sortingType = PersonSortingType.RANDOM;
    public int unimprovedSeconds = 30;
    public int timeDiffThreshold = 45;
    public int mainGroupFactor = 5;

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeLong(seed);
        stream.writeByte(sortingType.ordinal());
        stream.writeInt(unimprovedSeconds);
        stream.writeInt(timeDiffThreshold);
        stream.writeInt(mainGroupFactor);
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        this.seed = stream.readLong();
        sortingType = PersonSortingType.values()[stream.readByte()];
        if (stream.availableBytes() > 0){
            unimprovedSeconds = stream.readInt();
            timeDiffThreshold = stream.readInt();
        }
        if (stream.availableBytes() > 0){
            mainGroupFactor = stream.readInt();
        }
    }

}
