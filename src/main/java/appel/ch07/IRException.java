package appel.ch07;

import java.util.Map;
import appel.ch07.Symbol.Var;

@SuppressWarnings({"serial", "unused"})
public abstract class IRException extends RuntimeException {

	IRException(String message, int line, int pos) {
		super(message);
	}

	public static class UnboundVar extends IRException { 
		private final Symbol.Var var;
		public UnboundVar(int line, int pos, Var var) {
			super("Unbound variable: " + var, line, pos);
			this.var = var;
		}
	}
	public static class UnboundType extends IRException { 
		private final Symbol.Var type;
		public UnboundType(int line, int pos, Var type) {
			super("Unbound type: " + type, line, pos);
			this.type = type;
		}
	}
	public static class UnboundClass extends IRException { 
		private final Symbol.Var clss;
		public UnboundClass(int line, int pos, Var clss) {
			super("Unbound class: " + clss, line, pos);
			this.clss = clss;
		}
	}
	public static class UnboundLabel extends IRException { 
		private final Symbol.Var lbl;
		public UnboundLabel(int line, int pos, Var lbl) {
			super("Unbound label: " + lbl, line, pos);
			this.lbl = lbl;
		}
	}
	public static class NotImplemented extends IRException {
		NotImplemented(String message, int line, int pos) {
			super("Not Implemented: " + message, line, pos);
		} 
	}
	public static class InconvertibleTypes extends IRException {
		InconvertibleTypes(String message, int line, int pos) {
			super(message, line, pos);
		} 
	}
	public static class TypeError extends IRException {
		TypeError(String message, int line, int pos) {
			super(message, line, pos);
		} 
	}
	public static class TypeMismatch extends IRException {
		private final Type type1, type2;
		public TypeMismatch(String message, int line, int pos, Type type1, Type type2) {
			super(message, line, pos);
			this.type1 = type1;
			this.type2 = type2;
		}
	}
	public static class ArityMismatch extends IRException { 
		private final int arity1, arity2;
		public ArityMismatch(String message, int line, int pos, int arity1, int arity2) {
			super(message, line, pos);
			this.arity1 = arity1;
			this.arity2 = arity2;
		}
	}
	public static class StringError extends IRException { 
		private final String s;

		public StringError(String message, int line, int pos, String s) {
			super(message, line, pos);
			this.s = s;
		}
	}
	public static class StringIntError extends IRException {
		private final String s;
		private final int i;
		public StringIntError(String message, int line, int pos, String s, int i) {
			super(message, line, pos);
			this.s = s;
			this.i = i;
		}
	}
	public static class StringVarError extends IRException {
		private final String s;
		private final Symbol.Var var;
		public StringVarError(String message, int line, int pos, String s, Var var) {
			super(message, line, pos);
			this.s = s;
			this.var = var;
		}
	}
	public static class StringTypeError extends IRException {
		private final String s;
		private final Type type;
		public StringTypeError(String message, int line, int pos, String s, Type type) {
			super(message, line, pos);
			this.s = s;
			this.type = type;
		}
	}
	public static class StringVarVarError extends IRException {
		private final String s;
		private final Symbol.Var var1;
		private final Symbol.Var var2;
		public StringVarVarError(String message, int line, int pos, String s, Var var1, Var var2) {
			super(message, line, pos);
			this.s = s;
			this.var1 = var1;
			this.var2 = var2;
		}
	}
	public static class OverloadError extends IRException { 
		private final Symbol.Var var;
		private final Type[] classes;
		private final Map<Symbol.Var, Type> args;
		public OverloadError(String message, int line, int pos, Var var, Type[] classes, Map<Var, Type> args) {
			super(message, line, pos);
			this.var = var;
			this.classes = classes;
			this.args = args;
		}
	}
}
