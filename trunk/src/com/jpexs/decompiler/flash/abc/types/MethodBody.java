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
package com.jpexs.decompiler.flash.abc.types;

import com.jpexs.decompiler.flash.Configuration;
import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.avm2.AVM2Code;
import com.jpexs.decompiler.flash.abc.avm2.CodeStats;
import com.jpexs.decompiler.flash.abc.avm2.ConstantPool;
import com.jpexs.decompiler.flash.abc.types.traits.Trait;
import com.jpexs.decompiler.flash.abc.types.traits.Traits;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.HilightedTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.graph.ExportMode;
import com.jpexs.decompiler.graph.Graph;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.model.LocalData;
import com.jpexs.helpers.Helper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodBody implements Cloneable, Serializable {

    boolean debugMode = false;
    public int method_info;
    public int max_stack;
    public int max_regs;
    public int init_scope_depth;
    public int max_scope_depth;
    public byte[] codeBytes;
    public AVM2Code code;
    public ABCException[] exceptions = new ABCException[0];
    public Traits traits = new Traits();
    public transient List<GraphTargetItem> convertedItems;
    public transient Exception convertException;
    
    public List<Integer> getExceptionEntries() {
        List<Integer> ret = new ArrayList<>();
        for (ABCException e : exceptions) {
            ret.add(code.adr2pos(e.start));
            ret.add(code.adr2pos(e.end));
            ret.add(code.adr2pos(e.target));
        }
        return ret;
    }

    @Override
    public String toString() {
        String s = "";
        s += "method_info=" + method_info + " max_stack=" + max_stack + " max_regs=" + max_regs + " scope_depth=" + init_scope_depth + " max_scope=" + max_scope_depth;
        s += "\r\nCode:\r\n" + code.toString();
        return s;
    }

    public int removeDeadCode(ConstantPool constants, Trait trait, MethodInfo info) {
        return code.removeDeadCode(constants, trait, info, this);
    }

    public void restoreControlFlow(ConstantPool constants, Trait trait, MethodInfo info) {
        code.restoreControlFlow(constants, trait, info, this);
    }

    public int removeTraps(ConstantPool constants, ABC abc, Trait trait, int scriptIndex, int classIndex, boolean isStatic, String path) throws InterruptedException {
        return code.removeTraps(constants, trait, abc.method_info[method_info], this, abc, scriptIndex, classIndex, isStatic, path);
    }

    public HashMap<Integer, String> getLocalRegNames(ABC abc) {
        HashMap<Integer, String> ret = new HashMap<>();
        for (int i = 1; i <= abc.method_info[this.method_info].param_types.length; i++) {
            String paramName = "param" + i;
            if (abc.method_info[this.method_info].flagHas_paramnames() && Configuration.PARAM_NAMES_ENABLE) {
                paramName = abc.constants.constant_string[abc.method_info[this.method_info].paramNames[i - 1]];
            }
            ret.put(i, paramName);
        }
        int pos = abc.method_info[this.method_info].param_types.length + 1;
        if (abc.method_info[this.method_info].flagNeed_arguments()) {
            ret.put(pos, "arguments");
            pos++;
        }
        if (abc.method_info[this.method_info].flagNeed_rest()) {
            ret.put(pos, "rest");
            pos++;
        }

        HashMap<Integer, String> debugRegNames = code.getLocalRegNamesFromDebug(abc);
        for (int k : debugRegNames.keySet()) {
            ret.put(k, debugRegNames.get(k));
        }
        return ret;
    }

    public HilightedTextWriter toString(final String path, ExportMode exportMode, final boolean isStatic, final int scriptIndex, final int classIndex, final ABC abc, final Trait trait, final ConstantPool constants, final MethodInfo[] method_info, final Stack<GraphTargetItem> scopeStack, final boolean isStaticInitializer, final List<String> fullyQualifiedNames, final Traits initTraits) {
        convert(path, exportMode, isStatic, scriptIndex, classIndex, abc, trait, constants, method_info, scopeStack, isStaticInitializer, fullyQualifiedNames, initTraits, true);
        HilightedTextWriter writer = new HilightedTextWriter(false);
        toString(path, exportMode, isStatic, scriptIndex, classIndex, abc, trait, constants, method_info, scopeStack, isStaticInitializer, writer, fullyQualifiedNames, initTraits);
        return writer;
    }
    
    public void convert(final String path, ExportMode exportMode, final boolean isStatic, final int scriptIndex, final int classIndex, final ABC abc, final Trait trait, final ConstantPool constants, final MethodInfo[] method_info, final Stack<GraphTargetItem> scopeStack, final boolean isStaticInitializer, final List<String> fullyQualifiedNames, final Traits initTraits, boolean firstLevel) {
        if (debugMode) {
            System.err.println("Decompiling " + path);
        }
        if (exportMode == ExportMode.SOURCE) {
            int timeout = Configuration.getConfig("decompilationTimeoutSingleMethod");
            try {
                Callable<Void> callable = new Callable<Void>() {
                    @Override
                    public Void call() throws InterruptedException {
                        MethodBody converted = convertMethodBody(path, isStatic, scriptIndex, classIndex, abc, trait, constants, method_info, scopeStack, isStaticInitializer, fullyQualifiedNames, initTraits);
                        HashMap<Integer, String> localRegNames = getLocalRegNames(abc);
                        convertedItems = converted.code.toGraphTargetItems(path, isStatic, scriptIndex, classIndex, abc, constants, method_info, converted, localRegNames, scopeStack, isStaticInitializer, fullyQualifiedNames, initTraits, Graph.SOP_USE_STATIC, new HashMap<Integer, Integer>(), converted.code.visitCode(converted));
                        Graph.graphToString(convertedItems, new NulWriter(), LocalData.create(constants, localRegNames, fullyQualifiedNames));
                        return null;
                    }
                };
                if (firstLevel) {
                    Helper.timedCall(callable, timeout, TimeUnit.SECONDS);
                } else {
                    callable.call();
                }
            } catch (Exception ex) {
                Logger.getLogger(MethodBody.class.getName()).log(Level.SEVERE, "Decompilation error", ex);
                convertException = ex;
                if (ex instanceof ExecutionException && ex.getCause() instanceof Exception) {
                    convertException = (Exception) ex.getCause();
                }
            }
        }
    }

    public GraphTextWriter toString(final String path, ExportMode exportMode, final boolean isStatic, final int scriptIndex, final int classIndex, final ABC abc, final Trait trait, final ConstantPool constants, final MethodInfo[] method_info, final Stack<GraphTargetItem> scopeStack, final boolean isStaticInitializer, final GraphTextWriter writer, final List<String> fullyQualifiedNames, final Traits initTraits) {
        if (exportMode != ExportMode.SOURCE) {
            writer.indent();
            code.toASMSource(constants, trait, method_info[this.method_info], this, exportMode, writer);
            writer.unindent();
        } else {
            if (!Configuration.getConfig("decompile", true)) {
                writer.indent();
                writer.startMethod(this.method_info);
                writer.appendNoHilight("//Decompilation skipped");
                writer.endMethod();
                writer.unindent();
                return writer;
            }
            writer.indent();
            int timeout = Configuration.getConfig("decompilationTimeoutSingleMethod");

            if (convertException == null) {
                HashMap<Integer, String> localRegNames = getLocalRegNames(abc);
                writer.startMethod(this.method_info);
                Graph.graphToString(convertedItems, writer, LocalData.create(constants, localRegNames, fullyQualifiedNames));
                writer.endMethod();
            } else if (convertException instanceof TimeoutException) {
                Logger.getLogger(MethodBody.class.getName()).log(Level.SEVERE, "Decompilation error", convertException);
                writer.appendNoHilight("/*").newLine();
                writer.appendNoHilight(" * Decompilation error").newLine();
                writer.appendNoHilight(" * Timeout (" + Helper.formatTimeToText(timeout) + ") was reached").newLine();
                writer.appendNoHilight(" */").newLine();
                writer.appendNoHilight("throw new IllegalOperationError(\"Not decompiled due to timeout\");").newLine();
            } else {
                Logger.getLogger(MethodBody.class.getName()).log(Level.SEVERE, "Decompilation error", convertException);
                writer.appendNoHilight("/*").newLine();
                writer.appendNoHilight(" * Decompilation error").newLine();
                writer.appendNoHilight(" * Code may be obfuscated").newLine();
                writer.appendNoHilight(" * Error type: " + convertException.getClass().getSimpleName()).newLine();
                writer.appendNoHilight(" */").newLine();
                writer.appendNoHilight("throw new IllegalOperationError(\"Not decompiled due to error\");").newLine();
            }
            writer.unindent();
        }
        return writer;
    }

    public MethodBody convertMethodBody(String path, boolean isStatic, int scriptIndex, int classIndex, ABC abc, Trait trait, ConstantPool constants, MethodInfo[] method_info, Stack<GraphTargetItem> scopeStack, boolean isStaticInitializer, List<String> fullyQualifiedNames, Traits initTraits) {
        MethodBody b = (MethodBody) Helper.deepCopy(this);
        AVM2Code deobfuscated = b.code;
        deobfuscated.markMappedOffsets();
        if (Configuration.getConfig("autoDeobfuscate")) {
            try {
                deobfuscated.removeTraps(constants, trait, method_info[this.method_info], b, abc, scriptIndex, classIndex, isStatic, path);
            } catch (Exception | StackOverflowError ex) {
                Logger.getLogger(MethodBody.class.getName()).log(Level.SEVERE, "Error during remove traps in " + path, ex);
            }
        }
        //deobfuscated.restoreControlFlow(constants, b);

        return b;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MethodBody ret = new MethodBody();
        ret.code = code;
        ret.codeBytes = codeBytes;
        ret.exceptions = exceptions;
        ret.max_regs = max_regs;
        ret.max_scope_depth = max_scope_depth;
        ret.max_stack = max_stack;
        ret.method_info = method_info;
        ret.init_scope_depth = init_scope_depth;
        ret.traits = traits; //maybe deep clone
        return ret;
    }

    public boolean autoFillStats(ABC abc) {
        CodeStats stats = code.getStats(abc, this);
        if (stats == null) {
            return false;
        }
        max_stack = stats.maxstack;
        max_scope_depth = init_scope_depth + stats.maxscope;
        max_regs = stats.maxlocal;
        abc.method_info[method_info].setFlagSetsdxns(stats.has_set_dxns);
        abc.method_info[method_info].setFlagNeed_activation(stats.has_activation);
        return true;
    }
}
