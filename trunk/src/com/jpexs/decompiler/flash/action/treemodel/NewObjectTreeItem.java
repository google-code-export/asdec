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
package com.jpexs.decompiler.flash.action.treemodel;

import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.List;

public class NewObjectTreeItem extends TreeItem {

   public GraphTargetItem objectName;
   public List<GraphTargetItem> arguments;

   public NewObjectTreeItem(GraphSourceItem instruction, GraphTargetItem objectName, List<GraphTargetItem> arguments) {
      super(instruction, PRECEDENCE_PRIMARY);
      this.objectName = objectName;
      this.arguments = arguments;
   }

   @Override
   public String toString(ConstantPool constants) {
      String paramStr = "";
      for (int t = 0; t < arguments.size(); t++) {
         if (t > 0) {
            paramStr += ",";
         }
         paramStr += arguments.get(t).toString(constants);
      }
      return hilight("new ") + stripQuotes(objectName) + hilight("(") + paramStr + hilight(")");
   }

   @Override
   public List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> getNeededSources() {
      List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> ret = super.getNeededSources();
      ret.addAll(objectName.getNeededSources());
      for (GraphTargetItem ti : arguments) {
         ret.addAll(ti.getNeededSources());
      }
      return ret;
   }
}
