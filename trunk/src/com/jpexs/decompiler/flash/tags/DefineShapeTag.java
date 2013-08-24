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
import com.jpexs.decompiler.flash.tags.base.BoundedTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.SHAPEWITHSTYLE;
import com.jpexs.decompiler.flash.types.shaperecords.SHAPERECORD;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class DefineShapeTag extends CharacterTag implements BoundedTag, ShapeTag {

    public int shapeId;
    public RECT shapeBounds;
    public SHAPEWITHSTYLE shapes;
    public static final int ID = 2;

    @Override
    public int getShapeNum() {
        return 1;
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

    public DefineShapeTag(SWF swf, byte data[], int version, long pos) throws IOException {
        super(swf, ID, "DefineShape", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        shapeId = sis.readUI16();
        shapeBounds = sis.readRECT();
        shapes = sis.readSHAPEWITHSTYLE(1);
    }

    @Override
    public int getCharacterId() {
        return shapeId;
    }

    @Override
    public String toSVG() {
        return shapes.toSVG(1);
    }

    @Override
    public BufferedImage toImage(int frame, List<Tag> tags, RECT displayRect, HashMap<Integer, CharacterTag> characters, Stack<Integer> visited) {
        return shapes.toImage(1, tags);
    }

    @Override
    public Point getImagePos(int frame, HashMap<Integer, CharacterTag> characters, Stack<Integer> visited) {
        return new Point(shapeBounds.Xmin / 20, shapeBounds.Ymin / 20);
    }

    @Override
    public List<GeneralPath> getPaths(List<Tag> tags) {
        return SHAPERECORD.shapeToPaths(tags, 1, shapes.shapeRecords);
    }

    @Override
    public int getNumFrames() {
        return 1;
    }
}
