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
package com.jpexs.decompiler.flash.abc.avm2.model.operations;

import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.ecma.EcmaScript;
import com.jpexs.decompiler.flash.ecma.EcmaType;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.model.UnaryOpItem;

public class TypeOfAVM2Item extends UnaryOpItem {

    public TypeOfAVM2Item(AVM2Instruction instruction, GraphTargetItem value) {
        super(instruction, PRECEDENCE_UNARY, value, "typeof ");
    }

    @Override
    public boolean isCompileTime() {
        return value.isCompileTime();
    }

    @Override
    public Object getResult() {
        Object res = value.getResult();
        EcmaType type = EcmaScript.type(res);
        switch (type) {
            case UNDEFINED:
                return "undefined";
            case NULL:
                return "object";
            case BOOLEAN:
                return "Boolean";
            case NUMBER:
                return "number";
            case STRING:
                return "string";
            case OBJECT:
                return "object";

        }
        //TODO: function,xml
        return "object";
    }
}
