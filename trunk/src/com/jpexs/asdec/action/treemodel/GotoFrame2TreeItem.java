/*
 *  Copyright (C) 2010 JPEXS
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.jpexs.asdec.action.treemodel;

import com.jpexs.asdec.action.Action;

public class GotoFrame2TreeItem extends TreeItem {
    public TreeItem frame;
    public boolean sceneBiasFlag;
    public boolean playFlag;
    public int sceneBias;

    public GotoFrame2TreeItem(Action instruction, TreeItem frame, boolean sceneBiasFlag, boolean playFlag, int sceneBias) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.frame = frame;
        this.sceneBiasFlag = sceneBiasFlag;
        this.playFlag = playFlag;
        this.sceneBias = sceneBias;
    }

    @Override
    public String toString(ConstantPool constants) {
        String prefix = "gotoAndStop";
        if (playFlag) prefix = "gotoAndPlay";
        return prefix + "(" + frame.toString(constants) + (sceneBiasFlag ? "," + sceneBias : "") + ");";
    }
}
