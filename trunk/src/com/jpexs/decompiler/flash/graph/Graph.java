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
package com.jpexs.decompiler.flash.graph;

import com.jpexs.decompiler.flash.abc.avm2.treemodel.CommentTreeItem;
import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.graph.cfg.BBType;
import com.jpexs.decompiler.flash.graph.cfg.BasicBlock;
import com.jpexs.decompiler.flash.graph.cfg.CFG;
import com.jpexs.decompiler.flash.graph.cfg.StructType;
import com.jpexs.decompiler.flash.helpers.Highlighting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JPEXS
 */
public class Graph {

    public List<GraphPart> heads;
    protected GraphSource code;
    public List<CFG> headsCFG;

    public Graph(GraphSource code, List<Integer> alternateEntries) {
        this.code = code;
        headsCFG = new ArrayList<>();
        heads = makeGraph(code, new ArrayList<GraphPart>(), alternateEntries);
        List<BasicBlock> blocks=new ArrayList<>();
        List<BasicBlock> headsbb = makeCFG(code, blocks, alternateEntries);
        CFG cfg=new CFG(blocks, headsbb.get(0));
        cfg.structure();
        headsCFG.add(cfg);
        for (GraphPart head : heads) {
            fixGraph(head);
            makeMulti(head, new ArrayList<GraphPart>());
        }

    }

    protected static void populateParts(GraphPart part, List<GraphPart> allParts) {
        if (allParts.contains(part)) {
            return;
        }
        allParts.add(part);
        for (GraphPart p : part.nextParts) {
            populateParts(p, allParts);
        }
    }

    private void fixGraph(GraphPart part) {
        while (fixGraphOnce(part, new ArrayList<GraphPart>(), false)) {
        }
    }

    private boolean fixGraphOnce(GraphPart part, List<GraphPart> visited, boolean doChildren) {
        if (visited.contains(part)) {
            return false;
        }
        visited.add(part);
        boolean fixed = false;
        int i = 0;
        GraphPath lastpref = null;
        boolean modify = true;
        int prvni = -1;

        if (!doChildren) {

            List<GraphPart> uniqueRefs = new ArrayList<>();
            for (GraphPart r : part.refs) {
                if (!uniqueRefs.contains(r)) {
                    uniqueRefs.add(r);
                }
            }
            loopi:
            for (; i <= part.path.length(); i++) {
                lastpref = null;
                int pos = -1;
                for (GraphPart r : uniqueRefs) {
                    pos++;
                    if (r.path.rootName.equals("e") && !part.path.rootName.equals("e")) {
                        continue;
                    }
                    if (part.leadsTo(code, r, new ArrayList<Loop>())) {
                        modify = false;
                        continue;
                    }

                    prvni = pos;
                    if (i > r.path.length()) {
                        i--;
                        break loopi;
                    }
                    if (lastpref == null) {
                        lastpref = r.path.parent(i);
                    } else {
                        if (!r.path.startsWith(lastpref)) {
                            i--;
                            break loopi;
                        }
                    }
                }
            }
            if (i > part.path.length()) {
                i = part.path.length();
            }
            if (modify && ((uniqueRefs.size() > 1) && (prvni >= 0))) {
                GraphPath prvniUniq = uniqueRefs.get(prvni).path;
                GraphPath newpath = prvniUniq.parent(i);
                if (!part.path.equals(newpath)) {
                    if (part.path.startsWith(newpath) && ((newpath.length() == prvniUniq.length()) || (prvniUniq.getKey(newpath.length()) == part.path.getKey(newpath.length())))) {
                        GraphPath origPath = part.path;
                        GraphPart p = part;
                        part.path = newpath;
                        while (p.nextParts.size() == 1) {
                            p = p.nextParts.get(0);
                            if (!p.path.equals(origPath)) {
                                break;
                            }

                            p.path = newpath;
                        }
                        fixGraphOnce(part, new ArrayList<GraphPart>(), true);
                        fixed = true;
                    }
                }
            }
        } else {

            if (!fixed) {
                if (part.nextParts.size() == 1) {
                    if (!(part.path.rootName.equals("e") && (!part.nextParts.get(0).path.rootName.equals("e")))) {
                        if (part.nextParts.get(0).path.length() > part.path.length()) {
                            part.nextParts.get(0).path = part.path;
                            fixed = true;
                        }
                    }
                }
                if (part.nextParts.size() > 1) {
                    for (int j = 0; j < part.nextParts.size(); j++) {
                        GraphPart npart = part.nextParts.get(j);

                        if (npart.path.length() > part.path.length() + 1) {
                            npart.path = part.path.sub(j, part.end);
                            fixed = true;
                        }
                    }
                }
            }

        }
        if (part.nextParts.size() == 2) {
            if (part.nextParts.get(1).leadsTo(code, part.nextParts.get(0), new ArrayList<Loop>() /*visited*/)) {
                fixGraphOnce(part.nextParts.get(1), visited, doChildren);
                fixGraphOnce(part.nextParts.get(0), visited, doChildren);
            } else {
                fixGraphOnce(part.nextParts.get(0), visited, doChildren);
                fixGraphOnce(part.nextParts.get(1), visited, doChildren);
            }
        } else {
            for (int j = part.nextParts.size() - 1; j >= 0; j--) {
                GraphPart p = part.nextParts.get(j);
                fixGraphOnce(p, visited, doChildren);
            }
        }
        return fixed;
    }

    private void makeMulti(GraphPart part, List<GraphPart> visited) {
        if(true) return;
        if (visited.contains(part)) {
            return;
        }
        visited.add(part);
        GraphPart p = part;
        List<GraphPart> multiList = new ArrayList<>();
        multiList.add(p);
        while ((p.nextParts.size() == 1) && (p.nextParts.get(0).refs.size() == 1)) {
            p = p.nextParts.get(0);
            multiList.add(p);
        }
        if (multiList.size() > 1) {
            GraphPartMulti gpm = new GraphPartMulti(multiList);
            gpm.refs = part.refs;
            GraphPart lastPart = multiList.get(multiList.size() - 1);
            gpm.nextParts = lastPart.nextParts;
            for (GraphPart next : gpm.nextParts) {
                int index = next.refs.indexOf(lastPart);
                if (index == -1) {

                    continue;
                }
                next.refs.remove(lastPart);
                next.refs.add(index, gpm);
            }
            for (GraphPart parent : part.refs) {
                if (parent.start == -1) {
                    continue;
                }
                int index = parent.nextParts.indexOf(part);
                if (index == -1) {
                    continue;
                }
                parent.nextParts.remove(part);
                parent.nextParts.add(index, gpm);
            }
        }
        for (int i = 0; i < part.nextParts.size(); i++) {
            makeMulti(part.nextParts.get(i), visited);
        }
    }

    public GraphPart deepCopy(GraphPart part, List<GraphPart> visited, List<GraphPart> copies) {
        if (visited == null) {
            visited = new ArrayList<>();
        }
        if (copies == null) {
            copies = new ArrayList<>();
        }
        if (visited.contains(part)) {
            return copies.get(visited.indexOf(part));
        }
        visited.add(part);
        GraphPart copy = new GraphPart(part.start, part.end);
        copy.path = part.path;
        copies.add(copy);
        copy.nextParts = new ArrayList<>();
        for (int i = 0; i < part.nextParts.size(); i++) {
            copy.nextParts.add(deepCopy(part.nextParts.get(i), visited, copies));
        }
        for (int i = 0; i < part.refs.size(); i++) {
            copy.refs.add(deepCopy(part.refs.get(i), visited, copies));
        }
        return copy;
    }

    public void resetGraph(GraphPart part, List<GraphPart> visited) {
        if (visited.contains(part)) {
            return;
        }
        visited.add(part);
        int pos = 0;
        for (GraphPart p : part.nextParts) {
            if (!visited.contains(p)) {
                p.path = part.path.sub(pos, p.end);
            }
            resetGraph(p, visited);
            pos++;
        }
    }

    private void getReachableParts(GraphPart part, List<GraphPart> ret, List<Loop> loops) {
        getReachableParts(part, ret, loops, true);
    }

    private void getReachableParts(GraphPart part, List<GraphPart> ret, List<Loop> loops, boolean first) {

        if (first) {
            for (Loop l : loops) {
                l.reachableMark = 0;
            }
        }
        Loop currentLoop = null;

        for (Loop l : loops) {
            if ((l.phase == 1) || (l.reachableMark == 1)) {
                if (l.loopContinue == part) {
                    return;
                }
                if (l.loopBreak == part) {
                    return;
                }
                if (l.loopPreContinue == part) {
                    return;
                }
            }
            if (l.reachableMark == 0) {
                if (l.loopContinue == part) {
                    l.reachableMark = 1;
                    currentLoop = l;
                }
            }
        }

        List<GraphPart> newparts = new ArrayList<>();
        loopnext:
        for (GraphPart next : part.nextParts) {
            for (Loop l : loops) {
                if ((l.phase == 1) || (l.reachableMark == 1)) {
                    if (l.loopContinue == next) {
                        continue loopnext;
                    }
                    if (l.loopBreak == next) {
                        continue loopnext;
                    }
                    if (l.loopPreContinue == next) {
                        continue loopnext;
                    }
                }

            }
            if (!ret.contains(next)) {
                newparts.add(next);
            }
        }

        ret.addAll(newparts);
        for (GraphPart next : newparts) {
            getReachableParts(next, ret, loops);
        }

        if (currentLoop != null) {
            if (currentLoop.loopBreak != null) {
                if (!ret.contains(currentLoop.loopBreak)) {
                    ret.add(currentLoop.loopBreak);
                    currentLoop.reachableMark = 2;
                    getReachableParts(currentLoop.loopBreak, ret, loops);
                }
            }
        }
    }

    /* public GraphPart getNextCommonPart(GraphPart part, List<Loop> loops) {
     return getNextCommonPart(part, new ArrayList<GraphPart>(),loops);
     }*/
    public GraphPart getNextCommonPart(GraphPart part, List<Loop> loops) {
        return getCommonPart(part.nextParts, loops);
    }

    public GraphPart getCommonPart(List<GraphPart> parts, List<Loop> loops) {
        if (parts.isEmpty()) {
            return null;
        }

        List<GraphPart> loopContinues = getLoopsContinues(loops);

        for (GraphPart p : parts) {
            if (loopContinues.contains(p)) {
                break;
            }
            boolean common = true;
            for (GraphPart q : parts) {
                if (q == p) {
                    continue;
                }
                if (!q.leadsTo(code, p, loops)) {
                    common = false;
                    break;
                }
            }
            if (common) {
                return p;
            }
        }
        List<List<GraphPart>> reachable = new ArrayList<>();
        for (GraphPart p : parts) {
            List<GraphPart> r1 = new ArrayList<>();
            getReachableParts(p, r1, loops);
            r1.add(p);
            reachable.add(r1);
        }
        List<GraphPart> first = reachable.get(0);
        for (GraphPart p : first) {
            /*if (ignored.contains(p)) {
             continue;
             }*/
            boolean common = true;
            for (List<GraphPart> r : reachable) {
                if (!r.contains(p)) {
                    common = false;
                    break;
                }
            }
            if (common) {
                return p;
            }
        }
        return null;
    }

    public GraphPart getNextNoJump(GraphPart part) {
        while (code.get(part.start).isJump()) {
            part = part.getSubParts().get(0).nextParts.get(0);
        }
        return part;
    }

    public static List<GraphTargetItem> translateViaGraph(List<Object> localData, String path, GraphSource code, List<Integer> alternateEntries) {
        Graph g = new Graph(code, alternateEntries);
        return g.translate(localData);
    }
    
    private List<Loop> getLoopsFromCFG(){
        List<Loop> ret=new ArrayList<>();
        List<GraphPart> allParts=new ArrayList<>();
        for(GraphPart h:heads){
            populateParts(h, allParts);
        }
        for(CFG cfg:headsCFG){
            for(BasicBlock bb:cfg.m_listBB){
                if(bb.getStructType()== StructType.Loop
                        || bb.getStructType()== StructType.LoopCond){
                    GraphPart loopContinue=null;
                    GraphPart loopBreak=null;                    
                    for(GraphPart p:allParts){
                        if(p.start==bb.startAddress){
                            loopContinue=p;
                            break;
                        }
                    }
                    if(bb.loopFollow!=null){
                    for(GraphPart p:allParts){
                        if(p.start==bb.loopFollow.startAddress){
                            loopBreak=p;
                            break;
                        }
                    }
                    }
                    ret.add(new Loop(ret.size(),loopContinue,loopBreak));
                }
            }
        }
        return ret;
    }

    public List<GraphTargetItem> translate(List<Object> localData) {
        try{
        List<GraphPart> allParts = new ArrayList<>();
        for (GraphPart head : heads) {
            populateParts(head, allParts);
        }
        Stack<GraphTargetItem> stack = new Stack<>();
        List<Loop> loops = new ArrayList<>();
        loops=getLoopsFromCFG();
        //getLoops(heads.get(0), loops, null);
        /*System.out.println("<loops>");
        for (Loop el : loops) {
            System.out.println(el);
        }
        System.out.println("</loops>");*/
        getPrecontinues(null, heads.get(0), loops, null);
        /*System.out.println("<loopspre>");
        for (Loop el : loops) {
            System.out.println(el);
        }
        System.out.println("</loopspre>");*/
        
        List<GraphTargetItem> ret = printGraph(new ArrayList<GraphPart>(), localData, stack, allParts, null, heads.get(0), null, loops);
        processIfs(ret);
        finalProcessStack(stack, ret);
        finalProcessAll(ret, 0);
        return ret;
        } catch (StackOverflowError soe) {
            List<GraphTargetItem> ret =new ArrayList<>();
            ret.add(new CommentTreeItem(null, "StackOverflowError"));
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, "error during printGraph", soe);
            return ret;
        }
        
    }

    public void finalProcessStack(Stack<GraphTargetItem> stack, List<GraphTargetItem> output) {
    }

    private void finalProcessAll(List<GraphTargetItem> list, int level) {
        finalProcess(list, level);
        for (GraphTargetItem item : list) {
            if (item instanceof Block) {
                List<List<GraphTargetItem>> subs = ((Block) item).getSubs();
                for (List<GraphTargetItem> sub : subs) {
                    finalProcessAll(sub, level + 1);
                }
            }
        }
    }

    protected void finalProcess(List<GraphTargetItem> list, int level) {
    }

    private void processIfs(List<GraphTargetItem> list) {
        //if(true) return;
        for (int i = 0; i < list.size(); i++) {
            GraphTargetItem item = list.get(i);
            if (item instanceof Block) {
                List<List<GraphTargetItem>> subs = ((Block) item).getSubs();
                for (List<GraphTargetItem> sub : subs) {
                    processIfs(sub);
                }
            }
            if ((item instanceof LoopItem) && (item instanceof Block)) {
                List<List<GraphTargetItem>> subs = ((Block) item).getSubs();
                for (List<GraphTargetItem> sub : subs) {
                    processIfs(sub);
                    checkContinueAtTheEnd(sub, ((LoopItem) item).loop);
                }
            }
            if (item instanceof IfItem) {
                IfItem ifi = (IfItem) item;
                List<GraphTargetItem> onTrue = ifi.onTrue;
                List<GraphTargetItem> onFalse = ifi.onFalse;
                if ((!onTrue.isEmpty()) && (!onFalse.isEmpty())) {
                    if (onTrue.get(onTrue.size() - 1) instanceof ContinueItem) {
                        if (onFalse.get(onFalse.size() - 1) instanceof ContinueItem) {
                            if (((ContinueItem) onTrue.get(onTrue.size() - 1)).loopId == ((ContinueItem) onFalse.get(onFalse.size() - 1)).loopId) {
                                onTrue.remove(onTrue.size() - 1);
                                list.add(i + 1, onFalse.remove(onFalse.size() - 1));
                            }
                        }
                    }
                }

                if ((!onTrue.isEmpty()) && (!onFalse.isEmpty())) {
                    GraphTargetItem last = onTrue.get(onTrue.size() - 1);
                    if ((last instanceof ExitItem) || (last instanceof ContinueItem) || (last instanceof BreakItem)) {
                        list.addAll(i + 1, onFalse);
                        onFalse.clear();
                    }
                }

                if ((!onTrue.isEmpty()) && (!onFalse.isEmpty())) {
                    if (onFalse.get(onFalse.size() - 1) instanceof ExitItem) {
                        if (onTrue.get(onTrue.size() - 1) instanceof ContinueItem) {
                            list.add(i + 1, onTrue.remove(onTrue.size() - 1));
                        }
                    }
                }
            }
        }

        //Same continues in onTrue and onFalse gets continue on parent level


    }

    protected List<GraphPart> getLoopsContinuesPreAndBreaks(List<Loop> loops) {
        List<GraphPart> ret = new ArrayList<>();
        for (Loop l : loops) {
            if (l.loopContinue != null) {
                ret.add(l.loopContinue);
            }
            if (l.loopPreContinue != null) {
                ret.add(l.loopPreContinue);
            }
            if (l.loopBreak != null) {
                ret.add(l.loopBreak);
            }
        }
        return ret;
    }

    protected List<GraphPart> getLoopsContinuesAndPre(List<Loop> loops) {
        List<GraphPart> ret = new ArrayList<>();
        for (Loop l : loops) {
            if (l.loopContinue != null) {
                ret.add(l.loopContinue);
            }
            if (l.loopPreContinue != null) {
                ret.add(l.loopPreContinue);
            }
        }
        return ret;
    }

    protected List<GraphPart> getLoopsContinues(List<Loop> loops) {
        List<GraphPart> ret = new ArrayList<>();
        for (Loop l : loops) {
            if (l.loopContinue != null) {
                ret.add(l.loopContinue);
            }
            /*if (l.loopPreContinue != null) {
             ret.add(l.loopPreContinue);
             }*/
        }
        return ret;
    }

    protected GraphTargetItem checkLoop(GraphPart part, List<GraphPart> stopPart, List<Loop> loops) {
        if (stopPart.contains(part)) {
            return null;
        }
        for (Loop l : loops) {
            if (l.loopContinue == part) {
                return (new ContinueItem(null, l.id));
            }
            if (l.loopBreak == part) {
                return (new BreakItem(null, l.id));
            }
        }
        return null;
    }

    private void checkContinueAtTheEnd(List<GraphTargetItem> commands, Loop loop) {
        if (!commands.isEmpty()) {
            int i = commands.size() - 1;
            for (; i >= 0; i--) {
                if (commands.get(i) instanceof ContinueItem) {
                    continue;
                }
                if (commands.get(i) instanceof BreakItem) {
                    continue;
                }
                break;
            }
            if (i < commands.size() - 1) {
                for (int k = i + 2; k < commands.size(); k++) {
                    commands.remove(k);
                }
            }
            if (commands.get(commands.size() - 1) instanceof ContinueItem) {
                if (((ContinueItem) commands.get(commands.size() - 1)).loopId == loop.id) {
                    commands.remove(commands.size() - 1);
                }
            }
        }
    }

    protected boolean isEmpty(List<GraphTargetItem> output) {
        if (output.isEmpty()) {
            return true;
        }
        if (output.size() == 1) {
            if (output.get(0) instanceof MarkItem) {
                return true;
            }
        }
        return false;
    }

    protected List<GraphTargetItem> check(GraphSource code, List<Object> localData, List<GraphPart> allParts, Stack<GraphTargetItem> stack, GraphPart parent, GraphPart part, List<GraphPart> stopPart, List<Loop> loops, List<GraphTargetItem> output) {
        return null;
    }

    protected GraphPart checkPart(List<Object> localData, GraphPart part) {
        return part;
    }

    @SuppressWarnings("unchecked")
    protected GraphTargetItem translatePartGetStack(List<Object> localData, GraphPart part, Stack<GraphTargetItem> stack) {
        stack = (Stack<GraphTargetItem>) stack.clone();
        translatePart(localData, part, stack);
        return stack.pop();
    }

    protected List<GraphTargetItem> translatePart(List<Object> localData, GraphPart part, Stack<GraphTargetItem> stack) {
        List<GraphPart> sub = part.getSubParts();
        List<GraphTargetItem> ret = new ArrayList<>();
        int end = 0;
        for (GraphPart p : sub) {
            if (p.end == -1) {
                p.end = code.size() - 1;
            }
            if (p.start == code.size()) {
                continue;
            } else if (p.end == code.size()) {
                p.end--;
            }
            end = p.end;
            int start = p.start;
            ret.addAll(code.translatePart(part, localData, stack, start, end));
        }
        return ret;
    }

    private void markBranchEnd(List<GraphTargetItem> items) {
        if (!items.isEmpty()) {
            if (items.get(items.size() - 1) instanceof BreakItem) {
                return;
            }
            if (items.get(items.size() - 1) instanceof ContinueItem) {
                return;
            }
            if (items.get(items.size() - 1) instanceof ExitItem) {
                return;
            }
        }
        items.add(new MarkItem("finish"));
    }

    private static GraphTargetItem getLastNoEnd(List<GraphTargetItem> list) {
        if (list.isEmpty()) {
            return null;
        }
        if (list.get(list.size() - 1) instanceof ScriptEndItem) {
            if (list.size() >= 2) {
                return list.get(list.size() - 2);
            }
            return list.get(list.size() - 1);
        }
        return list.get(list.size() - 1);
    }

    private static void removeLastNoEnd(List<GraphTargetItem> list) {
        if (list.isEmpty()) {
            return;
        }
        if (list.get(list.size() - 1) instanceof ScriptEndItem) {
            if (list.size() >= 2) {
                list.remove(list.size() - 2);
            }
            return;
        }
        list.remove(list.size() - 1);
    }

    protected List<GraphTargetItem> printGraph(List<GraphPart> visited, List<Object> localData, Stack<GraphTargetItem> stack, List<GraphPart> allParts, GraphPart parent, GraphPart part, List<GraphPart> stopPart, List<Loop> loops) {
        return printGraph(visited, localData, stack, allParts, parent, part, stopPart, loops, null);
    }

    protected GraphTargetItem checkLoop(LoopItem loopItem, List<Object> localData, List<Loop> loops) {
        return loopItem;
    }

    private void getPrecontinues(GraphPart parent, GraphPart part, List<Loop> loops, List<GraphPart> stopPart) {
        clearLoops(loops);
        getPrecontinues(parent, part, loops, stopPart, 0, new ArrayList<GraphPart>());
        clearLoops(loops);
    }
    
    
    private void getPrecontinues(GraphPart parent, GraphPart part, List<Loop> loops, List<GraphPart> stopPart, int level, List<GraphPart> visited) {

        if (stopPart == null) {
            stopPart = new ArrayList<>();
        }

        for (Loop el : loops) {
            if (el.phase != 1) {
                continue;
            }
            if (el.loopContinue == part) {
                return;
            }
            if (el.loopPreContinue == part) {
                return;
            }
            if (el.loopBreak == part) {
                return;
            }
        }
        if (stopPart.contains(part)) {
            return;
        }

        if (visited.contains(part)) { //(part.level > level) {
            List<GraphPart> nextList = new ArrayList<>();
            populateParts(part, nextList);
            Loop nearestLoop = null;
            loopn:
            for (GraphPart n : nextList) {
                for (Loop l : loops) {
                    if (l.loopContinue == n) {
                        nearestLoop = l;
                        break loopn;
                    }
                }
            }

            if ((nearestLoop != null) && (nearestLoop.loopContinue != part)) {// && (nearestLoop.loopBreak != null)) {
                if (nearestLoop.phase == 1) {
                    if ((nearestLoop.loopPreContinue == null)) {// || (nearestLoop.loopPreContinue.leadsTo(code, part, getLoopsContinues(loops)))) {
                        nearestLoop.loopPreContinue = part;
                        return;
                    }
                }
            }
        }
        part.level = level;
        if (!visited.contains(part)) {
            visited.add(part);
        }

        List<GraphPart> loopContinues = getLoopsContinues(loops);
        boolean isLoop = false;
        Loop currentLoop = null;
        for (Loop el : loops) {
            if ((el.phase == 0) && (el.loopContinue == part)) {
                isLoop = true;
                currentLoop = el;
                el.phase = 1;
                break;
            }
        }
        if (part.nextParts.size() == 2) {
            GraphPart next = getNextCommonPart(part, loops);//part.getNextPartPath(new ArrayList<GraphPart>());
            List<GraphPart> stopParts2 = new ArrayList<>(stopPart);
            if (next != null) {
                stopParts2.add(next);
            }
            if (next != part.nextParts.get(0)) {
                getPrecontinues(part, part.nextParts.get(0), loops, next == null ? stopPart : stopParts2, level + 1, visited);
            }
            if (next != part.nextParts.get(1)) {
                getPrecontinues(part, part.nextParts.get(1), loops, next == null ? stopPart : stopParts2, level + 1, visited);
            }
            if (next != null) {
                getPrecontinues(part, next, loops, stopPart, level, visited);
            }
        }

        if (part.nextParts.size() > 2) {
            GraphPart next = getNextCommonPart(part, loops);
            List<GraphPart> stopParts2 = new ArrayList<>(stopPart);
            if (next != null) {
                stopParts2.add(next);
            }
            for (GraphPart p : part.nextParts) {
                if (next != p) {
                    getPrecontinues(part, p, loops, next == null ? stopPart : stopParts2, level + 1, visited);
                }
            }
            if (next != null) {
                getPrecontinues(part, next, loops, stopPart, level, visited);
            }
        }

        if (part.nextParts.size() == 1) {
            getPrecontinues(part, part.nextParts.get(0), loops, stopPart, level, visited);
        }
        if (isLoop) {
            if (currentLoop.loopBreak != null) {
                currentLoop.phase = 2;
                getPrecontinues(null, currentLoop.loopBreak, loops, stopPart, level, visited);
            }
        }
    }

    private void clearLoops(List<Loop> loops) {
        for (Loop l : loops) {
            l.phase = 0;
        }
    }

    private void getLoops(GraphPart part, List<Loop> loops, List<GraphPart> stopPart) {
        clearLoops(loops);
        getLoops(part, loops, stopPart, true);
        clearLoops(loops);
    }

    private void getLoops(GraphPart part, List<Loop> loops, List<GraphPart> stopPart, boolean first) {
        if(stopPart==null){
            stopPart=new ArrayList<>();
        }
        if (part == null) {
            return;
        }
        List<GraphPart> loopContinues = getLoopsContinues(loops);
        Loop lastP1=null;
        for (Loop el : loops) {
            if ((el.phase==1) && el.loopBreak == null) { //break not found yet                
                if (el.loopContinue != part) {
                    lastP1=el;
                    
                }

            }
        }
        if(lastP1!=null){
            if (lastP1.breakCandidates.contains(part)) {
                        lastP1.breakCandidates.add(part);
                        return;
                    } else {
                        List<GraphPart> loopContinues2 = new ArrayList<>(loopContinues);
                        loopContinues2.remove(lastP1.loopContinue);
                        List<Loop> loops2 = new ArrayList<>(loops);
                        loops2.remove(lastP1);
                        if (!part.leadsTo(code, lastP1.loopContinue, loops2)) {
                            lastP1.breakCandidates.add(part);
                            return;
                        }
                    }
        }

        for (Loop el : loops) {
            if (el.loopContinue == part) {
                return;
            }
        }

        if (part == stopPart) {
            return;
        }

        boolean isLoop = part.leadsTo(code, part, loops);
        Loop currentLoop = null;
        if (isLoop) {
            currentLoop = new Loop(loops.size(), part, null);
            currentLoop.phase = 1;
            loops.add(currentLoop);
            loopContinues.add(part);
        }

        if (part.nextParts.size() == 2) {
            GraphPart next = getNextCommonPart(part, loops);//part.getNextPartPath(loopContinues);
            List<GraphPart> stopPart2=new ArrayList<>(stopPart);
            if(next!=null){
                stopPart2.add(next);
            }
            getLoops(part.nextParts.get(0), loops, stopPart2, false);
            getLoops(part.nextParts.get(1), loops, stopPart2, false);
            if (next != null) {
                getLoops(next, loops, stopPart, false);
            }
        }
        if (part.nextParts.size() > 2) {
            GraphPart next = getNextCommonPart(part, loops);
            List<GraphPart> stopPart2=new ArrayList<>(stopPart);
            if(next!=null){
                stopPart2.add(next);
            }
            for (GraphPart p : part.nextParts) {
                getLoops(p, loops, stopPart2, false);
            }
            if (next != null) {
                getLoops(next, loops, stopPart, false);
            }
        }
        if (part.nextParts.size() == 1) {
            getLoops(part.nextParts.get(0), loops, stopPart, false);
        }


        if (isLoop) {
            
            GraphPart found;
            List<GraphPart> backupCandidates=new ArrayList<>();
            for(int i=currentLoop.breakCandidates.size()-1;i>=0;i--){
                if(stopPart.contains(currentLoop.breakCandidates.get(i))){
                    backupCandidates.add(currentLoop.breakCandidates.remove(i));
                }
            }
            Set<GraphPart> removed=new HashSet<>();
            /*
            Set<GraphPart> newcommon=new HashSet<>();
            for (GraphPart cand : currentLoop.breakCandidates) {
                    for (GraphPart cand2 : currentLoop.breakCandidates) {
                        if(cand==cand2){
                            continue;
                        }
                        List<GraphPart> c=new ArrayList<>();
                        c.add(cand);
                        c.add(cand2);
                        
                        GraphPart common=getCommonPart(c, loops);
                        if(common!=null){
                            if(!currentLoop.breakCandidates.contains(common)){
                                newcommon.add(common);
                            }
                        }
                    }
            }
            currentLoop.breakCandidates.addAll(newcommon);*/
            do {
                found = null;
                loopcand:
                for (GraphPart cand : currentLoop.breakCandidates) {
                    for (GraphPart cand2 : currentLoop.breakCandidates) {
                        if (cand.leadsTo(code, cand2, loops)) {
                            /*if (cand.path.equals(cand2.path)) {
                                found = cand2;
                            } else {*/
                            found = cand;
                            //}
                            break loopcand;
                        }
                    }
                }
                if (found != null) {
                    currentLoop.breakCandidates.remove(found);
                    removed.add(found);
                }
            } while (found != null);

            Map<GraphPart, Integer> count = new HashMap<>();
            GraphPart winner = null;
            int winnerCount = 0;
            boolean winnerBreakCandidate=true;
            for (GraphPart cand : currentLoop.breakCandidates) {
                
                if (!count.containsKey(cand)) {
                    count.put(cand, 0);
                }
                count.put(cand, count.get(cand) + 1);
                boolean otherBreakCandidate=false;
                for(Loop el:loops){
                    if(el==currentLoop) continue;
                    if(el.breakCandidates.contains(cand)){
                        otherBreakCandidate=true;
                        break;
                    }
                }
                /*if(winnerBreakCandidate && !otherBreakCandidate){
                    winnerCount = count.get(cand);
                    winner = cand;
                    winnerBreakCandidate=otherBreakCandidate;
                }else if(!winnerBreakCandidate && otherBreakCandidate){
                  */
                if(otherBreakCandidate){

                }else if (count.get(cand) > winnerCount) {
                    winnerCount = count.get(cand);
                    winner = cand;
                } else if (count.get(cand) == winnerCount) {
                    if (cand.path.length() < winner.path.length()) {
                        winner = cand;
                    }
                }
            }
            if(winner==null){
                if(!backupCandidates.isEmpty()){
                    winner=backupCandidates.get(backupCandidates.size()-1);
                }            
            }
            currentLoop.loopBreak = winner;
            currentLoop.phase = 2;
            boolean start=false;
            for(int l=0;l<loops.size();l++){
                Loop el=loops.get(l);
                if(start){
                    el.phase=1;
                }
                if(el==currentLoop){
                    start=true;
                }                
            }
            for(GraphPart r:removed){
                getLoops(r, loops, stopPart, false);
            }
            start = false;
            for(int l=0;l<loops.size();l++){
                Loop el=loops.get(l);
                if(el==currentLoop){
                    start=true;
                }
                if(start){
                    el.phase=2;
                }
            }
            //currentLoop.phase = 2;
            getLoops(currentLoop.loopBreak, loops, stopPart, false);
        }
    }

    protected List<GraphTargetItem> printGraph(List<GraphPart> visited, List<Object> localData, Stack<GraphTargetItem> stack, List<GraphPart> allParts, GraphPart parent, GraphPart part, List<GraphPart> stopPart, List<Loop> loops, List<GraphTargetItem> ret) {
        if (stopPart == null) {
            stopPart = new ArrayList<>();
        }
        if (visited.contains(part)) {
            //return new ArrayList<GraphTargetItem>();
        } else {
            visited.add(part);
        }
        if (ret == null) {
            ret = new ArrayList<>();
        }
        //try {
            boolean debugMode = false;

            if (debugMode) {
                System.err.println("PART " + part);
            }

            /*while (((part != null) && (part.getHeight() == 1)) && (code.size() > part.start) && (code.get(part.start).isJump())) {  //Parts with only jump in it gets ignored                

             if (part == stopPart) {
             return ret;
             }
             GraphTargetItem lop = checkLoop(part.nextParts.get(0), stopPart, loops);
             if (lop == null) {
             part = part.nextParts.get(0);
             } else {
             break;
             }
             }*/





            if (part == null) {
                return ret;
            }
            part = checkPart(localData, part);
            if (part == null) {
                return ret;
            }

            if (part.ignored) {
                return ret;
            }


            /*    if ((parent != null) && (part.path.length() < parent.path.length())) {
             boolean can = true;
             for (Loop el : loops) {
             if (el.loopContinue == part) {
             can = false;
             break;
             }
             if (el.loopBreak == part) {
             can = false;
             break;
             }
             if (el.breakCandidates.containsKey(part)) {
             can = false;
             break;
             }
             }
             if (can) {
             if ((part != stopPart) && (part.refs.size() > 1)) {
             List<GraphPart> nextList = new ArrayList<>();
             populateParts(part, nextList);
             Loop nearestLoop = null;
             loopn:
             for (GraphPart n : nextList) {
             for (Loop l : loops) {
             if (l.loopContinue == n) {
             nearestLoop = l;
             break loopn;
             }
             }
             }

             if ((nearestLoop != null)) {// && (nearestLoop.loopBreak != null)) {

             List<GraphTargetItem> finalCommands = printGraph(visited, localData, stack, allParts, null, part, nearestLoop.loopContinue, loops);
             nearestLoop.loopContinue = part;
             forFinalCommands.put(nearestLoop, finalCommands);
             ContinueItem cti = new ContinueItem(null, nearestLoop.id);
             ret.add(cti);
             //ret.add(new CommentItem("CONTTEST"));
             return ret;
             }
             }
             }
             }
             */
            List<GraphPart> loopContinues = getLoopsContinues(loops);
            boolean isLoop = false; //part.leadsTo(code, part, loopContinues);
            Loop currentLoop = null;
            for (Loop el : loops) {
                if ((el.loopContinue == part) && (el.phase == 0)) {
                    currentLoop = el;
                    currentLoop.phase = 1;
                    isLoop = true;
                    break;
                }
            }
            /*Loop currentLoop = null;
             if (isLoop) {
             currentLoop = new Loop(loops.size(), part, null);
             loops.add(currentLoop);
             loopContinues.add(part);
             }*/


            for (int l = loops.size() - 1; l >= 0; l--) {
                Loop el = loops.get(l);
                if (el == currentLoop) {
                    continue;
                }
                if (el.phase != 1) {
                    continue;
                }
                if (el.loopBreak == part) {
                    ret.add(new BreakItem(null, el.id));
                    return ret;
                }
                if (el.loopPreContinue == part) {
                    ret.add(new ContinueItem(null, el.id));
                    return ret;
                }
                if (el.loopContinue == part) {
                    ret.add(new ContinueItem(null, el.id));
                    return ret;
                }
            }

            

            if (stopPart.contains(part)) {
                return ret;
            }
            
            if ((part != null) && (code.size() <= part.start)) {
                ret.add(new ScriptEndItem());
                return ret;
            }

            if (currentLoop != null) {
                currentLoop.used = true;
            }


            List<GraphTargetItem> currentRet = ret;
            UniversalLoopItem loopItem = null;
            if (isLoop) {
                loopItem = new UniversalLoopItem(null, currentLoop);
                //loopItem.commands=printGraph(visited, localData, stack, allParts, parent, part, stopPart, loops);
                currentRet.add(loopItem);
                loopItem.commands = new ArrayList<>();
                currentRet = loopItem.commands;
                //return ret;
            }

            boolean parseNext = true;

            //****************************DECOMPILING PART*************
            List<GraphTargetItem> output = new ArrayList<>();

            List<GraphPart> parts = new ArrayList<>();
            if (part instanceof GraphPartMulti) {
                parts = ((GraphPartMulti) part).parts;
            } else {
                parts.add(part);
            }
            int end = part.end;
            for (GraphPart p : parts) {
                end = p.end;
                int start = p.start;

                try {
                    output.addAll(code.translatePart(p, localData, stack, start, end));
                    if ((end >= code.size() - 1) && p.nextParts.isEmpty()) {
                        output.add(new ScriptEndItem());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, "error during printgraph", ex);
                    return ret;
                }
            }

            //Assuming part with two nextparts is an IF

            /* //If with both branches empty
             if (part.nextParts.size() == 2) {
             if (part.nextParts.get(0) == part.nextParts.get(1)) {
             if (!stack.isEmpty()) {
             GraphTargetItem expr = stack.pop();
             if (expr instanceof LogicalOpItem) {
             expr = ((LogicalOpItem) expr).invert();
             } else {
             expr = new NotItem(null, expr);
             }
             output.add(new IfItem(null, expr, new ArrayList<GraphTargetItem>(), new ArrayList<GraphTargetItem>()));
             }
             part.nextParts.remove(0);
             }
             }*/

            /**
             * AND / OR detection
             */
            if (part.nextParts.size() == 2) {
                if ((stack.size() >= 2) && (stack.get(stack.size() - 1) instanceof NotItem) && (((NotItem) (stack.get(stack.size() - 1))).getOriginal().getNotCoerced() == stack.get(stack.size() - 2).getNotCoerced())) {
                    currentRet.addAll(output);
                    GraphPart sp0 = getNextNoJump(part.nextParts.get(0));
                    GraphPart sp1 = getNextNoJump(part.nextParts.get(1));
                    boolean reversed = false;
                    loopContinues = getLoopsContinues(loops);
                    loopContinues.add(part);//???
                    if (sp1.leadsTo(code, sp0, loops)) {
                    } else if (sp0.leadsTo(code, sp1, loops)) {
                        reversed = true;
                    }
                    GraphPart next = reversed ? sp0 : sp1;
                    GraphTargetItem ti;
                    if ((ti = checkLoop(next, stopPart, loops)) != null) {
                        currentRet.add(ti);
                    } else {
                        List<GraphPart> stopPart2 = new ArrayList<>(stopPart);
                        stopPart2.add(reversed ? sp1 : sp0);
                        printGraph(visited, localData, stack, allParts, parent, next, stopPart2, new ArrayList<Loop>()/*ignore loops*/);
                        GraphTargetItem second = stack.pop();
                        GraphTargetItem first = stack.pop();

                        if (!reversed) {
                            AndItem a = new AndItem(null, first, second);
                            stack.push(a);
                            a.firstPart = part;
                            if (second instanceof AndItem) {
                                a.firstPart = ((AndItem) second).firstPart;
                            }
                            if (second instanceof OrItem) {
                                a.firstPart = ((OrItem) second).firstPart;
                            }
                        } else {
                            OrItem o = new OrItem(null, first, second);
                            stack.push(o);
                            o.firstPart = part;
                            if (second instanceof AndItem) {
                                o.firstPart = ((AndItem) second).firstPart;
                            }
                            if (second instanceof OrItem) {
                                o.firstPart = ((OrItem) second).firstPart;
                            }
                        }
                        next = reversed ? sp1 : sp0;
                        if ((ti = checkLoop(next, stopPart, loops)) != null) {
                            currentRet.add(ti);
                        } else {
                            currentRet.addAll(printGraph(visited, localData, stack, allParts, parent, next, stopPart, loops));
                        }
                    }
                    parseNext = false;
                    //return ret;
                } else if ((stack.size() >= 2) && (stack.get(stack.size() - 1).getNotCoerced() == stack.get(stack.size() - 2).getNotCoerced())) {
                    currentRet.addAll(output);
                    GraphPart sp0 = getNextNoJump(part.nextParts.get(0));
                    GraphPart sp1 = getNextNoJump(part.nextParts.get(1));
                    boolean reversed = false;
                    loopContinues = getLoopsContinues(loops);
                    loopContinues.add(part);//???
                    if (sp1.leadsTo(code, sp0, loops)) {
                    } else if (sp0.leadsTo(code, sp1, loops)) {
                        reversed = true;
                    }
                    GraphPart next = reversed ? sp0 : sp1;
                    GraphTargetItem ti;
                    if ((ti = checkLoop(next, stopPart, loops)) != null) {
                        currentRet.add(ti);
                    } else {
                        List<GraphPart> stopPart2 = new ArrayList<>(stopPart);
                        stopPart2.add(reversed ? sp1 : sp0);
                        printGraph(visited, localData, stack, allParts, parent, next, stopPart2, new ArrayList<Loop>()/*ignore loops*/);
                        GraphTargetItem second = stack.pop();
                        GraphTargetItem first = stack.pop();

                        if (reversed) {
                            AndItem a = new AndItem(null, first, second);
                            stack.push(a);
                            a.firstPart = part;
                            if (second instanceof AndItem) {
                                a.firstPart = ((AndItem) second).firstPart;
                            }
                            if (second instanceof OrItem) {
                                a.firstPart = ((AndItem) second).firstPart;
                            }
                        } else {
                            OrItem o = new OrItem(null, first, second);
                            stack.push(o);
                            o.firstPart = part;
                            if (second instanceof OrItem) {
                                o.firstPart = ((OrItem) second).firstPart;
                            }
                            if (second instanceof OrItem) {
                                o.firstPart = ((OrItem) second).firstPart;
                            }
                        }

                        next = reversed ? sp1 : sp0;
                        if ((ti = checkLoop(next, stopPart, loops)) != null) {
                            currentRet.add(ti);
                        } else {
                            currentRet.addAll(printGraph(visited, localData, stack, allParts, parent, next, stopPart, loops));
                        }
                    }
                    parseNext = false;
                    //return ret;
                }
            }
//********************************END PART DECOMPILING

            if (parseNext) {
                List<GraphTargetItem> retCheck = check(code, localData, allParts, stack, parent, part, stopPart, loops, output);
                if (retCheck != null) {
                    if (!retCheck.isEmpty()) {
                        currentRet.addAll(retCheck);
                    }
                    return ret;
                } else {
                    currentRet.addAll(output);
                }

                if (part.nextParts.size() == 2) {
                    //List<GraphPart> ignore = new ArrayList<>();
                    //ignore.addAll(loopContinues);

                    /*for (Loop el : loops) {
                     if (el.loopContinue == next) {
                     next = null;
                     break;
                     }
                     if (el.loopBreak == next) {
                     next = null;
                     break;
                     }
                     }*/

                    GraphTargetItem expr = stack.pop();
                    if (expr instanceof LogicalOpItem) {
                        expr = ((LogicalOpItem) expr).invert();
                    } else {
                        expr = new NotItem(null, expr);
                    }
                    GraphPart next = getNextCommonPart(part, loops);//part.getNextPartPath(loopContinues); //loopContinues);

                    @SuppressWarnings("unchecked")
                    Stack<GraphTargetItem> trueStack = (Stack<GraphTargetItem>) stack.clone();
                    @SuppressWarnings("unchecked")
                    Stack<GraphTargetItem> falseStack = (Stack<GraphTargetItem>) stack.clone();
                    int trueStackSizeBefore = trueStack.size();
                    int falseStackSizeBefore = falseStack.size();
                    List<GraphTargetItem> onTrue = new ArrayList<>();
                    boolean isEmpty = part.nextParts.get(0) == part.nextParts.get(1);

                    if (isEmpty) {
                        next = part.nextParts.get(0);
                    }

                    List<GraphPart> stopPart2 = new ArrayList<>(stopPart);
                    if (next != null) {
                        stopPart2.add(next);
                    }
                    if (!isEmpty) {
                        onTrue = printGraph(visited, localData, trueStack, allParts, part, part.nextParts.get(1), stopPart2, loops);
                    }
                    List<GraphTargetItem> onFalse = new ArrayList<>();

                    if (!isEmpty) {
                        onFalse = printGraph(visited, localData, falseStack, allParts, part, part.nextParts.get(0), stopPart2, loops);
                    }
                    if (isEmpty(onTrue) && isEmpty(onFalse) && (trueStack.size() > trueStackSizeBefore) && (falseStack.size() > falseStackSizeBefore)) {
                        stack.push(new TernarOpItem(null, expr, trueStack.pop(), falseStack.pop()));
                    } else {
                        currentRet.add(new IfItem(null, expr, onTrue, onFalse));
                    }
                    if (next != null) {
                        printGraph(visited, localData, stack, allParts, part, next, stopPart, loops, currentRet);
                        //currentRet.addAll();
                    }
                } else if (part.nextParts.size() == 1) {

                    printGraph(visited, localData, stack, allParts, part, part.nextParts.get(0), stopPart, loops, currentRet);
                }

            }
            if (isLoop) {
                currentLoop.phase = 2;
                LoopItem li = loopItem;
                boolean loopTypeFound = false;


                checkContinueAtTheEnd(loopItem.commands, currentLoop);

                //Loop with condition at the beginning (While)
                if (!loopTypeFound && (!loopItem.commands.isEmpty())) {
                    if (loopItem.commands.get(0) instanceof IfItem) {
                        IfItem ifi = (IfItem) loopItem.commands.get(0);                                              
                        
                        
                        List<GraphTargetItem> bodyBranch = null;
                        boolean inverted = false;
                        if ((ifi.onTrue.size() == 1) && (ifi.onTrue.get(0) instanceof BreakItem)) {
                            BreakItem bi = (BreakItem) ifi.onTrue.get(0);
                            if (bi.loopId == currentLoop.id) {
                                bodyBranch = ifi.onFalse;
                                inverted = true;
                            }
                        } else if ((ifi.onFalse.size() == 1) && (ifi.onFalse.get(0) instanceof BreakItem)) {
                            BreakItem bi = (BreakItem) ifi.onFalse.get(0);
                            if (bi.loopId == currentLoop.id) {
                                bodyBranch = ifi.onTrue;
                            }
                        }
                        if (bodyBranch != null) {
                            int index = ret.indexOf(loopItem);
                            ret.remove(index);
                            List<GraphTargetItem> exprList = new ArrayList<>();
                            GraphTargetItem expr = ifi.expression;
                            if (inverted) {
                                if (expr instanceof LogicalOpItem) {
                                    expr = ((LogicalOpItem) expr).invert();
                                } else {
                                    expr = new NotItem(null, expr);
                                }
                            }
                            exprList.add(expr);
                            List<GraphTargetItem> commands = new ArrayList<>();
                            commands.addAll(bodyBranch);
                            loopItem.commands.remove(0);
                            commands.addAll(loopItem.commands);
                            checkContinueAtTheEnd(commands, currentLoop);
                            List<GraphTargetItem> finalComm = new ArrayList<>();
                            if (currentLoop.loopPreContinue != null) {
                                GraphPart backup = currentLoop.loopPreContinue;
                                currentLoop.loopPreContinue = null;
                                List<GraphPart> stopPart2 = new ArrayList<>(stopPart);
                                stopPart2.add(currentLoop.loopContinue);
                                finalComm = printGraph(visited, localData, new Stack<GraphTargetItem>(), allParts, null, backup, stopPart2, loops);
                                currentLoop.loopPreContinue = backup;
                                checkContinueAtTheEnd(finalComm, currentLoop);
                            }
                            if (!finalComm.isEmpty()) {
                                ret.add(index, li = new ForTreeItem(null, currentLoop, new ArrayList<GraphTargetItem>(), exprList.get(exprList.size() - 1), finalComm, commands));
                            } else {
                                ret.add(index, li = new WhileItem(null, currentLoop, exprList, commands));
                            }

                            loopTypeFound = true;
                        }
                    }
                }


                //Loop with condition at the end (Do..While)
                if (!loopTypeFound && (!loopItem.commands.isEmpty())) {
                    if (loopItem.commands.get(loopItem.commands.size() - 1) instanceof IfItem) {
                        IfItem ifi = (IfItem) loopItem.commands.get(loopItem.commands.size() - 1);
                        List<GraphTargetItem> bodyBranch = null;
                        boolean inverted = false;
                        if ((ifi.onTrue.size() == 1) && (ifi.onTrue.get(0) instanceof BreakItem)) {
                            BreakItem bi = (BreakItem) ifi.onTrue.get(0);
                            if (bi.loopId == currentLoop.id) {
                                bodyBranch = ifi.onFalse;
                                inverted = true;
                            }
                        } else if ((ifi.onFalse.size() == 1) && (ifi.onFalse.get(0) instanceof BreakItem)) {
                            BreakItem bi = (BreakItem) ifi.onFalse.get(0);
                            if (bi.loopId == currentLoop.id) {
                                bodyBranch = ifi.onTrue;
                            }
                        }
                        if (bodyBranch != null) {
                            //Condition at the beginning
                            int index = ret.indexOf(loopItem);
                            ret.remove(index);
                            List<GraphTargetItem> exprList = new ArrayList<>();
                            GraphTargetItem expr = ifi.expression;
                            if (inverted) {
                                if (expr instanceof LogicalOpItem) {
                                    expr = ((LogicalOpItem) expr).invert();
                                } else {
                                    expr = new NotItem(null, expr);
                                }
                            }

                            checkContinueAtTheEnd(bodyBranch, currentLoop);


                            List<GraphTargetItem> commands = new ArrayList<>();
                            loopItem.commands.remove(loopItem.commands.size() - 1);
                            if (!bodyBranch.isEmpty()) {
                                /*exprList.addAll(loopItem.commands);
                                commands.addAll(bodyBranch);
                                exprList.add(expr);
                                checkContinueAtTheEnd(commands, currentLoop);
                                ret.add(index, li = new WhileItem(null, currentLoop, exprList, commands));*/
                            } else {
                                commands.addAll(loopItem.commands);
                                commands.addAll(bodyBranch);
                                exprList.add(expr);
                                checkContinueAtTheEnd(commands, currentLoop);
                                ret.add(index, li = new DoWhileItem(null, currentLoop, commands, exprList));

                            }
                            loopTypeFound = true;
                        }
                    }
                }
                
                if (!loopTypeFound) {
                    if (currentLoop.loopPreContinue != null) {
                        loopTypeFound = true;
                        GraphPart backup = currentLoop.loopPreContinue;
                        currentLoop.loopPreContinue = null;
                        List<GraphPart> stopPart2 = new ArrayList<>(stopPart);
                        stopPart2.add(currentLoop.loopContinue);
                        List<GraphTargetItem> finalComm = printGraph(visited, localData, new Stack<GraphTargetItem>(), allParts, null, backup, stopPart2, loops);
                        currentLoop.loopPreContinue = backup;
                        checkContinueAtTheEnd(finalComm, currentLoop);

                        if (!finalComm.isEmpty()) {
                            if (finalComm.get(finalComm.size() - 1) instanceof IfItem) {
                                IfItem ifi = (IfItem) finalComm.get(finalComm.size() - 1);
                                boolean ok = false;
                                boolean invert = false;
                                if (((ifi.onTrue.size() == 1) && (ifi.onTrue.get(0) instanceof BreakItem) && (((BreakItem) ifi.onTrue.get(0)).loopId == currentLoop.id))
                                        && ((ifi.onTrue.size() == 1) && (ifi.onFalse.get(0) instanceof ContinueItem) && (((ContinueItem) ifi.onFalse.get(0)).loopId == currentLoop.id))) {
                                    ok = true;
                                    invert = true;
                                }
                                if (((ifi.onTrue.size() == 1) && (ifi.onTrue.get(0) instanceof ContinueItem) && (((ContinueItem) ifi.onTrue.get(0)).loopId == currentLoop.id))
                                        && ((ifi.onTrue.size() == 1) && (ifi.onFalse.get(0) instanceof BreakItem) && (((BreakItem) ifi.onFalse.get(0)).loopId == currentLoop.id))) {
                                    ok = true;
                                }
                                if (ok) {
                                    finalComm.remove(finalComm.size() - 1);
                                    int index = ret.indexOf(loopItem);
                                    ret.remove(index);
                                    List<GraphTargetItem> exprList = new ArrayList<>(finalComm);
                                    GraphTargetItem expr = ifi.expression;
                                    if (invert) {
                                        if (expr instanceof LogicalOpItem) {
                                            expr = ((LogicalOpItem) expr).invert();
                                        } else {
                                            expr = new NotItem(null, expr);
                                        }
                                    }
                                    exprList.add(expr);
                                    ret.add(index, li = new DoWhileItem(null, currentLoop, loopItem.commands, exprList));
                                }
                            }
                        }
                    }
                }

                if (!loopTypeFound) {
                    checkContinueAtTheEnd(loopItem.commands, currentLoop);
                }

                GraphTargetItem replaced = checkLoop(li, localData, loops);
                if (replaced != li) {
                    int index = ret.indexOf(li);
                    ret.remove(index);
                    if (replaced != null) {
                        ret.add(index, replaced);
                    }
                }

                //loops.remove(currentLoop);
                if (currentLoop.loopBreak != null) {
                    ret.addAll(printGraph(visited, localData, stack, allParts, part, currentLoop.loopBreak, stopPart, loops));
                }
            }

            return ret;
        
    }

    private List<GraphPart> makeGraph(GraphSource code, List<GraphPart> allBlocks, List<Integer> alternateEntries) {
        HashMap<Integer, List<Integer>> refs = code.visitCode(alternateEntries);
        List<GraphPart> ret = new ArrayList<>();
        boolean visited[] = new boolean[code.size()];
        ret.add(makeGraph(null, new GraphPath(), code, 0, 0, allBlocks, refs, visited));
        for (int pos : alternateEntries) {
            GraphPart e1 = new GraphPart(-1, -1);
            e1.path = new GraphPath("e");
            ret.add(makeGraph(e1, new GraphPath("e"), code, pos, pos, allBlocks, refs, visited));
        }
        return ret;
    }
    
    private List<BasicBlock> makeCFG(GraphSource code, List<BasicBlock> allBlocks, List<Integer> alternateEntries) {
        HashMap<Integer, List<Integer>> refs = code.visitCode(alternateEntries);
        List<BasicBlock> ret = new ArrayList<>();
        boolean visited[] = new boolean[code.size()];
        ret.add(makeCFG(null, new GraphPath(), code, 0, 0, allBlocks, refs, visited));
        for (int pos : alternateEntries) {
            BasicBlock e1 = new BasicBlock();
            e1.startAddress=-1;
            e1.endAddress=-1;
            //e1.path = new GraphPath("e");
            ret.add(makeCFG(e1, new GraphPath("e"), code, pos, pos, allBlocks, refs, visited));
        }
        return ret;
    }

    protected int checkIp(int ip) {
        return ip;
    }

    
    private GraphPart makeGraph(GraphPart parent, GraphPath path, GraphSource code, int startip, int lastIp, List<GraphPart> allBlocks, HashMap<Integer, List<Integer>> refs, boolean visited2[]) {

        int ip = startip;
        for (GraphPart p : allBlocks) {
            if (p.start == ip) {
                p.refs.add(parent);
                return p;
            }
        }
        GraphPart g;
        GraphPart ret = new GraphPart(ip, -1);
        ret.path = path;
        GraphPart part = ret;
        while (ip < code.size()) {
            if (visited2[ip] || ((ip != startip) && (refs.get(ip).size() > 1))) {
                part.end = lastIp;
                GraphPart found = null;
                for (GraphPart p : allBlocks) {
                    if (p.start == ip) {
                        found = p;
                        break;
                    }
                }

                allBlocks.add(part);

                if (found != null) {
                    part.nextParts.add(found);
                    found.refs.add(part);
                    break;
                } else {
                    GraphPart gp = new GraphPart(ip, -1);
                    gp.path = path;
                    part.nextParts.add(gp);
                    gp.refs.add(part);
                    part = gp;
                }
            }

            ip = checkIp(ip);
            lastIp = ip;
            GraphSourceItem ins = code.get(ip);
            if (ins.isIgnored()) {
                ip++;
                continue;
            }
            if (ins instanceof GraphSourceItemContainer) {
                GraphSourceItemContainer cnt = (GraphSourceItemContainer) ins;
                if (ins instanceof Action) { //TODO: Remove dependency of AVM1
                    long endAddr = ((Action) ins).getAddress() + cnt.getHeaderSize();
                    for (long size : cnt.getContainerSizes()) {
                        endAddr += size;
                    }
                    ip = code.adr2pos(endAddr);
                }
                continue;
            } else if (ins.isExit()) {
                part.end = ip;
                allBlocks.add(part);
                break;
            } else if (ins.isJump()) {
                part.end = ip;
                allBlocks.add(part);
                ip = ins.getBranches(code).get(0);
                part.nextParts.add(g = makeGraph(part, path, code, ip, lastIp, allBlocks, refs, visited2));
                g.refs.add(part);
                break;
            } else if (ins.isBranch()) {
                part.end = ip;

                allBlocks.add(part);
                List<Integer> branches = ins.getBranches(code);
                for (int i = 0; i < branches.size(); i++) {
                    part.nextParts.add(g = makeGraph(part, path.sub(i, ip), code, branches.get(i), ip, allBlocks, refs, visited2));
                    g.refs.add(part);
                }
                break;
            }
            ip++;
        }
        if ((part.end == -1) && (ip >= code.size())) {
            if (part.start == code.size()) {
                part.end = code.size();
                allBlocks.add(part);
            } else {
                part.end = ip - 1;
                for (GraphPart p : allBlocks) {
                    if (p.start == ip) {
                        p.refs.add(part);
                        part.nextParts.add(p);
                        allBlocks.add(part);
                        return ret;
                    }
                }
                GraphPart gp = new GraphPart(ip, ip);
                allBlocks.add(gp);
                gp.refs.add(part);
                part.nextParts.add(gp);
                allBlocks.add(part);
            }
        }
        return ret;
    }
    
    private BasicBlock makeCFG(BasicBlock parent, GraphPath path, GraphSource code, int startip, int lastIp, List<BasicBlock> allBlocks, HashMap<Integer, List<Integer>> refs, boolean visited2[]) {

        int ip = startip;
        for (BasicBlock p : allBlocks) {
            if (p.startAddress == ip) {
                p.addInEdge(parent);
                return p;
            }
        }
        BasicBlock g;
        BasicBlock ret = new BasicBlock();
        ret.startAddress=ip;
        ret.endAddress=-1;
        //ret.path = path;
        BasicBlock part = ret;
        while (ip < code.size()) {
            if (visited2[ip] || ((ip != startip) && (refs.get(ip).size() > 1))) {
                part.endAddress = lastIp;
                BasicBlock found = null;
                for (BasicBlock p : allBlocks) {
                    if (p.startAddress == ip) {
                        found = p;
                        break;
                    }
                }

                allBlocks.add(part);

                if (found != null) {
                    part.addOutEdge(found);
                    found.addInEdge(part);
                    break;
                } else {
                    BasicBlock gp = new BasicBlock();
                    gp.startAddress=ip;
                    gp.endAddress=-1;
                    //gp.path = path;
                    part.addOutEdge(gp);
                    gp.addInEdge(part);
                    part = gp;
                }
            }

            ip = checkIp(ip);
            lastIp = ip;
            GraphSourceItem ins = code.get(ip);
            if (ins.isIgnored()) {
                ip++;
                continue;
            }
            if (ins instanceof GraphSourceItemContainer) {
                GraphSourceItemContainer cnt = (GraphSourceItemContainer) ins;
                if (ins instanceof Action) { //TODO: Remove dependency of AVM1
                    long endAddr = ((Action) ins).getAddress() + cnt.getHeaderSize();
                    for (long size : cnt.getContainerSizes()) {
                        endAddr += size;
                    }
                    ip = code.adr2pos(endAddr);
                }
                continue;
            } else if (ins.isExit()) {
                part.endAddress = ip;
                allBlocks.add(part);
                part.setType(BBType.RET);
                break;
            } else if (ins.isJump()) {
                part.endAddress = ip;
                allBlocks.add(part);
                part.setType(BBType.ONEWAY);
                ip = ins.getBranches(code).get(0);
                part.addOutEdge(g = makeCFG(part, path, code, ip, lastIp, allBlocks, refs, visited2));
                g.addInEdge(part);
                
                break;
            } else if (ins.isBranch()) {
                part.endAddress = ip;

                
                allBlocks.add(part);
                List<Integer> branches = ins.getBranches(code);
                if(branches.size()==2){
                        part.setType(BBType.TWOWAY);
                    }else{
                        part.setType(BBType.NWAY);
                    }
                for (int i = 0; i < branches.size(); i++) {
                    part.addOutEdge(g = makeCFG(part, path.sub(i, ip), code, branches.get(i), ip, allBlocks, refs, visited2));
                    g.addInEdge(part);                    
                }
                break;
            }
            ip++;
        }
        if ((part.endAddress == -1) && (ip >= code.size())) {
            if (part.startAddress == code.size()) {
                part.endAddress = code.size();
                part.setType(BBType.RET);
                allBlocks.add(part);
            } else {
                part.endAddress = ip - 1;
                for (BasicBlock p : allBlocks) {
                    if (p.startAddress == ip) {
                        p.addInEdge(part);
                        part.addOutEdge(p);
                        allBlocks.add(part);
                        return ret;
                    }
                }
                part.setType(BBType.RET);
                BasicBlock gp = new BasicBlock();
                gp.startAddress=ip;
                gp.endAddress=ip;
                allBlocks.add(gp);
                gp.addInEdge(part);
                part.addOutEdge(gp);
                allBlocks.add(part);
            }
        }
        return ret;
    }
    /**
     * String used to indent line when converting to string
     */
    public static final String INDENTOPEN = "INDENTOPEN";
    /**
     * String used to unindent line when converting to string
     */
    public static final String INDENTCLOSE = "INDENTCLOSE";
    private static final String INDENT_STRING = "   ";

    private static String tabString(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            ret += INDENT_STRING;
        }
        return ret;
    }

    /**
     * Converts list of TreeItems to string
     *
     * @param tree List of TreeItem
     * @return String
     */
    public static String graphToString(List<GraphTargetItem> tree, Object... localData) {
        StringBuilder ret = new StringBuilder();
        List<Object> localDataList = new ArrayList<>();
        for (Object o : localData) {
            localDataList.add(o);
        }
        for (GraphTargetItem ti : tree) {
            if (!ti.isEmpty()) {
                ret.append(ti.toStringSemicoloned(localDataList));
                ret.append("\r\n");
            }
        }
        String parts[] = ret.toString().split("\r\n");
        ret = new StringBuilder();

        String labelPattern = "loop(switch)?[0-9]*:";
        try {
            Stack<String> loopStack = new Stack<>();
            for (int p = 0; p < parts.length; p++) {
                String stripped = Highlighting.stripHilights(parts[p]);
                if (stripped.matches(labelPattern)) {
                    loopStack.add(stripped.substring(0, stripped.length() - 1));
                }
                if (stripped.startsWith("break ")) {
                    if (stripped.equals("break " + loopStack.peek().replace("switch", "") + ";")) {
                        parts[p] = parts[p].replace(" " + loopStack.peek().replace("switch", ""), "");
                    }
                }
                if (stripped.startsWith("continue ")) {
                    if (loopStack.size() > 0) {
                        int pos = loopStack.size() - 1;
                        String loopname = "";
                        do {
                            loopname = loopStack.get(pos);
                            pos--;
                        } while ((pos >= 0) && (loopname.startsWith("loopswitch")));
                        if (stripped.equals("continue " + loopname + ";")) {
                            parts[p] = parts[p].replace(" " + loopname, "");
                        }
                    }
                }
                if (stripped.startsWith(":")) {
                    loopStack.pop();
                }
            }
        } catch (Exception ex) {
        }

        int level = 0;
        for (int p = 0; p < parts.length; p++) {
            String strippedP = Highlighting.stripHilights(parts[p]).trim();
            if (strippedP.matches(labelPattern)) {//endsWith(":") && (!strippedP.startsWith("case ")) && (!strippedP.equals("default:"))) {
                String loopname = strippedP.substring(0, strippedP.length() - 1);
                boolean dorefer = false;
                for (int q = p + 1; q < parts.length; q++) {
                    String strippedQ = Highlighting.stripHilights(parts[q]).trim();
                    if (strippedQ.equals("break " + loopname.replace("switch", "") + ";")) {
                        dorefer = true;
                        break;
                    }
                    if (strippedQ.equals("continue " + loopname + ";")) {
                        dorefer = true;
                        break;
                    }
                    if (strippedQ.equals(":" + loopname)) {
                        break;
                    }
                }
                if (!dorefer) {
                    continue;
                }
            }
            if (strippedP.startsWith(":")) {
                continue;
            }
            if (Highlighting.stripHilights(parts[p]).equals(INDENTOPEN)) {
                level++;
                continue;
            }
            if (Highlighting.stripHilights(parts[p]).equals(INDENTCLOSE)) {
                level--;
                continue;
            }
            if (Highlighting.stripHilights(parts[p]).equals("}")) {
                level--;
            }
            if (Highlighting.stripHilights(parts[p]).equals("};")) {
                level--;
            }
            ret.append(tabString(level));
            ret.append(parts[p]);
            ret.append("\r\n");
            if (Highlighting.stripHilights(parts[p]).equals("{")) {
                level++;
            }
        }
        return ret.toString();
    }

    public List<Object> prepareBranchLocalData(List<Object> localData) {
        return localData;
    }
    
}
