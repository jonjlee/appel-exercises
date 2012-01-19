package appel.ch01;

import java.io.PrintStream;

public class Interp {

  /** Return number of expressions in an ExpList */
  public static int numargs(ExpList exps) {
    if (exps instanceof LastExpList) {
      return 1;
    }
    return 1 + numargs(((PairExpList) exps).tail);
  }

  /** Helper method to creates an ExpList from the arg list */ 
  public static ExpList explist(Exp firstArg, Exp... otherArgs) {
    if (otherArgs.length == 0) {
      return new LastExpList(firstArg);
    }
    ExpList tail = new LastExpList(otherArgs[otherArgs.length-1]);
    for (int i = otherArgs.length-2; i >= 0; i--) {
      tail = new PairExpList(otherArgs[i], tail);
    }
    return new PairExpList(firstArg, tail);
  }

  /** Return the maximum number of args provided to any print statement occurring in s */ 
  public static int maxargs(Stm s) {
    if (s instanceof PrintStm) {
      PrintStm printStm = (PrintStm) s;
      return Math.max(numargs(printStm.exps), maxargs(printStm.exps));
    } else if (s instanceof CompoundStm) {
      CompoundStm cs = (CompoundStm) s;
      return Math.max(maxargs(cs.stm1), maxargs(cs.stm2));
    } else if (s instanceof AssignStm) {
      return maxargs(((AssignStm) s).exp);
    }
    return 0;
  }

  /** maxarg helper for recursing into ExpLists, which can contain Exp objects that contain statements */
  private static int maxargs(ExpList exps) {
    if (exps instanceof LastExpList) {
      return maxargs(((LastExpList) exps).head);
    }
    PairExpList pairExpList = (PairExpList) exps;
    return Math.max(maxargs(pairExpList.head), maxargs(pairExpList.tail));
  }
  
  /** maxarg helper for recursing into Exp objects, two subclasses of which can contain statements */
  private static int maxargs(Exp e) {
    if (e instanceof EseqExp) {
      return maxargs(((EseqExp) e).stm);
    } else if (e instanceof OpExp) {
      OpExp opExp = (OpExp) e;
      return Math.max(maxargs(opExp.left), maxargs(opExp.right));
    }
    return 0;
  }
  
  /** "Interprets" an program given an AST composed of our limited node types */
  public void interp(Stm s) {
    interpStm(s, null);
  }
  
  /** Helper for interp that processes all subclasses of Stm */
  private Table interpStm(Stm s, Table t) {
    if (s instanceof PrintStm) {
      return print(((PrintStm) s).exps, t);
    } else if (s instanceof AssignStm) {
      AssignStm assignStm = (AssignStm) s;
      IntAndTable expVal = interpExp(assignStm.exp, t);
      return new Table(assignStm.id, expVal.i, expVal.t);
    } else if (s instanceof CompoundStm) {
      CompoundStm cs = (CompoundStm) s;
      Table stm1Ret= interpStm(cs.stm1, t);
      Table stm2Ret = interpStm(cs.stm2, stm1Ret);
      return stm2Ret;
    }
    return null;
  }

  /** Helper for interp that processes all subclasses of Exp using a symbol table */
  private IntAndTable interpExp(Exp e, Table t) {
    if (e instanceof NumExp) {
      return new IntAndTable(((NumExp) e).num, t);
    } else if (e instanceof IdExp) {
      return new IntAndTable(lookup(t, ((IdExp) e).id), t);
    } else if (e instanceof EseqExp) {
      EseqExp eseqExp = (EseqExp) e;
      Table stmRet = interpStm(eseqExp.stm, t);
      IntAndTable expVal = interpExp(eseqExp.exp, stmRet);
      return expVal;
    } else if (e instanceof OpExp) {
      OpExp opExp = (OpExp) e;
      IntAndTable exp1Val = interpExp(opExp.left, t);
      IntAndTable exp2Val = interpExp(opExp.right, exp1Val.t);
      if (opExp.oper == OpExp.Plus) {
        return new IntAndTable(exp1Val.i + exp2Val.i, exp2Val.t); 
      } else if (opExp.oper == OpExp.Minus) {
        return new IntAndTable(exp1Val.i - exp2Val.i, exp2Val.t); 
      } else if (opExp.oper == OpExp.Times) {
        return new IntAndTable(exp1Val.i * exp2Val.i, exp2Val.t); 
      } else if (opExp.oper == OpExp.Div) {
        return new IntAndTable(exp1Val.i / exp2Val.i, exp2Val.t); 
      } 
    }
    throw new UnsupportedOperationException("Unsupported Exp type: " + e);
  }

  int lookup(Table t, String id) {
    if (t == null) {
      throw new IllegalArgumentException("Variable " + id + " not found in table");
    }
    if (t.id.equals(id)) {
      return t.value;
    }
    return lookup(t.tail, id);
  }

  private Table print(ExpList exps, Table t) {
    if (exps instanceof LastExpList) {
      IntAndTable expVal = interpExp(((LastExpList) exps).head, t);
      print(expVal);
      return expVal.t;
    } else {
      PairExpList pairExpList = (PairExpList) exps;
      IntAndTable expVal = interpExp(pairExpList.head, t);
      print(expVal);
      return print(pairExpList.tail, expVal.t);
    }
  }

  private void print(IntAndTable t) {
    getOutStream().print(t.i);
  }

  /** Helper for interp that processes all subclasses of Exp */
  public PrintStream getOutStream() {
    return System.out;
  }
}

class IntAndTable {
  int i; Table t;
  public IntAndTable(int ii, Table tt) {i=ii; t=tt;}
}