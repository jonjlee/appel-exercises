package util;

import static org.testng.Assert.*;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.Test;
import appel.ch07.Symbol;

public class TestFieldMTable {
	@Test public void testTable() {
		FieldMTable<Integer> t = FieldMTable.create();
		java.util.Random gen = new java.util.Random();

		for (int i = 0; i < 1000; i++) {
			Symbol internal = new Symbol.Var("i");
			Symbol external = new Symbol.Var("e", gen.nextInt(100));
			int v = gen.nextInt(10000);

			t = t.add(external, internal, v);

			// Check that (internal,value) is returns for external symbol
			assertTrue(t.contains(external));
			boolean found = false;
			for (FieldMTable.Entry<Integer> entry : t.get(external)) {
				if (internal.equals(entry.symbol) && v == entry.value) {
					found = true;
				}
			}
			assertTrue(found, external + ", " + internal + " -> " + v + ") not found in table");

			// Check that (external,value) is returns for internal symbol
			FieldMTable.Entry<Integer> entry = t.getInternal(internal);
			assertEquals(entry.symbol, external);
			assertEquals(entry.value.intValue(), v);
		}
	}

	@Test public void internalSymbolsReturnedInInsertionOrder() {
		FieldMTable<Integer> t = FieldMTable.create();
		List<Symbol> internals = new LinkedList<Symbol>();
		java.util.Random gen = new java.util.Random();

		for (int i = 0; i < 1000; i++) {
			Symbol internal = new Symbol.Var("i");
			Symbol external = new Symbol.Var("e", gen.nextInt(100));
			int v = gen.nextInt(10000);

			internals.add(internal);
			t = t.add(external, internal, v);
		}
		
		List<Symbol> symbols = t.internals();
		for (int i = 0; i < symbols.size(); i++) {
			assertEquals(symbols.get(0), internals.get(0));
		}
	}
}
