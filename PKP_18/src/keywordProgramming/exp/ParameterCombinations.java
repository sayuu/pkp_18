package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 全探索用の、
 * 引数の組み合わせを全て列挙し、
 * リストのリストにして返すクラス
 * 
 * @author sayuu
 *
 */
public class ParameterCombinations {

	private ArrayList<ArrayList<FunctionTree>> all_combinations;
	private HashMap<Integer, ArrayList<FunctionTree>> list_map = new HashMap<Integer, ArrayList<FunctionTree>>();
	
	public ParameterCombinations(){
		all_combinations = new ArrayList<ArrayList<FunctionTree>>();
	}
	private void decideParams(int num, ArrayList<FunctionTree> head_list){
		if(num == list_map.size()){
//			System.out.println(head_list);
			all_combinations.add(new ArrayList<FunctionTree>(head_list));
			return;
		}
		Iterator<FunctionTree> it = list_map.get(num).iterator();
		num++;
		while(it.hasNext()){
			FunctionTree new_head = (FunctionTree) it.next();
			head_list.add(new_head);
			decideParams(num, head_list);
			head_list.remove(new_head);
		}
		return;
	}
	/**
	 * list_mapは、
	 * キーに引数の番号、
	 * 値にその引数の候補を保持したマップである。
	 * 
	 * 返り値all_combinations は、
	 * マップに保持された引数の全ての組み合わせを保持したリストである。
	 * 引数が３つある場合リストの中身はこのようになっている。
	 * [[1, 2, 3], [1, 2, 3], [1, 2, 3]...]
	 * @param list_map
	 * @return
	 */
	public ArrayList<ArrayList<FunctionTree>> getAllCombinations(HashMap<Integer, ArrayList<FunctionTree>> list_map){
		this.list_map = list_map;
		ArrayList<FunctionTree> head_list = new ArrayList<FunctionTree>();
		decideParams(0, head_list);
		return all_combinations;
	}
	
	public static void main(String[] args){
		
	}
}
