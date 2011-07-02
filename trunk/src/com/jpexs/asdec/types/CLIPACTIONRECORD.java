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

package com.jpexs.asdec.types;

import com.jpexs.asdec.SWFInputStream;
import com.jpexs.asdec.action.Action;
import com.jpexs.asdec.tags.ASMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

/**
 * Event handler
 *
 * @author JPEXS
 */
public class CLIPACTIONRECORD implements ASMSource {
    /**
     * Events to which this handler applies
     */
    public CLIPEVENTFLAGS eventFlags;
    /**
     * If EventFlags contain ClipEventKeyPress: Key code to trap
     */
    public int keyCode;
    /**
     * Actions to perform
     */
    //public List<Action> actions;
    public byte[] actionBytes;

    /**
     * Returns a string representation of the object
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "CLIPACTIONRECORD";
    }


    /**
     * Returns header with events converted to string
     *
     * @return String representation of events
     */
    public String getHeader() {
        String ret = "";
        ret = eventFlags.toString();
        if (eventFlags.clipEventKeyPress) {
            ret = ret.replace("keyPress", "keyPress<" + keyCode + ">");
        }
        return ret;
    }

    /**
     * Converts actions to ASM source
     *
     * @param version SWF version
     * @return ASM source
     */
    public String getASMSource(int version) {
        List<Action> actions=new ArrayList<Action>();
        try {
            actions = (new SWFInputStream(new ByteArrayInputStream(actionBytes), version)).readActionList();
        } catch (IOException ex) {

        }
        return Action.actionsToString(actions, null, version);
    }

    /**
     * Whether or not this object contains ASM source
     *
     * @return True when contains
     */
    public boolean containsSource() {
        return true;
    }


    public List<Action> getActions(int version) {
        try {
            return (new SWFInputStream(new ByteArrayInputStream(actionBytes), version)).readActionList();
        } catch (IOException ex) {
            return new ArrayList<Action>();
        }
    }

    public void setActions(List<Action> actions, int version) {
        actionBytes=Action.actionsToBytes(actions, true, version);
    }

    public byte[] getActionBytes() {
        return actionBytes;
    }

    public void setActionBytes(byte[] actionBytes) {
        this.actionBytes=actionBytes;
    }
}
