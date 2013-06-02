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
package com.jpexs.decompiler.flash;

import SevenZip.Compression.LZMA.Encoder;
import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.action.ActionGraphSource;
import com.jpexs.decompiler.flash.action.swf4.ActionEquals;
import com.jpexs.decompiler.flash.action.swf4.ActionGetVariable;
import com.jpexs.decompiler.flash.action.swf4.ActionIf;
import com.jpexs.decompiler.flash.action.swf4.ActionPush;
import com.jpexs.decompiler.flash.action.swf4.ActionSetVariable;
import com.jpexs.decompiler.flash.action.swf4.Null;
import com.jpexs.decompiler.flash.action.swf5.ActionCallFunction;
import com.jpexs.decompiler.flash.action.swf5.ActionCallMethod;
import com.jpexs.decompiler.flash.action.swf5.ActionConstantPool;
import com.jpexs.decompiler.flash.action.swf5.ActionDefineFunction;
import com.jpexs.decompiler.flash.action.swf5.ActionDefineLocal;
import com.jpexs.decompiler.flash.action.swf5.ActionDefineLocal2;
import com.jpexs.decompiler.flash.action.swf5.ActionEquals2;
import com.jpexs.decompiler.flash.action.swf5.ActionGetMember;
import com.jpexs.decompiler.flash.action.swf5.ActionNewMethod;
import com.jpexs.decompiler.flash.action.swf5.ActionNewObject;
import com.jpexs.decompiler.flash.action.swf5.ActionSetMember;
import com.jpexs.decompiler.flash.action.swf7.ActionDefineFunction2;
import com.jpexs.decompiler.flash.action.treemodel.ConstantPool;
import com.jpexs.decompiler.flash.action.treemodel.DirectValueTreeItem;
import com.jpexs.decompiler.flash.flv.AUDIODATA;
import com.jpexs.decompiler.flash.flv.FLVOutputStream;
import com.jpexs.decompiler.flash.flv.FLVTAG;
import com.jpexs.decompiler.flash.flv.VIDEODATA;
import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphSourceItemContainer;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import com.jpexs.decompiler.flash.helpers.Cache;
import com.jpexs.decompiler.flash.helpers.Helper;
import com.jpexs.decompiler.flash.tags.ABCContainerTag;
import com.jpexs.decompiler.flash.tags.DefineBinaryDataTag;
import com.jpexs.decompiler.flash.tags.DefineButton2Tag;
import com.jpexs.decompiler.flash.tags.DefineButtonTag;
import com.jpexs.decompiler.flash.tags.DefineSoundTag;
import com.jpexs.decompiler.flash.tags.DefineSpriteTag;
import com.jpexs.decompiler.flash.tags.DefineVideoStreamTag;
import com.jpexs.decompiler.flash.tags.DoInitActionTag;
import com.jpexs.decompiler.flash.tags.ExportAssetsTag;
import com.jpexs.decompiler.flash.tags.JPEGTablesTag;
import com.jpexs.decompiler.flash.tags.PlaceObjectTypeTag;
import com.jpexs.decompiler.flash.tags.SetBackgroundColorTag;
import com.jpexs.decompiler.flash.tags.ShowFrameTag;
import com.jpexs.decompiler.flash.tags.SoundStreamBlockTag;
import com.jpexs.decompiler.flash.tags.SoundStreamHeadTypeTag;
import com.jpexs.decompiler.flash.tags.SymbolClassTag;
import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.decompiler.flash.tags.VideoFrameTag;
import com.jpexs.decompiler.flash.tags.base.ASMSource;
import com.jpexs.decompiler.flash.tags.base.BoundedTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.tags.base.Container;
import com.jpexs.decompiler.flash.tags.base.DrawableTag;
import com.jpexs.decompiler.flash.tags.base.ImageTag;
import com.jpexs.decompiler.flash.tags.base.RemoveTag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag;
import com.jpexs.decompiler.flash.tags.base.TextTag;
import com.jpexs.decompiler.flash.types.CXFORM;
import com.jpexs.decompiler.flash.types.CXFORMWITHALPHA;
import com.jpexs.decompiler.flash.types.MATRIX;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.filters.BlendComposite;
import com.jpexs.decompiler.flash.types.filters.FILTER;
import com.jpexs.decompiler.flash.types.filters.Filtering;
import com.jpexs.decompiler.flash.types.sound.AdpcmDecoder;
import com.jpexs.decompiler.flash.xfl.XFLConverter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.imageio.ImageIO;

/**
 * Class representing SWF file
 *
 * @author JPEXS
 */
public class SWF {

    /**
     * Default version of SWF file format
     */
    public static final int DEFAULT_VERSION = 10;
    /**
     * Tags inside of file
     */
    public List<Tag> tags = new ArrayList<Tag>();
    /**
     * Rectangle for the display
     */
    public RECT displayRect;
    /**
     * Movie frame rate
     */
    public int frameRate;
    /**
     * Number of frames in movie
     */
    public int frameCount;
    /**
     * Version of SWF
     */
    public int version;
    /**
     * Size of the file
     */
    public long fileSize;
    /**
     * Use compression
     */
    public boolean compressed = false;
    /**
     * Use LZMA compression
     */
    public boolean lzma = false;
    /**
     * Compressed size of the file (LZMA)
     */
    public long compressedSize;
    /**
     * LZMA Properties
     */
    public byte lzmaProperties[];

    /**
     * Gets all tags with specified id
     *
     * @param tagId Identificator of tag type
     * @return List of tags
     */
    public List<Tag> getTagData(int tagId) {
        List<Tag> ret = new ArrayList<Tag>();
        for (Tag tag : tags) {
            if (tag.getId() == tagId) {
                ret.add(tag);
            }
        }
        return ret;
    }

    /**
     * Saves this SWF into new file
     *
     * @param os OutputStream to save SWF in
     * @throws IOException
     */
    public void saveTo(OutputStream os) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SWFOutputStream sos = new SWFOutputStream(baos, version);
            sos.writeRECT(displayRect);
            sos.writeUI8(0);
            sos.writeUI8(frameRate);
            sos.writeUI16(frameCount);

            sos.writeTags(tags);
            sos.writeUI16(0);
            sos.close();
            if (compressed && lzma) {
                os.write('Z');
            } else if (compressed) {
                os.write('C');
            } else {
                os.write('F');
            }
            os.write('W');
            os.write('S');
            os.write(version);
            byte data[] = baos.toByteArray();
            sos = new SWFOutputStream(os, version);
            sos.writeUI32(data.length + 8);

            if (compressed) {
                if (lzma) {
                    Encoder enc = new Encoder();
                    int val = lzmaProperties[0] & 0xFF;
                    int lc = val % 9;
                    int remainder = val / 9;
                    int lp = remainder % 5;
                    int pb = remainder / 5;
                    int dictionarySize = 0;
                    for (int i = 0; i < 4; i++) {
                        dictionarySize += ((int) (lzmaProperties[1 + i]) & 0xFF) << (i * 8);
                    }
                    enc.SetDictionarySize(dictionarySize);
                    enc.SetLcLpPb(lc, lp, pb);
                    baos = new ByteArrayOutputStream();
                    enc.SetEndMarkerMode(true);
                    enc.Code(new ByteArrayInputStream(data), baos, -1, -1, null);
                    data = baos.toByteArray();
                    byte udata[] = new byte[4];
                    udata[0] = (byte) (data.length & 0xFF);
                    udata[1] = (byte) ((data.length >> 8) & 0xFF);
                    udata[2] = (byte) ((data.length >> 16) & 0xFF);
                    udata[3] = (byte) ((data.length >> 24) & 0xFF);
                    os.write(udata);
                    os.write(lzmaProperties);
                } else {
                    os = new DeflaterOutputStream(os);
                }
            }
            os.write(data);
        } finally {
            if (os != null) {
                os.close();
            }
        }

    }

    public SWF(InputStream is) throws IOException {
        this(is, null);
    }

    /**
     * Construct SWF from stream
     *
     * @param is Stream to read SWF from
     * @throws IOException
     */
    public SWF(InputStream is, PercentListener listener) throws IOException {
        byte hdr[] = new byte[3];
        is.read(hdr);
        String shdr = new String(hdr);
        if ((!shdr.equals("FWS")) && (!shdr.equals("CWS")) && (!shdr.equals("ZWS"))) {
            throw new IOException("Invalid SWF file");
        }
        version = is.read();
        SWFInputStream sis = new SWFInputStream(is, version, 4);
        fileSize = sis.readUI32();

        if (hdr[0] == 'C') {
            sis = new SWFInputStream(new InflaterInputStream(is), version, 8);
            compressed = true;
        }

        if (hdr[0] == 'Z') {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long outSize = sis.readUI32();
            int propertiesSize = 5;
            lzmaProperties = new byte[propertiesSize];
            if (sis.read(lzmaProperties, 0, propertiesSize) != propertiesSize) {
                throw new IOException("LZMA:input .lzma file is too short");
            }
            SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
            if (!decoder.SetDecoderProperties(lzmaProperties)) {
                throw new IOException("LZMA:Incorrect stream properties");
            }

            if (!decoder.Code(sis, baos, fileSize - 8)) {
                throw new IOException("LZMA:Error in data stream");
            }
            sis = new SWFInputStream(new ByteArrayInputStream(baos.toByteArray()), version, 8);
            compressed = true;
            lzma = true;
        }


        if (listener != null) {
            sis.addPercentListener(listener);
        }
        sis.setPercentMax(fileSize);
        displayRect = sis.readRECT();
        // FIXED8 (16 bit fixed point) frameRate
        int tmpFirstByetOfFrameRate = sis.readUI8();
        frameRate = sis.readUI8();
        frameCount = sis.readUI16();
        tags = sis.readTagList(0);
        assignClassesToSymbols();
    }

    public void assignClassesToSymbols() {
        HashMap<Integer, String> classes = new HashMap<Integer, String>();
        for (Tag t : tags) {
            if (t instanceof SymbolClassTag) {
                SymbolClassTag sct = (SymbolClassTag) t;
                for (int i = 0; i < sct.tagIDs.length; i++) {
                    if ((!classes.containsKey(sct.tagIDs[i])) && (!classes.containsValue(sct.classNames[i]))) {
                        classes.put(sct.tagIDs[i], sct.classNames[i]);
                    }
                }
            }
        }
        for (Tag t : tags) {
            if (t instanceof CharacterTag) {
                CharacterTag ct = (CharacterTag) t;
                if (classes.containsKey(ct.getCharacterID())) {
                    ct.setClassName(classes.get(ct.getCharacterID()));
                }
            }
        }
    }

    /**
     * Compress SWF file
     *
     * @param fis Input stream
     * @param fos Output stream
     */
    public static boolean fws2cws(InputStream fis, OutputStream fos) {
        try {
            byte swfHead[] = new byte[8];
            fis.read(swfHead);

            if (swfHead[0] != 'F') {
                fis.close();
                return false;
            }
            swfHead[0] = 'C';
            fos.write(swfHead);
            fos = new DeflaterOutputStream(fos);
            int i;
            while ((i = fis.read()) != -1) {
                fos.write(i);
            }

            fis.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public static boolean decompress(InputStream fis, OutputStream fos) {
        try {
            byte hdr[] = new byte[3];
            fis.read(hdr);
            String shdr = new String(hdr);
            if (shdr.equals("CWS")) {
                int version = fis.read();
                SWFInputStream sis = new SWFInputStream(fis, version, 4);
                long fileSize = sis.readUI32();
                SWFOutputStream sos = new SWFOutputStream(fos, version);
                sos.write("FWS".getBytes());
                sos.writeUI8(version);
                sos.writeUI32(fileSize);
                InflaterInputStream iis = new InflaterInputStream(fis);
                int i;
                while ((i = iis.read()) != -1) {
                    fos.write(i);
                }

                fis.close();
                fos.close();
            } else if (shdr.equals("ZWS")) {
                int version = fis.read();
                SWFInputStream sis = new SWFInputStream(fis, version, 4);
                long fileSize = sis.readUI32();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                long outSize = sis.readUI32();
                int propertiesSize = 5;
                byte lzmaProperties[] = new byte[propertiesSize];
                if (sis.read(lzmaProperties, 0, propertiesSize) != propertiesSize) {
                    throw new IOException("LZMA:input .lzma file is too short");
                }
                SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
                if (!decoder.SetDecoderProperties(lzmaProperties)) {
                    throw new IOException("LZMA:Incorrect stream properties");
                }

                if (!decoder.Code(sis, baos, fileSize - 8)) {
                    throw new IOException("LZMA:Error in data stream");
                }
                SWFOutputStream sos = new SWFOutputStream(fos, version);
                sos.write("FWS".getBytes());
                sos.write(version);
                sos.writeUI32(fileSize);
                sos.write(baos.toByteArray());
                sos.close();
                fis.close();
                fos.close();
            } else {
                return false;
            }
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public boolean exportAS3Class(String className, String outdir, boolean isPcode) throws Exception {
        List<ABCContainerTag> abcTags = new ArrayList<ABCContainerTag>();

        for (Tag t : tags) {
            if (t instanceof ABCContainerTag) {
                ABCContainerTag cnt = (ABCContainerTag) t;
                abcTags.add(cnt);
            }
        }
        for (int i = 0; i < abcTags.size(); i++) {
            ABC abc = abcTags.get(i).getABC();
            ScriptPack scr = abc.findScriptTraitByPath(className);
            if (scr != null) {
                String cnt = "";
                if (abc.script_info.length > 1) {
                    cnt = "script " + (i + 1) + "/" + abc.script_info.length + " ";
                }
                String exStr = "Exporting " + "tag " + (i + 1) + "/" + abcTags.size() + " " + cnt + scr.getPath() + " ...";
                informListeners("export", exStr);
                scr.export(outdir, abcTags, isPcode);
                return true;
            }
        }
        return false;
    }

    public boolean exportActionScript(String outdir, boolean isPcode) throws Exception {
        boolean asV3Found = false;
        final EventListener evl = new EventListener() {
            @Override
            public void handleEvent(String event, Object data) {
                if (event.equals("export")) {
                    informListeners(event, data);
                }
            }
        };
        List<ABCContainerTag> abcTags = new ArrayList<ABCContainerTag>();
        for (Tag t : tags) {
            if (t instanceof ABCContainerTag) {
                abcTags.add((ABCContainerTag) t);
                asV3Found = true;
            }
        }
        for (int i = 0; i < abcTags.size(); i++) {
            ABCContainerTag t = abcTags.get(i);
            t.getABC().addEventListener(evl);
            t.getABC().export(outdir, isPcode, abcTags, "tag " + (i + 1) + "/" + abcTags.size() + " ");
        }

        if (!asV3Found) {
            List<Object> list2 = new ArrayList<Object>();
            list2.addAll(tags);
            List<TagNode> list = createASTagList(list2, null);

            TagNode.setExport(list, true);
            if (!outdir.endsWith(File.separator)) {
                outdir += File.separator;
            }
            outdir += "scripts" + File.separator;
            return TagNode.exportNodeAS(list, outdir, isPcode, evl);
        }
        return asV3Found;
    }

    public static List<TagNode> createASTagList(List<Object> list, Object parent) {
        List<TagNode> ret = new ArrayList<TagNode>();
        int frame = 1;
        List<TagNode> frames = new ArrayList<TagNode>();

        List<ExportAssetsTag> exportAssetsTags = new ArrayList<ExportAssetsTag>();
        for (Object t : list) {
            if (t instanceof ExportAssetsTag) {
                exportAssetsTags.add((ExportAssetsTag) t);
            }
            if (t instanceof ShowFrameTag) {
                TagNode tti = new TagNode(new FrameNode(frame, parent, false));

                for (int r = ret.size() - 1; r >= 0; r--) {
                    if (!(ret.get(r).tag instanceof DefineSpriteTag)) {
                        if (!(ret.get(r).tag instanceof DefineButtonTag)) {
                            if (!(ret.get(r).tag instanceof DefineButton2Tag)) {
                                if (!(ret.get(r).tag instanceof DoInitActionTag)) {
                                    tti.subItems.add(ret.get(r));
                                    ret.remove(r);
                                }
                            }
                        }
                    }
                }
                frame++;
                frames.add(tti);
            } else if (t instanceof ASMSource) {
                TagNode tti = new TagNode(t);
                ret.add(tti);
            } else if (t instanceof Container) {
                if (((Container) t).getItemCount() > 0) {

                    TagNode tti = new TagNode(t);
                    List<Object> subItems = ((Container) t).getSubItems();

                    tti.subItems = createASTagList(subItems, t);
                    ret.add(tti);
                }
            }

        }
        ret.addAll(frames);
        for (int i = ret.size() - 1; i >= 0; i--) {
            if (ret.get(i).tag instanceof DefineSpriteTag) {
                ((DefineSpriteTag) ret.get(i).tag).exportAssetsTags = exportAssetsTags;
            }
            if (ret.get(i).tag instanceof DefineButtonTag) {
                ((DefineButtonTag) ret.get(i).tag).exportAssetsTags = exportAssetsTags;
            }
            if (ret.get(i).tag instanceof DefineButton2Tag) {
                ((DefineButton2Tag) ret.get(i).tag).exportAssetsTags = exportAssetsTags;
            }
            if (ret.get(i).tag instanceof DoInitActionTag) {
                //((DoInitActionTag) ret.get(i).tag).exportAssetsTags = exportAssetsTags;
            }
            if (ret.get(i).tag instanceof ASMSource) {
                ASMSource ass = (ASMSource) ret.get(i).tag;
                if (ass.containsSource()) {
                    continue;
                }
            }
            if (ret.get(i).subItems.isEmpty()) {
                ret.remove(i);
            }
        }
        return ret;
    }
    private HashSet<EventListener> listeners = new HashSet<EventListener>();

    public final void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    public final void removeEventListener(EventListener listener) {
        listeners.remove(listener);
    }

    protected void informListeners(String event, Object data) {
        for (EventListener listener : listeners) {
            listener.handleEvent(event, data);
        }
    }

    public static boolean hasErrorHeader(byte data[]) {
        if (data.length > 4) {
            if ((data[0] & 0xff) == 0xff) {
                if ((data[1] & 0xff) == 0xd9) {
                    if ((data[2] & 0xff) == 0xff) {
                        if ((data[3] & 0xff) == 0xd8) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void populateSoundStreamBlocks(List<Object> tags, Tag head, List<SoundStreamBlockTag> output) {
        boolean found = false;
        for (Object t : tags) {
            if (t == head) {
                found = true;
                continue;
            }
            if (!found) {
                continue;
            }
            if (t instanceof SoundStreamBlockTag) {
                output.add((SoundStreamBlockTag) t);
            }
            if (t instanceof SoundStreamHeadTypeTag) {
                break;
            }
            if (t instanceof Container) {
                populateSoundStreamBlocks(((Container) t).getSubItems(), head, output);
            }
        }
    }

    public void populateVideoFrames(int streamId, List<Object> tags, HashMap<Integer, VideoFrameTag> output) {
        for (Object t : tags) {
            if (t instanceof VideoFrameTag) {
                output.put(((VideoFrameTag) t).frameNum, (VideoFrameTag) t);
            }
            if (t instanceof Container) {
                populateVideoFrames(streamId, ((Container) t).getSubItems(), output);
            }
        }
    }

    public void exportMovies(String outdir) throws IOException {
        exportMovies(outdir, tags);
    }

    public void exportSounds(String outdir, boolean mp3, boolean wave) throws IOException {
        exportSounds(outdir, tags, mp3, wave);
    }

    public byte[] exportSound(Tag t) throws IOException {
        boolean mp3 = true;
        boolean wave = true;
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        int id = 0;
        if (t instanceof DefineSoundTag) {
            id = ((DefineSoundTag) t).soundId;
        }


        if (t instanceof DefineSoundTag) {
            DefineSoundTag st = (DefineSoundTag) t;
            if ((st.soundFormat == DefineSoundTag.FORMAT_ADPCM) && wave) {
                fos = new ByteArrayOutputStream();
                createWavFromAdpcm(fos, st.soundRate, st.soundSize, st.soundType, st.soundData);
            } else if ((st.soundFormat == DefineSoundTag.FORMAT_MP3) && mp3) {
                fos = new ByteArrayOutputStream();
                fos.write(st.soundData, 2, st.soundData.length - 2);
            } else {
                fos = new ByteArrayOutputStream();
                FLVOutputStream flv = new FLVOutputStream(fos);
                flv.writeHeader(true, false);
                flv.writeTag(new FLVTAG(0, new AUDIODATA(st.soundFormat, st.soundRate, st.soundSize, st.soundType, st.soundData)));
            }
        }
        if (t instanceof SoundStreamHeadTypeTag) {
            SoundStreamHeadTypeTag shead = (SoundStreamHeadTypeTag) t;
            List<SoundStreamBlockTag> blocks = new ArrayList<SoundStreamBlockTag>();
            List<Object> objs = new ArrayList<Object>(this.tags);
            populateSoundStreamBlocks(objs, t, blocks);
            if ((shead.getSoundFormat() == DefineSoundTag.FORMAT_ADPCM) && wave) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int b = 0; b < blocks.size(); b++) {
                    byte data[] = blocks.get(b).getData(SWF.DEFAULT_VERSION);
                    baos.write(data);
                }
                fos = new ByteArrayOutputStream();
                createWavFromAdpcm(fos, shead.getSoundRate(), shead.getSoundSize(), shead.getSoundType(), baos.toByteArray());
            } else if ((shead.getSoundFormat() == DefineSoundTag.FORMAT_MP3) && mp3) {
                fos = new ByteArrayOutputStream();
                for (int b = 0; b < blocks.size(); b++) {
                    byte data[] = blocks.get(b).getData(SWF.DEFAULT_VERSION);
                    fos.write(data, 4, data.length - 4);
                }
            } else {
                fos = new ByteArrayOutputStream();
                FLVOutputStream flv = new FLVOutputStream(fos);
                flv.writeHeader(true, false);

                int ms = (int) (1000.0f / ((float) frameRate));
                for (int b = 0; b < blocks.size(); b++) {
                    byte data[] = blocks.get(b).getData(SWF.DEFAULT_VERSION);
                    if (shead.getSoundFormat() == 2) { //MP3
                        data = Arrays.copyOfRange(data, 4, data.length);
                    }
                    flv.writeTag(new FLVTAG(ms * b, new AUDIODATA(shead.getSoundFormat(), shead.getSoundRate(), shead.getSoundSize(), shead.getSoundType(), data)));
                }
            }
        }
        return fos.toByteArray();
    }

    private static void writeLE(OutputStream os, long val, int size) throws IOException {
        for (int i = 0; i < size; i++) {
            os.write((int) (val & 0xff));
            val = val >> 8;
        }
    }

    private static void createWavFromAdpcm(OutputStream fos, int soundRate, int soundSize, int soundType, byte data[]) throws IOException {
        try {
            byte pcmData[] = AdpcmDecoder.decode(data, soundType == 1 ? true : false);

            ByteArrayOutputStream subChunk1Data = new ByteArrayOutputStream();
            int audioFormat = 1; //PCM
            writeLE(subChunk1Data, audioFormat, 2);
            int numChannels = soundType == 1 ? 2 : 1;
            writeLE(subChunk1Data, numChannels, 2);
            int rateMap[] = {5512, 11025, 22050, 44100};
            int sampleRate = rateMap[soundRate];
            writeLE(subChunk1Data, sampleRate, 4);
            int bitsPerSample = soundSize == 1 ? 16 : 8;
            int byteRate = sampleRate * numChannels * bitsPerSample / 8;
            writeLE(subChunk1Data, byteRate, 4);
            int blockAlign = numChannels * bitsPerSample / 8;
            writeLE(subChunk1Data, blockAlign, 2);
            writeLE(subChunk1Data, bitsPerSample, 2);

            ByteArrayOutputStream chunks = new ByteArrayOutputStream();
            chunks.write("fmt ".getBytes());
            byte subChunk1DataBytes[] = subChunk1Data.toByteArray();
            writeLE(chunks, subChunk1DataBytes.length, 4);
            chunks.write(subChunk1DataBytes);


            chunks.write("data".getBytes());
            writeLE(chunks, pcmData.length, 4);
            chunks.write(pcmData);

            fos.write("RIFF".getBytes());
            byte chunkBytes[] = chunks.toByteArray();
            writeLE(fos, 4 + chunkBytes.length, 4);
            fos.write("WAVE".getBytes());
            fos.write(chunkBytes);
            //size1=>16bit*/
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

    public void exportSounds(String outdir, List<Tag> tags, boolean mp3, boolean wave) throws IOException {
        if (tags.isEmpty()) {
            return;
        }
        if (!(new File(outdir)).exists()) {
            (new File(outdir)).mkdirs();
        }
        List<Object> os = new ArrayList<Object>(this.tags);
        for (Tag t : tags) {
            FileOutputStream fos = null;
            try {
                int id = 0;
                if (t instanceof DefineSoundTag) {
                    id = ((DefineSoundTag) t).soundId;
                }


                if (t instanceof DefineSoundTag) {
                    DefineSoundTag st = (DefineSoundTag) t;

                    if ((st.soundFormat == DefineSoundTag.FORMAT_ADPCM) && wave) {
                        fos = new FileOutputStream(outdir + File.separator + id + ".wav");
                        createWavFromAdpcm(fos, st.soundRate, st.soundSize, st.soundType, st.soundData);
                    } else if ((st.soundFormat == DefineSoundTag.FORMAT_MP3) && mp3) {
                        fos = new FileOutputStream(outdir + File.separator + id + ".mp3");
                        fos.write(st.soundData, 2, st.soundData.length - 2);
                    } else {
                        fos = new FileOutputStream(outdir + File.separator + id + ".flv");
                        FLVOutputStream flv = new FLVOutputStream(fos);
                        flv.writeHeader(true, false);
                        flv.writeTag(new FLVTAG(0, new AUDIODATA(st.soundFormat, st.soundRate, st.soundSize, st.soundType, st.soundData)));
                    }
                }
                if (t instanceof SoundStreamHeadTypeTag) {
                    SoundStreamHeadTypeTag shead = (SoundStreamHeadTypeTag) t;
                    List<SoundStreamBlockTag> blocks = new ArrayList<SoundStreamBlockTag>();
                    List<Object> objs = new ArrayList<Object>(this.tags);
                    populateSoundStreamBlocks(objs, t, blocks);
                    if ((shead.getSoundFormat() == DefineSoundTag.FORMAT_ADPCM) && wave) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        for (int b = 0; b < blocks.size(); b++) {
                            byte data[] = blocks.get(b).getData(SWF.DEFAULT_VERSION);
                            baos.write(data);
                        }
                        fos = new FileOutputStream(outdir + File.separator + id + ".wav");
                        createWavFromAdpcm(fos, shead.getSoundRate(), shead.getSoundSize(), shead.getSoundType(), baos.toByteArray());
                    } else if ((shead.getSoundFormat() == DefineSoundTag.FORMAT_MP3) && mp3) {
                        fos = new FileOutputStream(outdir + File.separator + id + ".mp3");
                        for (int b = 0; b < blocks.size(); b++) {
                            byte data[] = blocks.get(b).getData(SWF.DEFAULT_VERSION);
                            fos.write(data, 2, data.length - 2);
                        }
                    } else {
                        fos = new FileOutputStream(outdir + File.separator + id + ".flv");
                        FLVOutputStream flv = new FLVOutputStream(fos);
                        flv.writeHeader(true, false);

                        int ms = (int) (1000.0f / ((float) frameRate));
                        for (int b = 0; b < blocks.size(); b++) {
                            byte data[] = blocks.get(b).getData(SWF.DEFAULT_VERSION);
                            if (shead.getSoundFormat() == 2) { //MP3
                                data = Arrays.copyOfRange(data, 4, data.length);
                            }
                            flv.writeTag(new FLVTAG(ms * b, new AUDIODATA(shead.getSoundFormat(), shead.getSoundRate(), shead.getSoundSize(), shead.getSoundType(), data)));
                        }
                    }
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ex) {
                        //ignore
                    }
                }
            }

        }
    }

    public byte[] exportMovie(DefineVideoStreamTag videoStream) throws IOException {
        HashMap<Integer, VideoFrameTag> frames = new HashMap<Integer, VideoFrameTag>();
        List<Object> os = new ArrayList<Object>(this.tags);
        populateVideoFrames(videoStream.characterID, os, frames);



        long fileSize = 0;

        //double ms = 1000.0f / ((float) frameRate);

        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        fos = new ByteArrayOutputStream();
        //CopyOutputStream cos = new CopyOutputStream(fos, new FileInputStream("f:\\trunk\\testdata\\xfl\\xfl\\_obj\\streamvideo 7.flv"));
        OutputStream tos = fos;
        FLVOutputStream flv = new FLVOutputStream(tos);
        flv.writeHeader(false, true);
        //flv.writeTag(new FLVTAG(0, SCRIPTDATA.onMetaData(ms * frames.size() / 1000.0, videoStream.width, videoStream.height, 0, frameRate, videoStream.codecID, 0, 0, false, 0, fileSize)));
        int horizontalAdjustment = 0;
        int verticalAdjustment = 0;
        for (int i = 0; i < frames.size(); i++) {
            VideoFrameTag tag = frames.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int frameType = 1;

            if ((videoStream.codecID == DefineVideoStreamTag.CODEC_VP6)
                    || (videoStream.codecID == DefineVideoStreamTag.CODEC_VP6_ALPHA)) {
                SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(tag.videoData), SWF.DEFAULT_VERSION);
                if (videoStream.codecID == DefineVideoStreamTag.CODEC_VP6_ALPHA) {
                    sis.readUI24(); //offsetToAlpha
                }
                int frameMode = (int) sis.readUB(1);

                if (frameMode == 0) {
                    frameType = 1; //intra
                } else {
                    frameType = 2; //inter
                }
                int qp = (int) sis.readUB(6);
                int marker = (int) sis.readUB(1);
                if (frameMode == 0) {
                    int version = (int) sis.readUB(5);
                    int version2 = (int) sis.readUB(2);
                    boolean interlace = sis.readUB(1) == 1;//interlace
                    if (marker == 1 || version2 == 0) {
                        sis.readUI16();//offset
                    }
                    int dim_y = sis.readUI8();
                    int dim_x = sis.readUI8();
                    int render_y = sis.readUI8();
                    int render_x = sis.readUI8();
                    horizontalAdjustment = (int) (dim_x * Math.ceil(((double) videoStream.width) / (double) dim_x)) - videoStream.width;
                    verticalAdjustment = (int) (dim_y * Math.ceil(((double) videoStream.height) / (double) dim_y)) - videoStream.height;

                }

                SWFOutputStream sos = new SWFOutputStream(baos, SWF.DEFAULT_VERSION);
                sos.writeUB(4, horizontalAdjustment);
                sos.writeUB(4, verticalAdjustment);
            }
            if (videoStream.codecID == DefineVideoStreamTag.CODEC_SORENSON_H263) {
                SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(tag.videoData), SWF.DEFAULT_VERSION);
                sis.readUB(17);//pictureStartCode
                sis.readUB(5); //version
                sis.readUB(8); //temporalReference
                int pictureSize = (int) sis.readUB(3); //pictureSize
                if (pictureSize == 0) {
                    sis.readUB(8); //customWidth
                    sis.readUB(8); //customHeight
                }
                if (pictureSize == 1) {
                    sis.readUB(16); //customWidth
                    sis.readUB(16); //customHeight
                }
                int pictureType = (int) sis.readUB(2);
                switch (pictureType) {
                    case 0: //intra
                        frameType = 1; //keyframe
                        break;
                    case 1://inter
                        frameType = 2;
                        break;
                    case 2: //disposable
                        frameType = 3;
                        break;
                }
            }

            baos.write(tag.videoData);
            flv.writeTag(new FLVTAG((int) Math.floor(i * 1000.0f / ((float) frameRate)), new VIDEODATA(frameType, videoStream.codecID, baos.toByteArray())));
        }
        fileSize = fos.toByteArray().length;
        return fos.toByteArray();
    }

    public void exportMovies(String outdir, List<Tag> tags) throws IOException {
        if (tags.isEmpty()) {
            return;
        }
        if (!(new File(outdir)).exists()) {
            (new File(outdir)).mkdirs();
        }
        for (Tag t : tags) {
            if (t instanceof DefineVideoStreamTag) {
                DefineVideoStreamTag videoStream = (DefineVideoStreamTag) t;
                FileOutputStream fos = new FileOutputStream(outdir + File.separator + ((DefineVideoStreamTag) t).characterID + ".flv");
                fos.write(exportMovie(videoStream));
                fos.close();
            }
        }
    }

    public void exportTexts(String outdir, List<Tag> tags, boolean formatted) throws IOException {
        if (tags.isEmpty()) {
            return;
        }
        if (!(new File(outdir)).exists()) {
            (new File(outdir)).mkdirs();
        }
        for (Tag t : tags) {
            if (t instanceof TextTag) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outdir + File.separator + ((TextTag) t).getCharacterID() + ".txt");
                    if (formatted) {
                        fos.write(((TextTag) t).getFormattedText(this.tags).getBytes("UTF-8"));
                    } else {
                        fos.write(((TextTag) t).getText(this.tags).getBytes("UTF-8"));
                    }
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception ex) {
                            //ignore
                        }
                    }
                }
            }
        }
    }

    public void exportTexts(String outdir, boolean formatted) throws IOException {
        exportTexts(outdir, tags, formatted);
    }

    public static void exportShapes(String outdir, List<Tag> tags) throws IOException {
        if (tags.isEmpty()) {
            return;
        }
        if (!(new File(outdir)).exists()) {
            (new File(outdir)).mkdirs();
        }
        for (Tag t : tags) {
            if (t instanceof ShapeTag) {
                int characterID = 0;
                if (t instanceof CharacterTag) {
                    characterID = ((CharacterTag) t).getCharacterID();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outdir + File.separator + characterID + ".svg");
                    fos.write(((ShapeTag) t).toSVG().getBytes());
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception ex) {
                            //ignore
                        }
                    }
                }
            }
        }
    }

    public static void exportBinaryData(String outdir, List<Tag> tags) throws IOException {
        if (tags.isEmpty()) {
            return;
        }
        if (!(new File(outdir)).exists()) {
            (new File(outdir)).mkdirs();
        }
        for (Tag t : tags) {
            if (t instanceof DefineBinaryDataTag) {
                int characterID = 0;
                if (t instanceof CharacterTag) {
                    characterID = ((CharacterTag) t).getCharacterID();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outdir + File.separator + characterID + ".bin");
                    fos.write(((DefineBinaryDataTag) t).binaryData);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception ex) {
                            //ignore
                        }
                    }
                }
            }
        }
    }

    public void exportImages(String outdir, List<Tag> tags) throws IOException {
        if (tags.isEmpty()) {
            return;
        }
        if (!(new File(outdir)).exists()) {
            (new File(outdir)).mkdirs();
        }
        for (Tag t : tags) {
            if (t instanceof ImageTag) {
                ImageIO.write(((ImageTag) t).getImage(this.tags), ((ImageTag) t).getImageFormat().toUpperCase(), new File(outdir + File.separator + ((ImageTag) t).getCharacterID() + "." + ((ImageTag) t).getImageFormat()));
            }
        }
    }

    public void exportImages(String outdir) throws IOException {
        JPEGTablesTag jtt = null;
        for (Tag t : tags) {
            if (t instanceof JPEGTablesTag) {
                jtt = (JPEGTablesTag) t;
            }
        }
        exportImages(outdir, tags);
    }

    public void exportShapes(String outdir) throws IOException {
        exportShapes(outdir, tags);
    }

    public void exportBinaryData(String outdir) throws IOException {
        exportBinaryData(outdir, tags);
    }
    public static final String[] reservedWords = {
        "as", "break", "case", "catch", "class", "const", "continue", "default", "delete", "do", "each", "else",
        "extends", "false", "finally", "for", "function", "get", "if", "implements", "import", "in", "instanceof",
        "interface", "internal", "is", "native", "new", "null", "override", "package", "private", "protected", "public",
        "return", "set", "super", "switch", "this", "throw", "true", "try", "typeof", "use", "var", /*"void",*/ "while",
        "with", "dynamic", "default", "final", "in"};

    private boolean isReserved(String s) {
        for (String rw : reservedWords) {
            if (rw.equals(s.trim())) {
                return true;
            }
        }
        return false;
    }
    private HashMap<String, String> deobfuscated = new HashMap<String, String>();
    private Random rnd = new Random();
    private final int DEFAULT_FOO_SIZE = 10;
    public static final String validFirstCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    public static final String validNextCharacters = validFirstCharacters + "0123456789";
    public static final String fooCharacters = "bcdfghjklmnpqrstvwz";
    public static final String fooJoinCharacters = "aeiouy";
    private HashMap<DirectValueTreeItem, ConstantPool> allVariableNames = new HashMap<DirectValueTreeItem, ConstantPool>();
    private HashSet<String> allVariableNamesStr = new HashSet<String>();
    private List<GraphSourceItem> allFunctions = new ArrayList<GraphSourceItem>();

    private String fooString(String orig, boolean firstUppercase, int rndSize) {
        boolean exists;
        String ret;
        loopfoo:
        do {
            exists = false;
            int len = 3 + rnd.nextInt(rndSize - 3);
            ret = "";
            for (int i = 0; i < len; i++) {
                String c = "";
                if ((i % 2) == 0) {
                    c = "" + fooCharacters.charAt(rnd.nextInt(fooCharacters.length()));
                } else {
                    c = "" + fooJoinCharacters.charAt(rnd.nextInt(fooJoinCharacters.length()));
                }
                if (i == 0 && firstUppercase) {
                    c = c.toUpperCase();
                }
                ret += c;
            }
            if (allVariableNamesStr.contains(ret)) {
                exists = true;
                rndSize = rndSize + 1;
                continue loopfoo;
            }
            if (isReserved(ret)) {
                exists = true;
                rndSize = rndSize + 1;
                continue;
            }
            if (deobfuscated.containsValue(ret)) {
                exists = true;
                rndSize = rndSize + 1;
                continue;
            }
        } while (exists);
        deobfuscated.put(orig, ret);
        return ret;
    }

    public String deobfuscateName(HashMap<String, String> namesMap, String s, boolean firstUppercase) {
        boolean isValid = true;
        if (isReserved(s)) {
            isValid = false;
        }

        if (isValid) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) > 127) {
                    isValid = false;
                    break;
                }
            }
        }

        if (isValid) {
            Pattern pat = Pattern.compile("^[" + Pattern.quote(validFirstCharacters) + "]" + "[" + Pattern.quote(validFirstCharacters + validNextCharacters) + "]*$");
            if (!pat.matcher(s).matches()) {
                isValid = false;
            }
        }

        if (!isValid) {
            if (namesMap.containsKey(s)) {
                return namesMap.get(s);
            } else {
                String ret = fooString(s, firstUppercase, DEFAULT_FOO_SIZE);
                return ret;
            }
        }
        return null;
    }

    private static void getVariables(ConstantPool constantPool, List<Object> localData, Stack<GraphTargetItem> stack, List<GraphTargetItem> output, ActionGraphSource code, int ip, int lastIp, HashMap<DirectValueTreeItem, ConstantPool> variables, List<GraphSourceItem> functions, List<Integer> visited) {
        boolean debugMode = false;
        while ((ip > -1) && ip < code.size()) {
            if (visited.contains(ip)) {
                break;
            }

            lastIp = ip;
            GraphSourceItem ins = code.get(ip);

            if (debugMode) {
                System.err.println("Visit " + ip + ": ofs" + Helper.formatAddress(((Action) ins).getAddress()) + ":" + ((Action) ins).getASMSource(new ArrayList<GraphSourceItem>(), new ArrayList<Long>(), new ArrayList<String>(), code.version, false) + " stack:" + Helper.stackToString(stack, Helper.toList(new ConstantPool())));
            }
            if (ins.isIgnored()) {
                ip++;
                continue;
            }

            GraphTargetItem name = null;
            if ((ins instanceof ActionGetVariable)
                    || (ins instanceof ActionGetMember)
                    || (ins instanceof ActionDefineLocal2)
                    || (ins instanceof ActionNewMethod)
                    || (ins instanceof ActionNewObject)
                    || (ins instanceof ActionCallMethod)
                    || (ins instanceof ActionCallFunction)) {
                name = stack.peek();
            }


            if ((ins instanceof ActionDefineFunction) || (ins instanceof ActionDefineFunction2)) {
                functions.add(ins);
            }

            if (ins instanceof GraphSourceItemContainer) {
                GraphSourceItemContainer cnt = (GraphSourceItemContainer) ins;
                List<Long> cntSizes = cnt.getContainerSizes();
                long addr = code.pos2adr(ip + 1);
                for (Long size : cntSizes) {
                    if (size == 0) {
                        continue;
                    }
                    ip = code.adr2pos(addr);
                    addr += size;
                    int nextip = code.adr2pos(addr);
                    getVariables(variables, functions, new ActionGraphSource(code.getActions().subList(ip, nextip), code.version, new HashMap<Integer, String>(), new HashMap<String, GraphTargetItem>(), new HashMap<String, GraphTargetItem>()), 0);
                    ip = nextip;
                }
                ((GraphSourceItemContainer) ins).translateContainer(new ArrayList<List<GraphTargetItem>>(), stack, output, new HashMap<Integer, String>(), new HashMap<String, GraphTargetItem>(), new HashMap<String, GraphTargetItem>());
                continue;
            }

            if ((ins instanceof ActionSetVariable) || (ins instanceof ActionSetMember) || (ins instanceof ActionDefineLocal)) {
                name = stack.get(stack.size() - 2);
            }
            if (name instanceof DirectValueTreeItem) {
                variables.put((DirectValueTreeItem) name, constantPool);
            }

            //for..in return
            if (((ins instanceof ActionEquals) || (ins instanceof ActionEquals2)) && (stack.size() == 1) && (stack.peek() instanceof DirectValueTreeItem)) {
                stack.push(new DirectValueTreeItem(null, 0, new Null(), new ArrayList<String>()));
            }

            if (ins instanceof ActionConstantPool) {
                constantPool = new ConstantPool(((ActionConstantPool) ins).constantPool);
            }

            try {
                ins.translate(localData, stack, output);
            } catch (Exception ex) {
                Logger.getLogger(SWF.class.getName()).log(Level.SEVERE, "Error during getting variables", ex);
            }
            if (ins.isExit()) {
                break;
            }

            if (ins.isBranch() || ins.isJump()) {
                if (ins instanceof ActionIf) {
                    stack.pop();
                }
                visited.add(ip);
                List<Integer> branches = ins.getBranches(code);
                for (int b : branches) {
                    @SuppressWarnings("unchecked")
                    Stack<GraphTargetItem> brStack = (Stack<GraphTargetItem>) stack.clone();
                    if (b >= 0) {
                        getVariables(constantPool, localData, brStack, output, code, b, ip, variables, functions, visited);
                    } else {
                        if (debugMode) {
                            System.out.println("Negative branch:" + b);
                        }
                    }
                }
                // }
                break;
            }
            ip++;
        };
    }

    private static void getVariables(HashMap<DirectValueTreeItem, ConstantPool> variables, List<GraphSourceItem> functions, ActionGraphSource code, int addr) {
        List<Object> localData = Helper.toList(new HashMap<Integer, String>(), new HashMap<String, GraphTargetItem>(), new HashMap<String, GraphTargetItem>());
        try {
            getVariables(null, localData, new Stack<GraphTargetItem>(), new ArrayList<GraphTargetItem>(), code, code.adr2pos(addr), 0, variables, functions, new ArrayList<Integer>());
        } catch (Exception ex) {
            Logger.getLogger(SWF.class.getName()).log(Level.SEVERE, "Getting variables error", ex);
        }
    }

    private HashMap<DirectValueTreeItem, ConstantPool> getVariables(HashMap<DirectValueTreeItem, ConstantPool> variables, List<GraphSourceItem> functions, ASMSource src) {
        HashMap<DirectValueTreeItem, ConstantPool> ret = new HashMap<DirectValueTreeItem, ConstantPool>();
        List<Action> actions = src.getActions(version);
        actionsMap.put(src, actions);
        getVariables(variables, functions, new ActionGraphSource(actions, version, new HashMap<Integer, String>(), new HashMap<String, GraphTargetItem>(), new HashMap<String, GraphTargetItem>()), 0);
        return ret;
    }
    private HashMap<ASMSource, List<Action>> actionsMap = new HashMap<ASMSource, List<Action>>();

    private void getVariables(List<Object> objs, String path) {
        for (Object o : objs) {
            if (o instanceof ASMSource) {
                informListeners("getVariables", path + "/" + o.toString());
                getVariables(allVariableNames, allFunctions, (ASMSource) o);
            }
            if (o instanceof Container) {
                getVariables(((Container) o).getSubItems(), path + "/" + o.toString());
            }
        }
    }

    public int deobfuscateAS2Identifiers() {
        actionsMap = new HashMap<ASMSource, List<Action>>();
        allFunctions = new ArrayList<GraphSourceItem>();
        allVariableNames = new HashMap<DirectValueTreeItem, ConstantPool>();
        List<Object> objs = new ArrayList<Object>();
        int ret = 0;
        objs.addAll(tags);
        getVariables(objs, "");
        for (GraphSourceItem fun : allFunctions) {
            if (fun instanceof ActionDefineFunction) {
                ActionDefineFunction f = (ActionDefineFunction) fun;
                String changed = deobfuscateName(deobfuscated, f.functionName, false);
                if (changed != null) {
                    f.replacedFunctionName = changed;
                }
            }
            if (fun instanceof ActionDefineFunction2) {
                ActionDefineFunction2 f = (ActionDefineFunction2) fun;
                String changed = deobfuscateName(deobfuscated, f.functionName, false);
                if (changed != null) {
                    f.replacedFunctionName = changed;
                }
            }
        }
        for (DirectValueTreeItem ti : allVariableNames.keySet()) {
            String name = ti.toStringNoH(allVariableNames.get(ti));
            allVariableNamesStr.add(name);
        }
        for (DirectValueTreeItem ti : allVariableNames.keySet()) {
            String name = ti.toStringNoH(allVariableNames.get(ti));
            String changed = deobfuscateName(deobfuscated, name, false);
            if (changed != null) {
                ActionPush pu = (ActionPush) ti.src;
                if (pu.replacement == null) {
                    pu.replacement = new ArrayList<Object>();
                    pu.replacement.addAll(pu.values);
                }
                pu.replacement.set(ti.pos, changed);
                ret++;
            }
        }
        for (ASMSource src : actionsMap.keySet()) {
            actionsMap.put(src, Action.removeNops(0, actionsMap.get(src), version, 0));
            src.setActions(actionsMap.get(src), version);
        }
        return ret;
    }

    public void exportFla(String outfile, String swfName, String generator, String generatorVerName, String generatorVersion) {
        XFLConverter.convertSWF(this, swfName, outfile, true, generator, generatorVerName, generatorVersion);
    }

    public void exportXfl(String outfile, String swfName, String generator, String generatorVerName, String generatorVersion) {
        XFLConverter.convertSWF(this, swfName, outfile, false, generator, generatorVerName, generatorVersion);
    }

    public static float twipToPixel(int twip) {
        return ((float) twip) / 20.0f;
    }

    private static class CachedImage implements Serializable {

        private int data[];
        private int width;
        private int height;
        private int type;

        public CachedImage(BufferedImage img) {
            width = img.getWidth();
            height = img.getHeight();
            type = img.getType();
            data = Filtering.getRGB(img, 0, 0, width, height);
        }

        public BufferedImage getImage() {
            BufferedImage ret = new BufferedImage(width, height, type);
            Filtering.setRGB(ret, 0, 0, width, height, data);
            return ret;
        }
    }

    public static AffineTransform matrixToTransform(MATRIX mat) {
        return new AffineTransform(mat.getScaleXFloat(), mat.getRotateSkew0Float(),
                mat.getRotateSkew1Float(), mat.getScaleYFloat(),
                mat.translateX, mat.translateY);
    }
    private static Cache cache = new Cache(false);

    public static RECT fixRect(RECT rect) {
        RECT ret = new RECT();
        ret.Xmin = rect.Xmin;
        ret.Xmax = rect.Xmax;
        ret.Ymin = rect.Ymin;
        ret.Ymax = rect.Ymax;



        if (ret.Xmax <= 0) {
            ret.Xmax = ret.getWidth();
            ret.Xmin = 0;
        }
        if (ret.Ymax <= 0) {
            ret.Ymax = ret.getHeight();
            ret.Ymin = 0;
        }
        if (ret.Xmin < 0) {
            ret.Xmax += (-ret.Xmin);
            ret.Xmin = 0;
        }
        if (ret.Ymin < 0) {
            ret.Ymax += (-ret.Ymin);
            ret.Ymin = 0;
        }

        if (ret.getWidth() < 1 || ret.getHeight() < 1) {
            ret.Xmin = 0;
            ret.Ymin = 0;
            ret.Xmax = 20;
            ret.Ymax = 20;
        }
        return ret;
    }

    public static BufferedImage frameToImage(int containerId, int maxDepth, HashMap<Integer, Layer> layers, Color backgroundColor, HashMap<Integer, CharacterTag> characters, int frame, List<Tag> allTags, List<Tag> controlTags, RECT displayRect) {
        displayRect = fixRect(displayRect);

        String key = "frame_" + frame + "_" + containerId;
        if (cache.contains(key)) {
            return ((CachedImage) cache.get(key)).getImage();
        }
        float unzoom = 20;
        BufferedImage ret = new BufferedImage((int) (displayRect.Xmax / unzoom), (int) (displayRect.Ymax / unzoom), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) ret.getGraphics();
        g.setPaint(backgroundColor);
        g.fill(new Rectangle(ret.getWidth(), ret.getHeight()));
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 1; i <= maxDepth; i++) {
            if (!layers.containsKey(i)) {
                continue;
            }
            Layer layer = layers.get(i);
            if (!characters.containsKey(layer.characterId)) {
                continue;
            }
            if (!layer.visible) {
                continue;
            }
            CharacterTag character = characters.get(layer.characterId);
            MATRIX mat = new MATRIX(layer.matrix);
            if (mat == null) {
                mat = new MATRIX();
            }
            mat.translateX /= 20;
            mat.translateY /= 20;

            AffineTransform trans = matrixToTransform(mat);

            //trans.translate(transX, transY);
            g.setTransform(trans);
            if (character instanceof DrawableTag) {
                DrawableTag drawable = (DrawableTag) character;
                BufferedImage img = drawable.toImage(1/*layer.duration*/, allTags, displayRect, characters);


                if (layer.filters != null) {
                    for (FILTER filter : layer.filters) {
                        img = filter.apply(img);
                    }
                }
                if (layer.colorTransForm != null) {
                    img = layer.colorTransForm.apply(img);
                }

                if (layer.colorTransFormAlpha != null) {
                    img = layer.colorTransFormAlpha.apply(img);
                }
                Point imgPos = drawable.getImagePos(characters);
                if (imgPos.x < 0) {
                    imgPos.x = 0;
                }
                if (imgPos.y < 0) {
                    imgPos.y = 0;
                }
                switch (layer.blendMode) {
                    case 0:
                    case 1:
                        g.setComposite(AlphaComposite.SrcOver);
                        break;
                    case 2: //TODO:Layer
                        g.setComposite(AlphaComposite.SrcOver);
                        break;
                    case 3:
                        g.setComposite(BlendComposite.Multiply);
                        break;
                    case 4:
                        g.setComposite(BlendComposite.Screen);
                        break;
                    case 5:
                        g.setComposite(BlendComposite.Lighten);
                        break;
                    case 6:
                        g.setComposite(BlendComposite.Darken);
                        break;
                    case 7:
                        g.setComposite(BlendComposite.Difference);
                        break;
                    case 8:
                        g.setComposite(BlendComposite.Add);
                        break;
                    case 9:
                        g.setComposite(BlendComposite.Subtract);
                        break;
                    case 10:
                        g.setComposite(BlendComposite.Invert);
                        break;
                    case 11:
                        g.setComposite(BlendComposite.Alpha);
                        break;
                    case 12:
                        g.setComposite(BlendComposite.Erase);
                        break;
                    case 13:
                        g.setComposite(BlendComposite.Overlay);
                        break;
                    case 14:
                        g.setComposite(BlendComposite.HardLight);
                        break;
                    default: //Not implemented
                        g.setComposite(AlphaComposite.SrcOver);
                        break;
                }

                g.drawImage(img, imgPos.x, imgPos.y, null);
            } else if (character instanceof BoundedTag) {
                BoundedTag b = (BoundedTag) character;
                g.setPaint(new Color(255, 255, 255, 128));
                g.setComposite(BlendComposite.Invert);
                RECT r = b.getRect(characters);
                g.drawString(character.toString(), (r.Xmin) / 20 + 3, (r.Ymin) / 20 + 15);
                g.draw(new Rectangle(r.Xmin / 20, r.Ymin / 20, r.getWidth() / 20, r.getHeight() / 20));
                g.drawLine(r.Xmin / 20, r.Ymin / 20, r.Xmax / 20, r.Ymax / 20);
                g.drawLine(r.Xmax / 20, r.Ymin / 20, r.Xmin / 20, r.Ymax / 20);
                g.setComposite(AlphaComposite.Dst);
            }
        }
        g.setTransform(AffineTransform.getScaleInstance(1, 1));
        /*g.setPaint(Color.yellow);
         g.draw(new Rectangle(ret.getWidth()-1,ret.getHeight()-1));*/
        cache.put(key, new CachedImage(ret));
        return ret;
    }

    public static BufferedImage frameToImage(int containerId, int frame, List<Tag> allTags, List<Tag> controlTags, RECT displayRect, int totalFrameCount) {
        List<BufferedImage> ret = new ArrayList<BufferedImage>();
        framesToImage(containerId, ret, frame, frame, allTags, controlTags, displayRect, totalFrameCount);
        if (ret.isEmpty()) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        return ret.get(0);
    }

    public static void framesToImage(int containerId, List<BufferedImage> ret, int startFrame, int stopFrame, List<Tag> allTags, List<Tag> controlTags, RECT displayRect, int totalFrameCount) {
        for (int i = startFrame; i <= stopFrame; i++) {
            String key = "frame_" + i + "_" + containerId;
            if (cache.contains(key)) {
                ret.add(((CachedImage) cache.get(key)).getImage());
                startFrame++;
            } else {
                break;
            }
        }
        if (startFrame > stopFrame) {
            return;
        }
        if (totalFrameCount == 0) {
            return;
        }

        while (startFrame > totalFrameCount) {
            startFrame -= totalFrameCount;
        }

        while (stopFrame > totalFrameCount) {
            stopFrame -= totalFrameCount;
        }


        HashMap<Integer, CharacterTag> characters = new HashMap<Integer, CharacterTag>();
        for (Tag t : allTags) {
            if (t instanceof CharacterTag) {
                CharacterTag ch = (CharacterTag) t;
                characters.put(ch.getCharacterID(), ch);
            }
        }

        HashMap<Integer, Layer> layers = new HashMap<Integer, Layer>();

        int maxDepth = 0;
        int f = 1;
        Color backgroundColor = new Color(0, 0, 0, 0);
        for (Tag t : controlTags) {
            if (t instanceof SetBackgroundColorTag) {
                SetBackgroundColorTag c = (SetBackgroundColorTag) t;
                backgroundColor = new Color(c.backgroundColor.red, c.backgroundColor.green, c.backgroundColor.blue);
            }

            if (t instanceof PlaceObjectTypeTag) {
                PlaceObjectTypeTag po = (PlaceObjectTypeTag) t;
                int depth = po.getDepth();
                if (depth > maxDepth) {
                    maxDepth = depth;
                }

                if (!layers.containsKey(depth)) {
                    layers.put(depth, new Layer());
                }
                Layer layer = layers.get(depth);
                int characterId = po.getCharacterId();
                if (characterId != -1) {
                    layer.characterId = characterId;
                }
                layer.visible = po.isVisible();
                if (po.flagMove()) {
                    MATRIX matrix2 = po.getMatrix();
                    if (matrix2 != null) {
                        layer.matrix = matrix2;
                    }
                    String instanceName = po.getInstanceName();
                    if (instanceName != null) {
                        layer.instanceName = instanceName;
                    }
                    CXFORM colorTransForm = po.getColorTransform();
                    if (colorTransForm != null) {
                        layer.colorTransForm = colorTransForm;
                    }
                    CXFORMWITHALPHA colorTransFormAlpha = po.getColorTransformWithAlpha();
                    if (colorTransFormAlpha != null) {
                        layer.colorTransFormAlpha = colorTransFormAlpha;
                    }
                    if (po.cacheAsBitmap()) {
                        layer.cacheAsBitmap = true;
                    }
                    int blendMode = po.getBlendMode();
                    if (blendMode != 0) {
                        layer.blendMode = blendMode;
                    }
                    List<FILTER> filters = po.getFilters();
                    if (filters != null) {
                        layer.filters = filters;
                    }
                    int ratio = po.getRatio();
                    if (ratio != -1) {
                        layer.ratio = ratio;
                    }
                } else {
                    layer.matrix = po.getMatrix();
                    layer.instanceName = po.getInstanceName();
                    layer.colorTransForm = po.getColorTransform();
                    layer.colorTransFormAlpha = po.getColorTransformWithAlpha();
                    layer.cacheAsBitmap = po.cacheAsBitmap();
                    layer.blendMode = po.getBlendMode();
                    layer.filters = po.getFilters();
                    layer.ratio = po.getRatio();
                }
            }

            if (t instanceof RemoveTag) {
                RemoveTag rt = (RemoveTag) t;
                layers.remove(rt.getDepth());

            }
            for (Layer l : layers.values()) {
                l.duration++;
            }
            if (t instanceof ShowFrameTag) {
                if (f > stopFrame) {
                    break;
                }
                if ((f >= startFrame) && (f <= stopFrame)) {
                    ret.add(frameToImage(containerId, maxDepth, layers, backgroundColor, characters, f, allTags, controlTags, displayRect));
                }
                f++;
            }
        }
        return;
    }
}
