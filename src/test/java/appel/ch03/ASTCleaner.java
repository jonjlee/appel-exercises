package appel.ch03;

import java.util.LinkedList;
import java.util.List;

import appel.ch03.analysis.DepthFirstAdapter;
import appel.ch03.node.*;

public class ASTCleaner extends DepthFirstAdapter {
	@Override public void outAVarOrArrayDecl(AVarOrArrayDecl node) {
		int dimensions = getDimensions(node.getDims());
		if (dimensions > 0) {
			node.replaceBy(new AVarDecl(convertToArray(node.getType(), dimensions), node.getId()));
		}
	}
	@Override public void outAVarOrArrayDef(AVarOrArrayDef node) {
		int dimensions = getDimensions(node.getDims());
		if (dimensions > 0) {
			node.replaceBy(new AVarDef(convertToArray(node.getType(), dimensions), node.getId(), node.getExpr()));
		}
	}
	@Override public void outAVarsOrArraysDef(AVarsOrArraysDef node) {
		PType type = node.getType();
		List<PDef> defs = new LinkedList<PDef>();
		for (PVarAndInit varAndInit : node.getVars()) {
			defs.add(convertToVarDef((PType) type.clone(), varAndInit));
		}
		node.replaceBy(new AVarsDef(defs));
	}
	@Override public void outAArrayWithSizeType(AArrayWithSizeType node) {
		int dimensions = getDimensions(node.getDims());
		if (dimensions > 0) {
			node.replaceBy(convertToArray(node.getType(), dimensions));
		}
	}
	private int getDimensions(LinkedList<?> dimensions) {
		return (dimensions == null) ? 0 : dimensions.size();
	}
	private PType convertToArray(PType type, int dimensions) {
		for (int i = 0; i < dimensions; i++) {
			type = new AArrayType(type); 
		}
		return type;
	}
	private AVarDef convertToVarDef(PType type, PVarAndInit pVarAndInit) {
		AVarAndInit varAndInit = (AVarAndInit) pVarAndInit;
		type = convertToArray(type, getDimensions(varAndInit.getDims()));
		return new AVarDef(type, varAndInit.getId(), varAndInit.getExpr());
	}
}
