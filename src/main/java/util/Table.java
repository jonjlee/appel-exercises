package util;

import java.util.LinkedList;
import java.util.List;

/** Functional implementation of Red-Black Trees. */
public abstract class Table<K extends Comparable<? super K>, V> {
	/** Create an empty map */
	public static <K extends Comparable<? super K>, V> Table<K,V> create() { return new RBLeaf<K,V>(); }
	
	/** Returns a new RBMap that includes the given key/value */
	public Table<K,V> put(K k, V v){ return ins(k,v).makeBlack(); }
	/** Is the given key in this map? */
	public abstract boolean contains(K k);
	/** Return the value in this map that matches the given key */
	public abstract V get(K k);
	/** Return the number of elements in the map */
	public abstract int size();
	/** Return the entries in this map in sorted, ascending order */
	public abstract List<Entry<K, V>> toList();

	public abstract int hashCode();
	public abstract boolean equals(Object o);

	// "Private" methods for internal use only
	abstract boolean isBlack();
	abstract boolean isRed();
	abstract RBNode<K,V> asNode();
	abstract Table<K,V> makeBlack();
	abstract Table<K,V> makeRed();
	abstract Table<K,V> ins(K k, V v);
	abstract Table<K,V> left();
	abstract Table<K,V> right();
	
	boolean isBlackNode(){ return !isLeaf() && isBlack(); }
	boolean isRedNode(){ return !isLeaf() && isRed(); }
	
	// Is this node a leaf?
	abstract boolean isLeaf();
	// Return a new node without the given element
	abstract Table<K,V> replace(K k, V v);

	// Create a new internal node
	protected static <K extends Comparable<? super K>, V> RBNode<K,V> node(Color c, K k, V d, Table<K,V> l,Table<K,V> r) { return new RBNode<K,V>(c,k, d,l,r); }

	// A little messy... but better than keeping track of parents ;)
	protected static <K extends Comparable<? super K>, V> Table<K,V> balance(K key, V dat, Table<K,V> l, Table<K,V> r){
		if(l.isRedNode() && r.isRedNode())
			return node(Color.RED, key, dat, l.makeBlack(), r.makeBlack());
		if(l.isRedNode()){
			RBNode<K,V> L = l.asNode();
			if(L.left.isRedNode()){
				RBNode<K,V> LL = L.left.asNode();
				return node(Color.RED, L.key, L.value,
						node(Color.BLACK,LL.key,LL.value,LL.left,LL.right),
						node(Color.BLACK,key,dat,L.right,r));
			}
			if(L.right.isRedNode()){
				RBNode<K,V> LR = L.right.asNode();
				return node(Color.RED, LR.key, LR.value,
						node(Color.BLACK,L.key,L.value,L.left,LR.left),
						node(Color.BLACK,key,dat,LR.right,r));
			}
		}
		if(r.isRedNode()){
			RBNode<K,V> R = r.asNode();
			if(R.left.isRedNode()){
				RBNode<K,V> RL = R.left.asNode();
				return node(Color.RED, RL.key, RL.value,
						node(Color.BLACK,key,dat,l,RL.left),
						node(Color.BLACK,R.key,R.value,RL.right,R.right));
			}
			if(R.right.isRedNode()){
				RBNode<K,V> RR = R.right.asNode();
				return node(Color.RED, R.key, R.value,
						node(Color.BLACK,key,dat,l,R.left),
						node(Color.BLACK,RR.key,RR.value,RR.left,RR.right));
			}
		}
		return node(Color.BLACK,key,dat,l,r);
	}
	protected static <K extends Comparable<? super K>, V> Table<K,V> balleft(Table<K,V> l, K key, V y, Table<K,V> r){
		if(l.isRedNode()) return node(Color.RED,key,y,l.makeBlack(),r);
		if(r.isBlackNode()) return balance(key,y,l,r.makeRed());
		RBNode<K,V> right = r.asNode();
		RBNode<K,V> rtlt = right.left.asNode();
		return node(Color.RED,rtlt.key,rtlt.value,
				node(Color.BLACK,key,y,l,rtlt.left),
				balance(right.key,right.value,rtlt.right,right.right.makeRed()));
	}
	protected static <K extends Comparable<? super K>, V> Table<K,V> balright(Table<K,V> l, K key, V y, Table<K,V> r){
		if(r.isRedNode()) return node(Color.RED,key,y,l,r.makeBlack());
		if(l.isBlackNode()) return balance(key,y,l.makeRed(),r);
		RBNode<K,V> left = l.asNode();
		RBNode<K,V> ltrt = left.right.asNode();
		return node(Color.RED,ltrt.key,ltrt.value,
				balance(left.key,left.value,left.left.makeRed(),ltrt.left),
				node(Color.BLACK,key,y,ltrt.right,r));
	}
	protected static <K extends Comparable<? super K>, V> Table<K,V> append(Table<K,V> l, Table<K,V> r){
		if(l.isLeaf()) return r;
		if(r.isLeaf()) return l;
		RBNode<K,V> left = l.asNode(),
				  right = r.asNode();
		if(left.color.equals(right.color)){
			Color c = left.color;
			Table<K,V> rtlt = append(left.right,right.left);
			if(rtlt.isRedNode()){
				RBNode<K,V> rln = rtlt.asNode();
				return node(Color.RED,rln.key,rln.value,
						node(c,left.key,left.value,left.left,rln.left),
						node(c,right.key,right.value,rln.right,right.right));
			}
			if(c.isRed())
				return node(Color.RED,left.key,left.value,left.left,
						node(Color.RED,right.key,right.value,rtlt,right.right));
			return balleft(left.left,left.key,left.value,
					node(Color.BLACK,right.key,right.value,rtlt,right.right));
		}
		if(right.isRed())
			return node(Color.RED,right.key,right.value,append(left,right.left),right.right);
		return node(Color.RED,left.key,left.value,left.left,append(left.right,right));
	}
	
	public static class Entry<K, V> {
		final public K key;
		final public V value;
		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}
}

enum Color {
	RED,
	BLACK;
	
	public boolean isRed() { return RED.equals(this); }
	public boolean isBlack() { return BLACK.equals(this); }
}

/** Represents a Node of the tree, e.g., the non-empty tree */
class RBNode<K extends Comparable<? super K>,V> extends Table<K,V> {

	protected final Color color;
	protected final K key;
	protected final V value;
	protected final Table<K,V> left;
	protected final Table<K,V> right;
	private final int hash;

	public RBNode(Color color, K key, V value, Table<K,V> left, Table<K,V> right) {
		this.color = color;
		this.key = key;
		this.value = value;
		this.left = left;
		this.right = right;
		hash = key.hashCode() + (value == null ? 0 : value.hashCode()) + left.hashCode() + right.hashCode();
	}

	public boolean contains(K k) {
		int c = k.compareTo(key);
		if (c < 0)
			return left.contains(k);
		if (c > 0)
			return right.contains(k);
		return true;
	}

	public V get(K x) {
		int c = x.compareTo(key);
		if (c < 0)
			return left.get(x);
		if (c > 0)
			return right.get(x);
		return value;
	}

	public List<Table.Entry<K, V>> toList() {
		List<Table.Entry<K, V>> l = left.toList();
		l.add(new Table.Entry<K,V>(key, value));
		l.addAll(right.toList());
		return l;
	}

	public int size() { return 1 + left.size() + right.size(); }

	public String toString() { return "(node " + color + " " + value + " " + left + " " + right + ")"; }
	public int hashCode() { return hash; }
	public boolean equals(Object o) {
		if (!(o instanceof RBNode))
			return false;
		RBNode<?,?> oo = (RBNode<?,?>) o;
		return color.equals(oo.color) && key.equals(oo.key) && value.equals(oo.value) && left.equals(oo.left) && right.equals(oo.right);
	}

	Table<K,V> ins(K k, V v) {
		int c = k.compareTo(key);
		if (c == 0)
			return node(color, k, v, left, right);
		if (color.isBlack()) {
			if (c < 0)
				return balance(key, value, left.ins(k,v), right);
			return balance(key, value, left, right.ins(k,v));
		} else {
			if (c < 0)
				return node(Color.RED, key, value, left.ins(k,v), right);
			return node(Color.RED, key, value, left, right.ins(k,v));
		}
	}

	Table<K,V> replace(K k, V v) {
		int c = k.compareTo(key);
		if (c == 0)
			return node(color, k, v, left, right);
		if (c < 0)
			return node(color, key, value, left.replace(k,v), right);
		return node(color, key, value, left, right.replace(k,v));
	}

	Color getColor() { return color; } 
	V getValue() { return value; } 
	Table<K,V> left() { return left; } 
	Table<K,V> right() { return right; } 
	boolean isLeaf() { return false; }
	
	boolean isBlack() { return color.isBlack(); }
	boolean isRed() { return color.isRed(); }
	Table<K,V> makeBlack() { return node(Color.BLACK, key, value, left, right); }
	Table<K,V> makeRed() { return node(Color.RED, key, value, left, right); }
	RBNode<K,V> asNode() { return this; }
}

/** Represents a Leaf of the tree, e.g., the empty tree */
class RBLeaf<K extends Comparable<? super K>, V> extends Table<K,V>{
	
	public boolean contains(K x) { return false; }
	public V get(K x) { return null; }
	public List<Table.Entry<K, V>> toList() { return new LinkedList<Table.Entry<K,V>>(); }
	public int size() { return 0; }

	public String toString() { return ""; }
	public int hashCode() { return 82351; }
	public boolean equals(Object o) { return (o instanceof RBLeaf); }

	boolean isLeaf() { return true; }
	boolean isBlack() { return true; }
	boolean isRed() { return false; }
	Table<K,V> left() { return null; }
	Table<K,V> right() { return null; }
	
	RBNode<K,V> asNode() { throw new IllegalStateException("Leaf cannot be turned into a node"); }
	Table<K,V> makeBlack() { return this; }
	Table<K,V> makeRed() { throw new IllegalStateException("Leaf cannot be red"); }
	
	Table<K,V> ins(K k, V v) { return node(Color.RED, k, v, this, this); }
	Table<K,V> replace(K k, V v) { return node(Color.RED, k, v, this, this); }	
}

