/*
 *  Copyright (C) 2013 JPEXS
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
package com.jpexs.decompiler.flash.treenodes;

import com.jpexs.decompiler.flash.treeitems.AS2PackageNodeItem;

/**
 *
 * @author JPEXS
 */
public class AS2PackageNode extends TreeNode {

    public AS2PackageNode(AS2PackageNodeItem item) {
        super(item);
    }

    @Override
    public AS2PackageNodeItem getItem() {
        return (AS2PackageNodeItem) item;
    }

}
