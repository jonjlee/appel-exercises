package appel.ch03;

import static org.testng.Assert.*;

import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;

import appel.ch03.lexer.Lexer;
import appel.ch03.node.EOF;
import appel.ch03.node.TAnd;
import appel.ch03.node.TAssign;
import appel.ch03.node.TBang;
import appel.ch03.node.TBoolean;
import appel.ch03.node.TBreak;
import appel.ch03.node.TCatch;
import appel.ch03.node.TChar;
import appel.ch03.node.TClass;
import appel.ch03.node.TColon;
import appel.ch03.node.TComma;
import appel.ch03.node.TComment;
import appel.ch03.node.TDec;
import appel.ch03.node.TDecimal;
import appel.ch03.node.TDivide;
import appel.ch03.node.TDivideeq;
import appel.ch03.node.TDo;
import appel.ch03.node.TElse;
import appel.ch03.node.TEq;
import appel.ch03.node.TExtends;
import appel.ch03.node.TFalse;
import appel.ch03.node.TFinally;
import appel.ch03.node.TFloat;
import appel.ch03.node.TFor;
import appel.ch03.node.TGe;
import appel.ch03.node.TGt;
import appel.ch03.node.THex;
import appel.ch03.node.TId;
import appel.ch03.node.TIf;
import appel.ch03.node.TImplements;
import appel.ch03.node.TInc;
import appel.ch03.node.TInstanceof;
import appel.ch03.node.TInt;
import appel.ch03.node.TInterface;
import appel.ch03.node.TLbrace;
import appel.ch03.node.TLbrack;
import appel.ch03.node.TLe;
import appel.ch03.node.TLength;
import appel.ch03.node.TLparen;
import appel.ch03.node.TLt;
import appel.ch03.node.TMinus;
import appel.ch03.node.TMinuseq;
import appel.ch03.node.TMod;
import appel.ch03.node.TModeq;
import appel.ch03.node.TNeq;
import appel.ch03.node.TNew;
import appel.ch03.node.TNull;
import appel.ch03.node.TOctal;
import appel.ch03.node.TOr;
import appel.ch03.node.TPlus;
import appel.ch03.node.TPluseq;
import appel.ch03.node.TPrintln;
import appel.ch03.node.TPrivate;
import appel.ch03.node.TProtected;
import appel.ch03.node.TPublic;
import appel.ch03.node.TRbrace;
import appel.ch03.node.TRbrack;
import appel.ch03.node.TReturn;
import appel.ch03.node.TRparen;
import appel.ch03.node.TSemicolon;
import appel.ch03.node.TStatic;
import appel.ch03.node.TString;
import appel.ch03.node.TStringtype;
import appel.ch03.node.TThis;
import appel.ch03.node.TThrow;
import appel.ch03.node.TTimes;
import appel.ch03.node.TTimeseq;
import appel.ch03.node.TTrue;
import appel.ch03.node.TTry;
import appel.ch03.node.TVoid;
import appel.ch03.node.TWhile;
import appel.ch03.node.TWhitespace;

public class TestLexer {
	
	@Test public void symbols() {
		testTokens(",:;()[]{}", TComma.class, TColon.class, TSemicolon.class, TLparen.class, TRparen.class, TLbrack.class, TRbrack.class, TLbrace.class, TRbrace.class);
		testTokens("+-*/%", TPlus.class, TMinus.class, TTimes.class, TDivide.class, TMod.class);
		testTokens("== != > >= < <= && ||", TEq.class, TNeq.class, TGt.class, TGe.class, TLt.class, TLe.class, TAnd.class, TOr.class);
		testTokens("= += -= *= /= %=", TAssign.class, TPluseq.class, TMinuseq.class, TTimeseq.class, TDivideeq.class, TModeq.class);
		testTokens("! ++ --", TBang.class, TInc.class, TDec.class);
	}

	@Test public void reserved() {
		testTokens("System.out.println length", TPrintln.class, TLength.class);
		testTokens("if else", TIf.class, TElse.class);
		testTokens("for do while break", TFor.class, TDo.class, TWhile.class, TBreak.class);
		testTokens("try catch finally throw finally", TTry.class, TCatch.class, TFinally.class, TThrow.class, TFinally.class);
		testTokens("public protected private static new", TPublic.class, TProtected.class, TPrivate.class, TStatic.class, TNew.class);
		testTokens("interface class extends implements this instanceof", TInterface.class, TClass.class, TExtends.class, TImplements.class, TThis.class, TInstanceof.class);
		testTokens("return true false null", TReturn.class, TTrue.class, TFalse.class, TNull.class);
		testTokens("void int boolean String", TVoid.class, TInt.class, TBoolean.class, TStringtype.class);
	}
	
	@Test public void numbers() {
		testTokens("0 1 10", TDecimal.class, TDecimal.class, TDecimal.class);
		testTokens("01 07", TOctal.class, TOctal.class);
		testTokens("0x0 0x0111 0xAF1", THex.class, THex.class, THex.class);
		testTokens("1. 1.0 0.1 .1 .111 1.111", TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class);
		testTokens("1e1 1e+1 1e-1 1.e1 1.e+1 1.e-1 .1e10 0.1e10 1.5e10 1.0e+10", TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class);
	}
	
	@Test public void strings() {
		testTokens("' ' 'x' '\\n' '\\t' '\\''", TChar.class, TChar.class, TChar.class, TChar.class, TChar.class);
		testTokens("\"\" \"string\" \"\\\"\" \"\\\"\\\"\" \"\\\"abc\\\"\\\"\\\"\"", TString.class, TString.class, TString.class, TString.class, TString.class);
	}
	
	@Test public void lineComments() {
		testTokens("//", TComment.class);
		testTokens("//\n", TComment.class);
		testTokens("// ", TComment.class);
		testTokens("// this is a comment", TComment.class);
		testTokens( "// mutli \n// lines", TComment.class, TComment.class);
		testTokens("x      // inline", TId.class, TComment.class);
	}
	
	@Test public void comments() {
		testTokens("/**/", TComment.class);
		testTokens("/* */", TComment.class);
		testTokens("/*a*/", TComment.class);
		testTokens("/***/", TComment.class);
		testTokens("/*\n*/", TComment.class);
		testTokens("/* one line */", TComment.class);
		testTokens("/* multi\nline */", TComment.class);
		testTokens("/* if while */", TComment.class);
		testTokens("/* contains * */", TComment.class);
		testTokens("/* contains * /*/", TComment.class);
		testTokens("/* contains ****/", TComment.class);
	}

	public Lexer initLexer(String input) {
		Lexer lex = new Lexer(new PushbackReader(new StringReader(input)));
		return lex;
	}

	public void testTokens(String input, Class<?>... expectedTokens) {
		Lexer lex = initLexer(input);
		Class<?> tokClass;
		int i = 0;

		try {
			for (i = 0; i < expectedTokens.length; i++) {
					// Get next token, skipping whitespace unless explicitly specified in expected token list
					do {
						tokClass = lex.next().getClass();
					} while (tokClass == TWhitespace.class && expectedTokens[i] != TWhitespace.class);
					assertEquals(tokClass, expectedTokens[i], "Token " + i + " of '" + input + "' ");
			}
			// skip over any trailing whitespace
			do {
				tokClass = lex.next().getClass();
			} while (tokClass == TWhitespace.class);
			
			// and assert that the whole string was consumed
			assertEquals(tokClass, EOF.class);
		} catch (Exception e) {
			fail("Unable to get token " + i + " from " + input, e);
		}
	}
}
