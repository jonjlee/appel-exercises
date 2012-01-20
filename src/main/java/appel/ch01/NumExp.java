package appel.ch01;

public class NumExp implements Exp {
  int num;
  NumExp(int n) {num=n;}
  
  @Override public IntAndTable eval(Table t) {
    return new IntAndTable(num, t);
  }
}
