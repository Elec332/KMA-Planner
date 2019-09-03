package elec332.kmaplanner.util.io;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.util.UUID;

/**
 * Created by Elec332 on 26-8-2019
 */
public interface IByteArrayDataInputStream extends DataInput, IByteArrayObjectReader<AssertionError> {

    int getVersion();

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

}