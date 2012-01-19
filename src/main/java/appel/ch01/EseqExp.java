package appel.ch01;

public class EseqExp extends Exp {
  Stm stm; Exp exp;
  EseqExp(Stm s, Exp e) {stm=s; exp=e;}
}
