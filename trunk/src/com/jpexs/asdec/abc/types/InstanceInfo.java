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
package com.jpexs.asdec.abc.types;

import com.jpexs.asdec.abc.ABC;
import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.types.traits.Traits;
import com.jpexs.asdec.helpers.Helper;

public class InstanceInfo {

   public int name_index;
   public int super_index;
   public int flags; // 1 = sealed, 0 = dynamic, 2 = final, 4 = interface, 8 = ProtectedNs
   public int protectedNS; //if flags & 8
   public int interfaces[];
   public int iinit_index; // MethodInfo - constructor
   public Traits instance_traits;
   public static final int CLASS_SEALED = 1; //not dynamic
   public static final int CLASS_FINAL = 2;
   public static final int CLASS_INTERFACE = 4;
   public static final int CLASS_PROTECTEDNS = 8;

   @Override
   public String toString() {
      return "name_index=" + name_index + " super_index=" + super_index + " flags=" + flags + " protectedNS=" + protectedNS + " interfaces=" + Helper.intArrToString(interfaces) + " method_index=" + iinit_index + "\r\n" + instance_traits.toString();
   }

   public String toString(ABC abc) {
      String supIndexStr = "[nothing]";
      if (super_index > 0) {
         supIndexStr = abc.constants.constant_multiname[super_index].toString(abc.constants);
      }
      return "name_index=" + abc.constants.constant_multiname[name_index].toString(abc.constants) + " super_index=" + supIndexStr + " flags=" + flags + " protectedNS=" + protectedNS + " interfaces=" + Helper.intArrToString(interfaces) + " method_index=" + iinit_index + "\r\n" + instance_traits.toString(abc);
   }

   public String getClassHeaderStr(ABC abc) {
      String supIndexStr = "";
      if (super_index > 0) {
         supIndexStr = " extends " + abc.constants.constant_multiname[super_index].getName(abc.constants);////+" flags="+flags+" protectedNS="+protectedNS+" interfaces="+Helper.intArrToString(interfaces)+" method_index="+iinit_index
      }
      String implStr = "";
      if(interfaces.length>0){
         if(isInterface()){
            implStr = " extends ";
         }else{
            implStr = " implements ";
         }
         for(int i=0;i<interfaces.length;i++){
            if(i>0){
               implStr += ", ";
            }
            implStr += abc.constants.constant_multiname[interfaces[i]].getName(abc.constants);
         }
      }
      String modifiers;
      Namespace ns = abc.constants.constant_multiname[name_index].getNamespace(abc.constants);
      modifiers = ns.getPrefix(abc);
      if (!modifiers.equals("")) {
         modifiers += " ";
      }

      if (isFinal()) {
         modifiers = "final ";
      }
      if (isDynamic()) {
         modifiers = modifiers + "dynamic ";
      }
      String objType = "class ";
      if (isInterface()) {
         objType = "interface ";
      }
      return modifiers + objType + abc.constants.constant_multiname[name_index].getName(abc.constants) + supIndexStr + implStr;
   }

   public String getInstanceVarsStr(ABC abc) {
      return instance_traits.convert("\t", abc);
   }

   public Multiname getName(ConstantPool constants) {
      return constants.constant_multiname[name_index];
   }
   
   public boolean isInterface(){
      return ((flags & CLASS_INTERFACE) == CLASS_INTERFACE);
   }
   
   public boolean isDynamic(){
      return (flags & CLASS_SEALED) == 0;
   }
   
   public boolean isFinal()
   {
      return (flags & CLASS_FINAL) == CLASS_FINAL;
   }
}
