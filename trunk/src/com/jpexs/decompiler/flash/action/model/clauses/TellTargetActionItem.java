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
package com.jpexs.decompiler.flash.action.model.clauses;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.model.ActionItem;
import com.jpexs.decompiler.flash.action.swf3.ActionSetTarget;
import com.jpexs.decompiler.flash.action.swf4.ActionSetTarget2;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphSourceItemPos;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;
import java.util.ArrayList;
import java.util.List;

public class TellTargetActionItem extends ActionItem {

    public List<GraphTargetItem> commands;
    public GraphTargetItem target;

    public TellTargetActionItem(GraphSourceItem instruction, GraphTargetItem target, List<GraphTargetItem> commands) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.target = target;
        this.commands = commands;
    }

    @Override
    protected GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) throws InterruptedException {
        writer.append("tellTarget(");
        target.toString(writer, localData);
        writer.append(")").newLine();
        writer.append("{").newLine();
        writer.indent();
        for (GraphTargetItem ti : commands) {
            ti.toString(writer, localData).newLine();
        }
        writer.unindent();
        return writer.append("}");
    }

    @Override
    public List<GraphSourceItemPos> getNeededSources() {
        List<GraphSourceItemPos> ret = super.getNeededSources();
        ret.addAll(target.getNeededSources());
        return ret;
    }

    @Override
    public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData, SourceGenerator generator) {
        List<GraphSourceItem> ret = new ArrayList<>();
        ret.addAll(target.toSource(localData, generator));
        ret.add(new ActionSetTarget2());
        ret.addAll(generator.generate(localData, commands));
        ret.add(new ActionSetTarget(""));
        return ret;
    }

    @Override
    public boolean hasReturnValue() {
        return false;
    }
}
