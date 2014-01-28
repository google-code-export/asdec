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

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.SWFInputStream;
import com.jpexs.decompiler.flash.SWFOutputStream;
import com.jpexs.decompiler.flash.tags.base.AloneTag;
import com.jpexs.decompiler.flash.tags.base.ImageTag;
import com.jpexs.helpers.SerializableImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.imageio.ImageIO;

public class DefineBitsJPEG3Tag extends ImageTag implements AloneTag {

    public int characterID;
    public byte[] imageData;
    public byte[] bitmapAlphaData;
    public static final int ID = 35;

    @Override
    public int getCharacterId() {
        return characterID;
    }

    @Override
    public void setImage(byte[] data) throws IOException {
        if (ImageTag.getImageFormat(data).equals("jpg")) {
            SerializableImage image = new SerializableImage(ImageIO.read(new ByteArrayInputStream(data)));
            byte[] ba = new byte[image.getWidth() * image.getHeight()];
            for (int i = 0; i < ba.length; i++) {
                ba[i] = (byte) 255;
            }
            bitmapAlphaData = ba;
        } else {
            bitmapAlphaData = new byte[0];
        }
        imageData = data;
    }

    @Override
    public String getImageFormat() {
        String fmt = ImageTag.getImageFormat(imageData);
        if (fmt.equals("jpg")) {
            fmt = "png"; //transparency
        }
        return fmt;
    }

    @Override
    public InputStream getImageData() {
        return null;
    }

    @Override
    public SerializableImage getImage(List<Tag> tags) {
        try {
            InputStream stream;
            if (SWF.hasErrorHeader(imageData)) {
                stream = new ByteArrayInputStream(imageData, 4, imageData.length - 4);
            } else {
                stream = new ByteArrayInputStream(imageData);
            }
            SerializableImage img = new SerializableImage(ImageIO.read(stream));
            if (bitmapAlphaData.length == 0) {
                return img;
            }
            SerializableImage img2 = new SerializableImage(img.getWidth(), img.getHeight(), SerializableImage.TYPE_INT_ARGB_PRE);
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int val = img.getRGB(x, y);
                    int a = bitmapAlphaData[x + y * img.getWidth()] & 0xff;
                    val = (val & 0xffffff) | (a << 24);
                    img2.setRGB(x, y, colorToInt(multiplyAlpha(intToColor(val))));
                }
            }
            return img2;
        } catch (IOException ex) {
        }
        return null;
    }

    public DefineBitsJPEG3Tag(SWF swf, byte[] data, int version, long pos) throws IOException {
        super(swf, ID, "DefineBitsJPEG3", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        characterID = sis.readUI16();
        long alphaDataOffset = sis.readUI32();
        imageData = sis.readBytesEx(alphaDataOffset);
        bitmapAlphaData = sis.readBytesZlib(sis.available());
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
            sos.writeUI32(imageData.length);
            sos.write(imageData);
            sos.writeBytesZlib(bitmapAlphaData);
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }
}
