package appel.ch07;

import util.Table;
import appel.ch07.Symbol.Var;

public class Prog {
	public final Table<Symbol, TypeDef> types;
	public final SymbolSet absTypes;
	public final Table<Symbol, FunInfo> funs;
	public final Symbol.Var main;
	public final Symbol.Var object;
	public Prog(Table<Symbol, TypeDef> types, SymbolSet absTypes, Table<Symbol, FunInfo> funs, Var main, Var object) {
		this.types = types;
		this.absTypes = absTypes;
		this.funs = funs;
		this.main = main;
		this.object = object;
	}
}
