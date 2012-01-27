package appel.ch03;

import static org.testng.Assert.*;

import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;

import appel.ch03.analysis.DepthFirstAdapter;
import appel.ch03.lexer.Lexer;
import appel.ch03.node.*;
import appel.ch03.parser.Parser;
import appel.ch03.parser.ParserException;

@Test
public class TestParser {

	public void emptyInterfaceDecl() {
		Node s = parse("interface MainProgram { }");
		assertValid(s);
		assertContainsSingleClassOrInterface(s, "MainProgram");
	}

	public void simpleInterfaceDecl() {
		Node s = parse("interface MainProgram { void Main(); }");
		assertValid(s);
		assertContainsSingleClassOrInterface(s, "MainProgram");
		s.apply(new DepthFirstAdapter() {
			@Override public void inAVoidPrimitiveType(AVoidPrimitiveType node) {
			}
		});
	}
	public void errorInInterfaceDecl() {
		//              1        10        20        30
		Node s = parse("interface MainProgram { M; }");
		assertInvalid(s, 1, 1, 20, 30);
		s =      parse("interface MainProgram { M(int a); }");
		assertInvalid(s, 1, 1, 20, 30);
	}
	public void interfaceDeclWithFunParams() {
		Node result = parse("interface MainProgram { int M(X x, Y y(A a, int b)); }");
		assertValid(result);
	}
	public void interfaceWithManyMethods() {
		Node s = parse(
				"interface MainProgram {\n" +
				"  int x();\n" +
				"  String y(String[] params);\n" +
				"  int z(boolean a, int b, AnotherClass c(int[] param));\n" +
				"}");
		assertValid(s);
		assertContainsSingleClassOrInterface(s, "MainProgram");
		assertMethodExists(s, "x");
		assertMethodExists(s, "y");
		assertMethodExists(s, "z");
	}
	
	public void emptyClassDecl() {
		Node s = parse("class Main { }");
		assertValid(s);
		assertContainsSingleClassOrInterface(s, "Main");
	}
	
	public void classWithConstructor() {
		Node s = parse("class Main { Main(int x) {} }");
		assertValid(s);
		assertContainsSingleClassOrInterface(s, "Main");
	}
	
	public void classWithVar() {
		Node s = parse("class Main { int[] x = 10; }");
		assertValid(s);
	}
	
	public void classWithMethod() {
		Node s = parse("class Main { int[] x() {} }");
		assertValid(s);
		assertMethodExists(s, "x");
	}
	
	public void classWithManyMembers() {
		Node s = parse(
				"class Main {\n" +
				"  boolean[] b = true;\n" +
				"  \n" +
				"  Main(int arg, T obj) {}\n" +
				"  \n" +
				"  int x() { 0e-10; }\n" +
				"  String y(String[] params) { }\n" +
				"  int z(boolean a, int b, AnotherClass c(int[] param)) {\n" +
				"  }\n" +
				"}");
		assertValid(s);
		assertMethodExists(s, "x");
		assertMethodExists(s, "y");
		assertMethodExists(s, "z");
	}
	
	private Node parse(String input) {
		try {
			Lexer l = new Lexer(new PushbackReader(new StringReader(input)));
			Parser p = new Parser(l);
			return p.parse();
		} catch (Exception e) {
			return new ParseFailed(input, e);
		}
	}
	
	private void assertValid(Node parseResult) {
		if (parseResult instanceof ParseFailed) {
			ParseFailed r = (ParseFailed) parseResult;
			fail(r.getMessage(), r.e);
		}
	}
	
	private void assertInvalid(Node parseResult, int linestart, int lineend, int colstart, int colend) {
		if (!(parseResult instanceof ParseFailed)) {
			fail("Expected parse error but succeeded");
		}
		ParseFailed r = (ParseFailed) parseResult;
		if (!(r.e instanceof ParserException)) {
			fail("Expected a parse exception, but got " + r.e.getMessage(), r.e);
		}
		String[] pos = r.e.getMessage().split("\\[|\\]|,");
		int line = Integer.parseInt(pos[1]), col = Integer.parseInt(pos[2]);
		assertTrue(linestart < 0 || (linestart <= line && lineend >= line), "\n    Error at unexpected position: " + r.getMessage());
		assertTrue(colstart < 0 || (colstart <= col && colend >= col),      "\n    Error at unexpected position: " + r.getMessage());
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
			@Override public void inAMemberDecl(AMemberDecl node) { inMember(node); }
			@Override public void inAFunDef(AFunDef node) { inMember(node); }
			public void inMember(Node node) {
				node.apply(new DepthFirstAdapter() {
					@Override public void inAStdFunDirectDecl(AStdFunDirectDecl node) {
						if (!inVarList && name.equals(node.getId().getText())) { found = true; }
					}
					@Override public void inAOptParamDeclList(AOptParamDeclList node) { inVarList = true; }
					@Override public void outAOptParamDeclList(AOptParamDeclList node) { inVarList = false; }
				});
			}
			@Override public void outAClassClassOrInterfaceDef(AClassClassOrInterfaceDef node) { assertTrue(found, "Method " + name + " not found."); }
			@Override public void outAInterfaceClassOrInterfaceDef(AInterfaceClassOrInterfaceDef node) { assertTrue(found, "Method " + name + " not found."); }
		});
	}
	
	class ParseFailed extends Token {
		String input;
		Exception e;
		public ParseFailed(String input, Exception e) {
			this.input = input;
			this.e = e;
		}
		public String getMessage() {
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
		@Override public void apply(Switch sw) {}
		@Override public Object clone() { return null; }
	}
}