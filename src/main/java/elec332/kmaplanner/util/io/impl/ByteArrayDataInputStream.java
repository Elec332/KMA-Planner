package elec332.kmaplanner.util.io.impl;

import elec332.kmaplanner.util.io.IByteArrayDataInputStream;
import elec332.kmaplanner.util.io.IDataSerializable;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 26-8-2019
 */
public class ByteArrayDataInputStream implements IByteArrayDataInputStream {

    public ByteArrayDataInputStream(ByteArrayInputStream bis) {
        this.input = new DataInputStream(bis);
        this.bis = bis;
    }

    private final DataInput input;
    private final ByteArrayInputStream bis;

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
    public int availableBytes() {
        return bis.available();
    }

    @Override
    public <T> List<T> readObjects(Function<IByteArrayDataInputStream, T> deserializer) {
        int siz = readInt();
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < siz; i++) {
            ret.add(readObject(deserializer));
        }
        return ret;
    }

    @Override
    public <T> T readObject(Function<IByteArrayDataInputStream, T> deserializer) {
        ByteArrayInputStream bis = new ByteArrayInputStream(readByteArray());
        IByteArrayDataInputStream dis = new ByteArrayDataInputStream(bis);
        return deserializer.apply(dis);
    }

    @Override
    public <T extends IDataSerializable> List<T> readObjects(Supplier<T> typeCreator) {
        int siz = readInt();
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < siz; i++) {
            T add = typeCreator.get();
            readObject(add);
            ret.add(add);
        }
        return ret;
    }

    @Override
    public <T extends IDataSerializable> T readObject(T serializable) {
        readObject((Consumer<IByteArrayDataInputStream>) serializable::readObject);
        return serializable;
    }

    @Override
    public void readObject(Consumer<IByteArrayDataInputStream> reader) {
        readObject((Function<IByteArrayDataInputStream, Void>) t -> {
            reader.accept(t);
            return null;
        });
    }

}
