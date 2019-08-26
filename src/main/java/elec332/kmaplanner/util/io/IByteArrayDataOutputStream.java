package elec332.kmaplanner.util.io;

import javax.annotation.Nonnull;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 26-8-2019
 */
public interface IByteArrayDataOutputStream extends DataOutput {

    @Override
    void write(int b);

    @Override
    void write(@Nonnull byte[] b);

    @Override
    void write(@Nonnull byte[] b, int off, int len);

    void writeByteArray(@Nonnull byte[] bytes);

    @Override
    void writeBoolean(boolean v);

    @Override
    void writeByte(int v);

    @Override
    void writeShort(int v);

    @Override
    void writeChar(int v);

    @Override
    void writeInt(int v);

    @Override
    void writeLong(long v);

    @Override
    void writeFloat(float v);

    @Override
    void writeDouble(double v);

    @Override
    void writeChars(@Nonnull String s);

    @Override
    void writeUTF(@Nonnull String s);

    @Override
    @Deprecated
    void writeBytes(@Nonnull String s);

    default <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, Collection<T> objects) {
        writeObjects(serializer, new ArrayList<>(objects));
    }

    default void writeObjects(Collection<? extends IDataSerializable> writers) {
        writeObjects(new ArrayList<>(writers));
    }

    <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, List<T> objects);

    <T> void writeObject(BiConsumer<IByteArrayDataOutputStream, T> serializer, T object);

    void writeObjects(List<IDataSerializable> writers);

    void writeObject(IDataSerializable writer);

    void writeObject(Consumer<IByteArrayDataOutputStream> writer);

}