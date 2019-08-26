package elec332.kmaplanner.io;

import elec332.kmaplanner.util.PersonSortingType;
import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;

/**
 * Created by Elec332 on 14-6-2019
 */
public final class ProjectSettings implements IDataSerializable {

    public long seed;
    public PersonSortingType sortingType = PersonSortingType.RANDOM;

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeLong(seed);
        stream.writeByte(sortingType.ordinal());
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        this.seed = stream.readLong();
        sortingType = PersonSortingType.values()[stream.readByte()];
    }

}
