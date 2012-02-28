package appel.ch07;

import java.util.Map;
import appel.ch07.Symbol.Var;
import util.Table;

public class Env {
	
	public static final Table<Symbol, Type> BaseTypes;
	public static final Env Empty;
	
	static {
		BaseTypes = Table.<Symbol, Type> create()
				.put(new Symbol.TypeVar("void", 0), Type.Unit)
				.put(new Symbol.TypeVar("boolean", 0), Type.Bool)
				.put(new Symbol.TypeVar("char", 0), Type.Char)
				.put(new Symbol.TypeVar("int", 0), Type.Int)
				.put(new Symbol.TypeVar("float", 0), Type.Float)
				.put(new Symbol.TypeVar("String", 0), Type.String);

		Empty = new Env(
			Table.<Symbol, TypeDef> create(),
			BaseTypes,
			Table.<Symbol, Type> create(),
			Table.<Symbol, Map<Var, Type>> create(),
			null,
			null,
			null,
			IRState.objectType);
	}

	public final Table<Symbol, TypeDef> types;
	public final Table<Symbol, Type> base;
	public final Table<Symbol, Type> vars;
	public final Table<Symbol, Map<Symbol.Var, Type>> funs;
	public final Type ret;
	public final Map<Symbol.Var, TypeDef> cls;
	public final Symbol.Var brk;
	public final Type obj;
	public Env(Table<Symbol, TypeDef> types, Table<Symbol, Type> base, Table<Symbol, Type> vars, Table<Symbol, Map<Var, Type>> funs,
			Type ret, Map<Var, TypeDef> cls, Var brk, Type obj) {
		this.types = types;
		this.base = base;
		this.vars = vars;
		this.funs = funs;
		this.ret = ret;
		this.cls = cls;
		this.brk = brk;
		this.obj = obj;
	}
	
}
