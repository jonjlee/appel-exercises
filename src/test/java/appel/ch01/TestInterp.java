package appel.ch01;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static appel.ch01.Interp.*;

public class TestInterp {

  private NumExp zero = new NumExp(0);
  private PrintStm simplePrintStm = print(zero);
  static Stm prog = 
      new CompoundStm(new AssignStm("a", new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3))),
      new CompoundStm(new AssignStm("b", new EseqExp(
                          new PrintStm(new PairExpList(new IdExp("a"), new LastExpList(new OpExp(new IdExp("a"), OpExp.Minus, new NumExp(1))))),
                          new OpExp(new NumExp(10), OpExp.Times, new IdExp("a"))
                      )),
                      new PrintStm(new LastExpList(new IdExp("b")))));
  
  // numargs() tests
  @Test public void numargsWithOneArg() {
    assertEquals(numargs(new LastExpList(zero)), 1);
  }
  @Test public void numargsWithMultipleArgs() {
    assertEquals(numargs(new PairExpList(zero, new PairExpList(zero, new LastExpList(zero)))), 3);
  }

  // explist() tests
  @Test public void explistSingleElementList() {
    assertEquals(numargs(explist(zero)), 1);
  }
  @Test public void explistMultiElementList() {
    assertEquals(numargs(explist(zero, zero, zero)), 3);
  }
  @Test public void explistAcceptsArrayAsVarargs() {
    assertEquals(numargs(explist(zero, new NumExp[] { zero, zero })), 3);
  }

  // maxargs() tests
  @Test public void maxargsSimplePrintStatement() {
    assertEquals(maxargs(simplePrintStm), 1);
  }
  @Test public void maxargsPrintStatementWithMultipleArgs() {
    Stm s = print(zero, zero, zero);
    assertEquals(maxargs(s), 3);
  }
  @Test public void maxargsCompoundStatement() {
    Stm s = new CompoundStm(simplePrintStm, new AssignStm("x", zero));
    assertEquals(maxargs(s), 1);
  }
  @Test public void maxargsESequence() {
    Stm s = print(new EseqExp(print(zero, zero), zero));
    assertEquals(maxargs(s), 2);
  }
  @Test public void maxargsOpExp() {
    Stm s = print(new OpExp(new EseqExp(print(zero, zero, zero), new IdExp("x")), 0, zero));
    assertEquals(maxargs(s), 3);
  }
  @Test public void maxargsAssignStm() {
    Stm s = new AssignStm("x", new EseqExp(print(zero, zero, zero), zero));
    assertEquals(maxargs(s), 3);
  }
  @Test public void maxargsLongProgram() {
    assertEquals(maxargs(prog), 2);
  }
  
  
  private PrintStm print(Exp head, Exp... tail) {
    return new PrintStm(explist(head, tail));
  }
}
