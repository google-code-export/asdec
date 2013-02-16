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
package com.jpexs.asdec.abc.types;

import com.jpexs.asdec.Main;
import com.jpexs.asdec.abc.ABC;
import com.jpexs.asdec.abc.avm2.AVM2Code;
import com.jpexs.asdec.abc.avm2.CodeStats;
import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.avm2.treemodel.TreeItem;
import com.jpexs.asdec.abc.types.traits.Traits;
import com.jpexs.asdec.helpers.Helper;
import com.jpexs.asdec.helpers.Highlighting;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class MethodBody implements Cloneable, Serializable {

   public int method_info;
   public int max_stack;
   public int max_regs;
   public int init_scope_depth;
   public int max_scope_depth;
   public byte codeBytes[];
   public AVM2Code code;
   public ABCException exceptions[] = new ABCException[0];
   public Traits traits = new Traits();

   @Override
   public String toString() {
      String s = "";
      s += "method_info=" + method_info + " max_stack=" + max_stack + " max_regs=" + max_regs + " scope_depth=" + init_scope_depth + " max_scope=" + max_scope_depth;
      s += "\r\nCode:\r\n" + code.toString();
      return s;
   }

   public int removeDeadCode(ConstantPool constants) {
      return code.removeDeadCode(constants, this);
   }

   public void restoreControlFlow(ConstantPool constants) {
      code.restoreControlFlow(constants, this);
   }

   public int removeTraps(ConstantPool constants) {
      return code.removeTraps(constants, this);
   }

   public HashMap<Integer, String> getLocalRegNames(ABC abc) {
      HashMap<Integer, String> ret = new HashMap<Integer, String>();
      for (int i = 1; i <= abc.method_info[this.method_info].param_types.length; i++) {
         String paramName = "param" + i;
         if (abc.method_info[this.method_info].flagHas_paramnames() && Main.PARAM_NAMES_ENABLE) {
            paramName = abc.constants.constant_string[abc.method_info[this.method_info].paramNames[i - 1]];
         }
         ret.put(i, paramName);
      }
      int pos = abc.method_info[this.method_info].param_types.length + 1;
      if (abc.method_info[this.method_info].flagNeed_arguments()) {
         ret.put(pos, "arguments");
         pos++;
      }
      if (abc.method_info[this.method_info].flagNeed_rest()) {
         ret.put(pos, "rest");
         pos++;
      }

      HashMap<Integer, String> debugRegNames = code.getLocalRegNamesFromDebug(abc);
      for (int k : debugRegNames.keySet()) {
         ret.put(k, debugRegNames.get(k));
      }
      return ret;
   }

   public String toString(String path, boolean pcode, boolean isStatic, int classIndex, ABC abc, ConstantPool constants, MethodInfo method_info[], Stack<TreeItem> scopeStack, boolean isStaticInitializer, boolean hilight, List<String> fullyQualifiedNames, Traits initTraits) {
      String s = "";
      if (!Main.DO_DECOMPILE) {
         s = "//NOT DECOMPILED";
         if (hilight) {
            s = Highlighting.hilighMethod(s, this.method_info);
         }
         return s;
      }
      if (pcode) {
         s += code.toASMSource(constants, this);
      } else {
         AVM2Code deobfuscated = null;
         MethodBody b = (MethodBody) Helper.deepCopy(this);
         deobfuscated = b.code;
         deobfuscated.markMappedOffsets();
         deobfuscated.removeTraps(constants, b);
         //deobfuscated.restoreControlFlow(constants, b);
         try {
            s += deobfuscated.toSource(path, isStatic, classIndex, abc, constants, method_info, b, hilight, getLocalRegNames(abc), scopeStack, isStaticInitializer, fullyQualifiedNames, initTraits);
            s = s.trim();
            if (hilight) {
               s = Highlighting.hilighMethod(s, this.method_info);
            }
         } catch (Exception ex) {
            ex.printStackTrace();
            s = "//error:" + ex.toString();
         }
      }
      return s;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      MethodBody ret = new MethodBody();
      ret.code = code;
      ret.codeBytes = codeBytes;
      ret.exceptions = exceptions;
      ret.max_regs = max_regs;
      ret.max_scope_depth = max_scope_depth;
      ret.max_stack = max_stack;
      ret.method_info = method_info;
      ret.init_scope_depth = init_scope_depth;
      ret.traits = traits; //maybe deep clone
      return ret;
   }

   public boolean autoFillStats(ABC abc) {
      CodeStats stats = code.getStats(abc, this);
      if (stats == null) {
         return false;
      }
      max_stack = stats.maxstack;
      max_scope_depth = init_scope_depth + stats.maxscope;
      max_regs = stats.maxlocal;
      abc.method_info[method_info].setFlagSetsdxns(stats.has_set_dxns);
      abc.method_info[method_info].setFlagNeed_activation(stats.has_activation);
      return true;
   }
}
