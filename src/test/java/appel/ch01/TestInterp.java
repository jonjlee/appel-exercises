package appel.ch01;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class TestInterp {

  @Test public void maxargsHandlesSimplePrintStatement() {
    Stm s = new PrintStm(new LastExpList(new NumExp(0)));
    assertEquals(Interp.maxargs(s), 1);
  }
  
}
