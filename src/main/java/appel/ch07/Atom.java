package appel.ch07;

public abstract class Atom {
	public static class Unit extends Atom {};
	public static class Nil extends Atom {};
	
	public static class Bool extends Atom {
		final boolean v;
		public Bool(boolean v) { this.v = v; }
	};
	
	public static class Char extends Atom {
		final char v;
		public Char(char v) { this.v = v; }
	};
	
	public static class Int extends Atom {
		final int v;
		public Int(int v) { this.v = v; }
	};
	
	public static class Float extends Atom {
		final float v;
		public Float(float v) { this.v = v; }
	};
	
	public static class Var extends Atom {
		final Symbol.Var name;
		public Var(Symbol.Var name) { this.name = name; }
	};
}
