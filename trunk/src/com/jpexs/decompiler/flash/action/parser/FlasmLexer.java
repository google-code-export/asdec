/* The following code was generated by JFlex 1.4.3 on 9.3.13 17:48 */

/* Flash assembler language lexer specification */
package com.jpexs.decompiler.flash.action.parser;

import com.jpexs.decompiler.flash.action.swf4.ConstantIndex;
import com.jpexs.decompiler.flash.action.swf4.Null;
import com.jpexs.decompiler.flash.action.swf4.RegisterNumber;
import com.jpexs.decompiler.flash.action.swf4.Undefined;

/**
 * This class is a scanner generated by <a href="http://www.jflex.de/">JFlex</a>
 * 1.4.3 on 9.3.13 17:48 from the specification file
 * <tt>D:/Dokumenty/Programovani/JavaSE/FFDec/trunk/src/com/jpexs/decompiler/flash/action/parser/flasm.flex</tt>
 */
public final class FlasmLexer {

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
   public static final int STRING = 2;
   public static final int YYINITIAL = 0;
   public static final int PARAMETERS = 4;
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
   private static final String ZZ_CMAP_PACKED =
           "\11\6\1\4\1\2\1\0\1\4\1\1\16\6\4\0\1\4\1\0"
           + "\1\42\1\0\1\5\2\0\1\44\3\0\1\34\1\0\1\31\1\32"
           + "\1\0\1\30\3\45\4\35\2\11\1\12\1\3\5\0\4\7\1\33"
           + "\25\7\1\0\1\36\2\0\1\10\1\0\1\22\1\43\1\40\1\26"
           + "\1\20\1\21\1\37\1\7\1\27\2\7\1\23\1\7\1\25\1\41"
           + "\2\7\1\16\1\24\1\15\1\17\5\7\1\13\1\0\1\14\1\0"
           + "\41\6\2\0\4\5\4\0\1\5\2\0\1\6\7\0\1\5\4\0"
           + "\1\5\5\0\27\5\1\0\37\5\1\0\u013f\5\31\0\162\5\4\0"
           + "\14\5\16\0\5\5\11\0\1\5\21\0\130\6\5\0\23\6\12\0"
           + "\1\5\13\0\1\5\1\0\3\5\1\0\1\5\1\0\24\5\1\0"
           + "\54\5\1\0\46\5\1\0\5\5\4\0\202\5\1\0\4\6\3\0"
           + "\105\5\1\0\46\5\2\0\2\5\6\0\20\5\41\0\46\5\2\0"
           + "\1\5\7\0\47\5\11\0\21\6\1\0\27\6\1\0\3\6\1\0"
           + "\1\6\1\0\2\6\1\0\1\6\13\0\33\5\5\0\3\5\15\0"
           + "\4\6\14\0\6\6\13\0\32\5\5\0\13\5\16\6\7\0\12\6"
           + "\4\0\2\5\1\6\143\5\1\0\1\5\10\6\1\0\6\6\2\5"
           + "\2\6\1\0\4\6\2\5\12\6\3\5\2\0\1\5\17\0\1\6"
           + "\1\5\1\6\36\5\33\6\2\0\3\5\60\0\46\5\13\6\1\5"
           + "\u014f\0\3\6\66\5\2\0\1\6\1\5\20\6\2\0\1\5\4\6"
           + "\3\0\12\5\2\6\2\0\12\6\21\0\3\6\1\0\10\5\2\0"
           + "\2\5\2\0\26\5\1\0\7\5\1\0\1\5\3\0\4\5\2\0"
           + "\1\6\1\5\7\6\2\0\2\6\2\0\3\6\11\0\1\6\4\0"
           + "\2\5\1\0\3\5\2\6\2\0\12\6\4\5\15\0\3\6\1\0"
           + "\6\5\4\0\2\5\2\0\26\5\1\0\7\5\1\0\2\5\1\0"
           + "\2\5\1\0\2\5\2\0\1\6\1\0\5\6\4\0\2\6\2\0"
           + "\3\6\13\0\4\5\1\0\1\5\7\0\14\6\3\5\14\0\3\6"
           + "\1\0\11\5\1\0\3\5\1\0\26\5\1\0\7\5\1\0\2\5"
           + "\1\0\5\5\2\0\1\6\1\5\10\6\1\0\3\6\1\0\3\6"
           + "\2\0\1\5\17\0\2\5\2\6\2\0\12\6\1\0\1\5\17\0"
           + "\3\6\1\0\10\5\2\0\2\5\2\0\26\5\1\0\7\5\1\0"
           + "\2\5\1\0\5\5\2\0\1\6\1\5\6\6\3\0\2\6\2\0"
           + "\3\6\10\0\2\6\4\0\2\5\1\0\3\5\4\0\12\6\1\0"
           + "\1\5\20\0\1\6\1\5\1\0\6\5\3\0\3\5\1\0\4\5"
           + "\3\0\2\5\1\0\1\5\1\0\2\5\3\0\2\5\3\0\3\5"
           + "\3\0\10\5\1\0\3\5\4\0\5\6\3\0\3\6\1\0\4\6"
           + "\11\0\1\6\17\0\11\6\11\0\1\5\7\0\3\6\1\0\10\5"
           + "\1\0\3\5\1\0\27\5\1\0\12\5\1\0\5\5\4\0\7\6"
           + "\1\0\3\6\1\0\4\6\7\0\2\6\11\0\2\5\4\0\12\6"
           + "\22\0\2\6\1\0\10\5\1\0\3\5\1\0\27\5\1\0\12\5"
           + "\1\0\5\5\2\0\1\6\1\5\7\6\1\0\3\6\1\0\4\6"
           + "\7\0\2\6\7\0\1\5\1\0\2\5\4\0\12\6\22\0\2\6"
           + "\1\0\10\5\1\0\3\5\1\0\27\5\1\0\20\5\4\0\6\6"
           + "\2\0\3\6\1\0\4\6\11\0\1\6\10\0\2\5\4\0\12\6"
           + "\22\0\2\6\1\0\22\5\3\0\30\5\1\0\11\5\1\0\1\5"
           + "\2\0\7\5\3\0\1\6\4\0\6\6\1\0\1\6\1\0\10\6"
           + "\22\0\2\6\15\0\60\5\1\6\2\5\7\6\4\0\10\5\10\6"
           + "\1\0\12\6\47\0\2\5\1\0\1\5\2\0\2\5\1\0\1\5"
           + "\2\0\1\5\6\0\4\5\1\0\7\5\1\0\3\5\1\0\1\5"
           + "\1\0\1\5\2\0\2\5\1\0\4\5\1\6\2\5\6\6\1\0"
           + "\2\6\1\5\2\0\5\5\1\0\1\5\1\0\6\6\2\0\12\6"
           + "\2\0\2\5\42\0\1\5\27\0\2\6\6\0\12\6\13\0\1\6"
           + "\1\0\1\6\1\0\1\6\4\0\2\6\10\5\1\0\42\5\6\0"
           + "\24\6\1\0\2\6\4\5\4\0\10\6\1\0\44\6\11\0\1\6"
           + "\71\0\42\5\1\0\5\5\1\0\2\5\1\0\7\6\3\0\4\6"
           + "\6\0\12\6\6\0\6\5\4\6\106\0\46\5\12\0\51\5\7\0"
           + "\132\5\5\0\104\5\5\0\122\5\6\0\7\5\1\0\77\5\1\0"
           + "\1\5\1\0\4\5\2\0\7\5\1\0\1\5\1\0\4\5\2\0"
           + "\47\5\1\0\1\5\1\0\4\5\2\0\37\5\1\0\1\5\1\0"
           + "\4\5\2\0\7\5\1\0\1\5\1\0\4\5\2\0\7\5\1\0"
           + "\7\5\1\0\27\5\1\0\37\5\1\0\1\5\1\0\4\5\2\0"
           + "\7\5\1\0\47\5\1\0\23\5\16\0\11\6\56\0\125\5\14\0"
           + "\u026c\5\2\0\10\5\12\0\32\5\5\0\113\5\3\0\3\5\17\0"
           + "\15\5\1\0\4\5\3\6\13\0\22\5\3\6\13\0\22\5\2\6"
           + "\14\0\15\5\1\0\3\5\1\0\2\6\14\0\64\5\40\6\3\0"
           + "\1\5\3\0\2\5\1\6\2\0\12\6\41\0\3\6\2\0\12\6"
           + "\6\0\130\5\10\0\51\5\1\6\126\0\35\5\3\0\14\6\4\0"
           + "\14\6\12\0\12\6\36\5\2\0\5\5\u038b\0\154\5\224\0\234\5"
           + "\4\0\132\5\6\0\26\5\2\0\6\5\2\0\46\5\2\0\6\5"
           + "\2\0\10\5\1\0\1\5\1\0\1\5\1\0\1\5\1\0\37\5"
           + "\2\0\65\5\1\0\7\5\1\0\1\5\3\0\3\5\1\0\7\5"
           + "\3\0\4\5\2\0\6\5\4\0\15\5\5\0\3\5\1\0\7\5"
           + "\17\0\4\6\32\0\5\6\20\0\2\5\23\0\1\5\13\0\4\6"
           + "\6\0\6\6\1\0\1\5\15\0\1\5\40\0\22\5\36\0\15\6"
           + "\4\0\1\6\3\0\6\6\27\0\1\5\4\0\1\5\2\0\12\5"
           + "\1\0\1\5\3\0\5\5\6\0\1\5\1\0\1\5\1\0\1\5"
           + "\1\0\4\5\1\0\3\5\1\0\7\5\3\0\3\5\5\0\5\5"
           + "\26\0\44\5\u0e81\0\3\5\31\0\11\5\6\6\1\0\5\5\2\0"
           + "\5\5\4\0\126\5\2\0\2\6\2\0\3\5\1\0\137\5\5\0"
           + "\50\5\4\0\136\5\21\0\30\5\70\0\20\5\u0200\0\u19b6\5\112\0"
           + "\u51a6\5\132\0\u048d\5\u0773\0\u2ba4\5\u215c\0\u012e\5\2\0\73\5\225\0"
           + "\7\5\14\0\5\5\5\0\1\5\1\6\12\5\1\0\15\5\1\0"
           + "\5\5\1\0\1\5\1\0\2\5\1\0\2\5\1\0\154\5\41\0"
           + "\u016b\5\22\0\100\5\2\0\66\5\50\0\15\5\3\0\20\6\20\0"
           + "\4\6\17\0\2\5\30\0\3\5\31\0\1\5\6\0\5\5\1\0"
           + "\207\5\2\0\1\6\4\0\1\5\13\0\12\6\7\0\32\5\4\0"
           + "\1\5\1\0\32\5\12\0\132\5\3\0\6\5\2\0\6\5\2\0"
           + "\6\5\2\0\3\5\3\0\2\5\3\0\2\5\22\0\3\6\4\0";
   /**
    * Translates characters to character classes
    */
   private static final char[] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);
   /**
    * Translates DFA states to action switch labels.
    */
   private static final int[] ZZ_ACTION = zzUnpackAction();
   private static final String ZZ_ACTION_PACKED_0 =
           "\3\0\3\1\1\2\1\3\1\4\2\5\1\1\1\6"
           + "\2\7\1\10\1\11\1\12\1\13\5\11\1\12\2\1"
           + "\1\11\1\14\1\0\1\15\1\16\1\17\1\20\1\21"
           + "\1\22\2\23\1\24\1\25\1\26\1\27\1\0\1\30"
           + "\5\11\1\30\2\12\1\11\1\23\1\30\1\0\6\11"
           + "\1\31\3\11\1\32\3\11\1\33\12\11\2\34\1\35"
           + "\2\36";

   private static int[] zzUnpackAction() {
      int[] result = new int[86];
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
   private static final String ZZ_ROWMAP_PACKED_0 =
           "\0\0\0\46\0\114\0\162\0\230\0\276\0\344\0\162"
           + "\0\u010a\0\u0130\0\162\0\u0156\0\162\0\u017c\0\162\0\u01a2"
           + "\0\u01c8\0\u01ee\0\162\0\u0214\0\u023a\0\u0260\0\u0286\0\u02ac"
           + "\0\u02d2\0\u02f8\0\u031e\0\u0344\0\162\0\276\0\162\0\162"
           + "\0\162\0\162\0\162\0\162\0\u036a\0\u0390\0\162\0\162"
           + "\0\162\0\162\0\u03b6\0\u03dc\0\u0402\0\u0428\0\u044e\0\u0474"
           + "\0\u049a\0\u02d2\0\u04c0\0\162\0\u04e6\0\162\0\u050c\0\u050c"
           + "\0\u0532\0\u0558\0\u057e\0\u05a4\0\u05ca\0\u05f0\0\u01c8\0\u0616"
           + "\0\u063c\0\u0662\0\u01c8\0\u0688\0\u06ae\0\u06d4\0\u01c8\0\u06fa"
           + "\0\u0720\0\u0746\0\u076c\0\u0792\0\u07b8\0\u07de\0\u0804\0\u082a"
           + "\0\u0850\0\u0876\0\u01c8\0\u01c8\0\u089c\0\u01c8";

   private static int[] zzUnpackRowMap() {
      int[] result = new int[86];
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
   private static final String ZZ_TRANS_PACKED_0 =
           "\4\4\1\5\1\6\1\4\1\7\1\6\3\4\1\10"
           + "\13\7\3\4\1\7\3\4\3\7\1\4\1\7\2\4"
           + "\1\11\1\12\1\13\33\11\1\14\3\11\1\15\3\11"
           + "\1\4\1\16\1\17\1\20\1\4\1\21\1\4\2\21"
           + "\1\22\1\4\1\23\1\4\1\24\1\25\1\26\1\21"
           + "\1\27\3\21\1\30\2\21\1\31\1\32\1\33\1\21"
           + "\1\4\1\22\1\4\1\21\1\34\1\21\1\35\1\21"
           + "\1\4\1\22\52\0\1\5\46\0\5\36\1\37\2\0"
           + "\14\36\2\0\1\36\1\0\1\36\1\0\3\36\1\0"
           + "\1\36\1\0\1\36\5\0\2\36\3\7\1\37\2\0"
           + "\14\7\2\0\1\7\1\0\1\7\1\0\3\7\1\0"
           + "\1\7\1\0\1\7\1\11\2\0\33\11\1\0\3\11"
           + "\1\0\3\11\2\0\1\13\43\0\2\40\1\0\12\40"
           + "\1\41\1\42\2\40\1\43\3\40\1\44\2\40\1\45"
           + "\4\40\1\46\1\47\3\40\1\50\1\51\1\52\1\45"
           + "\2\0\1\17\43\0\1\20\2\0\43\20\5\0\5\21"
           + "\3\0\14\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\11\0\1\22\6\0\1\53"
           + "\7\0\1\22\1\0\1\54\1\53\1\0\1\22\7\0"
           + "\1\22\5\0\5\21\3\0\1\21\1\55\12\21\2\0"
           + "\1\21\1\0\1\21\1\0\3\21\1\0\1\21\1\0"
           + "\1\21\5\0\5\21\3\0\3\21\1\56\10\21\2\0"
           + "\1\21\1\0\1\21\1\0\3\21\1\0\1\21\1\0"
           + "\1\21\5\0\5\21\3\0\10\21\1\57\3\21\2\0"
           + "\1\21\1\0\1\21\1\0\3\21\1\0\1\21\1\0"
           + "\1\21\5\0\5\21\3\0\5\21\1\60\6\21\2\0"
           + "\1\21\1\0\1\21\1\0\3\21\1\0\1\21\1\0"
           + "\1\21\5\0\5\21\3\0\2\21\1\61\11\21\2\0"
           + "\1\21\1\0\1\21\1\0\3\21\1\0\1\21\1\0"
           + "\1\21\11\0\1\62\6\0\1\53\7\0\1\62\1\0"
           + "\1\54\1\53\1\0\1\62\7\0\1\62\11\0\1\63"
           + "\16\0\1\64\4\0\1\63\7\0\1\63\11\0\1\54"
           + "\16\0\1\54\4\0\1\54\7\0\1\54\5\0\5\21"
           + "\3\0\14\21\2\0\1\21\1\0\1\21\1\0\2\21"
           + "\1\65\1\0\1\21\1\0\1\21\30\0\1\46\4\0"
           + "\1\46\7\0\1\46\30\0\1\66\4\0\1\66\7\0"
           + "\1\66\11\0\1\67\16\0\1\67\1\70\2\0\1\70"
           + "\1\67\7\0\1\67\11\0\1\54\6\0\1\53\7\0"
           + "\1\54\2\0\1\53\1\0\1\54\7\0\1\54\5\0"
           + "\5\21\3\0\2\21\1\71\11\21\2\0\1\21\1\0"
           + "\1\21\1\0\3\21\1\0\1\21\1\0\1\21\5\0"
           + "\5\21\3\0\14\21\2\0\1\21\1\0\1\21\1\0"
           + "\1\72\2\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\11\21\1\73\2\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\6\21\1\74\5\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\6\21\1\75\5\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\11\0\1\63"
           + "\16\0\1\63\4\0\1\63\7\0\1\63\5\0\5\21"
           + "\3\0\10\21\1\76\3\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\11\0\1\67"
           + "\16\0\1\67\4\0\1\67\7\0\1\67\5\0\5\21"
           + "\3\0\3\21\1\77\10\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\12\21\1\100\1\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\3\21\1\101\10\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\7\21\1\102\4\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\6\21\1\103\5\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\7\21\1\104\4\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\7\21\1\105\4\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\4\21\1\106\7\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\3\21\1\107\10\21\2\0\1\21\1\0\1\21"
           + "\1\0\3\21\1\0\1\21\1\0\1\21\5\0\5\21"
           + "\3\0\1\110\13\21\2\0\1\21\1\0\1\21\1\0"
           + "\3\21\1\0\1\21\1\0\1\21\5\0\5\21\3\0"
           + "\1\111\13\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\12\21"
           + "\1\112\1\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\5\21"
           + "\1\113\6\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\3\21"
           + "\1\114\10\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\10\21"
           + "\1\115\3\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\10\21"
           + "\1\116\3\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\1\21"
           + "\1\117\12\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\3\21"
           + "\1\120\10\21\2\0\1\21\1\0\1\21\1\0\3\21"
           + "\1\0\1\21\1\0\1\21\5\0\5\21\3\0\1\121"
           + "\13\21\2\0\1\21\1\0\1\21\1\0\3\21\1\0"
           + "\1\21\1\0\1\21\5\0\4\21\1\122\3\0\13\21"
           + "\1\123\2\0\1\21\1\0\1\122\1\0\3\21\1\0"
           + "\1\21\1\0\1\122\5\0\5\21\3\0\11\21\1\124"
           + "\2\21\2\0\1\21\1\0\1\21\1\0\3\21\1\0"
           + "\1\21\1\0\1\21\5\0\4\21\1\125\3\0\13\21"
           + "\1\126\2\0\1\21\1\0\1\125\1\0\3\21\1\0"
           + "\1\21\1\0\1\125\5\0\4\21\1\122\3\0\13\21"
           + "\1\122\2\0\1\21\1\0\1\122\1\0\3\21\1\0"
           + "\1\21\1\0\1\122\5\0\4\21\1\125\3\0\13\21"
           + "\1\125\2\0\1\21\1\0\1\125\1\0\3\21\1\0"
           + "\1\21\1\0\1\125";

   private static int[] zzUnpackTrans() {
      int[] result = new int[2242];
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
    * ZZ_ATTRIBUTE[aState] contains the attributes of state
    * <code>aState</code>
    */
   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
   private static final String ZZ_ATTRIBUTE_PACKED_0 =
           "\3\0\1\11\3\1\1\11\2\1\1\11\1\1\1\11"
           + "\1\1\1\11\3\1\1\11\11\1\1\11\1\0\6\11"
           + "\2\1\4\11\1\0\10\1\1\11\1\1\1\11\1\1"
           + "\1\0\36\1";

   private static int[] zzUnpackAttribute() {
      int[] result = new int[86];
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
   StringBuffer string = new StringBuffer();

   /**
    * Create an empty lexer, yyrset will be called later to reset and assign the
    * reader
    */
   public FlasmLexer() {
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
   public FlasmLexer(java.io.Reader in) {
      this.zzReader = in;
   }

   /**
    * Creates a new scanner. There is also java.io.Reader version of this
    * constructor.
    *
    * @param in the java.io.Inputstream to read input from.
    */
   public FlasmLexer(java.io.InputStream in) {
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
      while (i < 1740) {
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
    */
   public final void yyclose() throws java.io.IOException {
      zzAtEOF = true;            /* indicate end of file */
      zzEndRead = zzStartRead;  /* invalidate buffer    */

      if (zzReader != null) {
         zzReader.close();
      }
   }

   /**
    * Resets the scanner to read from a new input stream. Does not close the old
    * reader.
    *
    * All internal variables are reset, the old input stream <b>cannot</b> be
    * reused (internal buffer is discarded and lost). Lexical state is set to
    * <tt>ZZ_INITIAL</tt>.
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
    */
   public final int yylength() {
      return zzMarkedPos - zzStartRead;
   }

   /**
    * Reports an error that occured while scanning.
    *
    * In a wellformed scanner (no or only correct usage of yypushback(int) and a
    * match-all fallback rule) this method will only be called with things that
    * "Can't Possibly Happen". If this method is called, something is seriously
    * wrong (e.g. a JFlex bug producing a faulty scanner etc.).
    *
    * Usual syntax/scanner level error handling should be done in error fallback
    * rules.
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
            case 9: {
               return new ParsedSymbol(ParsedSymbol.TYPE_IDENTIFIER, yytext());
            }
            case 31:
               break;
            case 23: {
               string.append('\'');
            }
            case 32:
               break;
            case 30: {
               return new ParsedSymbol(ParsedSymbol.TYPE_CONSTANT, new ConstantIndex(Integer.parseInt(yytext().substring(8))));
            }
            case 33:
               break;
            case 10: {
               return new ParsedSymbol(ParsedSymbol.TYPE_INTEGER, new Long(Long.parseLong((yytext()))));
            }
            case 34:
               break;
            case 7: {
               yybegin(YYINITIAL);
               return new ParsedSymbol(ParsedSymbol.TYPE_EOL);
            }
            case 35:
               break;
            case 19: {
               char val = (char) Integer.parseInt(yytext().substring(1), 8);
               string.append(val);
            }
            case 36:
               break;
            case 4: {
               string.append(yytext());
            }
            case 37:
               break;
            case 2: {
               yybegin(PARAMETERS);
               return new ParsedSymbol(ParsedSymbol.TYPE_INSTRUCTION_NAME, yytext());
            }
            case 38:
               break;
            case 16: {
               string.append('\r');
            }
            case 39:
               break;
            case 12: {
               yybegin(STRING);
               string.setLength(0);
            }
            case 40:
               break;
            case 6: {
               yybegin(PARAMETERS);
               // length also includes the trailing quote
               return new ParsedSymbol(ParsedSymbol.TYPE_STRING, string.toString());
            }
            case 41:
               break;
            case 8: {
               return new ParsedSymbol(ParsedSymbol.TYPE_COMMENT, yytext().substring(1));
            }
            case 42:
               break;
            case 27: {
               return new ParsedSymbol(ParsedSymbol.TYPE_BOOLEAN, Boolean.FALSE);
            }
            case 43:
               break;
            case 22: {
               string.append('\b');
            }
            case 44:
               break;
            case 13: {
               String s = yytext();
               return new ParsedSymbol(ParsedSymbol.TYPE_LABEL, s.substring(0, s.length() - 1));
            }
            case 45:
               break;
            case 5: {
               throw new ParseException("Unterminated string at end of line", yyline + 1);
            }
            case 46:
               break;
            case 15: {
               string.append('\t');
            }
            case 47:
               break;
            case 28: {
               return new ParsedSymbol(ParsedSymbol.TYPE_REGISTER, new RegisterNumber(Integer.parseInt(yytext().substring(8))));
            }
            case 48:
               break;
            case 24: {
               return new ParsedSymbol(ParsedSymbol.TYPE_FLOAT, new Double(Double.parseDouble((yytext()))));
            }
            case 49:
               break;
            case 25: {
               return new ParsedSymbol(ParsedSymbol.TYPE_BOOLEAN, Boolean.TRUE);
            }
            case 50:
               break;
            case 11: {
               return new ParsedSymbol(ParsedSymbol.TYPE_BLOCK_START);
            }
            case 51:
               break;
            case 26: {
               return new ParsedSymbol(ParsedSymbol.TYPE_NULL, new Null());
            }
            case 52:
               break;
            case 20: {
               string.append('\\');
            }
            case 53:
               break;
            case 14: {
               throw new ParseException("Illegal escape sequence \"" + yytext() + "\"", yyline + 1);
            }
            case 54:
               break;
            case 18: {
               string.append('\n');
            }
            case 55:
               break;
            case 3: {
               return new ParsedSymbol(ParsedSymbol.TYPE_BLOCK_END);
            }
            case 56:
               break;
            case 29: {
               return new ParsedSymbol(ParsedSymbol.TYPE_UNDEFINED, new Undefined());
            }
            case 57:
               break;
            case 17: {
               string.append('\f');
            }
            case 58:
               break;
            case 21: {
               string.append('\"');
            }
            case 59:
               break;
            case 1: {
            }
            case 60:
               break;
            default:
               if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
                  zzAtEOF = true;
                  {
                     return new ParsedSymbol(ParsedSymbol.TYPE_EOF);
                  }
               } else {
                  zzScanError(ZZ_NO_MATCH);
               }
         }
      }
   }
}
