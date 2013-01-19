/*
 *  Copyright (C) 2011-2013 JPEXS
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


package com.jpexs.asdec.abc.usages;

import com.jpexs.asdec.abc.ABC;
import com.jpexs.asdec.tags.DoABCTag;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class TypeNameMultinameUsage extends MultinameUsage {

   public int typename_index;

   public TypeNameMultinameUsage(int typename_index) {
      this.typename_index = typename_index;
   }

   @Override
   public String toString(List<DoABCTag> abcTags, ABC abc) {
      return "TypeName " + abc.constants.constant_multiname[typename_index].toString(abc.constants, new ArrayList<String>());
   }
}
