package elec332.kmaplanner.util.io;

import javax.annotation.Nonnull;
import java.io.*;

/**
 * Created by Elec332 on 26-8-2019
 */
public class DataInputStream extends FilterInputStream implements IByteArrayObjectReader<IOException> {

    public DataInputStream(InputStream in) {
        super(new BufferedInputStream(in)); //Buffered stream to improve performance
    }

    @Override
    public void readFully(@Nonnull byte[] b) throws IOException {
        int q = read(b);
        if (b.length != q) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4);
    }

}
