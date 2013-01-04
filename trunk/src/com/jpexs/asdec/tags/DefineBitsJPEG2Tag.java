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

public class DefineBitsJPEG2Tag extends Tag {

   public int characterId;
   public byte[] imageData;

   public DefineBitsJPEG2Tag(byte[] data, int version, long pos) throws IOException {
      super(21, data, pos);
      SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
      characterId = sis.readUI16();
      imageData = sis.readBytes(sis.available());
   }

   @Override
   public String toString() {
      return "DefineBitsJPEG2";
   }
}
