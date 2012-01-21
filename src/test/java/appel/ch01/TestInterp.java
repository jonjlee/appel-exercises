package appel.ch01;

import static appel.ch01.Interp.explist;
import static appel.ch01.Interp.interp;
import static appel.ch01.Interp.maxargs;
import static appel.ch01.Interp.numargs;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestInterp {

  private NumExp zero = new NumExp(0);
  private NumExp one = new NumExp(1);
  private PrintStm simplePrintStm = print(zero);
  private Stm prog = 
      new CompoundStm(new AssignStm("a", new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3))),  // a=5+3
      new CompoundStm(new AssignStm("b", new EseqExp(                                           // b=( print(a, a-1), 10*a )  
                          new PrintStm(new PairExpList(new IdExp("a"), new LastExpList(new OpExp(new IdExp("a"), OpExp.Minus, new NumExp(1))))),
                          new OpExp(new NumExp(10), OpExp.Times, new IdExp("a"))
                      )),
                      new PrintStm(new LastExpList(new IdExp("b")))));                          // print(b)
                                                                                                // --> 8780
  private Stm complexProg = 
      // x = 5                                               -->     ; x=5
      // print(( print(x), x = x+5, print(x), y = x-1, y ))  --> 5109; x=10, y=9
      // print(y)                                            --> 9   ;
      // print( (5+( y = y+1, y )-12) * 10 / (1+1) )         --> 15  ; y=10
      // z = ( x = x+1, y=x+1, y+1)                          -->     ; x=11, y=12, z=13 
      // print(x, y, z)                                      --> 111213
      new CompoundStm(new AssignStm("x", new NumExp(5)), 
      new CompoundStm(print(
                        new EseqExp(print(new IdExp("x")),
                        new EseqExp(new AssignStm("x", new OpExp(new IdExp("x"), OpExp.Plus, new NumExp(5))),
                        new EseqExp(print(new IdExp("x")),
                        new EseqExp(new AssignStm("y", new OpExp(new IdExp("x"), OpExp.Minus, new NumExp(1))),
                                    new IdExp("y")))))),
      new CompoundStm(print(new IdExp("y")),
      new CompoundStm(print(
                        new OpExp(
                            new OpExp(
                                new OpExp(
                                    new OpExp(new NumExp(5), OpExp.Plus, new EseqExp(new AssignStm("y", new OpExp(new IdExp("y"), OpExp.Plus, new NumExp(1))), new IdExp("y"))),
                                    OpExp.Minus,
                                    new NumExp(12)),
                                OpExp.Times,
                                new NumExp(10)),
                            OpExp.Div,
                            new OpExp(one, OpExp.Plus, one))),
      new CompoundStm(new AssignStm("z", 
                        new EseqExp(new AssignStm("x", new OpExp(new IdExp("x"), OpExp.Plus, one)),
                        new EseqExp(new AssignStm("y", new OpExp(new IdExp("x"), OpExp.Plus, one)),
                                    new OpExp(new IdExp("y"), OpExp.Plus, one)))),
                      print(new IdExp("x"), new IdExp("y"), new IdExp("z")))))));
  
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
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  @BeforeClass public void replaceSystemOut() {
    System.setOut(new PrintStream(out));
  }
  @BeforeMethod public void resetOutputStream() {
    out.reset();
  }
  @Test public void interpSimplePrint() {
    interp(print(zero));
    assertEquals(out.toString(), "0");
  }
  @Test public void interpMultiArgPrint() {
    interp(print(zero, zero, zero));
    assertEquals(out.toString(), "000");
  }
  @Test public void interpCompoundPrints() {
    Stm s = new CompoundStm(simplePrintStm, new CompoundStm(simplePrintStm, simplePrintStm));
    interp(s);
    assertEquals(out.toString(), "000");
  }
  @Test public void interpAssignment() {
    Stm s = new CompoundStm(new AssignStm("x", zero), print(new IdExp("x"), one));
    interp(s);
    assertEquals(out.toString(), "01");
  }
  @Test public void interpESeq() {
    Stm s = new CompoundStm(new AssignStm("x", one), print(new EseqExp(print(zero, zero), new IdExp("x"))));
    interp(s);
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
    interp(s);
    assertEquals(out.toString(), "2031");
  }
  @Test public void interpProg() {
    interp(prog);
    assertEquals(out.toString(), "8780");
  }
  @Test public void interpComplexProg() {
    interp(complexProg);
    assertEquals(out.toString(), "5109915111213");
  }

  // helpers
  private PrintStm print(Exp head, Exp... tail) {
    return new PrintStm(explist(head, tail));
  }
}
