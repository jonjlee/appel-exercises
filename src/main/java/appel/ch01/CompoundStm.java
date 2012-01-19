package appel.ch01;

public class CompoundStm extends Stm {
  Stm stm1, stm2;
  CompoundStm(Stm s1, Stm s2) {stm1=s1; stm2=s2;}
}
