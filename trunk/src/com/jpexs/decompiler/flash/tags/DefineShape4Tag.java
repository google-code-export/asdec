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

public class DefineShape4Tag extends CharacterTag implements BoundedTag, ShapeTag {

    public int shapeId;
    public RECT shapeBounds;
    public RECT edgeBounds;
    public boolean usesFillWindingRule;
    public boolean usesNonScalingStrokes;
    public boolean usesScalingStrokes;
    public SHAPEWITHSTYLE shapes;

    @Override
    public Point getImagePos(int frame, HashMap<Integer, CharacterTag> characters) {
        return new Point(shapeBounds.Xmin / 20, shapeBounds.Ymin / 20);
    }

    @Override
    public int getShapeNum() {
        return 4;
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
    public String toSVG() {
        return shapes.toSVG(4);
    }

    @Override
    public BufferedImage toImage(int frame, List<Tag> tags, RECT displayRect, HashMap<Integer, CharacterTag> characters) {
        return shapes.toImage(4, tags);
    }

    @Override
    public int getCharacterID() {
        return shapeId;
    }

    @Override
    public RECT getRect(HashMap<Integer, CharacterTag> characters) {
        return shapeBounds;
    }

    public DefineShape4Tag(byte[] data, int version, long pos) throws IOException {
        super(83, "DefineShape4", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        shapeId = sis.readUI16();
        shapeBounds = sis.readRECT();
        edgeBounds = sis.readRECT();
        sis.readUB(5);
        usesFillWindingRule = sis.readUB(1) == 1;
        usesNonScalingStrokes = sis.readUB(1) == 1;
        usesScalingStrokes = sis.readUB(1) == 1;
        shapes = sis.readSHAPEWITHSTYLE(4);
    }

    @Override
    public List<GeneralPath> getPaths(List<Tag> tags) {
        return SHAPERECORD.shapeToPaths(tags, 4, shapes.shapeRecords);
    }

    @Override
    public int getNumFrames() {
        return 1;
    }
}
