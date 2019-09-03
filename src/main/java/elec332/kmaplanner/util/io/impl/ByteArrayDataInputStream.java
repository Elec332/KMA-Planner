package elec332.kmaplanner.util.io.impl;

import elec332.kmaplanner.util.io.IByteArrayDataInputStream;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.UUID;

/**
 * Created by Elec332 on 26-8-2019
 */
public class ByteArrayDataInputStream implements IByteArrayDataInputStream {

    @SuppressWarnings("unused")
    public ByteArrayDataInputStream(ByteArrayInputStream bis) {
        this(bis, 0);
    }

    public ByteArrayDataInputStream(ByteArrayInputStream bis, int version) {
        this.input = new DataInputStream(bis);
        this.bis = bis;
        this.version = version;
    }

    private final DataInput input;
    private final ByteArrayInputStream bis;
    private int version;

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void readFully(@Nonnull byte[] b) {
        try {
            input.readFully(b);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void readFully(@Nonnull byte[] b, int off, int len) {
        try {
            input.readFully(b, off, len);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int skipBytes(int n) {
        try {
            return input.skipBytes(n);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] readByteArray() {
        int siz = readInt();
        byte[] ret = new byte[siz];
        readFully(ret);
        return ret;
    }

    @Override
    public boolean readBoolean() {
        try {
            return input.readBoolean();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte readByte() {
        try {
            return input.readByte();
        } catch (EOFException e) {
            throw new IllegalStateException(e);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public int readUnsignedByte() {
        try {
            return input.readUnsignedByte();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return input.readShort();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int readUnsignedShort() {
        try {
            return input.readUnsignedShort();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public char readChar() {
        try {
            return input.readChar();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return input.readInt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long readLong() {
        try {
            return input.readLong();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public float readFloat() {
        try {
            return input.readFloat();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public double readDouble() {
        try {
            return input.readDouble();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Deprecated
    public String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nonnull
    @Override
    public String readUTF() {
        try {
            return input.readUTF();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public UUID readUUID() {
        long msb = readLong();
        long lsb = readLong();
        return new UUID(msb, lsb);
    }

    @Override
    public int availableBytes() {
        return bis.available();
    }

}
