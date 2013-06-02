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

import com.jpexs.decompiler.flash.graph.GraphPart;
import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.List;

public class SetMemberTreeItem extends TreeItem implements SetTypeTreeItem {

    public GraphTargetItem object;
    public GraphTargetItem objectName;
    //public GraphTargetItem value;
    private int tempRegister = -1;

    @Override
    public GraphPart getFirstPart() {
        return value.getFirstPart();
    }

    @Override
    public void setValue(GraphTargetItem value) {
        this.value = value;
    }

    @Override
    public int getTempRegister() {
        return tempRegister;
    }

    @Override
    public void setTempRegister(int tempRegister) {
        this.tempRegister = tempRegister;
    }

    @Override
    public GraphTargetItem getValue() {
        return value;
    }

    public SetMemberTreeItem(GraphSourceItem instruction, GraphTargetItem object, GraphTargetItem objectName, GraphTargetItem value) {
        super(instruction, PRECEDENCE_ASSIGMENT);
        this.object = object;
        this.objectName = objectName;
        this.value = value;
    }

    @Override
    public String toString(ConstantPool constants) {
        if (!((objectName instanceof DirectValueTreeItem) && (((DirectValueTreeItem) objectName).value instanceof String))) {
            //if(!(functionName instanceof GetVariableTreeItem))
            return object.toString(constants) + "[" + stripQuotes(objectName, constants) + "]" + "=" + value.toString(constants);
        }
        return object.toString(constants) + "." + stripQuotes(objectName, constants) + "=" + value.toString(constants);
    }

    @Override
    public GraphTargetItem getObject() {
        return new GetMemberTreeItem(src, object, objectName);
    }

    @Override
    public List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> getNeededSources() {
        List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> ret = super.getNeededSources();
        ret.addAll(object.getNeededSources());
        ret.addAll(objectName.getNeededSources());
        ret.addAll(value.getNeededSources());
        return ret;
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }
}
