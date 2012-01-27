package appel.ch03;

import static org.testng.Assert.*;

import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;

import appel.ch03.lexer.Lexer;
import appel.ch03.node.*;

@Test
public class TestLexer {
	
	public void symbols() {
		testTokens(",:;()[]{}", TComma.class, TColon.class, TSemi.class, TLparen.class, TRparen.class, TLbrack.class, TRbrack.class, TLbrace.class, TRbrace.class);
		testTokens("+-*/%", TPlus.class, TMinus.class, TTimes.class, TDivide.class, TMod.class);
		testTokens("== != > >= < <= && ||", TEqeq.class, TNeq.class, TGt.class, TGe.class, TLt.class, TLe.class, TAnd.class, TOr.class);
		testTokens("= += -= *= /= %=", TEq.class, TPluseq.class, TMinuseq.class, TTimeseq.class, TDivideeq.class, TModeq.class);
		testTokens("! ++ --", TBang.class, TInc.class, TDec.class);
	}

	public void reserved() {
		testTokens("System.out.println length", TPrintln.class, TLength.class);
		testTokens("if else", TIf.class, TElse.class);
		testTokens("for do while break", TFor.class, TDo.class, TWhile.class, TBreak.class);
		testTokens("try catch finally throw finally", TTry.class, TCatch.class, TFinally.class, TThrow.class, TFinally.class);
		testTokens("new interface class extends implements this instanceof", TNew.class, TInterface.class, TClasskeyword.class, TExtends.class, TImplements.class, TThis.class, TInstanceof.class);
		testTokens("return true false null", TReturn.class, TTrue.class, TFalse.class, TNull.class);
		testTokens("void int boolean String", TVoid.class, TInt.class, TBoolean.class, TStringtype.class);
	}
	
	public void numbers() {
		testTokens("0 1 10", TDecimal.class, TDecimal.class, TDecimal.class);
		testTokens("01 07", TOctal.class, TOctal.class);
		testTokens("0x0 0x0111 0xAF1", THex.class, THex.class, THex.class);
		testTokens("1. 1.0 0.1 .1 .111 1.111", TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class);
		testTokens("1e1 1e+1 1e-1 1.e1 1.e+1 1.e-1 .1e10 0.1e10 1.5e10 1.0e+10", TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class, TFloat.class);
	}
	
	public void strings() {
		testTokens("' ' 'x' '\\n' '\\t' '\\''", TChar.class, TChar.class, TChar.class, TChar.class, TChar.class);
		testTokens("\"\" \"string\" \"\\\"\" \"\\\"\\\"\" \"\\\"abc\\\"\\\"\\\"\"", TString.class, TString.class, TString.class, TString.class, TString.class);
	}
	
	public void lineComments() {
		testTokens("//", TComment.class);
		testTokens("//\n", TComment.class);
		testTokens("// ", TComment.class);
		testTokens("// this is a comment", TComment.class);
		testTokens( "// mutli \n// lines", TComment.class, TComment.class);
		testTokens("x      // inline", TId.class, TComment.class);
	}
	
	public void comments() {
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

	private Lexer initLexer(String input) {
		Lexer lex = new Lexer(new PushbackReader(new StringReader(input)));
		return lex;
	}

	private void testTokens(String input, Class<?>... expectedTokens) {
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
