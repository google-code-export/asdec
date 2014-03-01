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

import com.jpexs.decompiler.flash.Layer;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.SWFInputStream;
import com.jpexs.decompiler.flash.SWFOutputStream;
import com.jpexs.decompiler.flash.abc.CopyOutputStream;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.Matrix;
import com.jpexs.decompiler.flash.exporters.Point;
import com.jpexs.decompiler.flash.tags.base.BoundedTag;
import com.jpexs.decompiler.flash.tags.base.ButtonTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.tags.base.Container;
import com.jpexs.decompiler.flash.tags.base.ContainerItem;
import com.jpexs.decompiler.flash.types.BUTTONCONDACTION;
import com.jpexs.decompiler.flash.types.BUTTONRECORD;
import com.jpexs.decompiler.flash.types.BasicType;
import com.jpexs.decompiler.flash.types.ColorTransform;
import com.jpexs.decompiler.flash.types.MATRIX;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.annotations.Reserved;
import com.jpexs.decompiler.flash.types.annotations.SWFType;
import com.jpexs.helpers.Cache;
import com.jpexs.helpers.SerializableImage;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends the capabilities of DefineButton by allowing any state transition to
 * trigger actions
 *
 * @author JPEXS
 */
public class DefineButton2Tag extends CharacterTag implements Container, BoundedTag, ButtonTag {

    /**
     * ID for this character
     */
    @SWFType(BasicType.UI16)
    public int buttonId;

    @Reserved
    @SWFType(value = BasicType.UB, count = 7)
    public int reserved;

    /**
     * Track as menu button
     */
    public boolean trackAsMenu;
    /**
     * Characters that make up the button
     */
    public List<BUTTONRECORD> characters;
    /**
     * Actions to execute at particular button events
     */
    public List<BUTTONCONDACTION> actions = new ArrayList<>();
    public static final int ID = 34;

    @Override
    public int getCharacterId() {
        return buttonId;
    }

    @Override
    public List<BUTTONRECORD> getRecords() {
        return characters;
    }

    /**
     * Constructor
     *
     * @param swf
     * @param data Data bytes
     * @param version SWF version
     * @param pos
     * @throws IOException
     */
    public DefineButton2Tag(SWF swf, byte[] data, int version, long pos) throws IOException {
        super(swf, ID, "DefineButton2", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        buttonId = sis.readUI16();
        reserved = (int) sis.readUB(7);
        trackAsMenu = sis.readUB(1) == 1;
        int actionOffset = sis.readUI16();
        characters = sis.readBUTTONRECORDList(true);
        if (actionOffset > 0) {
            actions = sis.readBUTTONCONDACTIONList(swf);
        }
    }

    /**
     * Gets data bytes
     *
     * @return Bytes of data
     */
    @Override
    public byte[] getData() {
        if (Configuration.disableDangerous.get()) {
            return super.getData();
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(super.data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        if (Configuration.debugCopy.get()) {
            os = new CopyOutputStream(os, bais);
        }
        SWFOutputStream sos = new SWFOutputStream(os, getVersion());
        try {
            sos.writeUI16(buttonId);
            sos.writeUB(7, reserved);
            sos.writeUB(1, trackAsMenu ? 1 : 0);

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            OutputStream os2 = baos2;
            byte[] origbrdata = null;
            if (Configuration.debugCopy.get()) {
                SWFInputStream sis = new SWFInputStream(bais, getVersion());
                int len = sis.readUI16();
                if (len != 0) {
                    origbrdata = sis.readBytesEx(len - 2);
                    os2 = new CopyOutputStream(os2, new ByteArrayInputStream(origbrdata));
                }
            }
            try (SWFOutputStream sos2 = new SWFOutputStream(os2, getVersion())) {
                sos2.writeBUTTONRECORDList(characters, true);
            }
            byte[] brdata = baos2.toByteArray();
            if (Configuration.debugCopy.get()) {
                if (origbrdata != null) {
                    if (origbrdata.length != brdata.length) {
                        /*throw nso*/
                    }
                }
            }
            if (Configuration.debugCopy.get()) {
                sos = new SWFOutputStream(baos, getVersion());
            }
            if ((actions == null) || (actions.isEmpty())) {
                sos.writeUI16(0);
            } else {
                sos.writeUI16(2 + brdata.length);
            }
            sos.write(brdata);
            if (Configuration.debugCopy.get()) {
                sos = new SWFOutputStream(new CopyOutputStream(baos, bais), getVersion());
            }
            sos.writeBUTTONCONDACTIONList(actions);
            sos.close();
        } catch (IOException e) {
            Logger.getLogger(DefineButton2Tag.class.getName()).log(Level.SEVERE, null, e);
        }
        return baos.toByteArray();
    }

    /**
     * Returns all sub-items
     *
     * @return List of sub-items
     */
    @Override
    public List<ContainerItem> getSubItems() {
        List<ContainerItem> ret = new ArrayList<>();
        ret.addAll(actions);
        return ret;
    }

    /**
     * Returns number of sub-items
     *
     * @return Number of sub-items
     */
    @Override
    public int getItemCount() {
        return actions.size();
    }

    @Override
    public Set<Integer> getNeededCharacters() {
        HashSet<Integer> needed = new HashSet<>();
        for (BUTTONRECORD r : characters) {
            needed.add(r.characterId);
        }
        return needed;
    }
    private static final Cache<RECT> rectCache = Cache.getInstance(true);

    @Override
    public RECT getRect(Map<Integer, CharacterTag> allCharacters, Stack<Integer> visited) {
        if (rectCache.contains(this)) {
            return (RECT) rectCache.get(this);
        }
        if (visited.contains(buttonId)) {
            return new RECT();
        }
        visited.push(buttonId);
        RECT rect = new RECT(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE);
        for (BUTTONRECORD r : characters) {
            CharacterTag ch = allCharacters.get(r.characterId);
            if (ch instanceof BoundedTag) {
                if (visited.contains(ch.getCharacterId())) {
                    continue;
                }
                RECT r2 = ((BoundedTag) ch).getRect(allCharacters, visited);
                MATRIX mat = r.placeMatrix;
                if (mat != null) {
                    r2 = mat.apply(r2);
                }
                rect.Xmin = Math.min(r2.Xmin, rect.Xmin);
                rect.Ymin = Math.min(r2.Ymin, rect.Ymin);
                rect.Xmax = Math.max(r2.Xmax, rect.Xmax);
                rect.Ymax = Math.max(r2.Ymax, rect.Ymax);
            }
        }
        visited.pop();
        rectCache.put(this, rect);
        return rect;
    }

    @Override
    public boolean trackAsMenu() {
        return trackAsMenu;
    }

    @Override
    public SerializableImage toImage(int frame, int ratio, List<Tag> tags, Map<Integer, CharacterTag> characters, Stack<Integer> visited, Matrix transformation, ColorTransform colorTransform) {
        throw new Error("this overload of toImage call is not supported on BoundedTag");
    }

    @Override
    public void toImage(int frame, int ratio, List<Tag> tags, Map<Integer, CharacterTag> characters, Stack<Integer> visited, SerializableImage image, Matrix transformation, ColorTransform colorTransform) {
        if (visited.contains(buttonId)) {
            return;
        }
        visited.push(buttonId);
        HashMap<Integer, Layer> layers = new HashMap<>();
        int maxDepth = 0;
        for (BUTTONRECORD r : this.characters) {
            if (r.buttonStateUp) {
                Layer layer = new Layer();
                layer.colorTransForm = r.colorTransform;
                layer.blendMode = r.blendMode;
                layer.filters = r.filterList;
                layer.matrix = r.placeMatrix;
                layer.characterId = r.characterId;
                if (r.placeDepth > maxDepth) {
                    maxDepth = r.placeDepth;
                }
                layers.put(r.placeDepth, layer);
            }
        }
        visited.pop();
        RECT displayRect = getRect(characters, visited);
        visited.push(buttonId);
        SWF.frameToImage(buttonId, maxDepth, layers, new Color(0, 0, 0, 0), characters, 1, tags, tags, displayRect, visited, image, transformation, colorTransform);
    }

    @Override
    public Point getImagePos(int frame, Map<Integer, CharacterTag> characters, Stack<Integer> visited) {
        RECT r = getRect(characters, visited);
        return new Point(r.Xmin / SWF.unitDivisor, r.Ymin / SWF.unitDivisor);
    }

    @Override
    public int getNumFrames() {
        return 1;
    }
}
