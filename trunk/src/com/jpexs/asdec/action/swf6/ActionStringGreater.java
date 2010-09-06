package com.jpexs.asdec.action.swf6;

import com.jpexs.asdec.action.Action;
import com.jpexs.asdec.action.treemodel.ConstantPool;
import com.jpexs.asdec.action.treemodel.TreeItem;
import com.jpexs.asdec.action.treemodel.operations.GtTreeItem;

import java.util.List;
import java.util.Stack;

public class ActionStringGreater extends Action {

    public ActionStringGreater() {
        super(0x68, 0);
    }

    @Override
    public String toString() {
        return "StringGreater";
    }

    @Override
    public void translate(Stack<TreeItem> stack, ConstantPool constants, List<TreeItem> output, java.util.HashMap<Integer,String> regNames) {
        TreeItem a = stack.pop();
        TreeItem b = stack.pop();
        stack.push(new GtTreeItem(this, b, a));
    }
}