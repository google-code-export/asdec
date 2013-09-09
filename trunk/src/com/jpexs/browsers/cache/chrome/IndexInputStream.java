package com.jpexs.browsers.cache.chrome;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author JPEXS
 */
public class IndexInputStream extends InputStream {

    private InputStream is;
    public long pos = 0;

    public long getPos() {
        return pos;
    }

    public IndexInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        int r = is.read();
        //System.out.print("$"+Integer.toHexString(r));
        pos++;
        return r;
    }

    public long readUInt32() throws IOException {
        long r = (((long) read()) + ((long) (read() << 8)) + ((long) (read() << 16)) + ((long) (read() << 24))) & 0xffffffffL;
        //System.out.println("");
        return r;
    }

    public long readUInt64() throws IOException {
        return readUInt32() + (readUInt32() << 32);
    }

    public long readInt64() throws IOException {
        return readUInt64(); //FIXME
    }

    public int readInt32() throws IOException {
        return (int) readUInt32();
    }

    public int readInt16() throws IOException {
        return (short) ((int) read() + (int) (read() << 8));
    }

    public long readInt() throws IOException {
        return readInt32();
    }

    public String readString() throws IOException {
        int len = (int) readInt();
        byte data[] = new byte[len];
        read(data);
        return new String(data);
    }
}
