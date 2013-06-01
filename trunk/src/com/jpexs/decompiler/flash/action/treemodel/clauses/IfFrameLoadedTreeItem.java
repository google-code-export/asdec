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
package com.jpexs.decompiler.flash.action.treemodel.clauses;

import com.jpexs.decompiler.flash.action.treemodel.ConstantPool;
import com.jpexs.decompiler.flash.action.treemodel.TreeItem;
import com.jpexs.decompiler.flash.graph.Block;
import com.jpexs.decompiler.flash.graph.ContinueItem;
import com.jpexs.decompiler.flash.graph.Graph;
import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class IfFrameLoadedTreeItem extends TreeItem implements Block {

    private List<GraphTargetItem> actions;
    private GraphTargetItem frame;

    public IfFrameLoadedTreeItem(GraphTargetItem frame, List<GraphTargetItem> actions, GraphSourceItem instruction) {
        super(instruction, NOPRECEDENCE);
        this.actions = actions;
        this.frame = frame;
    }

    @Override
    public String toString(ConstantPool constants) {
        return hilight("ifFrameLoaded(") + frame.toString(constants) + hilight(")") + "\r\n" + hilight("{") + "\r\n" + Graph.graphToString(actions, constants) + "}";
    }

    @Override
    public boolean needsSemicolon() {
        return false;
    }

    @Override
    public List<ContinueItem> getContinues() {
        return new ArrayList<ContinueItem>();
    }

    @Override
    public List<List<GraphTargetItem>> getSubs() {
        List<List<GraphTargetItem>> ret = new ArrayList<List<GraphTargetItem>>();
        ret.add(actions);
        return ret;
    }
}
