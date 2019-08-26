package elec332.kmaplanner.util.io;

import elec332.kmaplanner.util.io.impl.ByteArrayDataInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 26-8-2019
 */
public class DataInputStream extends FilterInputStream {

    public DataInputStream(InputStream in) {
        super(in);
    }

    public <T> List<T> readObjects(Function<IByteArrayDataInputStream, T> deserializer) throws IOException {
        int siz = readInt();
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < siz; i++) {
            ret.add(readObject(deserializer));
        }
        return ret;
    }

    public <T> T readObject(Function<IByteArrayDataInputStream, T> deserializer) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(readByteArray());
        IByteArrayDataInputStream dis = new ByteArrayDataInputStream(bis);
        return deserializer.apply(dis);
    }

    public <T extends IDataSerializable> List<T> readObjects(Supplier<T> typeCreator) throws IOException {
        int siz = readInt();
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < siz; i++) {
            T add = typeCreator.get();
            readObject(add);
            ret.add(add);
        }
        return ret;
    }

    public <T extends IDataSerializable> T readObject(T serializable) throws IOException {
        readObject((Consumer<IByteArrayDataInputStream>) serializable::readObject);
        return serializable;
    }

    public void readObject(Consumer<IByteArrayDataInputStream> reader) throws IOException {
        readObject((Function<IByteArrayDataInputStream, Void>) t -> {
            reader.accept(t);
            return null;
        });
    }

    private byte[] readByteArray() throws IOException {
        int len = readInt();
        byte[] ret = new byte[len];
        int q = read(ret);
        if (len != q) {
            throw new EOFException();
        }
        return ret;
    }

    private int readInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4);
    }

}
