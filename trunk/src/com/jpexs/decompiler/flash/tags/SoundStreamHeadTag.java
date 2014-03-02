/*
 *  Copyright (C) 2010-2014 JPEXS
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
import com.jpexs.decompiler.flash.tags.base.CharacterIdTag;
import com.jpexs.decompiler.flash.tags.base.SoundStreamHeadTypeTag;
import com.jpexs.decompiler.flash.types.BasicType;
import com.jpexs.decompiler.flash.types.annotations.Conditional;
import com.jpexs.decompiler.flash.types.annotations.Internal;
import com.jpexs.decompiler.flash.types.annotations.Reserved;
import com.jpexs.decompiler.flash.types.annotations.SWFType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 *
 * @author JPEXS
 */
public class SoundStreamHeadTag extends CharacterIdTag implements SoundStreamHeadTypeTag {

    @Reserved
    @SWFType(value = BasicType.UB, count = 4)
    public int reserved;
    @SWFType(value = BasicType.UB, count = 2)
    public int playBackSoundRate;
    public boolean playBackSoundSize;
    public boolean playBackSoundType;
    @SWFType(value = BasicType.UB, count = 4)
    public int streamSoundCompression;
    @SWFType(value = BasicType.UB, count = 2)
    public int streamSoundRate;
    public boolean streamSoundSize;
    public boolean streamSoundType;
    @SWFType(value = BasicType.UI16)
    public int streamSoundSampleCount;
    @Conditional(value = "streamSoundCompression", options = {2})
    public int latencySeek;
    @Internal
    private int virtualCharacterId = 0;
    public static final int ID = 18;

    @Override
    public String getExportFormat() {
        if (streamSoundCompression == DefineSoundTag.FORMAT_MP3) {
            return "mp3";
        }
        if (streamSoundCompression == DefineSoundTag.FORMAT_ADPCM) {
            return "wav";
        }
        return "flv";
    }

    @Override
    public int getCharacterId() {
        return virtualCharacterId;
    }

    @Override
    public void setVirtualCharacterId(int ch) {
        virtualCharacterId = ch;
    }

    @Override
    public long getSoundSampleCount() {
        return streamSoundSampleCount;
    }

    /**
     * Gets data bytes
     *
     * @return Bytes of data
     */
    @Override
    public byte[] getData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        SWFOutputStream sos = new SWFOutputStream(os, getVersion());
        try {
            sos.writeUB(4, reserved);
            sos.writeUB(2, playBackSoundRate);
            sos.writeUB(1, playBackSoundSize ? 1 : 0);
            sos.writeUB(1, playBackSoundType ? 1 : 0);
            sos.writeUB(4, streamSoundCompression);
            sos.writeUB(2, streamSoundRate);
            sos.writeUB(1, streamSoundSize ? 1 : 0);
            sos.writeUB(1, streamSoundType ? 1 : 0);
            sos.writeUI16(streamSoundSampleCount);
            if (streamSoundCompression == 2) {
                sos.writeSI16(latencySeek);
            }
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }

    /**
     * Constructor
     *
     * @param swf
     * @param data Data bytes
     * @param pos
     * @throws IOException
     */
    public SoundStreamHeadTag(SWF swf, byte[] data, long pos) throws IOException {
        super(swf, ID, "SoundStreamHead", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), swf.version);
        reserved = (int) sis.readUB(4);
        playBackSoundRate = (int) sis.readUB(2);
        playBackSoundSize = sis.readUB(1) == 1;
        playBackSoundType = sis.readUB(1) == 1;
        streamSoundCompression = (int) sis.readUB(4);
        streamSoundRate = (int) sis.readUB(2);
        streamSoundSize = sis.readUB(1) == 1;
        streamSoundType = sis.readUB(1) == 1;
        streamSoundSampleCount = sis.readUI16();
        if (streamSoundCompression == 2) {
            latencySeek = sis.readSI16();
        }
    }

    @Override
    public int getSoundFormat() {
        return streamSoundCompression;
    }

    @Override
    public int getSoundRate() {
        return streamSoundRate;
    }

    @Override
    public boolean getSoundSize() {
        return streamSoundSize;
    }

    @Override
    public boolean getSoundType() {
        return streamSoundType;
    }
}
