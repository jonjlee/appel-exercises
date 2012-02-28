package appel.ch07;

import java.util.Map;
import util.FieldMTable;
import appel.ch07.Symbol.InternalVar;
import appel.ch07.Symbol.Var;

public class TypeDef {
	public final Symbol.Var[] parents;
	public final Map<Symbol.InternalVar, Type> constructors;
	public final FieldMTable<Type> methods; 
	public final FieldMTable<Type> fields;
	/** Builds declaration without class info; used to build initial dummy environment */
	public TypeDef() {
		parents = null;
		constructors = null;
		methods = null;
		fields = null;
	}
	public TypeDef(Var[] parents, Map<InternalVar, Type> constructors, FieldMTable<Type> methods, FieldMTable<Type> fields) {
		this.parents = parents;
		this.constructors = constructors;
		this.methods = methods;
		this.fields = fields;
	} 
}
