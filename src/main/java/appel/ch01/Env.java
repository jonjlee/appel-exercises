package appel.ch01;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Env {
  public PrintStream out = System.out;

  public Env() {
    out = System.out;
  }
  
  public Env(ByteArrayOutputStream out) {
    this.out = new PrintStream(out);
  }

}
