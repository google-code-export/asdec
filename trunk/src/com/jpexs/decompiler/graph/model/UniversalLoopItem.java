/*
 * Copyright (C) 2013 JPEXS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.graph.model;

import com.jpexs.decompiler.graph.Block;
import com.jpexs.decompiler.graph.Graph;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.Loop;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class UniversalLoopItem extends LoopItem implements Block {

    public List<GraphTargetItem> commands;

    public UniversalLoopItem(GraphSourceItem src, Loop loop) {
        super(src, loop);
    }

    @Override
    public String toString(boolean highlight, List<Object> localData) {
        String ret = "";
        ret += hilight("loop" + loop.id + ":", highlight) + "\r\n";
        ret += hilight("while(true)", highlight);
        ret += "\r\n" + hilight("{", highlight) + "\r\n";
        ret += Graph.INDENTOPEN + "\r\n";
        for (GraphTargetItem ti : commands) {
            if (!ti.isEmpty()) {
                ret += ti.toStringSemicoloned(highlight, localData) + "\r\n";
            }
        }
        ret += Graph.INDENTCLOSE + "\r\n";
        ret += hilight("}", highlight) + "\r\n";
        ret += hilight(":loop" + loop.id, highlight);
        return ret;
    }

    @Override
    public List<ContinueItem> getContinues() {
        List<ContinueItem> ret = new ArrayList<>();
        for (GraphTargetItem ti : commands) {
            if (ti instanceof ContinueItem) {
                ret.add((ContinueItem) ti);
            }
            if (ti instanceof Block) {
                ret.addAll(((Block) ti).getContinues());
            }
        }
        return ret;
    }

    @Override
    public List<List<GraphTargetItem>> getSubs() {
        List<List<GraphTargetItem>> ret = new ArrayList<>();
        ret.add(commands);
        return ret;
    }

    @Override
    public boolean hasReturnValue() {
        return false;
    }
}
