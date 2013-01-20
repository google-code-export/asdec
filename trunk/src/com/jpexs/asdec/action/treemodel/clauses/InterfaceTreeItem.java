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
package com.jpexs.asdec.action.treemodel.clauses;

import com.jpexs.asdec.action.Action;
import com.jpexs.asdec.action.treemodel.ConstantPool;
import com.jpexs.asdec.action.treemodel.TreeItem;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class InterfaceTreeItem extends TreeItem {

   public TreeItem name;
   public List<TreeItem> superInterfaces;

   public InterfaceTreeItem(TreeItem name, List<TreeItem> superInterfaces) {
      super(null, NOPRECEDENCE);
      this.name = name;
      this.superInterfaces = superInterfaces;
   }

   @Override
   public String toString(ConstantPool constants) {
      String ret = "";
      ret += "interface " + name.toStringNoQuotes(constants);
      boolean first = true;
      if (!superInterfaces.isEmpty()) {
         ret += " extends ";
      }
      for (TreeItem ti : superInterfaces) {
         if (!first) {
            ret += ", ";
         }
         first = false;
         ret += Action.getWithoutGlobal(ti).toStringNoQuotes(constants);
      }
      ret += "\r\n{\r\n}\r\n";
      return ret;
   }
}
