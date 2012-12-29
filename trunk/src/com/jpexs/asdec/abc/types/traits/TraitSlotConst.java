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

   public String getNameValueStr(ABC abc) {

      String typeStr = getType(abc.constants);
      if (typeStr.equals("*")) {
         typeStr = "";
      } else {
         typeStr = ":" + typeStr;
      }
      String valueStr = "";
      ValueKind val = null;
      if (value_kind != 0) {
         val = new ValueKind(value_index, value_kind);
         valueStr = " = " + val.toString(abc.constants);
      }

      if (assignedValue != null) {
         valueStr = " = " + Highlighting.stripHilights(assignedValue.toString(abc.constants, new HashMap<Integer, String>()));
      }
      String slotconst = "var";
      if (kindType == TRAIT_CONST) {
         slotconst = "const";
      }
      if (val != null && val.isNamespace()) {
         slotconst = "namespace";
      }
      return slotconst + " " + getName(abc).getName(abc.constants) + typeStr + valueStr + ";";
   }

   @Override
   public String convert(ABC abc, boolean isStatic, boolean pcode, int classIndex, boolean highlight) {
      String modifier = getModifiers(abc, isStatic) + " ";
      if (modifier.equals(" ")) {
         modifier = "";
      }
      return ABC.IDENT_STRING + ABC.IDENT_STRING + modifier + getNameValueStr(abc);
   }

   public boolean isConst() {
      return kindType == TRAIT_CONST;
   }

   public boolean isVar() {
      return kindType == TRAIT_SLOT;
   }
}
