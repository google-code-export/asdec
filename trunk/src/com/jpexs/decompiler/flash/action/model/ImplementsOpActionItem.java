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

import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphSourceItemPos;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.model.LocalData;
import java.util.List;

public class ImplementsOpActionItem extends ActionItem {

    public GraphTargetItem subclass;
    public List<GraphTargetItem> superclasses;

    public ImplementsOpActionItem(GraphSourceItem instruction, GraphTargetItem subclass, List<GraphTargetItem> superclasses) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.subclass = subclass;
        this.superclasses = superclasses;
    }

    @Override
    protected GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) {
        subclass.toString(writer, localData);
        writer.append(" implements ");
        for (int i = 0; i < superclasses.size(); i++) {
            if (i > 0) {
                writer.append(",");
            }
            superclasses.get(i).toString(writer, localData);
        }
        return writer;
    }

    @Override
    public List<GraphSourceItemPos> getNeededSources() {
        List<GraphSourceItemPos> ret = super.getNeededSources();
        ret.addAll(subclass.getNeededSources());
        for (GraphTargetItem ti : superclasses) {
            ret.addAll(ti.getNeededSources());
        }
        return ret;
    }

    @Override
    public boolean hasReturnValue() {
        return true;
    }
}
