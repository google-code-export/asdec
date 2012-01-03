/*
 *  Copyright (C) 2010-2011 JPEXS
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

package com.jpexs.asdec.abc.avm2.treemodel.clauses;

import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.asdec.abc.avm2.treemodel.ContinueTreeItem;
import com.jpexs.asdec.abc.avm2.treemodel.TreeItem;

import java.util.ArrayList;
import java.util.List;


public class ForTreeItem extends LoopTreeItem implements Block {

    public List<TreeItem> firstCommands;
    public TreeItem expression;
    public List<TreeItem> finalCommands;
    public List<TreeItem> commands;

    public ForTreeItem(AVM2Instruction instruction, int loopBreak, int loopContinue, List<TreeItem> firstCommands, TreeItem expression, List<TreeItem> finalCommands, List<TreeItem> commands) {
        super(instruction, loopBreak, loopContinue);
        this.firstCommands = firstCommands;
        this.expression = expression;
        this.finalCommands = finalCommands;
        this.commands = commands;
    }

    private String stripSemicolon(String s) {
        if (s.endsWith(";")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    @Override
    public String toString(ConstantPool constants) {
        String ret = "";
        ret += "loop" + loopBreak + ":\r\n";
        ret += hilight("for(");
        for (int i = 0; i < firstCommands.size(); i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += stripSemicolon(firstCommands.get(i).toString(constants));
        }
        ret += ";";
        ret += expression.toString(constants);
        ret += ";";
        for (int i = 0; i < finalCommands.size(); i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += stripSemicolon(finalCommands.get(i).toString(constants));
        }
        ret += hilight(")") + "\r\n{\r\n";
        for (TreeItem ti : commands) {
            ret += ti.toString(constants) + "\r\n";
        }
        ret += hilight("}") + "\r\n";
        ret += ":loop" + loopBreak;
        return ret;
    }

    public List<ContinueTreeItem> getContinues() {
        List<ContinueTreeItem> ret = new ArrayList<ContinueTreeItem>();
        for (TreeItem ti : commands) {
            if (ti instanceof ContinueTreeItem) {
                ret.add((ContinueTreeItem) ti);
            }
            if (ti instanceof Block) {
                ret.addAll(((Block) ti).getContinues());
            }
        }
        return ret;
    }
}
