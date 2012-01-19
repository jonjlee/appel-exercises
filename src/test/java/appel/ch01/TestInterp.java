package appel.ch01;

import static appel.ch01.Interp.explist;
import static appel.ch01.Interp.maxargs;
import static appel.ch01.Interp.numargs;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestInterp {

  private NumExp zero = new NumExp(0);
  private NumExp one = new NumExp(1);
  private PrintStm simplePrintStm = print(zero);
  static Stm prog = 
      new CompoundStm(new AssignStm("a", new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3))),  // a=5+3
      new CompoundStm(new AssignStm("b", new EseqExp(                                           // b=( print(a, a-1), 10*a )  
                          new PrintStm(new PairExpList(new IdExp("a"), new LastExpList(new OpExp(new IdExp("a"), OpExp.Minus, new NumExp(1))))),
                          new OpExp(new NumExp(10), OpExp.Times, new IdExp("a"))
                      )),
                      new PrintStm(new LastExpList(new IdExp("b")))));                          // print(b)
                                                                                                // --> 8780
  
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
  @Test public void maxargsESeq() {
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
  
  // interp() tests
  ByteArrayOutputStream out;
  Interp interp;
  @BeforeMethod public void setup() {
    interp = spy(new Interp());
    out = new ByteArrayOutputStream();
    doReturn(new PrintStream(out)).when(interp).getOutStream();
  }
  @Test public void interpSimplePrint() {
    interp.interp(print(zero));
    assertEquals(out.toString(), "0");
  }
  @Test public void interpMultiArgPrint() {
    interp.interp(print(zero, zero, zero));
    assertEquals(out.toString(), "000");
  }
  @Test public void interpCompoundPrints() {
    Stm s = new CompoundStm(simplePrintStm, new CompoundStm(simplePrintStm, simplePrintStm));
    interp.interp(s);
    assertEquals(out.toString(), "000");
  }
  @Test public void interpAssignment() {
    Stm s = new CompoundStm(new AssignStm("x", zero), print(new IdExp("x"), one));
    interp.interp(s);
    assertEquals(out.toString(), "01");
  }
  @Test public void interpESeq() {
    Stm s = new CompoundStm(new AssignStm("x", one), print(new EseqExp(print(zero, zero), new IdExp("x"))));
    interp.interp(s);
    assertEquals(out.toString(), "001");
  }
  @Test public void interpOpExp() {
    Stm s = new CompoundStm(
        new AssignStm("x", one), 
        print(
            new OpExp(new IdExp("x"), OpExp.Plus, one),
            new OpExp(new IdExp("x"), OpExp.Minus, one),
            new OpExp(new IdExp("x"), OpExp.Times, new NumExp(3)),
            new OpExp(new IdExp("x"), OpExp.Div, one)));
    interp.interp(s);
    assertEquals(out.toString(), "2031");
  }
  @Test public void interpProg() {
    interp.interp(prog);
    assertEquals(out.toString(), "8780");
  }

  // helpers
  private PrintStm print(Exp head, Exp... tail) {
    return new PrintStm(explist(head, tail));
  }
}
