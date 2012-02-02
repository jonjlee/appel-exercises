package appel.ch03;

import static org.testng.Assert.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import appel.ch03.analysis.DepthFirstAdapter;
import appel.ch03.lexer.Lexer;
import appel.ch03.lexer.LexerException;
import appel.ch03.node.*;
import appel.ch03.parser.Parser;
import appel.ch03.parser.ParserException;

@Test
public class TestParser {
	
	private static Logger LOG = Logger.getLogger(TestParser.class);

	public void emptyInterfaceDecl() {
		Node s = parse("interface MainProgram { }");
		assertValid(s);
		assertContainsSingleClassOrInterface(s, "MainProgram");
	}

	public void simpleInterfaceDecl() {
		Node s = parse("interface MainProgram { void Main(); }");
		assertValid(s);
		assertMethodExists(s, "Main");
	}
	public void errorInInterfaceDecl() {
		//              1        10        20        30
		Node s = parse("interface MainProgram { M; }");
		assertInvalid(s, 1, 1, 20, 30);
		s =      parse("interface MainProgram { M(int a); }");
		assertInvalid(s, 1, 1, 20, 30);
	}
	public void interfaceDeclWithFunParams() {
		Node result = parse("interface MainProgram { int M(X x, Y y(A, int)); }");
		assertValid(result);
	}
	public void interfaceWithManyMethods() {
		Node s = parse(
				"interface MainProgram {\n" +
				"  int x();\n" +
				"  String y(String[] params);\n" +
				"  int z(boolean a, int b, AnotherClass c(int[]));\n" +
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
				"  int x() { int a = 0e-10; }\n" +
				"  String y(String[] params) { String[] s = params; }\n" +
				"  int z(boolean a, int b, AnotherClass c(int[])) {\n" +
				"  }\n" +
				"}");
		assertValid(s);
		assertMethodExists(s, "x");
		assertMethodExists(s, "y");
		assertMethodExists(s, "z");
	}
	
	public void primitiveVars() {
		assertValid(parseStmt("int x"));
		assertValid(parseStmt("boolean x, y = 0"));
		assertValid(parseStmt("String x"));
		assertValid(parseStmt("MyClass x"));
	}

	public void funDecls() {
		assertValid(parseStmt("void x()"));
		assertValid(parseStmt("int x()"));
		assertValid(parseStmt("String x() { x(); }"));
		assertValid(parseStmt("int x(boolean y, void z(int)) {}"));
		assertValid(parseStmt("int x(boolean y, void z(int(int,void()))) {}"));

		assertValid(parseStmt("int[] x()"));
		assertInvalid(parseStmt("int x()[]"));

		assertValid(parseStmt("(A r(void()))[] x(boolean y, void z())"));
	}

	public void voidVarTypeIsInvalid() {
		assertInvalid(parseStmt("void x"));
		assertInvalid(parseStmt("int x(void y)"));
	}
	
	public void newExpr() {
		assertValid(parseStmt("Main m = new Main()"));
		assertInvalid(parseStmt("Main m = new Main"));
		assertValid(parseStmt("Main[] m = new Main[10]"));
		assertValid(parseStmt("int[] m = new int[2]"));
		assertValid(parseStmt("m = new boolean[2][3][]"));
		assertInvalid(parseStmt("m = new boolean[2][][3]"));
		assertValid(parseStmt("String[][] m[][] = new String[2][1][][]"));
		assertInvalid(parseStmt("m = new int[]"));
	}
	
	public void binopExpr() {
		assertValid(parseStmt("1 + 2"));
		assertValid(parseStmt("a + b / 2 * c / (d % e && true || false)"));
	}
	
	public void binopPrecedence() throws ParserException, LexerException, IOException {
		assertEquals(ExprEvaluator.evalInt("1+2"), 3);
		assertEquals(ExprEvaluator.evalInt("1+2*2"), 5);
		assertEquals(ExprEvaluator.evalInt("(1+2)*2"), 6);
		assertEquals(ExprEvaluator.evalInt("1+-2"), -1);
		assertEquals(ExprEvaluator.evalInt("-1+2"), 1);
		
		assertEquals(ExprEvaluator.evalInt("2+2--"), 4);
		assertEquals(ExprEvaluator.evalInt("2+--2"), 3);

		assertEquals(ExprEvaluator.evalBool("1+2==3"), true);
		assertEquals(ExprEvaluator.evalBool("1+2==2"), false);
		assertEquals(ExprEvaluator.evalBool("1+2<=2"), false);


		assertEquals(ExprEvaluator.evalBool("true == true"), true);
		assertEquals(ExprEvaluator.evalBool("true || false && false"), true);
		assertEquals(ExprEvaluator.evalBool("(false || true) && false"), false);
		
		assertEquals(ExprEvaluator.evalBool("!true"), false);
		assertEquals(ExprEvaluator.evalBool("!true || true"), true);
		assertEquals(ExprEvaluator.evalBool("!(true || true)"), false);

		assertEquals(ExprEvaluator.evalBool("1+2<=2 || true"), true);
	}
	
	public void binopAssociativity() throws ParserException, LexerException, IOException {
		assertEquals(ExprEvaluator.evalInt("1-2-3"), -4);
		assertEquals(ExprEvaluator.evalInt("1-(2-3)"), 2);
	}

	public void unopExpr() {
		assertValid(parseStmt("!x; -x; x++; x--"));
		assertValid(parseStmt("!(-x++--)"));
	}

	public void boolExpr() {
		assertValid(parseStmt("1 != 2"));
		assertValid(parseStmt("1 != 2 + 3 * 4 == 5"));
		assertValid(parseStmt("1 < 2 <= 3 > 4 >= 5"));
	}

	public void assignExpr() {
		assertValid(parseStmt("x = 2"));
		assertValid(parseStmt("x = y = z"));
		assertValid(parseStmt("x = (f || g)"));
	}
	
	public void binopAssignExpr() {
		assertValid(parseStmt("x += 2"));
		assertValid(parseStmt("a += b /= 2 *= c /= (d %= e && true)"));
	}
	
	public void projectionExpr() {
		assertValid(parseStmt("x.y"));
		assertValid(parseStmt("x.y()"));
		assertValid(parseStmt("x.y[2]"));
		assertValid(parseStmt("x.y.a.b = 12"));
		assertValid(parseStmt("x.y.a.b = c.d"));
		assertInvalid(parseStmt("x.1"));
	}
	
	public void subscriptExpr() {
		assertValid(parseStmt("x[1]"));
		assertValid(parseStmt("x[1][2][3].y.a.b = 12"));
		assertValid(parseStmt("x.y.a.b[3] = c[1][2].d[3]"));
		assertInvalid(parseStmt("x[]"));
	}
	
	public void applyExpr() {
		assertValid(parseStmt("x()"));
		assertValid(parseStmt("x(12, 'b', f(), !(-1 + 2))"));
		assertInvalid(parseStmt("x(int a)"));
	}
	
	public void forLoop() {
		assertValid(parseStmt("for (;;);"));
		assertValid(parseStmt("for (i = 0; i < 10; i++);"));
		assertValid(parseStmt("for (i = 0; i < 10; i++) {}"));
		assertValid(parseStmt("for (i = 0; i < 10; i++) { f(i); }"));
	}

	public void whileLoop() {
		assertValid(parseStmt("while (true);"));
		assertValid(parseStmt("while (true) {}"));
		assertValid(parseStmt("while ((i = s.length()) < 10) { f(i); }"));
	}
	
	public void returnStmt() {
		assertValid(parseStmt("return true;"));
	}

	public void breakStmt() {
		assertValid(parseStmt("for (;;) { break; }"));
	}

	public void tryCatchStmt() {
		assertValid(parseStmt("try {} catch (Exception e) {}"));
		assertInvalid(parseStmt("try {}"));
		assertInvalid(parseStmt("try {} catch ()"));
		assertInvalid(parseStmt("try {} catch () {}"));
		assertInvalid(parseStmt("try catch (Exception e) {}"));
	}
	
	public void ifStmt() {
		assertValid(parseStmt("if (true);"));
		assertValid(parseStmt("if (true) {} else {}"));
		assertValid(parseStmt("if (true) ; else ;"));
	}

	public void nestedIfStmt() {
		assertValid(parseStmt("if (true) if (true) {} else {}"));
		assertValid(parseStmt("if (true) if (true) {} else {} else {}"));
	}

	public void elseIfStmt() {
		assertValid(parseStmt("if (true) {} else if (true) {}"));
		assertValid(parseStmt("if (true) {} else if (true) {} else {}"));
	}

	private Node parse(String input) {
		try {
			Lexer l = new Lexer(new PushbackReader(new StringReader(input)));
			Parser p = new Parser(l);
			Start s = p.parse();
			s.apply(new ASTCleaner());
			return s;
		} catch (Exception e) {
			return new ParseFailed(input, e);
		}
	}
	
	private Node parseStmt(String stmt) {
		//            1        10        20
		Node s = parse("class _{void _(){  " + stmt + ";}}");
		if (!(s instanceof ParseFailed)) {
			LOG.debug(stmt + " ->\n" +
					nodeToString(((AFunDef) ((AClassDef) ((ADefExpr) ((ASeqExpr) ((Start) s).getPExpr()).getExpr().get(0)).getDef()).getDef().get(0)).getExpr().get(0)));
		}
		return s;
	}

	private void assertValid(Node parseResult) {
		if (parseResult instanceof ParseFailed) {
			ParseFailed r = (ParseFailed) parseResult;
			fail(r.getMessage(), r.e);
		}
	}
	
	private void assertInvalid(Node parseResult) {
		assertInvalid(parseResult, -1, -1, -1, -1);
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
			@Override public void inAClassDef(AClassDef node) {
				assertFalse(found, "Should only find 1 class/interface");
				found = true;
				assertEquals(node.getId().getText(), name);
			}
			@Override public void inAInterfaceType(AInterfaceType node) {
				assertFalse(found, "Should only find 1 class/interface");
				found = true;
				assertEquals(node.getId().getText(), name);
			}
			@Override public void outStart(Start node) {
				assertTrue(found);
			}
		});
	}
	private void assertMethodExists(final Node s, final String name) {
		s.apply(new DepthFirstAdapter() {
			boolean found = false;
			@Override public void inAFunDef(AFunDef node) {
				if (name.equals(node.getId().getText())) { found = true; }
			}
			@Override public void outStart(Start node) {
				assertTrue(found, "Method " + name + " not found.");
			}
		});
	}
	
	private String nodeToString(Node s) {
		final StringBuilder ast = new StringBuilder();
		s.apply(new DepthFirstAdapter() {
			int indent = 0;
			void append(Node n) {
				StringBuilder s = new StringBuilder();
				for (int i = 0; i < indent; i++) {
					s.append("|  ");
				}
				s.append(n.getClass().toString().replaceFirst("class appel.ch03.node.A?", "")).append(getText(n));
				ast.append(s).append("\n");
			}
			String getText(Node n) {
				String s = null;
				if (n instanceof ANumberExpr || n instanceof ABoolExpr || n instanceof ACharExpr || n instanceof AStringExpr || n instanceof AVarExpr) {
					s = n.toString();
				} else if (n instanceof AClassDef) {
					s = ((AClassDef) n).getId().toString();
				} else if (n instanceof AFunDef) {
					s = ((AFunDef) n).getId().toString().trim() + "(" + ((AFunDef) n).getFormal().toString() + ")";
				}
				if (s != null) {
					return "(" + s.trim() + ")";
				}
				return "";
			}
			@Override public void defaultIn(Node node) {
				append(node);
				indent++;
			}
			@Override public void defaultOut(Node node) {
				indent--;
			}
		});
		return ast.toString();
	}
}