package appel.ch01;

public class PairExpList extends ExpList {
  Exp head; ExpList tail;
  public PairExpList(Exp h, ExpList t) {head=h; tail=t;}
}
