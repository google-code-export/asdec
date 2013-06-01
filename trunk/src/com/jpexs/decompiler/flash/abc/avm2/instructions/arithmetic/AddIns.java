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
package com.jpexs.decompiler.flash.abc.avm2.instructions.arithmetic;

import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.avm2.ConstantPool;
import com.jpexs.decompiler.flash.abc.avm2.LocalDataArea;
import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.abc.avm2.instructions.InstructionDefinition;
import com.jpexs.decompiler.flash.abc.avm2.treemodel.operations.AddTreeItem;
import com.jpexs.decompiler.flash.abc.types.MethodInfo;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class AddIns extends InstructionDefinition {

    public AddIns() {
        super(0xa0, "add", new int[]{});
    }

    @Override
    public void execute(LocalDataArea lda, ConstantPool constants, List arguments) {
        Object o1 = lda.operandStack.pop();
        Object o2 = lda.operandStack.pop();
        if ((o1 instanceof Long) && ((o2 instanceof Long))) {
            Long ret = new Long(((Long) o1).longValue() + ((Long) o2).longValue());
            lda.operandStack.push(ret);
        } else if ((o1 instanceof Double) && ((o2 instanceof Double))) {
            Double ret = new Double(((Double) o1).doubleValue() + ((Double) o2).doubleValue());
            lda.operandStack.push(ret);
        } else if ((o1 instanceof Long) && ((o2 instanceof Double))) {
            Double ret = new Double(((Long) o1).longValue() + ((Double) o2).doubleValue());
            lda.operandStack.push(ret);
        } else if ((o1 instanceof Double) && ((o2 instanceof Long))) {
            Double ret = new Double(((Double) o1).doubleValue() + ((Long) o2).longValue());
            lda.operandStack.push(ret);
        } else {
            String s = o1.toString() + o2.toString();
            lda.operandStack.push(s);
        }
    }

    @Override
    public void translate(boolean isStatic, int scriptIndex, int classIndex, java.util.HashMap<Integer, GraphTargetItem> localRegs, Stack<GraphTargetItem> stack, java.util.Stack<GraphTargetItem> scopeStack, ConstantPool constants, AVM2Instruction ins, MethodInfo[] method_info, List<GraphTargetItem> output, com.jpexs.decompiler.flash.abc.types.MethodBody body, com.jpexs.decompiler.flash.abc.ABC abc, HashMap<Integer, String> localRegNames, List<String> fullyQualifiedNames) {
        GraphTargetItem v2 = (GraphTargetItem) stack.pop();
        GraphTargetItem v1 = (GraphTargetItem) stack.pop();
        stack.push(new AddTreeItem(ins, v1, v2));
    }

    @Override
    public int getStackDelta(AVM2Instruction ins, ABC abc) {
        return -2 + 1;
    }
}
