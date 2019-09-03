package elec332.kmaplanner.util.io;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.util.io.impl.ByteArrayDataInputStream;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 3-9-2019
 */
public interface IByteArrayObjectReader<E extends Throwable> {

    default <T> List<T> readObjects(Function<IByteArrayDataInputStream, T> deserializer) throws E {
        int siz = readInt();
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < siz; i++) {
            ret.add(readObject(deserializer));
        }
        return ret;
    }

    default <T> T readObject(Function<IByteArrayDataInputStream, T> deserializer) throws E {
        return readVersionedObject(i -> deserializer);
    }

    default <T> T readVersionedObject(Function<Integer, Function<IByteArrayDataInputStream, T>> deserializer) throws E {
        int len = readInt();
        int version = 0;
        if (len < 0) {
            version = readInt();
            len = -len;
        }
        byte[] ret = new byte[len];
        readFully(ret);
        ByteArrayInputStream bis = new ByteArrayInputStream(ret);
        IByteArrayDataInputStream dis = new ByteArrayDataInputStream(bis, version);
        return Preconditions.checkNotNull(deserializer.apply(version)).apply(dis);
    }

    default <T extends IDataSerializable> List<T> readObjects(Supplier<T> typeCreator) throws E {
        int siz = readInt();
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < siz; i++) {
            T add = typeCreator.get();
            readObject(add);
            ret.add(add);
        }
        return ret;
    }

    default <T extends IDataSerializable> T readObject(T serializable) throws E {
        readObject((Consumer<IByteArrayDataInputStream>) serializable::readObject);
        return serializable;
    }

    default void readObject(Consumer<IByteArrayDataInputStream> reader) throws E {
        readObject((Function<IByteArrayDataInputStream, Void>) t -> {
            reader.accept(t);
            return null;
        });
    }

    int readInt() throws E;

    void readFully(@Nonnull byte[] b) throws E;

}
