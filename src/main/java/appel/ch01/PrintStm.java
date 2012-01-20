package appel.ch01;

public class PrintStm implements Stm {
  ExpList exps;
  PrintStm(ExpList e) {exps=e;}
  
  @Override public Table eval(Table t) {
    return interpPrint(exps, t);
  }

  private Table interpPrint(ExpList exps, Table t) {
    if (exps instanceof LastExpList) {
      IntAndTable expVal = ((LastExpList) exps).head.eval(t);
      print(expVal.i);
      return expVal.t;
    } else {
      PairExpList pairExpList = (PairExpList) exps;
      IntAndTable expVal = pairExpList.head.eval(t);
      print(expVal.i);
      return interpPrint(pairExpList.tail, expVal.t);
    }
  }

  private void print(int i) {
    Interp.getEnv().out.print(i);
  }
}
