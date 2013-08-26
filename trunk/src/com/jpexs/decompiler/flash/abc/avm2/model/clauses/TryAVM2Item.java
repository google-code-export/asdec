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
package com.jpexs.decompiler.flash.abc.avm2.model.clauses;

import com.jpexs.decompiler.flash.abc.avm2.ConstantPool;
import com.jpexs.decompiler.flash.abc.avm2.model.AVM2Item;
import com.jpexs.decompiler.flash.abc.types.ABCException;
import com.jpexs.decompiler.graph.Block;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.model.ContinueItem;
import com.jpexs.helpers.Helper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TryAVM2Item extends AVM2Item implements Block {

    public List<GraphTargetItem> tryCommands;
    public List<ABCException> catchExceptions;
    public List<List<GraphTargetItem>> catchCommands;
    public List<GraphTargetItem> finallyCommands;

    @Override
    public List<List<GraphTargetItem>> getSubs() {
        List<List<GraphTargetItem>> ret = new ArrayList<>();
        ret.add(tryCommands);
        ret.addAll(catchCommands);
        ret.add(finallyCommands);
        return ret;
    }

    public TryAVM2Item(List<GraphTargetItem> tryCommands, List<ABCException> catchExceptions, List<List<GraphTargetItem>> catchCommands, List<GraphTargetItem> finallyCommands) {
        super(null, NOPRECEDENCE);
        this.tryCommands = tryCommands;
        this.catchExceptions = catchExceptions;
        this.catchCommands = catchCommands;
        this.finallyCommands = finallyCommands;
    }

    @Override
    public String toString(boolean highlight, ConstantPool constants, HashMap<Integer, String> localRegNames, List<String> fullyQualifiedNames) {
        String ret = "";
        ret += hilight("try", highlight) + "\r\n" + hilight("{", highlight) + "\r\n";
        for (GraphTargetItem ti : tryCommands) {
            if (!ti.isEmpty()) {
                ret += ti.toStringSemicoloned(highlight, Helper.toList(constants, localRegNames, fullyQualifiedNames)) + "\r\n";
            }
        }
        ret += hilight("}", highlight);
        for (int e = 0; e < catchExceptions.size(); e++) {
            ret += "\r\n" + hilight("catch(" + catchExceptions.get(e).getVarName(constants, fullyQualifiedNames) + ":" + catchExceptions.get(e).getTypeName(constants, fullyQualifiedNames) + ")", highlight) + "\r\n" + hilight("{", highlight) +"\r\n";
            List<GraphTargetItem> commands = catchCommands.get(e);
            for (GraphTargetItem ti : commands) {
                if (!ti.isEmpty()) {
                    ret += ti.toStringSemicoloned(highlight, Helper.toList(constants, localRegNames, fullyQualifiedNames)) + "\r\n";
                }
            }
            ret += hilight("}", highlight);
        }
        if (finallyCommands.size() > 0) {
            ret += "\r\n" + hilight("finally", highlight) +"\r\n" + hilight("{", highlight) + "\r\n";
            for (GraphTargetItem ti : finallyCommands) {
                if (!ti.isEmpty()) {
                    ret += ti.toStringSemicoloned(highlight, Helper.toList(constants, localRegNames, fullyQualifiedNames)) + "\r\n";
                }
            }
            ret += hilight("}", highlight);
        }
        return ret;
    }

    @Override
    public List<ContinueItem> getContinues() {
        List<ContinueItem> ret = new ArrayList<>();
        for (GraphTargetItem ti : tryCommands) {
            if (ti instanceof ContinueItem) {
                ret.add((ContinueItem) ti);
            }
            if (ti instanceof Block) {
                ret.addAll(((Block) ti).getContinues());
            }
        }
        if (finallyCommands != null) {
            for (GraphTargetItem ti : finallyCommands) {
                if (ti instanceof ContinueItem) {
                    ret.add((ContinueItem) ti);
                }
                if (ti instanceof Block) {
                    ret.addAll(((Block) ti).getContinues());
                }
            }
        }
        for (List<GraphTargetItem> commands : catchCommands) {
            for (GraphTargetItem ti : commands) {
                if (ti instanceof ContinueItem) {
                    ret.add((ContinueItem) ti);
                }
                if (ti instanceof Block) {
                    ret.addAll(((Block) ti).getContinues());
                }
            }
        }
        return ret;
    }

    @Override
    public boolean needsSemicolon() {
        return false;
    }
}
