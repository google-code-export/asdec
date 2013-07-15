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
package com.jpexs.decompiler.flash.action.treemodel.clauses;

import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.action.parser.script.ActionScriptSourceGenerator;
import com.jpexs.decompiler.flash.action.treemodel.ConstantPool;
import com.jpexs.decompiler.flash.action.treemodel.TreeItem;
import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import com.jpexs.decompiler.flash.graph.SourceGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class InterfaceTreeItem extends TreeItem {

    public GraphTargetItem name;
    public List<GraphTargetItem> superInterfaces;

    public InterfaceTreeItem(GraphTargetItem name, List<GraphTargetItem> superInterfaces) {
        super(null, NOPRECEDENCE);
        this.name = name;
        this.superInterfaces = superInterfaces;
    }

    @Override
    public String toString(ConstantPool constants) {
        String ret = "";
        ret += "interface " + name.toStringNoQuotes(constants);
        boolean first = true;
        if (!superInterfaces.isEmpty()) {
            ret += " extends ";
        }
        for (GraphTargetItem ti : superInterfaces) {
            if (!first) {
                ret += ", ";
            }
            first = false;
            ret += Action.getWithoutGlobal(ti).toStringNoQuotes(constants);
        }
        ret += "\r\n{\r\n}\r\n";
        return ret;
    }

    @Override
    public boolean needsSemicolon() {
        return false;
    }

    @Override
    public List<GraphSourceItem> toSource(List<Object> localData, SourceGenerator generator) {
        List<GraphSourceItem> ret = new ArrayList<>();
        ActionScriptSourceGenerator asGenerator = (ActionScriptSourceGenerator) generator;
        ret.addAll(asGenerator.generateTraits(localData, true, name, null, superInterfaces, null, null, null, null, null));
        return ret;
    }

    @Override
    public boolean hasReturnValue() {
        return false;
    }
}
