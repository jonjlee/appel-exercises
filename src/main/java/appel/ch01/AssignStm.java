package appel.ch01;

public class AssignStm extends Stm {
  String id; Exp exp;
  AssignStm(String i, Exp e) {id=i; exp=e;}
}
