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

import com.jpexs.decompiler.graph.Graph;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class BlockItem extends GraphTargetItem {

    List<GraphTargetItem> commands;

    public BlockItem(GraphSourceItem src, List<GraphTargetItem> commands) {
        super(src, PRECEDENCE_PRIMARY);
        this.commands = commands;
    }

    @Override
    public String toString(boolean highlight, List<Object> localData) {
        return hilight("{", highlight) + "\r\n" + Graph.graphToString(commands, highlight, localData) + "\r\n" + hilight("}", highlight);
    }

    @Override
    public List<GraphSourceItem> toSource(List<Object> localData, SourceGenerator generator) {
        return generator.generate(localData, commands);
    }

    @Override
    public boolean hasReturnValue() {
        return false;
    }
}
