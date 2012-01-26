package appel.ch03;

import static org.testng.Assert.*;

import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;

import appel.ch03.analysis.DepthFirstAdapter;
import appel.ch03.lexer.Lexer;
import appel.ch03.node.AClassDef;
import appel.ch03.node.AClassOrInterfaceDef1;
import appel.ch03.node.Start;
import appel.ch03.parser.Parser;

@Test
public class TestParser {

	public void classDecl() {
		Start s = parse("public class Main { }");
		s.apply(new DepthFirstAdapter() {
			boolean containsClass = false;
			@Override public void inAClassOrInterfaceDef1(AClassOrInterfaceDef1 node) {
				assertFalse(containsClass, "Should only find 1 class");
				containsClass = true;
			}
			@Override public void inAClassDef(AClassDef node) {
				assertEquals(node.getId().getText(), "Main");
			}
			@Override public void outStart(Start node) {
				assertTrue(containsClass);
			}
		});
	}
	
	private Start parse(String input) {
		Lexer lex = new Lexer(new PushbackReader(new StringReader(input)));
		Parser parser = new Parser(lex);
		try {
			return parser.parse();
		} catch (Exception e) {
			fail("Error parsing:\n" + pretty(input), e);
			return null;
		}
	}

	private String pretty(String input) {
		String[] lines = input.split("\n");
		StringBuilder s = new StringBuilder("   1        10        20        30\n");
		for (int i = 0; i < lines.length; i++) {
			s.append(i+1).append(": ").append(lines[i]).append('\n');
		}
		return s.toString();
	}
}
