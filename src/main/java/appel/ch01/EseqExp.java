package appel.ch01;

public class EseqExp implements Exp {
  Stm stm; Exp exp;
  EseqExp(Stm s, Exp e) {stm=s; exp=e;}
  
  @Override public IntAndTable eval(Table t) {
    Table stmRet = stm.eval(t);
    IntAndTable expVal = exp.eval(stmRet);
    return expVal;
  }
}
