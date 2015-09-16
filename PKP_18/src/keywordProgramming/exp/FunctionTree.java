package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

/**
 * 1つのFunctionTreeのノードを表すクラス
 * @author sayuu
 *
 */
public class FunctionTree implements Comparable<FunctionTree>{
	
	//関数本体(rootの持っているeはNode単体の評価値)
	private FunctionNode root;
	
	//この関数木の「子」関数木。関数の引数に相当する。引数の順番を保つため、要素にnullが入っていることもある。
	private ArrayList<FunctionTree> children = new ArrayList<FunctionTree>();
	
	//木を構成するノードの数
	private int length;
	
	//この関数木がユーザに選択されたかを表すフラグ
	private boolean flg_selected = false;

	//デバッグ用
	private String completeMethodString;	
	
	//この木の「根」関数ノードのラベルのリスト
	HashMap<String, Integer> numOfSameWordsFunc = new HashMap<String, Integer>();

	//評価ベクトル
	public ExplanationVector e_vec;

	private Params para;
	/**
	 * コンストラクタ
	 * 1つのノードに必要なものは、木のルートとなるFunctionRoot1つ。
	 * 
	 * @param f 関数
	 * @param input_words 入力キーワードのList
	 * @param numOfSameWordsIn　個々のキーワードの入力クエリ中の出現個数
	 */
	public FunctionTree(Params para, Function f, List<Word> input_words, HashMap<String, Integer> numOfSameWordsIn){
		this.para = para;
		root = new FunctionNode(f);
		length = 1;
		//label_list.addAll(Arrays.asList(f.getLabels()));

		//FunctionのLabelに含まれる同一の単語ごとにその個数をカウントする
		for(String flab: root.getFunction().getLabels()){

			if(numOfSameWordsFunc.containsKey(flab) == false){
				//キーが無ければ新しくキーを加える
				numOfSameWordsFunc.put(flab, 1);
			}else{
				//キーがすでにあるとき、数を１つ増やす
				int num = numOfSameWordsFunc.get(flab);
				num++;
				numOfSameWordsFunc.put(flab, num);
			}
		}
		//numOfSameWordsTree.putAll(numOfSameWordsFunc);
		
		String flg_common = para.common_subsequence;
		if(flg_common.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_OFF))
			e_vec = new ExplanationVector(para, input_words.size()).calcExplanationVector(input_words, numOfSameWordsIn, this);
		else
			//キーワードとラベルの部分一致でもＯＫ.
			e_vec = new ExplanationVector(para, input_words.size()).calcExplanationVector2(input_words, numOfSameWordsIn, this);
	}

	public FunctionNode getRoot(){
		return root;
	}

	public int getLength(){
		return length;
	}

	public boolean getSelectedFlg(){
		return flg_selected;
	}

	public void setSelectedFlg(){
		flg_selected = true;
	}

	/**
	 *  「子」関数木を付け加える(「親」関数木の引数の順番通りに使用する)
	 *  
	 * @param child 「子」関数木
	 */
	public void addChild(FunctionTree child){
		//順番を保つために、childがnullであっても入れる。
		this.children.add(child);
		//eの値は単純に足し算。
		if(child != null){
			this.length += child.length;
			this.e_vec.add(child.e_vec);
		}
	}

	/*
	 * 子のeに意味はない。
	 * （親のeに後からドンドン足されて内部での大小比較に使用しているだけ）
	 */
	public String toString(){
		String s = "FunctionTree[cs=" + toCompleteMethodString() + ", sum="+ e_vec.getSum() +", len= "+ this.length + ", p= ";
		for(int i = 0; i < this.e_vec.FEATURE_NUM; i++){
			s += e_vec.getFeature(i) + ", ";
		}
		s += "e_arr= ";
		for(int i = 0; i < this.e_vec.keywords_length; i++){
			s += this.e_vec.getKeywordFeature(i) + ", ";
		}

		s += "root= " + this.root.toString();

		if(this.children.size() > 0){
			s += "\n";
			for(FunctionTree child: this.children){
				if(child != null){
					//順番を保つため、childにはnullが入っていることもありうる。
					s += "\t" + child.toString();
				}
			}
			s += "\n";
		}
		s += "]";
		return s;

	}

	/*
	 * ログ保存用の文字列
	 * 特徴量＋出力文字列
	 */
	public String toLogDBString(){
		String s = "";
		for(int i = 0; i < this.e_vec.FEATURE_NUM; i++){
			s += e_vec.getFeature(i) + ", ";
		}
		for(int i = 0; i < this.e_vec.keywords_length; i++){
			s += this.e_vec.getKeywordFeature(i) + ", ";
		}
		s += "\"" + toCompleteMethodString() + "\n";
		return s;
	}
	
	/*
	 * 評価値を出力する
	 */
	public String toEvalString(){
		String s = getEvaluationValue() + ", p= ";
		for(int i = 0; i < this.e_vec.FEATURE_NUM; i++){
			s += e_vec.getFeature(i) + ", ";
		}
		s += "e_arr= ";
		for(int i = 0; i < this.e_vec.keywords_length; i++){
			s += this.e_vec.getKeywordFeature(i) + ", ";
		}
		return s;

	}

	/**
	 * この関数木全体をJavaのメソッドの文字列として整え、返す
	 * @return Javaのメソッドの文字列
	 */
	public String toCompleteMethodString(){
		String s = "";

		boolean isInThisClass = root.getFunction().isInThisClass();
		boolean isStatic = root.getFunction().isStatic();
		String funcType = root.getFunction().getFunctionType();

		if(isStatic == true && funcType.equals("method")){
			//static methodの場合、
			//クラス名.メソッド名となる。
			//rootの名前
			if(isInThisClass == false)
				s += getParentClassName(root.getFunction().getParentClass()) + "." + root.getFunction().getName();
			else
				s += root.getFunction().getName();//thisのとき。
			s += "(";
			//引数の名前
			for(int i = 0; i < this.children.size(); i++){
				if(i != 0)
					s += ", ";
				if(children.get(i) != null)
					s += children.get(i).toCompleteMethodString();
			}
			s += ")";
		}else if(isStatic == false && funcType.equals("method")){
			//Instance methodの場合、
			//1つ目のパラメータ(i==0)はレシーバであるから、
			//rootの前に書く
			if(isInThisClass == false)//(親がthis除く。
			if(children.size() > 0 && children.get(0) != null){
				s += children.get(0).toCompleteMethodString() + ".";
			}
			//rootの名前
			s += root.getFunction().getName();
			s += "(";
			//引数の名前
			for(int i = 1; i < this.children.size(); i++){
				if(i != 1)
					s += ", ";
				if(children.get(i) != null)
					s += children.get(i).toCompleteMethodString();
			}
			s += ")";
		}else if((isStatic == false && funcType.equals("field")) || funcType.equals("localvariable")){
			//Instance field の場合 (親がthisも無しも含む) または、ローカル変数の場合。
			//rootの名前
			s += root.getFunction().getName();
		}else if(isStatic == true && funcType.equals("field")){
			//static field の場合
			
			//親がthisまたは、親が無い（基本データ型）以外の時、
			if(!(isInThisClass == true || root.getFunction().getParentClass().equals("")))
				//rootの名前(クラス名.フィールド名)
				s += getParentClassName(root.getFunction().getParentClass()) + "." + root.getFunction().getName();
			else
				//「this.」から始まるタスクはほとんどない。皆無。というか、あっても除外して実験する。
				s += root.getFunction().getName();//親がthis
				
		}else if(funcType.equals("constructor")){
			//コンストラクタの場合
			//new + rootの名前
			s += "new " + root.getFunction().getName();
			s += "(";
			//引数の名前
				for(int i = 0; i < this.children.size(); i++){
					if(i != 0)
						s += ", ";
					if(children.get(i) != null)
						s += children.get(i).toCompleteMethodString();
				}
			s += ")";
		}else{
			//不明
			s += "???";
		}
		
		completeMethodString = s;

		return s;
	}

	/**
	 * 親クラスの名前を返す
	 * @param parent 親クラス名 or this or 何も無い""。
	 * @return
	 */
	private String getParentClassName(String parent){
		if(parent.equals("this") || parent.equals("")){
			return parent;
		}else{
			return parent.substring(parent.lastIndexOf(".")+1);
		}
	}

	public double getEvaluationValue(){
		return e_vec.getSum();
	}
	
	/*
	 *	ソートのときに使う。
	 */

	@Override
	public int compareTo(FunctionTree arg0) {
		if(arg0 == null)
			throw new NullPointerException();
		
		if(this.getEvaluationValue() < arg0.getEvaluationValue()){
			return 1;
		}else if(this.getEvaluationValue() == arg0.getEvaluationValue()){
			return this.toCompleteMethodString().compareTo(arg0.toCompleteMethodString());
		}else
			return -1;
	}



	/*
	 *	containsのときに使う。
	 */

	@Override
	public boolean equals(Object obj){
		if (this == obj)
	        return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FunctionTree o = (FunctionTree) obj;
		return this.toCompleteMethodString().equals(o.toCompleteMethodString());
	}



	/*
	 *	containsのときに使う。
	 */

	@Override
	public int hashCode(){
		return this.toCompleteMethodString().hashCode();
	}

}
