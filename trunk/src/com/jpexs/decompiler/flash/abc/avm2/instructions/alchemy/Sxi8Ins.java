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
package com.jpexs.decompiler.flash.abc.avm2.instructions.alchemy;

import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.avm2.ConstantPool;
import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.abc.avm2.instructions.InstructionDefinition;
import com.jpexs.decompiler.flash.abc.avm2.treemodel.AlchemySignExtendTreeItem;
import com.jpexs.decompiler.flash.abc.types.MethodBody;
import com.jpexs.decompiler.flash.abc.types.MethodInfo;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author JPEXS
 */
public class Sxi8Ins extends InstructionDefinition {

    public Sxi8Ins() {
        super(0x51, "sxi_8", new int[]{});
    }

    @Override
    public int getStackDelta(AVM2Instruction ins, ABC abc) {
        return 1 - 1;
    }

    @Override
    public int getScopeStackDelta(AVM2Instruction ins, ABC abc) {
        return 0;
    }

    @Override
    public void translate(boolean isStatic, int scriptIndex, int classIndex, HashMap<Integer, GraphTargetItem> localRegs, Stack<GraphTargetItem> stack, Stack<GraphTargetItem> scopeStack, ConstantPool constants, AVM2Instruction ins, MethodInfo[] method_info, List<GraphTargetItem> output, MethodBody body, ABC abc, HashMap<Integer, String> localRegNames, List<String> fullyQualifiedNames, String path) {
        GraphTargetItem value = stack.pop();
        stack.push(new AlchemySignExtendTreeItem(ins, value, instructionName));
    }
}
