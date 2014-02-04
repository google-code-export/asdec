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

import com.jpexs.decompiler.flash.types.annotations.Conditional;
import com.jpexs.decompiler.flash.types.annotations.SWFType;

/**
 *
 * @author JPEXS
 */
public class MORPHLINESTYLE2 {

    @SWFType(BasicType.UI16)
    public int startWidth;
    @SWFType(BasicType.UI16)
    public int endWidth;
    @SWFType(value=BasicType.UB,count = 2)
    public int startCapStyle;
     @SWFType(value=BasicType.UB,count = 2)
    public int joinStyle;
    public static final int ROUND_JOIN = 0;
    public static final int BEVEL_JOIN = 1;
    public static final int MITER_JOIN = 2;
    public boolean hasFillFlag;
    public boolean noHScaleFlag;
    public boolean noVScaleFlag;
    public boolean pixelHintingFlag;
    public boolean noClose;
    
    @SWFType(value=BasicType.UB,count = 2)
    public int endCapStyle;
    
    public static final int ROUND_CAP = 0;
    public static final int NO_CAP = 1;
    public static final int SQUARE_CAP = 2;
    
    @SWFType(value=BasicType.UI16)
    @Conditional(value="joinStyle",options = {MITER_JOIN})
    public int miterLimitFactor;
    
    @Conditional(value="hasFillFlag",revert = true)
    public RGBA startColor;
    
    @Conditional(value="hasFillFlag",revert = true)
    public RGBA endColor;
    
    @Conditional(value="hasFillFlag")
    public MORPHFILLSTYLE fillType;

    public LINESTYLE2 getLineStyle2At(int ratio) {
        LINESTYLE2 ret = new LINESTYLE2();
        ret.width = startWidth + (endWidth - startWidth) * ratio / 65535;
        ret.startCapStyle = startCapStyle;
        ret.joinStyle = joinStyle;
        ret.hasFillFlag = hasFillFlag;
        ret.noHScaleFlag = noHScaleFlag;
        ret.noVScaleFlag = noVScaleFlag;
        ret.pixelHintingFlag = pixelHintingFlag;
        ret.noClose = noClose;
        ret.endCapStyle = endCapStyle;
        ret.miterLimitFactor = miterLimitFactor;
        ret.color = MORPHGRADIENT.morphColor(startColor, endColor, ratio);
        if (hasFillFlag) {
            ret.fillType = fillType.getFillStyleAt(ratio);
        }
        return ret;
    }

    public LINESTYLE2 getStartLineStyle2() {
        LINESTYLE2 ret = new LINESTYLE2();
        ret.width = startWidth;
        ret.startCapStyle = startCapStyle;
        ret.joinStyle = joinStyle;
        ret.hasFillFlag = hasFillFlag;
        ret.noHScaleFlag = noHScaleFlag;
        ret.noVScaleFlag = noVScaleFlag;
        ret.pixelHintingFlag = pixelHintingFlag;
        ret.noClose = noClose;
        ret.endCapStyle = endCapStyle;
        ret.miterLimitFactor = miterLimitFactor;
        ret.color = startColor;
        if (hasFillFlag) {
            ret.fillType = fillType.getStartFillStyle();
        }
        return ret;
    }

    public LINESTYLE2 getEndLineStyle2() {
        LINESTYLE2 ret = new LINESTYLE2();
        ret.width = endWidth;
        ret.startCapStyle = startCapStyle;
        ret.joinStyle = joinStyle;
        ret.hasFillFlag = hasFillFlag;
        ret.noHScaleFlag = noHScaleFlag;
        ret.noVScaleFlag = noVScaleFlag;
        ret.pixelHintingFlag = pixelHintingFlag;
        ret.noClose = noClose;
        ret.endCapStyle = endCapStyle;
        ret.miterLimitFactor = miterLimitFactor;
        ret.color = endColor;
        if (hasFillFlag) {
            ret.fillType = fillType.getEndFillStyle();
        }
        return ret;
    }
}
