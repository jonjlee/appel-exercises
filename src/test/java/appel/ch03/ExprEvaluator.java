package appel.ch03;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import appel.ch03.analysis.AnalysisAdapter;
import appel.ch03.lexer.Lexer;
import appel.ch03.lexer.LexerException;
import appel.ch03.node.*;
import appel.ch03.parser.Parser;
import appel.ch03.parser.ParserException;

public class ExprEvaluator extends AnalysisAdapter {

	Value currentResult;

	public static int evalInt(String expr) throws ParserException, LexerException, IOException {
		PExpr node = parse(expr);
		return new ExprEvaluator().eval(node).getInt();
	}

	public static boolean evalBool(String expr) throws ParserException, LexerException, IOException {
		PExpr node = parse(expr);
		return new ExprEvaluator().eval(node).getBool();
	}

	private static PExpr parse(String expr) {
		try {
			String input = "class _{void _(){"
					+ expr + ";}}";
			Lexer l = new Lexer(new PushbackReader(new StringReader(input)));
			Parser p = new Parser(l);
			Start s = p.parse();
			return ((AFunDef) ((AClassDef) ((ADefExpr) ((ASeqExpr) s.getPExpr()).getExpr().get(0)).getDef()).getDef().get(0)).getExpr().get(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void visit(Node node) {
		if (node != null) {
			node.apply(this);
		}
	}

	public Value eval(Node node) {
		this.currentResult = null;
		visit(node);
		Value result = this.currentResult;
		this.currentResult = null;
		return result;
	}

	@Override public void caseABinopExpr(ABinopExpr node) {
		PBinop op = node.getOp();
		Value left = eval(node.getLeft()), right = eval(node.getRight());
		if (op instanceof APlusBinop) {
			this.currentResult = new IntValue(left.getInt() + right.getInt());
		} else if (op instanceof AMinusBinop) {
			this.currentResult = new IntValue(left.getInt() - right.getInt());
		} else if (op instanceof ATimesBinop) {
			this.currentResult = new IntValue(left.getInt() * right.getInt());
		} else if (op instanceof ADivideBinop) {
			this.currentResult = new IntValue(left.getInt() / right.getInt());
		} else if (op instanceof AModBinop) {
			this.currentResult = new IntValue(left.getInt() % right.getInt());
		} else if (op instanceof AEqBinop) {
			if (left.getType() == Type.BOOL) {
				this.currentResult = new BoolValue(left.getBool() == right.getBool());
			} else {
				this.currentResult = new BoolValue(left.getInt() == right.getInt());
			}
		} else if (op instanceof ANeqBinop) {
			if (left.getType() == Type.BOOL) {
				this.currentResult = new BoolValue(left.getBool() != right.getBool());
			} else {
				this.currentResult = new BoolValue(left.getInt() != right.getInt());
			}
		} else if (op instanceof AGtBinop) {
			this.currentResult = new BoolValue(left.getInt() > right.getInt());
		} else if (op instanceof ALtBinop) {
			this.currentResult = new BoolValue(left.getInt() < right.getInt());
		} else if (op instanceof AGeBinop) {
			this.currentResult = new BoolValue(left.getInt() >= right.getInt());
		} else if (op instanceof ALeBinop) {
			this.currentResult = new BoolValue(left.getInt() <= right.getInt());
		}
	}
	
	@Override public void caseABoolopExpr(ABoolopExpr node) {
		PBoolop op = node.getOp();
		Value left = eval(node.getLeft()), right = eval(node.getRight());
		if (op instanceof AAndBoolop) {
			this.currentResult = new BoolValue(left.getBool() && right.getBool());
		} else if (op instanceof AOrBoolop) {
			this.currentResult = new BoolValue(left.getBool() || right.getBool());
		}
	}
	
	@Override public void caseAUarithopExpr(AUarithopExpr node) {
		PUarithop op = node.getOp();
		Value expr = eval(node.getExpr());
		if (op instanceof APreIncUarithop) {
			this.currentResult = new IntValue(expr.getInt()+1);
		} else if (op instanceof APreDecUarithop) {
			this.currentResult = new IntValue(expr.getInt()-1);
		} else if (op instanceof APostIncUarithop) {
			this.currentResult = expr;
		} else if (op instanceof APostDecUarithop) {
			this.currentResult = expr;
		}
	}

	@Override public void caseAUnopExpr(AUnopExpr node) {
		PUnop op = node.getOp();
		Value expr = eval(node.getExpr());
		if (op instanceof ANegUnop) {
			this.currentResult = new IntValue(-expr.getInt());
		} else if (op instanceof ANotUnop) {
			this.currentResult = new BoolValue(!expr.getBool());
		}
	}
	
	@Override public void caseABoolExpr(ABoolExpr node) {
		this.currentResult = eval(node.getBool());
	}
	
	@Override public void caseATrueBool(ATrueBool node) {
		this.currentResult = new BoolValue(true);
	}
	@Override public void caseAFalseBool(AFalseBool node) {
		this.currentResult = new BoolValue(false);
	}

	@Override public void caseANumberExpr(ANumberExpr node) {
		this.currentResult = eval(node.getNumber());
	}

	@Override public void caseADecimalNumber(ADecimalNumber node) {
		this.currentResult = new IntValue(Integer.parseInt(node.getInteger().getText()));
	}

	@Override public void caseAOctalNumber(AOctalNumber node) {
		this.currentResult = new IntValue(Integer.decode(node.getOctal().getText()));
	}

	@Override public void caseAHexNumber(AHexNumber node) {
		this.currentResult = new IntValue(Integer.decode(node.getHex().getText()));
	}

	public static enum Type { BOOL, INT, STRING };
	public static abstract class Value {
		public abstract Type getType();
		public int getInt() { throw new IllegalArgumentException(); }
		public boolean getBool() { throw new IllegalArgumentException(); }
		public String getString() { throw new IllegalArgumentException(); } 
	}

	public static class IntValue extends Value {
		private final int value;
		public IntValue(int value) { this.value = value; }
		@Override public String toString() { return "" + this.value; }
		@Override public Type getType() { return Type.INT; }
		@Override public int getInt() { return value; }
	}

	public static class BoolValue extends Value {
		private final boolean value;
		BoolValue(boolean value) { this.value = value; }
		@Override public String toString() { return "" + this.value; }
		@Override public Type getType() { return Type.BOOL; }
		@Override public boolean getBool() { return value; }
	}

	public static class StringValue extends Value {
		private final String value;
		StringValue(String value) { this.value = value; }
		@Override public String toString() { return this.value; }
		@Override public Type getType() { return Type.BOOL; }
		@Override public String getString() { return value; }
	}
}
