package appel.ch03;

import static org.testng.Assert.*;

import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;

import appel.ch03.analysis.DepthFirstAdapter;
import appel.ch03.lexer.Lexer;
import appel.ch03.node.AClassClassOrInterfaceDef;
import appel.ch03.node.AClassDef;
import appel.ch03.node.ADeclListOptVarDeclList;
import appel.ch03.node.ADirectDecl;
import appel.ch03.node.AInterfaceClassOrInterfaceDef;
import appel.ch03.node.AMemberDecl;
import appel.ch03.node.AVoidPrimitiveType;
import appel.ch03.node.Node;
import appel.ch03.node.Start;
import appel.ch03.parser.Parser;
import appel.ch03.parser.ParserException;

@Test
public class TestParser {

	public void emptyClassDecl() {
		Start s = parse("class Main { }");
		assertContainsSingleClassOrInterface(s, "Main");
	}

	public void emptyInterfaceDecl() {
		Start s = parse("interface MainProgram { }");
		assertContainsSingleClassOrInterface(s, "MainProgram");
	}

	public void simpleInterfaceDecl() {
		Start s = parse("interface MainProgram { void Main(); }");
		assertContainsSingleClassOrInterface(s, "MainProgram");
		s.apply(new DepthFirstAdapter() {
			@Override public void inAVoidPrimitiveType(AVoidPrimitiveType node) {
			}
		});
	}
	public void errorInInterfaceDecl() {
		//          1        10        20        30
		parseError("interface MainProgram { M; }", 1, 1, 20, 30);
		parseError("interface MainProgram { M(int a); }", 1, 1, 20, 30);
	}
	public void interfaceDeclWithFunParams() {
		parse("interface MainProgram { int M(X x, Y y(A a, int b)); }");
	}
	public void interfaceWithManyMethods() {
		Start s = parse(
				"interface MainProgram {\n" +
				"  int x();\n" +
				"  String y(String[] params);\n" +
				"  int z(boolean a, int b, AnotherClass c(int[] param));\n" +
				"}");
		assertContainsSingleClassOrInterface(s, "MainProgram");
		assertMethodExists(s, "x");
		assertMethodExists(s, "y");
		assertMethodExists(s, "z");
	}
	
	private Parser initParser(String input) {
		return new Parser(new Lexer(new PushbackReader(new StringReader(input))));
	}

	private Start parse(String input) {
		try {
			return initParser(input).parse();
		} catch (Exception e) {
			fail(annotate(input, e), e);
			return null;
		}
	}

	private void parseError(String input, int linestart, int lineend, int colstart, int colend) {
		try {
			initParser(input).parse();
			fail("Expected parse error but succeeded");
		} catch (ParserException e) {
			String[] pos = e.getMessage().split("\\[|\\]|,");
			int line = Integer.parseInt(pos[1]), col = Integer.parseInt(pos[2]);
			assertTrue(linestart < 0 || (linestart <= line && lineend >= line), "\n    Error at unexpected position: " + annotate(input, e));
			assertTrue(colstart < 0 || (colstart <= col && colend >= col),      "\n    Error at unexpected position: " + annotate(input, e));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String annotate(String input, Exception e) {
		String indent = "       ";
		StringBuilder s = new StringBuilder("\n").append(indent).append(e.getMessage()).append(":\n");
		s.append(indent).append("1        10        20        30        40        50        60        70\n");
		if (e instanceof ParserException) {
			int col = Integer.parseInt(e.getMessage().split("\\[|\\]|,")[2]);
			s.append(indent);
			for (int i = 0; i < col-1; i++) {
				s.append(" ");
			}
			s.append("v\n");
		}

		String[] lines = input.split("\n");
		for (int i = 0; i < lines.length; i++) {
			s.append(indent.substring(3)).append(i+1).append(": ").append(lines[i]).append('\n');
		}
		return s.toString();
	}
	
	private void assertContainsSingleClassOrInterface(final Node s, final String name) {
		s.apply(new DepthFirstAdapter() {
			boolean found = false;
			@Override public void inAClassClassOrInterfaceDef(AClassClassOrInterfaceDef node) { found(); }
			@Override public void inAInterfaceClassOrInterfaceDef(AInterfaceClassOrInterfaceDef node) { found(); }
			private void found() {
				assertFalse(found, "Should only find 1 class/interface");
				found = true;
			}
			@Override public void inAClassDef(AClassDef node) {
				assertEquals(node.getId().getText(), name);
			}
			@Override public void outStart(Start node) {
				assertTrue(found);
			}
		});
	}
	private void assertMethodExists(final Node s, final String name) {
		s.apply(new DepthFirstAdapter() {
			boolean inVarList = false;
			boolean found = false;
			@Override public void inAMemberDecl(AMemberDecl node) {
				node.apply(new DepthFirstAdapter() {
					@Override public void inADirectDecl(ADirectDecl node) {
						if (!inVarList && name.equals(node.getId().getText())) { found = true; }
					}
					@Override public void inADeclListOptVarDeclList(ADeclListOptVarDeclList node) { inVarList = true; }
					@Override public void outADeclListOptVarDeclList(ADeclListOptVarDeclList node) { inVarList = false; }
				});
			}
			@Override public void outAClassClassOrInterfaceDef(AClassClassOrInterfaceDef node) { assertTrue(found, "Method " + name + " not found."); }
			@Override public void outAInterfaceClassOrInterfaceDef(AInterfaceClassOrInterfaceDef node) { assertTrue(found, "Method " + name + " not found."); }
		});
	}
}