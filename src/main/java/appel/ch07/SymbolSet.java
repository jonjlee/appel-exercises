package appel.ch07;

import util.Table;

public class SymbolSet {
	
	private final Table<Symbol, Object> map;
	
	public static SymbolSet create() {
		return new SymbolSet(Table.<Symbol, Object> create());
	}
	
	SymbolSet(Table<Symbol, Object> map) {
		this.map = map;
	}
	
	public SymbolSet put(Symbol sym) {
		return new SymbolSet(map.put(sym, null));
	}

	public boolean contains(Symbol sym) {
		return map.contains(sym);
	}
}
