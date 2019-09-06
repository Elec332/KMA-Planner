package elec332.kmaplanner.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Elec332 on 26-8-2019
 */
public class DataOutputStream extends FilterOutputStream implements IByteArrayObjectWriter<IOException> {

    public DataOutputStream(OutputStream out) {
        super(out);
    }

    private boolean compress = false;

    @Override
    public void setCompressed(boolean compressed) {
        compress = compressed;
    }

    @Override
    public boolean compress() {
        return compress;
    }

    @Override
    @SuppressWarnings("all")
    public void writeInt(int i) throws IOException {
        write((i >>> 24) & 0xFF);
        write((i >>> 16) & 0xFF);
        write((i >>> 8) & 0xFF);
        write((i >>> 0) & 0xFF); //Looks neater
    }

}
