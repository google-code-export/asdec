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
import com.jpexs.asdec.SWFOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DefineBitsLossless2Tag extends Tag {

   public int characterID;
   public int bitmapFormat;
   public int bitmapWidth;
   public int bitmapHeight;
   public int bitmapColorTableSize;
   public byte zlibBitmapData[]; //TODO: Parse ALPHACOLORMAPDATA,ALPHABITMAPDATA
   public static final int FORMAT_8BIT_COLORMAPPED = 3;
   public static final int FORMAT_15BIT_RGB = 4;
   public static final int FORMAT_24BIT_RGB = 5;

   public DefineBitsLossless2Tag(byte[] data, int version, long pos) throws IOException {
      super(36, data, pos);
      SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
      characterID = sis.readUI16();
      bitmapFormat = sis.readUI8();
      bitmapWidth = sis.readUI16();
      bitmapHeight = sis.readUI16();
      if (bitmapFormat == FORMAT_15BIT_RGB) {
         bitmapColorTableSize = sis.readUI8();
      }
      zlibBitmapData = sis.readBytes(sis.available());
   }

   /**
    * Gets data bytes
    *
    * @param version SWF version
    * @return Bytes of data
    */
   @Override
   public byte[] getData(int version) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStream os = baos;
      SWFOutputStream sos = new SWFOutputStream(os, version);
      try {
         sos.writeUI16(characterID);
         sos.writeUI8(bitmapFormat);
         sos.writeUI16(bitmapWidth);
         sos.writeUI16(bitmapHeight);
         if (bitmapFormat == FORMAT_15BIT_RGB) {
            sos.writeUI8(bitmapColorTableSize);
         }
         sos.write(zlibBitmapData);
      } catch (IOException e) {
      }
      return baos.toByteArray();
   }

   @Override
   public String toString() {
      return "DefineBitsLossless2";
   }
}
