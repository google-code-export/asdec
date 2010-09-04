/*
 * Copyright (c) 2010. JPEXS
 */

package com.jpexs.asdec.abc.avm2.treemodel.operations;

import com.jpexs.asdec.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.asdec.abc.avm2.treemodel.TreeItem;


public class RShiftTreeItem extends BinaryOpTreeItem {

    public RShiftTreeItem(AVM2Instruction instruction, TreeItem leftSide, TreeItem rightSide) {
        super(instruction, PRECEDENCE_BITWISESHIFT, leftSide, rightSide, ">>");
    }

}
