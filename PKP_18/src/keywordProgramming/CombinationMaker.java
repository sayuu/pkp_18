package keywordProgramming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * �^����ꂽ�����ő�l�Ƃ��鏇��̑g�ݍ��킹��
 * ���ׂďo�͂���N���X�B
 * 
 * @author sayuu
 *
 */
public class CombinationMaker {

	private ArrayList<ArrayList<Integer>> all_combinations;
	private int fNumber;	//��
	
	public CombinationMaker(){
		all_combinations = new ArrayList<ArrayList<Integer>>();
	}
	
	private void decideCombinations(ArrayList<Integer> head_list, TreeSet<Integer> tail_list){
		if(fNumber == head_list.size()){
//			System.out.println(head_list);
			all_combinations.add(new ArrayList<Integer>(head_list));
			return;
		}
		Iterator<Integer> it = tail_list.iterator();
		while(it.hasNext()){
			Integer new_head = (Integer) it.next();
			head_list.add(new_head);
			TreeSet<Integer> new_tail_list = new TreeSet<Integer>(tail_list);
			new_tail_list.remove(new_head);
			decideCombinations(head_list, new_tail_list);
			head_list.remove(new_head);
		}
		return;
	}
	
	public static void main(String[] args) {
		CombinationMaker sp = new CombinationMaker();
		ArrayList<ArrayList<Integer>> allCombinations = sp.getAllCombinations(5);
		
		for(ArrayList<Integer> comp: allCombinations){
			System.out.println(comp);
		}
	}
	
	/**
	 * number�܂ł̏���̑S�Ă̑g�ݍ��킹��Ԃ����\�b�h
	 * number = 3�Ȃ�A
	 * ����̑g�ݍ��킹��
	 * 1,2,3
	 * 1,3,2
	 * 2,1,3
	 * 2,3,1
	 * 3,1,2
	 * 3,2,1
	 * ��6�ʂ肠��B
	 * �����̂U�����X�g�ɂ������̂�Ԃ��B
	 * @param number
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> getAllCombinations(int number){
		fNumber = number;
		ArrayList<Integer> head_list = new ArrayList<Integer>();
		TreeSet<Integer> tail_list = new TreeSet<Integer>();
		for(int i = 1;i <= number; i++){
			tail_list.add(i);
		}
		decideCombinations(head_list, tail_list);
		return all_combinations;
	}
}
