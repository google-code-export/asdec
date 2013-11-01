package com.jpexs.decompiler.graph.model;

import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;

/**
 *
 * @author JPEXS
 */
public class TrueItem extends GraphTargetItem {

    public TrueItem(GraphSourceItem src) {
        super(src, PRECEDENCE_PRIMARY);
    }

    @Override
    protected GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) {
        return writer.append("true");
    }

    @Override
    public boolean hasReturnValue() {
        return true;
    }
}
