package util;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class TestTable {
	@Test public void testRBTree() {
		Table<Integer, Integer> t = Table.<Integer, Integer> create();
		java.util.Random gen = new java.util.Random();

		for (int i = 0; i < 1000; i++) {
			Integer k = gen.nextInt(10000);
			Integer v = gen.nextInt(10000);
			t = t.put(k, v);
			assertTrue(t.contains(k));
			assertEquals(t.get(k), v);
			assertCorrectRBTree(t);
		}
	}

	private static void assertCorrectRBTree(Table<?,?> t) {
		assertNodesColored(t);
		assertRootIsBlack(t);
		assertRedNodesHaveTwoBlackChildren(t);
		assertSameNumberOfBlackNodesInAllPaths(t, 0, -1);
	}

	private static void assertNodesColored(Table<?,?> n) {
		if (n == null)
			return;
		assertTrue(n.isRed() || n.isBlack());
		assertNodesColored(n.left());
		assertNodesColored(n.right());
	}

	private static void assertRootIsBlack(Table<?,?> root) {
		assertTrue(root.isBlack());
	}

	private static void assertRedNodesHaveTwoBlackChildren(Table<?,?> t) {
		if (t == null)
			return;
		if (t.isRed()) {
			assertNotNull(t.left());
			assertNotNull(t.right());
			assertTrue(t.left().isBlack());
			assertTrue(t.right().isBlack());
		}
		assertRedNodesHaveTwoBlackChildren(t.left());
		assertRedNodesHaveTwoBlackChildren(t.right());
	}

	private static int assertSameNumberOfBlackNodesInAllPaths(Table<?,?> t, int blackCount, int pathBlackCount) {
		if (t == null) {
			if (pathBlackCount == -1) {
				pathBlackCount = blackCount;
			} else {
				assertEquals(blackCount, pathBlackCount);
			}
			return pathBlackCount;
		}
		if (t.isBlack()) {
			blackCount++;
		}
		pathBlackCount = assertSameNumberOfBlackNodesInAllPaths(t.left(), blackCount, pathBlackCount);
		pathBlackCount = assertSameNumberOfBlackNodesInAllPaths(t.right(), blackCount, pathBlackCount);
		return pathBlackCount;
	}
}
