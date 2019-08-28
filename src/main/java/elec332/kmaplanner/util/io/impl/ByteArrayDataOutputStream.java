package elec332.kmaplanner.util.io.impl;

import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;
import elec332.kmaplanner.util.io.IDataSerializable;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 26-8-2019
 */
public class ByteArrayDataOutputStream implements IByteArrayDataOutputStream {

    public ByteArrayDataOutputStream(ByteArrayOutputStream bos) {
        output = new DataOutputStream(bos);
    }

    private final DataOutput output;

    @Override
    public void write(int b) {
        try {
            output.write(b);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void write(@Nonnull byte[] b) {
        try {
            output.write(b);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void write(@Nonnull byte[] b, int off, int len) {
        try {
            output.write(b, off, len);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeByteArray(@Nonnull byte[] bytes) {
        writeInt(bytes.length);
        write(bytes);
    }

    @Override
    public void writeBoolean(boolean v) {
        try {
            output.writeBoolean(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeByte(int v) {
        try {
            output.writeByte(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    @Deprecated
    public void writeBytes(@Nonnull String s) {
        try {
            output.writeBytes(s);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeChar(int v) {
        try {
            output.writeChar(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeChars(@Nonnull String s) {
        try {
            output.writeChars(s);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeDouble(double v) {
        try {
            output.writeDouble(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeFloat(float v) {
        try {
            output.writeFloat(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeInt(int v) {
        try {
            output.writeInt(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeLong(long v) {
        try {
            output.writeLong(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeShort(int v) {
        try {
            output.writeShort(v);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeUTF(@Nonnull String s) {
        try {
            output.writeUTF(s);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    @Override
    public void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    @SuppressWarnings("all")
    public <T> void writeObjects(BiConsumer<IByteArrayDataOutputStream, T> serializer, List<T> objects) {
        int siz = objects.size();
        writeInt(siz);
        for (int i = 0; i < siz; i++) {
            writeObject(serializer, objects.get(i));
        }
    }

    @Override
    public <T> void writeObject(BiConsumer<IByteArrayDataOutputStream, T> serializer, final T object) {
        writeObject(c -> serializer.accept(c, object));
    }

    @Override
    @SuppressWarnings("all")
    public void writeObjects(List<IDataSerializable> writers) {
        int siz = writers.size();
        writeInt(siz);
        for (int i = 0; i < siz; i++) {
            writeObject(writers.get(i));
        }
    }

    @Override
    public void writeObject(IDataSerializable writer) {
        writeObject(writer::writeObject);
    }

    @Override
    public void writeObject(Consumer<IByteArrayDataOutputStream> writer) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IByteArrayDataOutputStream dos = new ByteArrayDataOutputStream(bos);
        writer.accept(dos);
        writeByteArray(bos.toByteArray());
    }

}
