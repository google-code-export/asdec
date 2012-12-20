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

package com.jpexs.asdec.abc.avm2.instructions.debug;

import com.jpexs.asdec.abc.avm2.AVM2Code; import java.util.HashMap;
import com.jpexs.asdec.abc.avm2.instructions.InstructionDefinition;


public class DebugIns extends InstructionDefinition {

    public DebugIns() {
        super(0xef, "debug", new int[]{AVM2Code.DAT_DEBUG_TYPE, AVM2Code.DAT_STRING_INDEX, AVM2Code.DAT_REGISTER_INDEX, AVM2Code.OPT_U30});
    }

}
