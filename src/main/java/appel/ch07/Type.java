	package appel.ch07;


public class Type {

	public static final Type Unit = new Type();
	public static final Type Nil = new Type();
	public static final Type Any = new Type();
	public static final Type Bool = new Type();
	public static final Type Char = new Type();
	public static final Type String = new Type();
	public static final Type Int = new Type();
	public static final Type Float = new Type();
	
	public static class Array extends Type {
		public final Type elementType;
		public Array(Type elementType) { this.elementType = elementType; }
	};

	public static class Fun extends Type {
		public final Type[] args;
		public final Type ret;
		public Fun(Type[] args, Type ret) {
			this.args = args;
			this.ret = ret;
		}
	};

	public static class Method extends Type {
		public final Type thisType;
		public final Type[] args;
		public final Type ret;
		public Method(Type thisType, Type[] args, Type ret) {
			this.thisType = thisType;
			this.args = args;
			this.ret = ret;
		}
	};

	public static class Object extends Type {
		public final Symbol.TypeVar name;
		public Object(Symbol.TypeVar name) { this.name = name; }
	};
	
	private Type() {}
}
