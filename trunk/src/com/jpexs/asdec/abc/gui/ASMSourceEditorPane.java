/*
 *  Copyright (C) 2010 JPEXS
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

import com.jpexs.asdec.Main;
import com.jpexs.asdec.abc.ABC;
import com.jpexs.asdec.abc.avm2.AVM2Code;
import com.jpexs.asdec.abc.avm2.ConstantPool;
import com.jpexs.asdec.abc.avm2.ConvertException;
import com.jpexs.asdec.abc.avm2.flowgraph.Graph;
import com.jpexs.asdec.abc.avm2.parser.ASM3Parser;
import com.jpexs.asdec.abc.avm2.parser.ParseException;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;


public class ASMSourceEditorPane extends JEditorPane {

    public ABC abc;
    public int bodyIndex;

    public ASMSourceEditorPane() {

    }

    public void setBodyIndex(int bodyIndex, ABC abc) {
        this.bodyIndex = bodyIndex;
        this.abc = abc;
        setText(abc.bodies[bodyIndex].code.toASMSource(abc.constants));
    }

    public void graph(){
        (new GraphFrame(new Graph(abc.bodies[bodyIndex].code))).setVisible(true);
    }

    public void save(ConstantPool constants) {
        try {
            AVM2Code acode = ASM3Parser.parse(new ByteArrayInputStream(getText().getBytes()), constants, new DialogMissingSymbolHandler());
            abc.bodies[bodyIndex].code = acode;
            Main.abcMainFrame.decompiledTextArea.reloadClass();
            Main.abcMainFrame.decompiledTextArea.gotoLastTrait();
        } catch (IOException ex) {
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, (ex.text + " on line " + ex.line));
            selectLine((int) ex.line);
            return;
        }
        JOptionPane.showMessageDialog(this, ("Code Saved"));
    }

    public void verify(ConstantPool constants, ABC abc) {
        try {
            AVM2Code acode = ASM3Parser.parse(new ByteArrayInputStream(getText().getBytes()), constants, new DialogMissingSymbolHandler());
            acode.clearSecureSWF(abc.constants, abc.bodies[bodyIndex]);
            setText(acode.toASMSource(constants));


            //Main.mainFrame.decompiledTextArea.setBody(mb, abc);
        } catch (IOException ex) {
        } catch (ConvertException ex) {
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, (ex.text + " on line " + ex.line));
            selectLine((int) ex.line);
            return;
        }
        JOptionPane.showMessageDialog(this, ("Code OK"));
    }

    public void selectInstruction(int pos) {
        String text = getText();
        int lineCnt = 1;
        int lineStart = 0;
        int lineEnd = -1;
        int instrCount = 0;
        int dot = -2;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                if (!((i > 0) && (text.charAt(i - 1) == ':')))
                    instrCount++;
                lineCnt++;
                if (instrCount == pos) {
                    lineStart = i;
                    dot = lineCnt;
                }
                if (lineCnt == dot + 1) {
                    lineEnd = i;
                    break;
                }
            }
        }
        if (lineCnt == -1) {
            lineEnd = text.length() - 1;
        }
        select(lineStart, lineEnd);
        requestFocus();
    }

    public void selectLine(int line) {
        String text = getText();
        int lineCnt = 1;
        int lineStart = 0;
        int lineEnd = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lineCnt++;
                if (lineCnt == line) {
                    lineStart = i;
                }
                if (lineCnt == line + 1) {
                    lineEnd = i;
                }
            }
        }
        if (lineCnt == -1) {
            lineEnd = text.length() - 1;
        }
        select(lineStart, lineEnd);
        requestFocus();
    }
}
