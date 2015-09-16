package plugin.testSite.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import keywordProgramming.Function;

/**
 * 全探索用の、
 * 引数の組み合わせを全て列挙し、
 * リストのリストにして返すクラス
 * 
 * @author sayuu
 *
 */
public class Combinations {

	private ArrayList<ArrayList<Function>> all_combinations;
	private HashMap<Integer, ArrayList<Function>> list_map = new HashMap<Integer, ArrayList<Function>>();
	
	public Combinations(){
		all_combinations = new ArrayList<ArrayList<Function>>();
	}
	private void decideParams(int num, ArrayList<Function> head_list){
		if(num == list_map.size()){
//			System.out.println(head_list);
			all_combinations.add(new ArrayList<Function>(head_list));
			return;
		}
		Iterator<Function> it = list_map.get(num).iterator();
		num++;
		while(it.hasNext()){
			Function new_head = (Function) it.next();
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
	public ArrayList<ArrayList<Function>> getAllCombinations(HashMap<Integer, ArrayList<Function>> list_map){
		this.list_map = list_map;
		ArrayList<Function> head_list = new ArrayList<Function>();
		decideParams(0, head_list);
		return all_combinations;
	}

}
