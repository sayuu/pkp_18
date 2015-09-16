package keywordProgramming.exp;

import java.util.List;

/**
 * bestRootsÇÃíÜÇ…Ç†ÇÈ1Ç¬ÇÃRoot(=function node)Çï\Ç∑ÅB
 * @author sayuu
 *
 */
public class FunctionNode {
	private Function function;

	public FunctionNode(Function function){
		this.function = function;
	}

	public String toString(){
		return "FunctionNode[f=" + function.toString() + "]";
	}

	public Function getFunction(){
		return function;
	}

	public String getReturnType(){
		return function.getReturnType();
	}

	public List<String> getParameterTypes(){
		return function.getParameters();
	}
}
