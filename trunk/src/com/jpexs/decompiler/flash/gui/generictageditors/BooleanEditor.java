/*
 *  Copyright (C) 2010-2014 JPEXS
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
package com.jpexs.decompiler.flash.gui.generictageditors;

import java.awt.Color;
import java.lang.reflect.Field;
import javax.swing.JCheckBox;

/**
 *
 * @author JPEXS
 */
public class BooleanEditor extends JCheckBox implements GenericTagEditor {

    private final Object obj;
    private final Field field;
    private final int index;
    private final Class<?> type;

    public BooleanEditor(Object obj, Field field, int index, Class<?> type) {
        super();
        this.obj = obj;
        this.field = field;
        this.index = index;
        this.type = type;
        try {
            setSelected((boolean) ReflectionTools.getValue(obj, field, index));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            // ignore
        }
    }

    @Override
    public void save() {
        try {
            ReflectionTools.setValue(obj, field, index, isSelected());
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            // ignore
        }
    }
}
