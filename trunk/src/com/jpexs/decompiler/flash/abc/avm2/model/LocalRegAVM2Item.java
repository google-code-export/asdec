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
package com.jpexs.decompiler.flash.abc.avm2.model;

import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.abc.avm2.model.clauses.FilterAVM2Item;
import com.jpexs.decompiler.flash.ecma.Undefined;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.model.LocalData;
import java.util.Set;

public class LocalRegAVM2Item extends AVM2Item {

    public int regIndex;
    public GraphTargetItem computedValue;
    private final Object computedResult;
    private boolean isCT = false;

    public LocalRegAVM2Item(AVM2Instruction instruction, int regIndex, GraphTargetItem computedValue) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.regIndex = regIndex;
        if (computedValue == null) {
            computedResult = null;
        } else {
            if (computedValue.isCompileTime()) {
                computedResult = computedValue.getResult();
                isCT = true;
            } else {
                computedResult = null;
            }
        }
        this.computedValue = computedValue;
    }

    @Override
    public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) throws InterruptedException {
        if (computedValue instanceof FilterAVM2Item) {
            return computedValue.toString(writer, localData);
        }
        return writer.append(localRegName(localData.localRegNames, regIndex));
    }

    @Override
    public GraphTargetItem getThroughRegister() {
        if (computedValue == null) {
            return this;
        }
        return computedValue.getThroughRegister();
    }

    @Override
    public Object getResult() {
        if (computedResult == null) {
            return new Undefined();
        }
        return computedResult;

    }

    @Override
    public boolean isCompileTime(Set<GraphTargetItem> dependencies) {
        return isCT;
    }
}
