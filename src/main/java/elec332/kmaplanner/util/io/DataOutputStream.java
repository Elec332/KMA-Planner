package elec332.kmaplanner.util.io;

import elec332.kmaplanner.util.io.impl.ByteArrayDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 26-8-2019
 */
public class DataOutputStream extends FilterOutputStream {

    public DataOutputStream(OutputStream out) {
        super(out);
    }

    public <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, Collection<T> objects) throws IOException {
        writeObjects(serializer, new ArrayList<>(objects));
    }

    public void writeObjects(Collection<? extends IDataSerializable> writers) throws IOException {
        writeObjects(new ArrayList<>(writers));
    }

    @SuppressWarnings("all")
    public <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, List<T> objects) throws IOException {
        int siz = objects.size();
        writeInt(siz);
        for (int i = 0; i < siz; i++) {
            writeObject(serializer, objects.get(i));
        }
    }

    public <T> void writeObject(BiConsumer<IByteArrayDataOutputStream, T> serializer, final T object) throws IOException {
        writeObject(c -> serializer.accept(c, object));
    }

    @SuppressWarnings("all")
    public void writeObjects(List<IDataSerializable> writers) throws IOException {
        int siz = writers.size();
        writeInt(siz);
        for (int i = 0; i < siz; i++) {
            writeObject(writers.get(i));
        }
    }

    public void writeObject(IDataSerializable writer) throws IOException {
        writeObject(writer::writeObject);
    }

    public void writeObject(Consumer<IByteArrayDataOutputStream> writer) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IByteArrayDataOutputStream dos = new ByteArrayDataOutputStream(bos);
        writer.accept(dos);
        writeByteArray(bos.toByteArray());
    }

    private void writeByteArray(byte[] bytes) throws IOException {
        writeInt(bytes.length);
        write(bytes);
    }

    private void writeInt(int i) throws IOException {
        write((i >>> 24) & 0xFF);
        write((i >>> 16) & 0xFF);
        write((i >>> 8) & 0xFF);
        write((i >>> 0) & 0xFF);
    }

}
