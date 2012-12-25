/*
 *  Copyright (C) 2010-2011 JPEXS
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

package com.jpexs.asdec.tags;

import com.jpexs.asdec.SWFInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SymbolClass extends Tag {
	private int tagIDs[];
	private String classNames[];

	public SymbolClass(byte[] data, int version, long pos) throws IOException {
		super(76, data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        int numSymbols = sis.readUI16();
        tagIDs = new int[numSymbols];
        classNames = new String[numSymbols];
        for (int ii = 0; ii < numSymbols; ii++) {
        	int tagID = sis.readUI16();
        	String className = sis.readString();
        	tagIDs[ii] = tagID;
        	classNames[ii] = className;
        }
	}

	@Override
	public String toString() {
		return "SymbolClass";
	}
}
