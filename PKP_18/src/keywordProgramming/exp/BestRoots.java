package keywordProgramming.exp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * bestRootsを表すクラス
 */
public class BestRoots {
	//Stringはtype, 表の1つの要素に複数のFunctionTreeが入るので
	//FunctionTreeは2次元配列[高さ][上位best_r番]
	private  TreeMap<String, FunctionTree[][]> root_table;
	//best_r番のrootを決めるまでに候補を一時的に保存しておくオブジェクト
	//後で評価値でソートする。
	private  List<FunctionTree> tmpRoots = new ArrayList<FunctionTree>();
	private static final int _BEST_R_ = KeywordProgramming.BEST_R;
	private static final int _HEIGHT_ = KeywordProgramming.HEIGHT;
	
	private int max_tmpRoots_size;
	
	/**
	 * コンストラクタ
	 * @param types 全ての型を保持するHashMap
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
	 * 全ての要素にnullを入れる。
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
	 * DPの表のある１つの交点に保持されている「根」関数木群を全て返す.
	 * @param type 型名
	 * @param height　高さ
	 * @return 関数木群
	 */
	public FunctionTree[] getRoots(String type, int height){
		if(root_table.get(type) == null)
			return null;
		return root_table.get(type)[height];
	}
	
	/**
	 * DPの表のある１つの交点に保持されている「根」関数木群の個数を返す.
	 * @param type 型名
	 * @param height 高さ
	 * @return 関数木群の個数
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
	 * 「根」関数木を追加する
	 * @param root_function 「根」関数木
	 */
	public void addRoot(FunctionTree root_function){
		/*
		 * いちいちcontainsをして確かめると、非常に時間がかかるのでしない。
		 * 後でkeepBestRoots内で重複を削除する。
		 */
		tmpRoots.add(root_function);	
	}

	/**
	 * DPの表のある１つの交点に保持されている「根」関数木群を
	 * 評価値(ExplanationVectorの各要素の合計値)の順にソートし、
	 * 上位best_r番目までを残して、それ以下の関数木は全て削除する。
	 * 
	 * @param type 型名
	 * @param height 高さ
	 */
	public void keepBestRoots(String type, int height){

		if(tmpRoots == null || tmpRoots.size() == 0)
			return;
		
		/*
		 * TreeSetを使用することにより、
		 * 重複を無くしつつ、ソートも行う。
		 */
		TreeSet<FunctionTree> treeSet = new TreeSet<FunctionTree>(tmpRoots);
		
		int reserve_size =  treeSet.size();
		
		if(max_tmpRoots_size < reserve_size){
			max_tmpRoots_size = reserve_size;
		}
//		System.out.println(max_tmpRoots_size);
		
		if(reserve_size > _BEST_R_){
			//個数がbest_r個以上
			reserve_size = _BEST_R_;	//上限を_BEST_R_とする。
//			System.out.println("best r over!");
		}
		int i = 0;
		//best_r番目以内のrootだけを確保
		for(FunctionTree tree: treeSet){
			root_table.get(type)[height][i++] = tree;
			if(i == reserve_size)
				break;
		}

		//一時オブジェクトのクリア
		tmpRoots.clear();
	}


}
