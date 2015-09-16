package search;

import java.util.ArrayList;
import java.util.List;

public class State {


	private int paramSize;
	private List<Integer> numberList;//各引数番号順に並んだ、関数の番号

	public State(int paramSize, List<Integer> numberList){
		this.paramSize = paramSize;
		numberList = new ArrayList<Integer>(numberList);
	}
	
	/*
	 * 引数の個数
	 */
	public int getParamSize(){
		return paramSize;
	}
	
	/*
	 * 各引数番号順に並んだ、関数の番号
	 */
	public List<Integer> getNumberList(){
		return numberList;
	}
	
	
	/*
	 * 次の状態を取得する。
	 * 
	 * 各引数番号の関数のリストは評価値順にソートされている。
	 * 
	 * 各引数番号の関数のリストのそれぞれ次の関数を選択した状態を
	 * 次の状態とする。
	 * 現在(a,b,c)であれば、(1番目の引数の候補の関数のリストはa番目、2番目のリストはb番目、3番目のリストはc番目)
	 * (a+1,b,c)
	 * (a,b+1,c)
	 * (a,b,c+1)
	 * のいずれか
	 */
	public List<State> getNextStates(){
		List<State> next_states = new ArrayList<State>();
		
		for(int i = 0; i < paramSize; i++){
			int number = numberList.get(i);
			List<Integer> newNumList = new ArrayList<Integer>(numberList);
			newNumList.set(i, number +1);//対角要素を+1する.
			State s = new State(paramSize, newNumList);
			next_states.add(s);
		}
		return next_states;
	}
}
