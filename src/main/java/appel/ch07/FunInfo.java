package appel.ch07;

import appel.ch07.Symbol.Var;

/** 
 * A function definition has:
 *    1. a function classification  (global, method, or local)
 *    2. the function type
 *    3. then formal parameters
 *    4. the body
*/
public class FunInfo {
	public final FunClass funClass;
	public final Type type;
	public final Symbol.Var[] args;
	public final Exp exp;
	public FunInfo(FunClass funClass, Type type, Var[] args, Exp exp) {
		this.funClass = funClass;
		this.type = type;
		this.args = args;
		this.exp = exp;
	}
}
