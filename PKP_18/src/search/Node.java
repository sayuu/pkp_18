package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import keywordProgramming.FunctionTree;

public class Node implements Comparable<Node>{
	
	public static HashMap<Integer, ArrayList<keywordProgramming.FunctionTree>> param_list_map;
	
	private State state;//���
	
	private Node parent;//�e�m�[�h
	
	private int depth;//�[��
	
	public Node(State s, int depth){
		state = s;
		this.depth = depth;
	}
	
	public State getState(){
		return state;		
	}
	
	public int getDepth(){
		return depth;
	}
	
	public List<Node> getChildren(){
		List<Node> children = new ArrayList<Node>();
		for(State ns: state.getNextStates()){
			children.add(new Node(ns, this.depth + 1));
		}
		return children;
	}
	
	/*
	 * �\�z�R�X�g��Ԃ��B
	 * �\�z�R�X�g�́A
	 * �e�����ɑΉ�������̊֐��̕]���l�̍��v
	 */
	public static double getHeuristicCost(State state){
		double sum = 0.0;
		for(int i = 0; i < state.getParamSize(); i++){
			int number = state.getNumberList().get(i);
			List<FunctionTree> fList = param_list_map.get(i);
			FunctionTree ft = fList.get(number);
			sum += ft.getEvaluationValue();
		}
		return sum;
	}
	
	public void setParent(Node parent){
		this.parent = parent;
	}

	/*
	 * �\�z�R�X�g�Ń\�[�g����B
	 * 
	 * (�� Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Node o) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if(o == null)
			throw new NullPointerException();
		
		double c_this = getHeuristicCost(this.getState());
		double c = getHeuristicCost(o.getState());
		
		if(c_this < c){
			return -1;
		}else if(c_this > c){
			return 1;
		}else{
			return 0;
		}
	}
}
