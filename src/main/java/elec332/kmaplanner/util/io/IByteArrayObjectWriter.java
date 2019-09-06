package elec332.kmaplanner.util.io;

import elec332.kmaplanner.util.io.impl.ByteArrayDataOutputStream;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Elec332 on 3-9-2019
 */
@SuppressWarnings("all")
public interface IByteArrayObjectWriter<E extends Throwable> {

    default <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, List<T> objects) throws E {
        int siz = objects.size();
        writeInt(siz);
        for (int i = 0; i < siz; i++) {
            writeObject(serializer, objects.get(i));
        }
    }

    default <T> void writeObject(BiConsumer<IByteArrayDataOutputStream, T> serializer, final T object) throws E {
        writeObject(c -> serializer.accept(c, object));
    }

    default void writeObjects(List<IDataSerializable> writers) throws E {
        int siz = writers.size();
        writeInt(siz);
        for (int i = 0; i < siz; i++) {
            writeObject(writers.get(i));
        }
    }

    default void writeObject(IDataSerializable writer) throws E {
        writeObject(writer::writeObject);
    }

    default void writeObject(Consumer<IByteArrayDataOutputStream> writer) throws E {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayDataOutputStream dos = new ByteArrayDataOutputStream(bos);
        writer.accept(dos);
        byte[] bytes = bos.toByteArray();

        int length = bytes.length;
        int version = Math.abs(dos.getVersion()) + 1;
        if (compress()) {
            version = -version;
        }
        if (version != 1) {
            writeInt(-length);
            writeInt(version);
        } else {
            writeInt(length);
        }

        if (version < 0) {
            try {
                ByteArrayOutputStream bosC = new ByteArrayOutputStream();
                GZIPOutputStream zosC = new GZIPOutputStream(bosC);
                zosC.write(bytes);
                zosC.close();
                bytes = bosC.toByteArray();
                writeInt(bytes.length);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        write(bytes);
    }

    void writeInt(int v) throws E;

    void write(@Nonnull byte[] b) throws E;

    default boolean compress() {
        return false;
    }

    default void setCompressed(boolean compressed) {
        throw new UnsupportedOperationException();
    }

}
