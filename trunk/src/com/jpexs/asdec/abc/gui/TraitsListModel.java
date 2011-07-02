/*
 *  Copyright (C) 2010-2011 JPEXS
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.jpexs.asdec.abc.gui;

import com.jpexs.asdec.abc.ABC;

import javax.swing.*;
import javax.swing.event.ListDataListener;


public class TraitsListModel implements ListModel {
    ABC abc;
    int classIndex;

    public static final String STR_INSTANCE_INITIALIZER = "instance initializer";
    public static final String STR_CLASS_INITIALIZER = "class initializer";

    public TraitsListModel(ABC abc, int classIndex) {
        this.abc = abc;
        this.classIndex = classIndex;
    }

    public int getSize() {
        int cnt = abc.class_info[classIndex].static_traits.traits.length + abc.instance_info[classIndex].instance_traits.traits.length;
        //if(abc.instance_info[classIndex].iinit_index!=0) cnt++;
        cnt += 2;
        return cnt;
    }

    public Object getElementAt(int index) {
        if (index < abc.class_info[classIndex].static_traits.traits.length) {
            return abc.class_info[classIndex].static_traits.traits[index].convert(abc.constants, abc.method_info, true);
        } else if (index < abc.class_info[classIndex].static_traits.traits.length + abc.instance_info[classIndex].instance_traits.traits.length) {
            return abc.instance_info[classIndex].instance_traits.traits[index - abc.class_info[classIndex].static_traits.traits.length].convert(abc.constants, abc.method_info, false);
        } else if (index == abc.class_info[classIndex].static_traits.traits.length + abc.instance_info[classIndex].instance_traits.traits.length) {
            return STR_INSTANCE_INITIALIZER;
        } else {
            return STR_CLASS_INITIALIZER;
        }
    }

    public void addListDataListener(ListDataListener l) {

    }

    public void removeListDataListener(ListDataListener l) {

    }

}
