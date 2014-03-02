/* The following code was generated by JFlex 1.4.3 on 30.4.13 19:25 */

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
package com.jpexs.decompiler.flash.tags.text;

/**
 * This class is a scanner generated by
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3 on 30.4.13 19:25 from the
 * specification file
 * <tt>D:/Dokumenty/Programovani/JavaSE/FFDec/trunk/src/com/jpexs/decompiler/flash/tags/text/text.flex</tt>
 */
public final class TextLexer {

    /**
     * This character denotes the end of file
     */
    public static final int YYEOF = -1;
    /**
     * initial size of the lookahead buffer
     */
    private static final int ZZ_BUFFERSIZE = 16384;
    /**
     * lexical states
     */
    public static final int YYINITIAL = 0;
    public static final int VALUE = 4;
    public static final int PARAMETER = 2;
    /**
     * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
     * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l at the
     * beginning of a line l is of the form l = 2*k, k a non negative integer
     */
    private static final int ZZ_LEXSTATE[] = {
        0, 0, 1, 1, 2, 2
    };
    /**
     * Translates characters to character classes
     */
    private static final String ZZ_CMAP_PACKED
            = "\12\0\1\15\2\0\1\3\22\0\1\3\1\0\1\13\4\0\1\14"
            + "\10\0\12\1\41\0\1\4\1\5\1\2\1\0\1\1\1\0\1\1"
            + "\1\6\3\1\1\11\7\1\1\10\3\1\1\12\1\1\1\7\6\1"
            + "\uff85\0";
    /**
     * Translates characters to character classes
     */
    private static final char[] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);
    /**
     * Translates DFA states to action switch labels.
     */
    private static final int[] ZZ_ACTION = zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0
            = "\3\0\1\1\1\2\1\1\1\3\1\4\1\5\1\3"
            + "\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"
            + "\1\16\1\17\1\20\1\21";

    private static int[] zzUnpackAction() {
        int[] result = new int[22];
        int offset = 0;
        offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int i = 0;       /* index in packed string  */

        int j = offset;  /* index in unpacked array */

        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    /**
     * Translates a state to a row index in the transition table
     */
    private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0
            = "\0\0\0\16\0\34\0\52\0\52\0\70\0\52\0\106"
            + "\0\52\0\124\0\142\0\52\0\52\0\52\0\52\0\52"
            + "\0\52\0\52\0\52\0\52\0\52\0\52";

    private static int[] zzUnpackRowMap() {
        int[] result = new int[22];
        int offset = 0;
        offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result) {
        int i = 0;  /* index in packed string  */

        int j = offset;  /* index in unpacked array */

        int l = packed.length();
        while (i < l) {
            int high = packed.charAt(i++) << 16;
            result[j++] = high | packed.charAt(i++);
        }
        return j;
    }
    /**
     * The transition table of the DFA
     */
    private static final int[] ZZ_TRANS = zzUnpackTrans();
    private static final String ZZ_TRANS_PACKED_0
            = "\4\4\1\5\1\6\7\4\2\7\1\10\1\11\1\12"
            + "\2\7\5\10\2\7\1\12\2\13\1\11\1\12\11\13"
            + "\1\12\16\0\2\14\1\15\1\14\1\16\1\17\1\20"
            + "\1\21\1\22\1\23\1\24\1\25\1\26\2\0\1\10"
            + "\4\0\5\10\6\0\1\12\11\0\1\12\2\13\2\0"
            + "\11\13\1\0";

    private static int[] zzUnpackTrans() {
        int[] result = new int[112];
        int offset = 0;
        offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackTrans(String packed, int offset, int[] result) {
        int i = 0;       /* index in packed string  */

        int j = offset;  /* index in unpacked array */

        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            value--;
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    /* error codes */
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;

    /* error messages for the codes above */
    private static final String ZZ_ERROR_MSG[] = {
        "Unkown internal scanner error",
        "Error: could not match input",
        "Error: pushback value was too large"
    };
    /**
     * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
     */
    private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0
            = "\3\0\2\11\1\1\1\11\1\1\1\11\2\1\13\11";

    private static int[] zzUnpackAttribute() {
        int[] result = new int[22];
        int offset = 0;
        offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result) {
        int i = 0;       /* index in packed string  */

        int j = offset;  /* index in unpacked array */

        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    /**
     * the input device
     */
    private java.io.Reader zzReader;
    /**
     * the current state of the DFA
     */
    private int zzState;
    /**
     * the current lexical state
     */
    private int zzLexicalState = YYINITIAL;
    /**
     * this buffer contains the current text to be matched and is the source of
     * the yytext() string
     */
    private char zzBuffer[] = new char[ZZ_BUFFERSIZE];
    /**
     * the textposition at the last accepting state
     */
    private int zzMarkedPos;
    /**
     * the current text position in the buffer
     */
    private int zzCurrentPos;
    /**
     * startRead marks the beginning of the yytext() string in the buffer
     */
    private int zzStartRead;
    /**
     * endRead marks the last character in the buffer, that has been read from
     * input
     */
    private int zzEndRead;
    /**
     * number of newlines encountered up to the start of the matched text
     */
    private int yyline;
    /**
     * the number of characters up to the start of the matched text
     */
    private int yychar;
    /**
     * the number of characters from the last newline up to the start of the
     * matched text
     */
    private int yycolumn;
    /**
     * zzAtBOL == true <=> the scanner is currently at the beginning of a line
     */
    private boolean zzAtBOL = true;
    /**
     * zzAtEOF == true <=> the scanner is at the EOF
     */
    private boolean zzAtEOF;
    /**
     * denotes if the user-EOF-code has already been executed
     */
    private boolean zzEOFDone;

    /* user code: */
    StringBuffer string = null;
    boolean finish = false;
    String parameterName = null;

    /**
     * Create an empty lexer, yyrset will be called later to reset and assign
     * the reader
     */
    public TextLexer() {
    }

    public int yychar() {
        return yychar;
    }

    public int yyline() {
        return yyline + 1;
    }

    /**
     * Creates a new scanner There is also a java.io.InputStream version of this
     * constructor.
     *
     * @param in the java.io.Reader to read input from.
     */
    public TextLexer(java.io.Reader in) {
        this.zzReader = in;
    }

    /**
     * Creates a new scanner. There is also java.io.Reader version of this
     * constructor.
     *
     * @param in the java.io.Inputstream to read input from.
     */
    public TextLexer(java.io.InputStream in) {
        this(new java.io.InputStreamReader(in));
    }

    /**
     * Unpacks the compressed character translation table.
     *
     * @param packed the packed character translation table
     * @return the unpacked character translation table
     */
    private static char[] zzUnpackCMap(String packed) {
        char[] map = new char[0x10000];
        int i = 0;  /* index in packed string  */

        int j = 0;  /* index in unpacked array */

        while (i < 62) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }

    /**
     * Refills the input buffer.
     *
     * @return      <code>false</code>, iff there was new input.
     *
     * @exception java.io.IOException if any I/O-Error occurs
     */
    private boolean zzRefill() throws java.io.IOException {

        /* first: make room (if you can) */
        if (zzStartRead > 0) {
            System.arraycopy(zzBuffer, zzStartRead,
                    zzBuffer, 0,
                    zzEndRead - zzStartRead);

            /* translate stored positions */
            zzEndRead -= zzStartRead;
            zzCurrentPos -= zzStartRead;
            zzMarkedPos -= zzStartRead;
            zzStartRead = 0;
        }

        /* is the buffer big enough? */
        if (zzCurrentPos >= zzBuffer.length) {
            /* if not: blow it up */
            char newBuffer[] = new char[zzCurrentPos * 2];
            System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
            zzBuffer = newBuffer;
        }

        /* finally: fill the buffer with new input */
        int numRead = zzReader.read(zzBuffer, zzEndRead,
                zzBuffer.length - zzEndRead);

        if (numRead > 0) {
            zzEndRead += numRead;
            return false;
        }
        // unlikely but not impossible: read 0 characters, but not at end of stream    
        if (numRead == 0) {
            int c = zzReader.read();
            if (c == -1) {
                return true;
            } else {
                zzBuffer[zzEndRead++] = (char) c;
                return false;
            }
        }

        // numRead < 0
        return true;
    }

    /**
     * Closes the input stream.
     *
     * @throws java.io.IOException
     */
    public final void yyclose() throws java.io.IOException {
        zzAtEOF = true;            /* indicate end of file */

        zzEndRead = zzStartRead;  /* invalidate buffer    */

        if (zzReader != null) {
            zzReader.close();
        }
    }

    /**
     * Resets the scanner to read from a new input stream. Does not close the
     * old reader.
     *
     * All internal variables are reset, the old input stream
     * <b>cannot</b> be reused (internal buffer is discarded and lost). Lexical
     * state is set to <tt>ZZ_INITIAL</tt>.
     *
     * @param reader the new input stream
     */
    public final void yyreset(java.io.Reader reader) {
        zzReader = reader;
        zzAtBOL = true;
        zzAtEOF = false;
        zzEOFDone = false;
        zzEndRead = zzStartRead = 0;
        zzCurrentPos = zzMarkedPos = 0;
        yyline = yychar = yycolumn = 0;
        zzLexicalState = YYINITIAL;
    }

    /**
     * Returns the current lexical state.
     *
     * @return
     */
    public final int yystate() {
        return zzLexicalState;
    }

    /**
     * Enters a new lexical state
     *
     * @param newState the new lexical state
     */
    public final void yybegin(int newState) {
        zzLexicalState = newState;
    }

    /**
     * Returns the text matched by the current regular expression.
     *
     * @return
     */
    public final String yytext() {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    /**
     * Returns the character at position <tt>pos</tt> from the matched text.
     *
     * It is equivalent to yytext().charAt(pos), but faster
     *
     * @param pos the position of the character to fetch. A value from 0 to
     * yylength()-1.
     *
     * @return the character at position pos
     */
    public final char yycharat(int pos) {
        return zzBuffer[zzStartRead + pos];
    }

    /**
     * Returns the length of the matched text region.
     *
     * @return
     */
    public final int yylength() {
        return zzMarkedPos - zzStartRead;
    }

    /**
     * Reports an error that occured while scanning.
     *
     * In a wellformed scanner (no or only correct usage of yypushback(int) and
     * a match-all fallback rule) this method will only be called with things
     * that "Can't Possibly Happen". If this method is called, something is
     * seriously wrong (e.g. a JFlex bug producing a faulty scanner etc.).
     *
     * Usual syntax/scanner level error handling should be done in error
     * fallback rules.
     *
     * @param errorCode the code of the errormessage to display
     */
    private void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        } catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
        }

        throw new Error(message);
    }

    /**
     * Pushes the specified amount of characters back into the input stream.
     *
     * They will be read again by then next call of the scanning method
     *
     * @param number the number of characters to be read again. This number must
     * not be greater than yylength()!
     */
    public void yypushback(int number) {
        if (number > yylength()) {
            zzScanError(ZZ_PUSHBACK_2BIG);
        }

        zzMarkedPos -= number;
    }

    /**
     * Resumes scanning until the next regular expression is matched, the end of
     * input is encountered or an I/O-Error occurs.
     *
     * @return the next token
     * @exception java.io.IOException if any I/O-Error occurs
     * @throws com.jpexs.decompiler.flash.tags.text.ParseException
     */
    public ParsedSymbol yylex() throws java.io.IOException, ParseException {
        int zzInput;
        int zzAction;

        // cached fields:
        int zzCurrentPosL;
        int zzMarkedPosL;
        int zzEndReadL = zzEndRead;
        char[] zzBufferL = zzBuffer;
        char[] zzCMapL = ZZ_CMAP;

        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;

        while (true) {
            zzMarkedPosL = zzMarkedPos;

            yychar += zzMarkedPosL - zzStartRead;

            boolean zzR = false;
            for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                    zzCurrentPosL++) {
                switch (zzBufferL[zzCurrentPosL]) {
                    case '\u000B':
                    case '\u000C':
                    case '\u0085':
                    case '\u2028':
                    case '\u2029':
                        yyline++;
                        yycolumn = 0;
                        zzR = false;
                        break;
                    case '\r':
                        yyline++;
                        yycolumn = 0;
                        zzR = true;
                        break;
                    case '\n':
                        if (zzR) {
                            zzR = false;
                        } else {
                            yyline++;
                            yycolumn = 0;
                        }
                        break;
                    default:
                        zzR = false;
                        yycolumn++;
                }
            }

            if (zzR) {
                // peek one character ahead if it is \n (if we have counted one line too much)
                boolean zzPeek;
                if (zzMarkedPosL < zzEndReadL) {
                    zzPeek = zzBufferL[zzMarkedPosL] == '\n';
                } else if (zzAtEOF) {
                    zzPeek = false;
                } else {
                    boolean eof = zzRefill();
                    zzEndReadL = zzEndRead;
                    zzMarkedPosL = zzMarkedPos;
                    zzBufferL = zzBuffer;
                    if (eof) {
                        zzPeek = false;
                    } else {
                        zzPeek = zzBufferL[zzMarkedPosL] == '\n';
                    }
                }
                if (zzPeek) {
                    yyline--;
                }
            }
            zzAction = -1;

            zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

            zzState = ZZ_LEXSTATE[zzLexicalState];

            zzForAction:
            {
                while (true) {

                    if (zzCurrentPosL < zzEndReadL) {
                        zzInput = zzBufferL[zzCurrentPosL++];
                    } else if (zzAtEOF) {
                        zzInput = YYEOF;
                        break zzForAction;
                    } else {
                        // store back cached positions
                        zzCurrentPos = zzCurrentPosL;
                        zzMarkedPos = zzMarkedPosL;
                        boolean eof = zzRefill();
                        // get translated positions and possibly new buffer
                        zzCurrentPosL = zzCurrentPos;
                        zzMarkedPosL = zzMarkedPos;
                        zzBufferL = zzBuffer;
                        zzEndReadL = zzEndRead;
                        if (eof) {
                            zzInput = YYEOF;
                            break zzForAction;
                        } else {
                            zzInput = zzBufferL[zzCurrentPosL++];
                        }
                    }
                    int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput]];
                    if (zzNext == -1) {
                        break zzForAction;
                    }
                    zzState = zzNext;

                    int zzAttributes = zzAttrL[zzState];
                    if ((zzAttributes & 1) == 1) {
                        zzAction = zzState;
                        zzMarkedPosL = zzCurrentPosL;
                        if ((zzAttributes & 8) == 8) {
                            break zzForAction;
                        }
                    }

                }
            }

            // store back cached position
            zzMarkedPos = zzMarkedPosL;

            switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
                case 1: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append(yytext());
                }
                case 18:
                    break;
                case 7: {
                    throw new ParseException("Illegal escape sequence \"" + yytext() + "\"", yyline + 1);
                }
                case 19:
                    break;
                case 13: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\n');
                }
                case 20:
                    break;
                case 2: {
                    yybegin(PARAMETER);
                    if (string != null) {
                        String ret = string.toString();
                        string = null;
                        return new ParsedSymbol(SymbolType.TEXT, ret.toString());
                    }
                }
                case 21:
                    break;
                case 11: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\b');
                }
                case 22:
                    break;
                case 17: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\'');
                }
                case 23:
                    break;
                case 4: {
                    parameterName = yytext();
                    yybegin(VALUE);
                }
                case 24:
                    break;
                case 6: {
                    yybegin(PARAMETER);
                    return new ParsedSymbol(SymbolType.PARAMETER, new Object[]{parameterName, yytext()});
                }
                case 25:
                    break;
                case 8: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append(']');
                }
                case 26:
                    break;
                case 12: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\t');
                }
                case 27:
                    break;
                case 9: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('[');
                }
                case 28:
                    break;
                case 10: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\\');
                }
                case 29:
                    break;
                case 16: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\"');
                }
                case 30:
                    break;
                case 15: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\r');
                }
                case 31:
                    break;
                case 14: {
                    if (string == null) {
                        string = new StringBuffer();
                    }
                    string.append('\f');
                }
                case 32:
                    break;
                case 5: {
                    yybegin(YYINITIAL);
                }
                case 33:
                    break;
                case 3: {
                }
                case 34:
                    break;
                default:
                    if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
                        zzAtEOF = true;
                        switch (zzLexicalState) {
                            case YYINITIAL: {
                                if (finish) {
                                    return null;
                                } else {
                                    finish = true;
                                    return new ParsedSymbol(SymbolType.TEXT, string.toString());
                                }
                            }
                            case 23:
                                break;
                            default: {
                                return null;
                            }
                        }
                    } else {
                        zzScanError(ZZ_NO_MATCH);
                    }
            }
        }
    }
}
