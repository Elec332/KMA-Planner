package elec332.kmaplanner.util.io;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 26-8-2019
 */
public interface IByteArrayDataInputStream extends DataInput {

    @Override
    void readFully(@Nonnull byte[] b);

    @Override
    void readFully(@Nonnull byte[] b, int off, int len);

    @Override
    int skipBytes(int n);

    byte[] readByteArray();

    @Override
    boolean readBoolean();

    @Override
    byte readByte();

    @Override
    int readUnsignedByte();

    @Override
    short readShort();

    @Override
    int readUnsignedShort();

    @Override
    char readChar();

    @Override
    int readInt();

    @Override
    long readLong();

    @Override
    float readFloat();

    @Override
    double readDouble();

    @Override
    @Deprecated
    String readLine();

    @Nonnull
    @Override
    String readUTF();

    UUID readUUID();

    int availableBytes();

    <T> List<T> readObjects(Function<IByteArrayDataInputStream, T> deserializer);

    <T> T readObject(Function<IByteArrayDataInputStream, T> deserializer);

    <T extends IDataSerializable> List<T> readObjects(Supplier<T> typeCreator);

    <T extends IDataSerializable> T readObject(T serializable);

    void readObject(Consumer<IByteArrayDataInputStream> reader);


}