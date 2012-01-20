package appel.ch01;

public class AssignStm implements Stm {
  String id; Exp exp;
  AssignStm(String i, Exp e) {id=i; exp=e;}
  
  @Override public Table eval(Table t) {
    IntAndTable expVal = exp.eval(t);
    return new Table(id, expVal.i, expVal.t);
  }
}
