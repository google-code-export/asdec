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
package com.jpexs.decompiler.flash.action.model;

import com.jpexs.decompiler.flash.action.swf5.ActionCallMethod;
import com.jpexs.decompiler.flash.ecma.Undefined;
import com.jpexs.decompiler.flash.helpers.HilightedTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphSourceItemPos;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;
import java.util.ArrayList;
import java.util.List;

public class CallMethodActionItem extends ActionItem {

    public GraphTargetItem methodName;
    public GraphTargetItem scriptObject;
    public List<GraphTargetItem> arguments;

    @Override
    public List<GraphTargetItem> getAllSubItems() {
        List<GraphTargetItem> ret = new ArrayList<>();
        ret.addAll(arguments);
        ret.add(scriptObject);
        return ret;
    }

    public CallMethodActionItem(GraphSourceItem instruction, GraphTargetItem scriptObject, GraphTargetItem methodName, List<GraphTargetItem> arguments) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.methodName = methodName;
        this.arguments = arguments;
        this.scriptObject = scriptObject;
    }

    @Override
    public HilightedTextWriter toString(HilightedTextWriter writer, ConstantPool constants) {
        boolean blankMethod = false;
        if (methodName instanceof DirectValueActionItem) {
            if (((DirectValueActionItem) methodName).value instanceof Undefined) {
                blankMethod = true;
            }
            if (((DirectValueActionItem) methodName).value instanceof String) {
                if (((DirectValueActionItem) methodName).value.equals("")) {
                    blankMethod = true;
                }
            }
        }
        if (!blankMethod) {
            if (scriptObject.precedence > this.precedence) {
                hilight("(", writer);
                scriptObject.toString(writer, constants);
                hilight(")", writer);
            } else {
                scriptObject.toString(writer, constants);
            }
            hilight(".", writer);
            stripQuotes(methodName, constants, writer);
        } else {
            scriptObject.toString(writer, constants);
        }
        hilight("(", writer);

        for (int t = 0; t < arguments.size(); t++) {
            if (t > 0) {
                hilight(",", writer);
            }
            arguments.get(t).toStringNL(writer, LocalData.create(constants));
        }
        
        return hilight(")", writer);
    }

    @Override
    public List<GraphSourceItemPos> getNeededSources() {
        List<GraphSourceItemPos> ret = super.getNeededSources();
        ret.addAll(methodName.getNeededSources());
        ret.addAll(scriptObject.getNeededSources());
        for (GraphTargetItem ti : arguments) {
            ret.addAll(ti.getNeededSources());
        }
        return ret;
    }

    @Override
    public List<GraphSourceItem> toSource(List<Object> localData, SourceGenerator generator) {
        return toSourceMerge(localData, generator, toSourceCall(localData, generator, arguments), scriptObject, methodName, new ActionCallMethod());
    }

    @Override
    public boolean hasReturnValue() {
        return true;
    }
}
