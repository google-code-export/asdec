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


package com.jpexs.asdec.tags;

import com.jpexs.asdec.SWFInputStream;
import com.jpexs.asdec.tags.base.AloneTag;
import com.jpexs.asdec.tags.base.BoundedTag;
import com.jpexs.asdec.tags.base.CharacterTag;
import com.jpexs.asdec.types.RECT;
import com.jpexs.asdec.types.SHAPEWITHSTYLE;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DefineShape3Tag extends CharacterTag implements BoundedTag, AloneTag {

   public int shapeId;
   public RECT shapeBounds;
   public SHAPEWITHSTYLE shapes;

   @Override
   public RECT getRect() {
      return shapeBounds;
   }

   @Override
   public int getCharacterID() {
      return shapeId;
   }

   public DefineShape3Tag(byte[] data, int version, long pos) throws IOException {
      super(32, "DefineShape3", data, pos);
      SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
      shapeId = sis.readUI16();
      shapeBounds = sis.readRECT();
      shapes = sis.readSHAPEWITHSTYLE(3);
   }
}
