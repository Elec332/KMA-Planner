package elec332.kmaplanner.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 26-8-2019
 */
public class DataOutputStream extends FilterOutputStream implements IByteArrayObjectWriter<IOException> {

    public DataOutputStream(OutputStream out) {
        super(out);
    }

    public <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, Collection<T> objects) throws IOException {
        writeObjects(serializer, new ArrayList<>(objects));
    }

    public void writeObjects(Collection<? extends IDataSerializable> writers) throws IOException {
        writeObjects(new ArrayList<>(writers));
    }


    @Override
    @SuppressWarnings("all")
    public void writeInt(int i) throws IOException {
        write((i >>> 24) & 0xFF);
        write((i >>> 16) & 0xFF);
        write((i >>> 8) & 0xFF);
        write((i >>> 0) & 0xFF); //Looks neater
    }

}
