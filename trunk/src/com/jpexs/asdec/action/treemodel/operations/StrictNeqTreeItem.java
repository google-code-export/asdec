/*
 * Copyright (c) 2010. JPEXS
 */

package com.jpexs.asdec.action.treemodel.operations;

import com.jpexs.asdec.action.Action;
import com.jpexs.asdec.action.treemodel.TreeItem;


public class StrictNeqTreeItem extends BinaryOpTreeItem {

    public StrictNeqTreeItem(Action instruction, TreeItem leftSide, TreeItem rightSide) {
        super(instruction, PRECEDENCE_EQUALITY, leftSide, rightSide, "!==");
    }

}
