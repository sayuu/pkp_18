package keywordProgramming.exp;

import java.util.List;

/**
 * bestRootsの中にある1つのRoot(=function node)を表す。
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
