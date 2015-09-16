package plugin.testSite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import keywordProgramming.Function;

/**
 * �S�T���p�́A
 * �����̑g�ݍ��킹��S�ė񋓂��A
 * ���X�g�̃��X�g�ɂ��ĕԂ��N���X
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
	 * list_map�́A
	 * �L�[�Ɉ����̔ԍ��A
	 * �l�ɂ��̈����̌���ێ������}�b�v�ł���B
	 * 
	 * �Ԃ�lall_combinations �́A
	 * �}�b�v�ɕێ����ꂽ�����̑S�Ă̑g�ݍ��킹��ێ��������X�g�ł���B
	 * �������R����ꍇ���X�g�̒��g�͂��̂悤�ɂȂ��Ă���B
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
