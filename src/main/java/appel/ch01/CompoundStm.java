package appel.ch01;

public class CompoundStm implements Stm {
  Stm stm1, stm2;
  CompoundStm(Stm s1, Stm s2) {stm1=s1; stm2=s2;}
  
  @Override public Table eval(Table t) {
    Table stm1Ret= stm1.eval(t);
    Table stm2Ret = stm2.eval(stm1Ret);
    return stm2Ret;
  }
}
