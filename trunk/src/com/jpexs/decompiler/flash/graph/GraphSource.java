package com.jpexs.decompiler.flash.graph;

import com.jpexs.decompiler.flash.action.Action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author JPEXS
 */
public abstract class GraphSource {

    public abstract int size();

    public abstract GraphSourceItem get(int pos);

    public abstract boolean isEmpty();

    public abstract List<GraphTargetItem> translatePart(List localData, Stack<GraphTargetItem> stack, int start, int end);

    private void visitCode(int ip, int lastIp, HashMap<Integer, List<Integer>> refs, int endIp) {
        boolean debugMode = false;
        while (((endIp == -1) || (ip < endIp)) && (ip < size())) {
            refs.get(ip).add(lastIp);
            lastIp = ip;
            if (refs.get(ip).size() > 1) {
                break;
            }
            GraphSourceItem ins = get(ip);

            if (ins.isIgnored()) {
                ip++;
                continue;
            }
            if (debugMode) {
                System.err.println("visit ip " + ip + " action:" + ins);
            }
            if (ins.isExit()) {
                break;
            }

            if (ins instanceof GraphSourceItemContainer) {
                GraphSourceItemContainer cnt = (GraphSourceItemContainer) ins;
                if (ins instanceof Action) { //TODO: Remove dependency of AVM1
                    long endAddr = ((Action) ins).getAddress() + cnt.getHeaderSize();
                    for (long size : cnt.getContainerSizes()) {
                        if (size != 0) {
                            visitCode(adr2pos(endAddr), ip, refs, adr2pos(endAddr + size));
                        }
                        endAddr += size;
                    }
                    ip = adr2pos(endAddr);
                    continue;
                }

            }

            if (ins.isBranch() || ins.isJump()) {
                List<Integer> branches = ins.getBranches(this);
                for (int b : branches) {
                    if (b >= 0) {
                        visitCode(b, ip, refs, endIp);
                    }
                }
                break;
            }
            ip++;
        };
    }

    public HashMap<Integer, List<Integer>> visitCode(List<Integer> alternateEntries) {
        HashMap<Integer, List<Integer>> refs = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < size(); i++) {
            refs.put(i, new ArrayList<Integer>());
        }
        visitCode(0, 0, refs, -1);
        int pos = 0;
        for (int e : alternateEntries) {
            pos++;
            visitCode(e, -pos, refs, -1);
        }
        return refs;
    }

    public abstract int adr2pos(long adr);

    public abstract long pos2adr(int pos);
}
