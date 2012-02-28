package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import appel.ch07.Symbol;

public class FieldMTable<T> {

	final int count;
	final Table<Symbol, List<Entry<T>>> table;
	private Map<Symbol, Entry<T>> internalTable = null;

	public static <T> FieldMTable<T> create() {
		return new FieldMTable<T>();
	}

	FieldMTable() {
		this(Table.<Symbol, List<Entry<T>>> create(), 0);
	}

	FieldMTable(Table<Symbol, List<Entry<T>>> table, int count) {
		this.table = table;
		this.count = count;
	}

	public FieldMTable<T> add(Symbol external, Symbol internal, T x) {
		// Create new entry
		Entry<T> entry = new Entry<T>(internal, x, count);
		
		// Add to existing list if external symbol already exists, otherwise create a new list 
		List<Entry<T>> entries = table.get(external);
		if (entries == null) {
			entries = Collections.singletonList(entry);
		} else {
			entries = new LinkedList<FieldMTable.Entry<T>>(entries);
			entries.add(entry);
		}

		return new FieldMTable<T>(table.put(external, entries), count+1);
	}

	public boolean contains(Symbol external) {
		return table.contains(external);
	}

	/** Returns a list of Entry objects with the internal names/values stored for a given external name. 
	 *  Returns null if external symbol is not in the table. */
	public List<Entry<T>> get(Symbol external) {
		return table.get(external);
	}

	/** Returns an Entry object with the external name and value stored for the given internal name. 
	 *  Returns null if internal symbol is not in the table. */
	public Entry<T> getInternal(Symbol internal) {
		return getInternalTable().get(internal);
	}
	
	/** Returns a list of the external symbols in insertion order */
	public List<Symbol> keys() {
		List<Table.Entry<Symbol, List<Entry<T>>>> entries = table.toList();
		Collections.sort(entries, new Comparator<Table.Entry<Symbol, List<Entry<T>>>>() {
			public int compare(Table.Entry<Symbol, List<Entry<T>>> o1, Table.Entry<Symbol, List<Entry<T>>> o2) {
				Entry<T> max1 = Collections.max(o1.value, new EntryComparator());
				Entry<T> max2 = Collections.max(o2.value, new EntryComparator());
				return max1.index - max2.index;
			}
		});
		
		List<Symbol> ret = new ArrayList<Symbol>(entries.size());
		for (Table.Entry<Symbol, List<Entry<T>>> entry : entries) {
			ret.add(entry.key);
		}
		return ret;
	}
	
	/** Returns a list of the internal symbols in insertion order */
	public List<Symbol> internals() {
		List<Table.Entry<Symbol, List<Entry<T>>>> entries = table.toList();
		List<Entry<T>> internals = new ArrayList<Entry<T>>();
		
		for (Table.Entry<Symbol, List<Entry<T>>> entry : entries) {
			internals.addAll(entry.value);
		}
		
		Collections.sort(internals, new EntryComparator());

		List<Symbol> ret = new ArrayList<Symbol>(internals.size());
		for (Entry<T> entry : internals) {
			ret.add(entry.symbol);
		}
		return ret;
	}
	
	/** Returns symbols in insertion order in the format "[external_sym{internal_sym}=value; ...]*/
	@Override public String toString() {
		StringBuilder s = new StringBuilder("[");
		List<Symbol> externals = keys();
		for (Symbol external : externals) {
			List<Entry<T>> internals = get(external);
			for (Entry<T> entry : internals) {
				if (s.length() > 1) {
					s.append("; ");
				}
				s.append(external).append("{").append(entry.symbol).append("}=").append(entry.value);
			}
		}
		return s.append("]").toString();
	}

	// Returns a map of internal symbol -> (external symbol, value)   
	private Map<Symbol, Entry<T>> getInternalTable() {
		if (internalTable == null) {
			// Iterate table [external -> (internal, value) list] and build new map [internal -> (external, value)]
			internalTable = new HashMap<Symbol, Entry<T>>();
			List<Table.Entry<Symbol, List<Entry<T>>>> entries = table.toList();
			for (Table.Entry<Symbol, List<Entry<T>>> entry : entries) {
				for (Entry<T> internals : entry.value) {
					internalTable.put(internals.symbol, new Entry<T>(entry.key /* external symbol */, internals.value, internals.index));
				}
			}
		}
		return internalTable;
	}

	public static class Entry<T> {
		public final Symbol symbol;
		public final T value;
		protected final int index;
		private Entry(Symbol symbol, T value, int index) {
			this.symbol = symbol;
			this.value = value;
			this.index = index;
		}
		@Override public String toString() {
			return symbol + "," + value; 
		}
	}
	
	public static class EntryComparator implements Comparator<Entry<?>> {
		public int compare(Entry<?> o1, Entry<?> o2) {
			return o1.index - o2.index;
		}
		
	}
}