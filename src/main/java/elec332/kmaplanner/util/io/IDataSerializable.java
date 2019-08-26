package elec332.kmaplanner.util.io;

/**
 * Created by Elec332 on 26-8-2019
 */
public interface IDataSerializable {

    void writeObject(IByteArrayDataOutputStream stream);

    void readObject(IByteArrayDataInputStream stream);

}
