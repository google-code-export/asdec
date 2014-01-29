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
import com.jpexs.decompiler.flash.exporters.BitmapExporter;
import com.jpexs.decompiler.flash.exporters.SVGShapeExporter;
import com.jpexs.decompiler.flash.tags.base.BoundedTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.SHAPEWITHSTYLE;
import com.jpexs.helpers.SerializableImage;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class DefineShape3Tag extends CharacterTag implements BoundedTag, ShapeTag {

    public int shapeId;
    private final RECT shapeBounds;
    public SHAPEWITHSTYLE shapes;
    public static final int ID = 32;

    @Override
    public Point getImagePos(int frame, HashMap<Integer, CharacterTag> characters, Stack<Integer> visited) {
        return new Point(shapeBounds.Xmin / 20, shapeBounds.Ymin / 20);
    }

    @Override
    public int getShapeNum() {
        return 3;
    }

    @Override
    public SHAPEWITHSTYLE getShapes() {
        return shapes;
    }

    @Override
    public Set<Integer> getNeededCharacters() {
        return shapes.getNeededCharacters();
    }

    @Override
    public RECT getRect(HashMap<Integer, CharacterTag> characters, Stack<Integer> visited) {
        return shapeBounds;
    }

    @Override
    public String toSVG() {
        SVGShapeExporter exporter = new SVGShapeExporter(swf, getShapes());
        exporter.export();
        return exporter.getSVG();
    }

    @Override
    public SerializableImage toImage(int frame, List<Tag> tags, HashMap<Integer, CharacterTag> characters, Stack<Integer> visited) {
        BitmapExporter exporter = new BitmapExporter(swf, getShapes());
        exporter.export();
        return exporter.getImage();
    }

    @Override
    public int getCharacterId() {
        return shapeId;
    }

    public DefineShape3Tag(SWF swf, byte[] data, int version, long pos) throws IOException {
        super(swf, ID, "DefineShape3", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        shapeId = sis.readUI16();
        shapeBounds = sis.readRECT();
        shapes = sis.readSHAPEWITHSTYLE(3);
    }

    @Override
    public int getNumFrames() {
        return 1;
    }
}
