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
package com.jpexs.asdec.abc.types.traits;

import com.jpexs.asdec.abc.ABC;
import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.avm2.treemodel.TreeItem;
import com.jpexs.asdec.abc.types.MethodInfo;
import com.jpexs.asdec.abc.types.ValueKind;
import com.jpexs.asdec.helpers.Helper;
import com.jpexs.asdec.helpers.Highlighting;
import java.util.HashMap;

public class TraitSlotConst extends Trait {

   public int slot_id;
   public int type_index;
   public int value_index;
   public int value_kind;
   public TreeItem assignedValue;

   @Override
   public String toString(ABC abc) {
      String typeStr = "*";
      if (type_index > 0) {
         typeStr = abc.constants.constant_multiname[type_index].toString(abc.constants);
      }
      return "0x" + Helper.formatAddress(fileOffset) + " " + Helper.byteArrToString(bytes) + " SlotConst " + abc.constants.constant_multiname[name_index].toString(abc.constants) + " slot=" + slot_id + " type=" + typeStr + " value=" + (new ValueKind(value_index, value_kind)).toString(abc.constants) + " metadata=" + Helper.intArrToString(metadata);
   }

   public String getType(ConstantPool constants) {
      String typeStr = "*";
      if (type_index > 0) {
         typeStr = constants.constant_multiname[type_index].getName(constants);
      }
      return typeStr;
   }

   public String getNameValueStr(ConstantPool constants) {

      String typeStr = getType(constants);
      String valueStr = "";
      if (value_kind != 0) {
         valueStr = " = " + (new ValueKind(value_index, value_kind)).toString(constants);
      }

      if (assignedValue != null) {
         valueStr = " = " + Highlighting.stripHilights(assignedValue.toString(constants, new HashMap<Integer, String>()));
      }

      String slotconst = "var";
      if (kindType == TRAIT_CONST) {
         slotconst = "const";
      }
      return slotconst + " " + getName(constants) + ":" + typeStr + valueStr;
   }

   public String getName(ConstantPool constants) {
      return constants.constant_multiname[name_index].getName(constants);
   }

   @Override
   public String convert(ConstantPool constants, MethodInfo[] methodInfo, ABC abc, boolean isStatic) {
      String modifier = getModifiers(constants, isStatic) + " ";
      if (modifier.equals(" ")) {
         modifier = "";
      }
      return modifier + getNameValueStr(constants);
   }

   public boolean isConst() {
      return kindType == TRAIT_CONST;
   }

   public boolean isVar() {
      return kindType == TRAIT_SLOT;
   }
}
