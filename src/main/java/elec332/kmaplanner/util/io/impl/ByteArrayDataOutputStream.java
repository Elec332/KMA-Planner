package elec332.kmaplanner.util.io.impl;

import elec332.kmaplanner.util.io.IByteArrayDataOutputStream;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Elec332 on 26-8-2019
 */
public class ByteArrayDataOutputStream implements IByteArrayDataOutputStream {

    public ByteArrayDataOutputStream(ByteArrayOutputStream bos) {
        output = new DataOutputStream(bos);
    }

    private final DataOutput output;
    private int version;

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

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

}
