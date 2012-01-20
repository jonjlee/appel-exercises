package appel.ch01;

public class IdExp implements Exp {
  String id;
  IdExp(String i) {id=i;}
  
  @Override public IntAndTable eval(Table t) {
    return new IntAndTable(lookup(t, id), t);
  }
  
  private int lookup(Table t, String id) {
    if (t == null) {
      throw new IllegalArgumentException("Variable " + id + " not found in table");
    }
    if (t.id.equals(id)) {
      return t.value;
    }
    return lookup(t.tail, id);
  }
}
