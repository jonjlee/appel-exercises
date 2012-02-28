package appel.ch07;

public class IRState {
	public static final Symbol.Var thisVar = new Symbol.Var("this", 0);
	public static final Symbol.Var superVar = new Symbol.Var("super", 0);

	public static final Atom.Var thisAtom = new Atom.Var(thisVar);
	public static final Atom.Var superAtom = new Atom.Var(superVar);

	public static final String objectName = "FjObject";
	public static final Symbol.TypeVar objectVar = new Symbol.TypeVar(objectName, 0);
	public static final Type objectType = new Type.Object(objectVar);
	public static final Symbol.Var objectInitVar = new Symbol.Var("FjObject$init", 0);
}
