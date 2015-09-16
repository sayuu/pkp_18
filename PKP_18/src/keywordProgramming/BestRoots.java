package keywordProgramming;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * bestRoots��\���N���X
 */
public class BestRoots {
	//String��type, �\��1�̗v�f�ɕ�����FunctionTree������̂�
	//FunctionTree��2�����z��[����][���best_r��]
	private TreeMap<String, FunctionTree[][]> root_table;
	//best_r�Ԃ�root�����߂�܂łɌ����ꎞ�I�ɕۑ����Ă����I�u�W�F�N�g
	//��ŕ]���l�Ń\�[�g����B
	private List<FunctionTree> tmpRoots = new ArrayList<FunctionTree>();
	private static final int _BEST_R_ = KeywordProgramming.BEST_R;
	private static final int _HEIGHT_ = KeywordProgramming.HEIGHT;
	
	private int max_tmpRoots_size;
	
	/**
	 * �R���X�g���N�^
	 * @param types �S�Ă̌^��ێ�����HashMap
	 */
	public BestRoots(TreeMap<String, Type> types){
		root_table = new TreeMap<String, FunctionTree[][]>();
		for(Type t: types.values()){
			FunctionTree[][] roots = new FunctionTree[_HEIGHT_][_BEST_R_];
			root_table.put(t.getName(), roots);
		}
		max_tmpRoots_size = 0;
	}
	
	/*
	 * �S�Ă̗v�f��null������B
	 */
	public void clearTable(){
		Set<String> keySet = root_table.keySet();
		Iterator<String> iter = keySet.iterator();
		while(iter.hasNext()){
			String key = (String)iter.next();
			FunctionTree[][] roots = root_table.get(key);
			for(int i = 0; i < _HEIGHT_; i++){
				for(int j = 0; j < _BEST_R_; j++){
					roots[i][j] = null;
				}
			}
			roots = null;
		}
		root_table.clear();
		tmpRoots.clear();
		max_tmpRoots_size = 0;
	}
	
	/**
	 * DP�̕\�̂���P�̌�_�ɕێ�����Ă���u���v�֐��،Q��S�ĕԂ�.
	 * @param type �^��
	 * @param height�@����
	 * @return �֐��،Q
	 */
	public FunctionTree[] getRoots(String type, int height){
		if(root_table.get(type) == null)
			return null;
		return root_table.get(type)[height];
	}
	
	/**
	 * DP�̕\�̂���P�̌�_�ɕێ�����Ă���u���v�֐��،Q�̌���Ԃ�.
	 * @param type �^��
	 * @param height ����
	 * @return �֐��،Q�̌�
	 */
	public int getSizeOfRoots(String type, int height){
		if(root_table.get(type) == null)
			return 0;
		FunctionTree[] trees = root_table.get(type)[height];
		int count = 0;
		for(FunctionTree t: trees){
			if(t != null)
				count++;
		}
		return count;
	}

	/**
	 * �u���v�֐��؂�ǉ�����
	 * @param root_function �u���v�֐���
	 */
	public void addRoot(FunctionTree root_function){
		/*
		 * ��������contains�����Ċm���߂�ƁA���Ɏ��Ԃ�������̂ł��Ȃ��B
		 * ���keepBestRoots���ŏd�����폜����B
		 */
		tmpRoots.add(root_function);	
	}

	/**
	 * DP�̕\�̂���P�̌�_�ɕێ�����Ă���u���v�֐��،Q��
	 * �]���l(ExplanationVector�̊e�v�f�̍��v�l)�̏��Ƀ\�[�g���A
	 * ���best_r�Ԗڂ܂ł��c���āA����ȉ��̊֐��؂͑S�č폜����B
	 * 
	 * @param type �^��
	 * @param height ����
	 */
	public void keepBestRoots(String type, int height){

		if(tmpRoots == null || tmpRoots.size() == 0)
			return;
		
		/*
		 * TreeSet���g�p���邱�Ƃɂ��A
		 * �d���𖳂����A�\�[�g���s���B
		 */
		TreeSet<FunctionTree> treeSet = new TreeSet<FunctionTree>(tmpRoots);
		
		int reserve_size =  treeSet.size();
		
		if(max_tmpRoots_size < reserve_size){
			max_tmpRoots_size = reserve_size;
		}
//		System.out.println(max_tmpRoots_size);
		
		if(reserve_size > _BEST_R_){
			//����best_r�ȏ�
			reserve_size = _BEST_R_;	//�����_BEST_R_�Ƃ���B
//			System.out.println("best r over!");
		}
		int i = 0;
		//best_r�Ԗڈȓ���root�������m��
		for(FunctionTree tree: treeSet){
			root_table.get(type)[height][i++] = tree;
			if(i == reserve_size)
				break;
		}

		//�ꎞ�I�u�W�F�N�g�̃N���A
		tmpRoots.clear();
	}


}
