package appel.ch07;

public enum Unop {
	// arithmetic
	UMinusIntOp,
	UMinusFloatOp,
	UNotIntOp,
	UNotBoolOp,

	// numeric coercions
	UIntOfFloat,
	UFloatOfInt,
	UCharOfInt,
	UIntOfChar
}
