package appel.ch07;

import appel.ch07.Symbol.Var;

public class FunLocal {
	public final Symbol.Var var;
	public final Type type;
	public final Symbol.Var[] args;
	public final Exp exp;
	public FunLocal(Var var, Type type, Var[] args, Exp exp) {
		this.var = var;
		this.type = type;
		this.args = args;
		this.exp = exp;
	}
}
