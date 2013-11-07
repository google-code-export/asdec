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
package com.jpexs.decompiler.flash.abc.avm2.model;

import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.model.LocalData;
import java.util.List;

public class NewObjectAVM2Item extends AVM2Item {

    public List<NameValuePair> pairs;

    public NewObjectAVM2Item(AVM2Instruction instruction, List<NameValuePair> pairs) {
        super(instruction, PRECEDENCE_PRIMARY);
        this.pairs = pairs;
    }

    @Override
    protected GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) throws InterruptedException {
        boolean singleLine = pairs.size() < 2;
        if (!singleLine) {
            writer.newLine();
            writer.indent();
        }
        writer.append("{");
        if (!singleLine) {
            writer.newLine();
            writer.indent();
        }
        for (int n = 0; n < pairs.size(); n++) {
            if (n > 0) {
                writer.append(",").newLine();
            }
            pairs.get(n).toString(writer, localData);
        }
        if (!singleLine) {
            writer.newLine();
            writer.unindent();
        }
        writer.append("}");
        if (!singleLine) {
            writer.unindent();
        }
        return writer;
    }
}
