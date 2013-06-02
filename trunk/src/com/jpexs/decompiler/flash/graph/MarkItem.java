/*
 * Copyright (C) 2013 JPEXS
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
package com.jpexs.decompiler.flash.graph;

import java.util.List;

/**
 *
 * @author JPEXS
 */
public class MarkItem extends GraphTargetItem {

    private String mark;

    public MarkItem(String mark) {
        super(null, NOPRECEDENCE);
        this.mark = mark;
    }

    @Override
    public String toString(List<Object> localData) {
        return "//decompiler mark:" + mark;
    }

    public String getMark() {
        return mark;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
