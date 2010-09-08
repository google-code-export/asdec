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

package com.jpexs.asdec.abc.types;

import com.jpexs.asdec.abc.ABC;
import com.jpexs.asdec.abc.avm2.AVM2Code;
import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.avm2.instructions.InstructionDefinition;
import com.jpexs.asdec.abc.types.traits.Traits;


public class MethodBody implements Cloneable {

    public int method_info;
    public int max_stack;
    public int max_regs;
    public int scope_depth;
    public int max_scope;
    public byte codeBytes[];
    public AVM2Code code;
    public ABCException exceptions[] = new ABCException[0];
    public Traits traits = new Traits();

    @Override
    public String toString() {
        String s = "";
        s += "method_info=" + method_info + " max_stack=" + max_stack + " max_regs=" + max_regs + " scope_depth=" + scope_depth + " max_scope=" + max_scope;
        s += "\r\nCode:\r\n" + code.toString();
        return s;
    }

    private String replaceParams(String code, MethodInfo method_info[]) {
        for (int i = 1; i <= method_info[this.method_info].param_types.length; i++) {
            code = code.replace(InstructionDefinition.localRegName(i), "param" + i);
        }
        return code;
    }

    public String toString(boolean isStatic, int classIndex, ABC abc, ConstantPool constants, MethodInfo method_info[]) {
        return toString(isStatic, classIndex, abc, constants, method_info, false);
    }

    public String toString(boolean isStatic, int classIndex, ABC abc, ConstantPool constants, MethodInfo method_info[], boolean hilight) {
        String s = "";

        //s+="method_info="+method_info+" max_stack="+max_stack+" max_regs="+max_regs+" scope_depth="+scope_depth+" max_scope="+max_scope;
        //s+="\r\nCode:\r\n"+

        try {
            s += code.toSource(isStatic, classIndex, abc, constants, method_info, this, hilight);
            s = replaceParams(s, method_info);
        } catch (Exception ex) {
            s = "//error:" + ex.toString();
        }
        //s+="----------- ORIGINAL ------------\r\n";
        //s+=code.toString(constants);
        /*s+="Exceptions:";
        for(int i=0;i<exceptions.length;i++){
        s+="\r\n"+exceptions[i].toString(constants);
        }*/
        return s;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MethodBody ret = new MethodBody();
        ret.code = code;
        ret.codeBytes = codeBytes;
        ret.exceptions = exceptions;
        ret.max_regs = max_regs;
        ret.max_scope = max_scope;
        ret.max_stack = max_stack;
        ret.method_info = method_info;
        ret.scope_depth = scope_depth;
        ret.traits = traits; //maybe deep clone
        return ret;
    }


}
