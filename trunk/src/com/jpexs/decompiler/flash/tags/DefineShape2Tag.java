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
package com.jpexs.decompiler.flash.tags;

import com.jpexs.decompiler.flash.SWFInputStream;
import com.jpexs.decompiler.flash.tags.base.AloneTag;
import com.jpexs.decompiler.flash.tags.base.BoundedTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.SHAPEWITHSTYLE;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class DefineShape2Tag extends CharacterTag implements BoundedTag, AloneTag, ShapeTag {

    public int shapeId;
    public RECT shapeBounds;
    public SHAPEWITHSTYLE shapes;

    @Override
    public Set<Integer> getNeededCharacters() {
        return shapes.getNeededCharacters();
    }

    @Override
    public String toSVG() {
        return shapes.toSVG(2);
    }

    @Override
    public int getCharacterID() {
        return shapeId;
    }

    @Override
    public RECT getRect(HashMap<Integer, CharacterTag> characters) {
        return shapeBounds;
    }

    public DefineShape2Tag(byte[] data, int version, long pos) throws IOException {
        super(22, "DefineShape2", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        shapeId = sis.readUI16();
        shapeBounds = sis.readRECT();
        shapes = sis.readSHAPEWITHSTYLE(2);
    }
}
