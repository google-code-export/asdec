/*
 *  Copyright (C) 2010-2013 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.helpers;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author JPEXS
 */
public class LimitedInputStream extends InputStream {

    private InputStream is;
    private long pos = 0;
    private long limit;

    public LimitedInputStream(InputStream is, long limit) {
        this.is = is;
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        if (pos >= limit) {
            return -1;
        }
        pos++;
        return is.read();
    }

    @Override
    public int available() throws IOException {
        int avail = is.available();
        if (pos + avail > limit) {
            avail = (int) (limit - pos);
        }
        return avail;
    }
}
