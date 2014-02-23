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
package com.jpexs.decompiler.flash.types;

import com.jpexs.decompiler.flash.tags.base.NeedsCharacters;
import com.jpexs.decompiler.flash.types.annotations.Conditional;
import com.jpexs.decompiler.flash.types.annotations.SWFType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author JPEXS
 */
public class MORPHFILLSTYLE implements NeedsCharacters,Serializable {

    @SWFType(BasicType.UI8)
    public int fillStyleType;

    public static final int SOLID = 0x0;
    public static final int LINEAR_GRADIENT = 0x10;
    public static final int RADIAL_GRADIENT = 0x12;
    public static final int REPEATING_BITMAP = 0x40;
    public static final int CLIPPED_BITMAP = 0x41;
    public static final int NON_SMOOTHED_REPEATING_BITMAP = 0x42;
    public static final int NON_SMOOTHED_CLIPPED_BITMAP = 0x43;

    @Conditional(value = "fillStyleType", options = {SOLID})
    public RGBA startColor;

    @Conditional(value = "fillStyleType", options = {SOLID})
    public RGBA endColor;

    @Conditional(value = "fillStyleType", options = {LINEAR_GRADIENT, RADIAL_GRADIENT})
    public MATRIX startGradientMatrix;

    @Conditional(value = "fillStyleType", options = {LINEAR_GRADIENT, RADIAL_GRADIENT})
    public MATRIX endGradientMatrix;

    @Conditional(value = "fillStyleType", options = {LINEAR_GRADIENT, RADIAL_GRADIENT})
    public MORPHGRADIENT gradient;

    @Conditional(value = "fillStyleType", options = {CLIPPED_BITMAP, NON_SMOOTHED_REPEATING_BITMAP, NON_SMOOTHED_CLIPPED_BITMAP})
    public int bitmapId;

    @Conditional(value = "fillStyleType", options = {CLIPPED_BITMAP, NON_SMOOTHED_REPEATING_BITMAP, NON_SMOOTHED_CLIPPED_BITMAP})
    public MATRIX startBitmapMatrix;

    @Conditional(value = "fillStyleType", options = {CLIPPED_BITMAP, NON_SMOOTHED_REPEATING_BITMAP, NON_SMOOTHED_CLIPPED_BITMAP})
    public MATRIX endBitmapMatrix;

    @Override
    public Set<Integer> getNeededCharacters() {
        HashSet<Integer> ret = new HashSet<>();
        if ((fillStyleType == REPEATING_BITMAP)
                || (fillStyleType == CLIPPED_BITMAP)
                || (fillStyleType == NON_SMOOTHED_REPEATING_BITMAP)
                || (fillStyleType == NON_SMOOTHED_CLIPPED_BITMAP)) {
            ret.add(bitmapId);
        }
        return ret;
    }

    private MATRIX morphMatrix(MATRIX a, MATRIX b, int ratio) {
        if (a == null) {
            return null;
        }
        if (b == null) {
            return null;
        }
        MATRIX ret = new MATRIX();
        ret.scaleX = a.getScaleX() + (b.getScaleX() - a.getScaleX()) * ratio / 65535;
        ret.scaleY = a.getScaleY() + (b.getScaleY() - a.getScaleY()) * ratio / 65535;
        ret.rotateSkew0 = a.getRotateSkew0() + (b.getRotateSkew0() - a.getRotateSkew0()) * ratio / 65535;
        ret.rotateSkew1 = a.getRotateSkew1() + (b.getRotateSkew1() - a.getRotateSkew1()) * ratio / 65535;
        ret.translateX = a.translateX + (b.translateX - a.translateX) * ratio / 65535;
        ret.translateY = a.translateY + (b.translateY - a.translateY) * ratio / 65535;
        ret.hasRotate = true;
        ret.hasScale = true;
        return ret;
    }

    public FILLSTYLE getFillStyleAt(int ratio) {
        FILLSTYLE ret = new FILLSTYLE();
        ret.bitmapId = bitmapId;
        if (startBitmapMatrix != null) {
            ret.bitmapMatrix = morphMatrix(startBitmapMatrix, endBitmapMatrix, ratio);
        }
        if (startColor != null) {
            ret.color = MORPHGRADIENT.morphColor(startColor, endColor, ratio);
        }
        ret.fillStyleType = fillStyleType;
        if (gradient != null) {
            ret.gradient = gradient.getGradientAt(ratio);
        }
        if (startGradientMatrix != null) {
            ret.gradientMatrix = morphMatrix(startGradientMatrix, endGradientMatrix, ratio);
        }
        return ret;
    }

    public FILLSTYLE getStartFillStyle() {
        FILLSTYLE ret = new FILLSTYLE();
        ret.bitmapId = bitmapId;
        ret.bitmapMatrix = startBitmapMatrix;
        ret.color = startColor;
        ret.fillStyleType = fillStyleType;
        if (gradient != null) {
            ret.gradient = gradient.getStartGradient();
        }
        ret.gradientMatrix = startGradientMatrix;
        return ret;
    }

    public FILLSTYLE getEndFillStyle() {
        FILLSTYLE ret = new FILLSTYLE();
        ret.bitmapId = bitmapId;
        ret.bitmapMatrix = endBitmapMatrix;
        ret.color = endColor;
        ret.fillStyleType = fillStyleType;
        if (gradient != null) {
            ret.gradient = gradient.getEndGradient();
        }
        ret.gradientMatrix = endGradientMatrix;
        return ret;
    }
}
