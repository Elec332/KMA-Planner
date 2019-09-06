package elec332.kmaplanner.project;

import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;

/**
 * Created by Elec332 on 4-9-2019
 */
public class ProjectSettings implements IDataSerializable {

    public boolean enableCompression = true;

    @Override
    public void writeObject(IByteArrayDataOutputStream stream) {
        stream.writeBoolean(enableCompression);
    }

    @Override
    public void readObject(IByteArrayDataInputStream stream) {
        enableCompression = stream.readBoolean();
    }

}
