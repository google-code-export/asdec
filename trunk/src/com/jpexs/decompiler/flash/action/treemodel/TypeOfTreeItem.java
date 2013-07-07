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
package com.jpexs.decompiler.flash.action.treemodel;

import com.jpexs.decompiler.flash.ecma.EcmaScript;
import com.jpexs.decompiler.flash.ecma.EcmaType;
import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import com.jpexs.decompiler.flash.helpers.Helper;
import java.util.List;

public class TypeOfTreeItem extends TreeItem {

    private GraphTargetItem value;

    public TypeOfTreeItem(GraphSourceItem instruction, GraphTargetItem value) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.value = value;
    }

    @Override
    public String toString(ConstantPool constants) {
        return hilight("typeof(") + value.toString(Helper.toList(constants)) + hilight(")");
    }

    @Override
    public List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> getNeededSources() {
        List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> ret = super.getNeededSources();
        ret.addAll(value.getNeededSources());
        return ret;
    }

    @Override
    public Object getResult() {
        Object res = value.getResult();
        EcmaType type = EcmaScript.type(res);
        switch (type) {
            case STRING:
                return "string";
            case BOOLEAN:
                return "boolean";
            case NUMBER:
                return "number";
            case OBJECT:
                return "object";
            case UNDEFINED:
                return "undefined";
            case NULL:
                return "null";
        }
        //TODO: function,movieclip
        return "object";
    }

    @Override
    public boolean isCompileTime() {
        return value.isCompileTime();
    }
}
