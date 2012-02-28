package appel.ch07;

import appel.ch07.Symbol.InternalVar;
import appel.ch07.Symbol.TypeVar;
import appel.ch07.Symbol.Var;

public abstract class Exp {

	public static class LetFuns extends Exp {
		public final FunLocal[] funs;
		public final Exp exp;
		public LetFuns(FunLocal[] funs, Exp exp) {
			this.funs = funs;
			this.exp = exp;
		}
	};
	
	/** Simple constants and arithmetic.
	 *     let v = a in e
	 *     let v = unop a in e
	 *     let v = a1 binop a2 in e
	 *     let v = a1 relop a2 in e 
	 */
	public static class LetAtom extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Atom atom;
		public final Exp exp;
		public LetAtom(Var v, Type type, Atom atom, Exp exp) {
			this.v = v;
			this.type = type;
			this.atom = atom;
			this.exp = exp;
		}
	};
	public static class LetUnop extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Unop op;
		public final Atom atom;
		public final Exp exp;
		public LetUnop(Var v, Type type, Unop op, Atom atom, Exp exp) {
			this.v = v;
			this.type = type;
			this.op = op;
			this.atom = atom;
			this.exp = exp;
		}
	};
	public static class LetBinop extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Binop op;
		public final Atom left;
		public final Atom right;
		public final Exp exp;
		public LetBinop(Var v, Type type, Binop op, Atom left, Atom right, Exp exp) {
			this.v = v;
			this.type = type;
			this.op = op;
			this.left = left;
			this.right = right;
			this.exp = exp;
		}
	};
	public static class LetRelop extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Relop op;
		public final Atom left;
		public final Atom right;
		public final Exp exp;
		public LetRelop(Var v, Type type, Relop op, Atom left, Atom right, Exp exp) {
			this.v = v;
			this.type = type;
			this.op = op;
			this.left = left;
			this.right = right;
			this.exp = exp;
		}
	};
	
	/** Functions
	 * let v1 : ty = v2(a1, ..., aN) in exp
	 */
	public static class LetApply extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Symbol.Var fun;
		public final Atom[] args;
		public final Exp exp;
		public LetApply(Var v, Type type, Var fun, Atom[] args, Exp exp) {
			this.v = v;
			this.type = type;
			this.fun = fun;
			this.args = args;
			this.exp = exp;
		}
	};
	
	/** The self-pointer is passed explicitly when calling a method:
	 *  let v1 : ty = v2[a_this](a1, ..., aN) in exp *)
	 */
	public static class LetApplyMethod extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Symbol.Var fun;
		public final Atom thisAtom;
		public final Atom[] args;
		public final Exp exp;
		public LetApplyMethod(Var v, Type type, Var fun, Atom thisAtom, Atom[] args, Exp exp) {
			this.v = v;
			this.type = type;
			this.fun = fun;
			this.thisAtom = thisAtom;
			this.args = args;
			this.exp = exp;
		}
	};
	
	/** External functions need to have their types specified explicitly.
     *  let v1 : ty1 = External_fun["f_name", ext_fun_ty](a1, ..., aN) in exp
     */
	public static class LetExt extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final String fun;
		public final Type extType;
		public final Atom[] args;
		public final Exp exp;
		public LetExt(Var v, Type type, String fun, Type extType, Atom[] args, Exp exp) {
			this.v = v;
			this.type = type;
			this.fun = fun;
			this.extType = extType;
			this.args = args;
			this.exp = exp;
		}
	};
	
	/** v(a1, ..., aN) */
	public static class TailCall extends Exp {
		public final Symbol.Var fun;
		public final Atom[] args;
		public TailCall(Var fun, Atom[] args) {
			this.fun = fun;
			this.args = args;
		}
	};
	
	/** Return a value from the current function */
	public static class Return extends Exp {
		public final Atom atom;
		public Return(Atom atom) {
			this.atom = atom;
		}
	};
	
	/** Abort with an error message */
	public static class Abort extends Exp {
		public final String message;
		public Abort(String message) {
			this.message = message;
		}
	};
	
	/** Conditional */
	public static class IfThenElse extends Exp {
		public final Atom atom;
		public final Exp ifExp;
		public final Exp elseExp;
		public IfThenElse(Atom atom, Exp ifExp, Exp elseExp) {
			this.atom = atom;
			this.ifExp = ifExp;
			this.elseExp = elseExp;
		}
	};
	
	/** IfType (a, ty_name, v, e1, e2):
     *
     *  if a instanceof ty_name then
     *     let v : ty_name = a in e1
     *  else
     *     e2
     */
	public static class IfType extends Exp {
		public final Atom atom;
		public final Symbol.TypeVar typeName;
		public final Symbol.Var v;
		public final Exp ifExp;
		public final Exp elseExp;
		public IfType(Atom atom, TypeVar type, Var v, Exp ifExp, Exp elseExp) {
			this.atom = atom;
			this.typeName = type;
			this.v = v;
			this.ifExp = ifExp;
			this.elseExp = elseExp;
		}
	};
	
	/** Variable definitions.
     *  LetVar variables are mutable; they may be modified by assignment.
     *
     *  let v : ty = a in e
     *  v : ty <- a; e
     */
	public static class LetVar extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Atom atom;
		public final Exp exp;
		public LetVar(Var v, Type type, Atom atom, Exp exp) {
			this.v = v;
			this.type = type;
			this.atom = atom;
			this.exp = exp;
		}
	};
	public static class SetVar extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Atom atom;
		public final Exp exp;
		public SetVar(Var v, Type type, Atom atom, Exp exp) {
			this.v = v;
			this.type = type;
			this.atom = atom;
			this.exp = exp;
		}
	};
	
	/** Array/pointer operations
     *  a1[a2] : ty <- a3; e
     *  let v : ty = a1[a2] in e
     */
	public static class SetSubscript extends Exp {
		public final Atom atom;
		public final Atom index;
		public final Type type;
		public final Atom value;
		public final Exp exp;
		public SetSubscript(Atom atom, Atom index, Type type, Atom value, Exp exp) {
			this.atom = atom;
			this.index = index;
			this.type = type;
			this.value = value;
			this.exp = exp;
		}
	};
	public static class LetSubscript extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Atom atom;
		public final Atom index;
		public final Exp exp;
		public LetSubscript(Var v, Type type, Atom atom, Atom index, Exp exp) {
			this.v = v;
			this.type = type;
			this.atom = atom;
			this.index = index;
			this.exp = exp;
		}

	};
	
	/** Record projection
     *  a1.var : ty <- a2; e
     *  let v1 : ty = a.v2 in e
     */
	public static class SetProject extends Exp {
		public final Atom atom;
		public final Symbol.InternalVar field;
		public final Type type;
		public final Atom value;
		public final Exp exp;
		public SetProject(Atom atom, InternalVar field, Type type, Atom value, Exp exp) {
			this.atom = atom;
			this.field = field;
			this.type = type;
			this.value = value;
			this.exp = exp;
		}
	};
	public static class LetProject extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Atom atom;
		public final Symbol.InternalVar field;
		public final Exp exp;
		public LetProject(Var v, Type type, Atom atom, InternalVar field, Exp exp) {
			this.v = v;
			this.type = type;
			this.atom = atom;
			this.field = field;
			this.exp = exp;
		}
	};
	
	/** Heap allocation.
     *
     *  String allocation:
     *     let v = "s" in e
     *  Array allocation:
     *     a1,...,aN are the dimensions, a is the initial value
     *     let v : ty array = new array[a1]...[aN] of a in e
     *  Object allocation:
     *     let v1 : ty = new v2 in e 
     */
	public static class LetString extends Exp {
		public final Symbol.Var v;
		public final String s;
		public final Exp exp;
		public LetString(Var v, String s, Exp exp) {
			this.v = v;
			this.s = s;
			this.exp = exp;
		}
	};
	public static class LetArray extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Atom[] dimensions;
		public final Atom elementType;
		public final Exp exp;
		public LetArray(Var v, Type type, Atom[] dimensions, Atom elementType, Exp exp) {
			this.v = v;
			this.type = type;
			this.dimensions = dimensions;
			this.elementType = elementType;
			this.exp = exp;
		}
	};
	public static class LetObject extends Exp {
		public final Symbol.Var v;
		public final Type type;
		public final Symbol.TypeVar objectType;
		public final Exp exp;
		public LetObject(Var v, Type type, TypeVar objectType, Exp exp) {
			this.v = v;
			this.type = type;
			this.objectType = objectType;
			this.exp = exp;
		}
	};
}
