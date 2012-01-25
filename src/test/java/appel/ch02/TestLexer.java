package appel.ch02;

import static org.testng.Assert.*;

import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;

import appel.ch02.lexer.Lexer;
import appel.ch02.node.TFalse;
import appel.ch02.node.TIf;
import appel.ch02.node.TNew;
import appel.ch02.node.TPrintln;
import appel.ch02.node.TTrue;
import appel.ch02.node.TWhile;
import appel.ch02.node.TWhitespace;

public class TestLexer {
	
	@Test public void reservedWords() {
		testTokens("System.out.println", TPrintln.class);
		testTokens("if while new", TIf.class, TWhitespace.class, TWhile.class, TWhitespace.class, TNew.class);
		testTokens("true false", TTrue.class, TWhitespace.class, TFalse.class);
	}
	
	public Lexer initLexer(String input) {
		Lexer lex = new Lexer(new PushbackReader(new StringReader(input)));
		return lex;
	}

	public void testTokens(String input, Class<?>... tokens) {
		Lexer lex = initLexer(input);

			for (int i = 0; i < tokens.length; i++) {
				try {
					assertEquals(lex.next().getClass(), tokens[i]);
				} catch (Exception e) {
					fail("Unable to get token " + i + " from " + input, e);
				}
			}
	}
}
