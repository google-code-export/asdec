/*
 *  Copyright (C) 2010 JPEXS
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.jpexs.asdec.abc.avm2.instructions.other;

import com.jpexs.asdec.abc.avm2.AVM2Code;
import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.asdec.abc.avm2.instructions.InstructionDefinition;
import com.jpexs.asdec.abc.avm2.instructions.SetTypeIns;
import com.jpexs.asdec.abc.avm2.treemodel.FullMultinameTreeItem;
import com.jpexs.asdec.abc.avm2.treemodel.SetPropertyTreeItem;
import com.jpexs.asdec.abc.avm2.treemodel.TreeItem;
import com.jpexs.asdec.abc.types.MethodInfo;

import java.util.List;
import java.util.Stack;


public class SetPropertyIns extends InstructionDefinition implements SetTypeIns {

    public SetPropertyIns() {
        super(0x61, "setproperty", new int[]{AVM2Code.DAT_MULTINAME_INDEX});
    }

    @Override
    public void translate(boolean isStatic, int classIndex, java.util.HashMap<Integer, TreeItem> localRegs, Stack<TreeItem> stack, java.util.Stack<TreeItem> scopeStack, ConstantPool constants, AVM2Instruction ins, MethodInfo[] method_info, List<TreeItem> output, com.jpexs.asdec.abc.types.MethodBody body, com.jpexs.asdec.abc.ABC abc) {
        int multinameIndex = ins.operands[0];
        TreeItem value = (TreeItem) stack.pop();
        FullMultinameTreeItem multiname = resolveMultiname(stack, constants, multinameIndex, ins);
        TreeItem obj = (TreeItem) stack.pop();
        output.add(new SetPropertyTreeItem(ins, obj, multiname, value));
    }

    public String getObject(Stack<TreeItem> stack, ConstantPool constants, AVM2Instruction ins, MethodInfo[] method_info, List<TreeItem> output, com.jpexs.asdec.abc.types.MethodBody body) {
        int multinameIndex = ins.operands[0];
        String multiname = resolveMultinameNoPop(0, stack, constants, multinameIndex, ins);
        TreeItem obj = stack.get(1 + resolvedCount(constants, multinameIndex)); //pod vrcholem
        if ((!obj.toString().equals(""))) multiname = "." + multiname;
        return obj + multiname;
    }
}
