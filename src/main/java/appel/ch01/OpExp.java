package appel.ch01;

public class OpExp implements Exp {
  Exp left, right; int oper;
  final static int Plus=1,Minus=2,Times=3,Div=4;
  OpExp(Exp l, int o, Exp r) {left=l; oper=o; right=r;}
  
  @Override public IntAndTable eval(Table t) {
    IntAndTable exp1Val = left.eval(t);
    IntAndTable exp2Val = right.eval(exp1Val.t);
    if (oper == OpExp.Plus) {
      return new IntAndTable(exp1Val.i + exp2Val.i, exp2Val.t); 
    } else if (oper == OpExp.Minus) {
      return new IntAndTable(exp1Val.i - exp2Val.i, exp2Val.t); 
    } else if (oper == OpExp.Times) {
      return new IntAndTable(exp1Val.i * exp2Val.i, exp2Val.t); 
    } else if (oper == OpExp.Div) {
      return new IntAndTable(exp1Val.i / exp2Val.i, exp2Val.t); 
    }
    throw new UnsupportedOperationException("Unknown operator " + oper);
  }
}
