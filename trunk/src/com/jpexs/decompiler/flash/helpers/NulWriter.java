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
package com.jpexs.decompiler.flash.helpers;

import java.util.Stack;

/**
 * Provides methods for highlighting positions of instructions in the text.
 *
 * @author JPEXS
 */
public class NulWriter extends GraphTextWriter {
    
    private Stack<LoopWithType> loopStack = new Stack<>();

    public NulWriter() {
    }

    public void startLoop(long loopId, int loopType) {
        LoopWithType loop = new LoopWithType();
        loop.loopId = loopId;
        loop.type = loopType;
        loopStack.add(loop);
    }
    
    public LoopWithType endLoop(long loopId)  {
        LoopWithType loopIdInStack = loopStack.pop();
        if (loopId != loopIdInStack.loopId) {
            throw new Error("LoopId mismatch");
        }
        return loopIdInStack;
    }
    
    public long getLoop() {
        if (loopStack.isEmpty()) {
            return -1;
        }
        return loopStack.peek().loopId;
    }
    
    public long getNonSwitchLoop() {
        if (loopStack.isEmpty()) {
            return -1;
        }

        int pos = loopStack.size() - 1;
        LoopWithType loop;
        do {
            loop = loopStack.get(pos);
            pos--;
        } while ((pos >= 0) && (loop.type == LoopWithType.LOOP_TYPE_SWITCH));

        if (loop.type == LoopWithType.LOOP_TYPE_SWITCH) {
            return -1;
        }

        return loop.loopId;
    }

    public void setLoopUsed(long loopId) {
        if (loopStack.isEmpty()) {
            return;
        }

        int pos = loopStack.size() - 1;
        LoopWithType loop = null;
        do {
            loop = loopStack.get(pos);
            pos--;
        } while ((pos >= 0) && (loop.loopId != loopId));
        
        if (loop.loopId == loopId) {
            loop.used = true;
        }
    }
}
