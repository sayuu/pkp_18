package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * �S�T���p�́A
 * �����̑g�ݍ��킹��S�ė񋓂��A
 * ���X�g�̃��X�g�ɂ��ĕԂ��N���X
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
	public ArrayList<ArrayList<FunctionTree>> getAllCombinations(HashMap<Integer, ArrayList<FunctionTree>> list_map){
		this.list_map = list_map;
		ArrayList<FunctionTree> head_list = new ArrayList<FunctionTree>();
		decideParams(0, head_list);
		return all_combinations;
	}
	
	public static void main(String[] args){
		
	}
}
