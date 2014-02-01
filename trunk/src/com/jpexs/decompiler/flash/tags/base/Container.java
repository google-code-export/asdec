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
package com.jpexs.decompiler.flash.tags.base;

import com.jpexs.decompiler.flash.treeitems.TreeItem;
import java.util.List;

/**
 * Object which contains other objects
 *
 * @author JPEXS
 */
public interface Container extends TreeItem {

    /**
     * Returns all sub-items
     *
     * @return List of sub-items
     */
    public List<ContainerItem> getSubItems();

    /**
     * Returns number of sub-items
     *
     * @return Number of sub-items
     */
    public int getItemCount();
}
