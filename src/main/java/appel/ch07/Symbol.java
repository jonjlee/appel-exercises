package appel.ch07;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Symbol implements Comparable<Symbol> {

	public static final AtomicInteger counter = new AtomicInteger(0);

	public final String name;
	public final int index;

	public Symbol(String name) {
		this(name, counter.getAndIncrement());
	}
	
	public Symbol(String name, int index) {
		this.name = name;
		this.index = index;
	}

	@Override public int compareTo(Symbol o) {
			int ret = name.compareTo(o.name);
			if (ret == 0) {
				ret = index - o.index; 
			}
			return ret;
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof Symbol) { return compareTo((Symbol) obj) == 0; }
		return false;
	}
	
	@Override public int hashCode() {
		return name.hashCode() + 31 * index;
	}
	
	@Override public String toString() {
		if (index <= 0) { return name; }
		return name + "_" + index;
	}

	public static class Var extends Symbol { 
		public Var(String name) { super(name); }
		public Var(String name, int index) { super(name, index); }
	};
	public static class InternalVar extends Symbol {
		public InternalVar(String name) { super(name); }
		public InternalVar(String name, int index) { super(name, index); }
	};
	public static class ExternalVar extends Symbol {
		public ExternalVar(String name) { super(name); }
		public ExternalVar(String name, int index) { super(name, index); }
	};
	public static class TypeVar extends Symbol {
		public TypeVar(String name) { super(name); }
		public TypeVar(String name, int index) { super(name, index); }
	};
}
