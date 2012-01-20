package appel.ch01;


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
  
  /** The runtime environment, which currently just contains the output stream */
  private static Env env = new Env();
  public static Env getEnv() { return env; }
  public static void setEnv(Env env) { Interp.env = env; }

  /** "Interprets" an program given an AST composed of our limited node types */
  public static void interp(Stm s) {
    s.eval(null);
  }
}