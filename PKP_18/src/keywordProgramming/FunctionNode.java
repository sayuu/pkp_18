package keywordProgramming;

import java.util.List;

/**
 * bestRoots‚Ì’†‚É‚ ‚é1‚Â‚ÌRoot(=function node)‚ğ•\‚·B
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
