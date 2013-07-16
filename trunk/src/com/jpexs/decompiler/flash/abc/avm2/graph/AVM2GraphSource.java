package com.jpexs.decompiler.flash.abc.avm2.graph;

import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.avm2.AVM2Code;
import com.jpexs.decompiler.flash.abc.avm2.ConvertOutput;
import com.jpexs.decompiler.flash.abc.types.MethodBody;
import com.jpexs.decompiler.graph.GraphPart;
import com.jpexs.decompiler.graph.GraphSource;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author JPEXS
 */
public class AVM2GraphSource extends GraphSource {

    private AVM2Code code;
    boolean isStatic;
    int classIndex;
    int scriptIndex;
    HashMap<Integer, GraphTargetItem> localRegs;
    Stack<GraphTargetItem> scopeStack;
    ABC abc;
    MethodBody body;
    HashMap<Integer, String> localRegNames;
    List<String> fullyQualifiedNames;

    public AVM2Code getCode() {
        return code;
    }

    public AVM2GraphSource(AVM2Code code, boolean isStatic, int scriptIndex, int classIndex, HashMap<Integer, GraphTargetItem> localRegs, Stack<GraphTargetItem> scopeStack, ABC abc, MethodBody body, HashMap<Integer, String> localRegNames, List<String> fullyQualifiedNames) {
        this.code = code;
        this.isStatic = isStatic;
        this.classIndex = classIndex;
        this.localRegs = localRegs;
        this.scopeStack = scopeStack;
        this.abc = abc;
        this.body = body;
        this.localRegNames = localRegNames;
        this.fullyQualifiedNames = fullyQualifiedNames;
        this.scriptIndex = scriptIndex;
    }

    @Override
    public int size() {
        return code.code.size();
    }

    @Override
    public GraphSourceItem get(int pos) {
        return code.code.get(pos);
    }

    @Override
    public boolean isEmpty() {
        return code.code.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GraphTargetItem> translatePart(GraphPart part, List<Object> localData, Stack<GraphTargetItem> stack, int start, int end, int staticOperation, String path) {
        List<GraphTargetItem> ret = new ArrayList<>();
        Object o = localData.get(AVM2Graph.DATA_SCOPESTACK);
        Stack<GraphTargetItem> newstack = (Stack<GraphTargetItem>) o;
        ConvertOutput co = code.toSourceOutput(path, part, false, isStatic, scriptIndex, classIndex, localRegs, stack, newstack, abc, abc.constants, abc.method_info, body, start, end, localRegNames, fullyQualifiedNames, new boolean[size()]);
        ret.addAll(co.output);
        return ret;
    }

    @Override
    public int adr2pos(long adr) {
        return code.adr2pos(adr);
    }

    @Override
    public long pos2adr(int pos) {
        return code.pos2adr(pos);
    }
}
