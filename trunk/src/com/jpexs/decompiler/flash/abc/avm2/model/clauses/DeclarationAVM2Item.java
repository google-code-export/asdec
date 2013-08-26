/*
 *  Copyright (C) 2012-2013 JPEXS
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
import com.jpexs.decompiler.flash.abc.avm2.model.CoerceAVM2Item;
import com.jpexs.decompiler.flash.abc.avm2.model.ConvertAVM2Item;
import com.jpexs.decompiler.flash.abc.avm2.model.SetLocalAVM2Item;
import com.jpexs.decompiler.flash.abc.avm2.model.SetSlotAVM2Item;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.helpers.Helper;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class DeclarationAVM2Item extends AVM2Item {

    public GraphTargetItem assignment;
    public String type;

    public DeclarationAVM2Item(GraphTargetItem assignment, String type) {
        super(assignment.src, assignment.precedence);
        this.type = type;
        this.assignment = assignment;
    }

    public DeclarationAVM2Item(GraphTargetItem assignment) {
        this(assignment, null);
    }

    @Override
    public String toString(boolean highlight, ConstantPool constants, HashMap<Integer, String> localRegNames, List<String> fullyQualifiedNames) {
        if (assignment instanceof SetLocalAVM2Item) {
            SetLocalAVM2Item lti = (SetLocalAVM2Item) assignment;
            String type = "*";
            if (lti.value instanceof CoerceAVM2Item) {
                type = ((CoerceAVM2Item) lti.value).type;
            }
            if (lti.value instanceof ConvertAVM2Item) {
                type = ((ConvertAVM2Item) lti.value).type;
            }
            return hilight("var ", highlight) + hilight(localRegName(localRegNames, lti.regIndex) + ":" + type + " = ", highlight) + lti.value.toString(highlight, constants, localRegNames, fullyQualifiedNames);
        }
        if (assignment instanceof SetSlotAVM2Item) {
            SetSlotAVM2Item ssti = (SetSlotAVM2Item) assignment;
            return hilight("var ", highlight) + ssti.getName(highlight, constants, localRegNames, fullyQualifiedNames) + hilight(":", highlight) + hilight(type, highlight) + hilight(" = ", highlight) + ssti.value.toString(highlight, constants, localRegNames, fullyQualifiedNames);
        }
        return hilight("var ", highlight) + assignment.toString(highlight, Helper.toList(constants, localRegNames, fullyQualifiedNames));
    }
}
