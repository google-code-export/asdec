/*
 * Copyright (C) 2014 JPEXS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jpexs.decompiler.graph;

import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.model.LocalData;
import com.jpexs.decompiler.graph.model.UnboundedTypeItem;
import java.util.Objects;

/**
 *
 * @author JPEXS
 */
public class TypeFunctionItem extends GraphTargetItem{

    public static TypeFunctionItem BOOLEAN = new TypeFunctionItem("Boolean");
    public static TypeFunctionItem STRING = new TypeFunctionItem("String");
    public static TypeFunctionItem ARRAY = new TypeFunctionItem("Array");
    public static UnboundedTypeItem UNBOUNDED = new UnboundedTypeItem();
    
    public String fullTypeName;
    
    public TypeFunctionItem(String fullTypeName) {
        super(null,NOPRECEDENCE);
        this.fullTypeName = fullTypeName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.fullTypeName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TypeFunctionItem other = (TypeFunctionItem) obj;
        if (!Objects.equals(this.fullTypeName, other.fullTypeName)) {
            return false;
        }
        return true;
    }

    
    
    @Override
    public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) throws InterruptedException {
        writer.append(fullTypeName);
        return writer;
    }

    
    
    
    @Override
    public GraphTargetItem returnType() {
        return this;
    }

    @Override
    public boolean hasReturnValue() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Function["+fullTypeName+"]";
    }
    
}
