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
package com.jpexs.decompiler.flash.tags.base;

import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.decompiler.flash.types.SHAPE;
import java.awt.Font;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public abstract class FontTag extends CharacterTag implements AloneTag {

    public FontTag(int id, String name, byte data[], long pos) {
        super(id, name, data, pos);
    }

    public abstract int getFontId();

    public abstract List<SHAPE> getGlyphShapeTable();

    public abstract void addCharacter(List<Tag> tags, char character);

    public abstract char glyphToChar(List<Tag> tags, int glyphIndex);

    public abstract int charToGlyph(List<Tag> tags, char c);

    public abstract int getGlyphAdvance(int glyphIndex);

    public abstract int getGlyphWidth(int glyphIndex);

    public abstract String getFontName(List<Tag> tags);

    public abstract boolean isSmall();

    public abstract boolean isBold();

    public abstract boolean isItalic();

    public abstract int getDivider();

    public abstract int getAscent();

    public abstract int getDescent();

    public abstract int getLeading();

    public boolean containsChar(List<Tag> tags, char character) {
        return charToGlyph(tags, character) > -1;
    }

    public int getFontStyle() {
        int fontStyle = 0;
        if (isBold()) {
            fontStyle |= Font.BOLD;
        }
        if (isItalic()) {
            fontStyle |= Font.ITALIC;
        }
        return fontStyle;
    }

    public abstract String getCharacters(List<Tag> tags);
}
