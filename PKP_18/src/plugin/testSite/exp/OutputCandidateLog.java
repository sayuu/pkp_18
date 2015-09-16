package plugin.testSite.exp;

import keywordProgramming.ExplanationVector;
import keywordProgramming.FunctionTree;

/*
 * オンライン学習用に保存した１つの出力候補をあらわすクラス。
 * 
 * KP実行のたびに保存された出力文字列を格納する。
 * 
 * 格納するものは、
 * １　出力文字列
 * ２　各特長の特徴量
 * ３  e_arr (当時入力したキーワードに対するもの）
 * 
 */
public class OutputCandidateLog {

	private String completeMethodString;	//出力文字列
	private double p[] = new double [ExplanationVector.FEATURE_NUM];	//特徴量
	private double[] keyword_p;		//キーワードごとの特徴量 e_arr
	private double sum;	//評価値
	
	/*
	 *	lineはlogFileの1行を表す。
	 *　データの並びは、
	 * [4つの特徴の特徴量], [e_arr], 出力文字列
	 * の順。
	 *  
	 * 例：キーワード数が3つのとき。
	 * 3.0, 3.0, 2.0, 1.0, 1.0, 1.0, 1.0, "new InputStream().read(data)
	 * 
	 * 出力文字列の先頭には目印として " が付く。
	 */
	
	public OutputCandidateLog(String line, int keyword_length){
	
		keyword_p = new double [keyword_length];
		String []s_arr = line.split("\"");
		String []s_arr2 = s_arr[0].split(",");
		
		for(int i = 0; i < p.length + keyword_length; i++){
			if(i < p.length){
				p[i] = Double.parseDouble(s_arr2[i]);
			}
			else if(i < keyword_length){
				keyword_p[i] = Double.parseDouble(s_arr2[i]);
			}
		}
		completeMethodString = s_arr[1];
		
//		System.out.println(toString());
		
	}
	
	public OutputCandidateLog(FunctionTree tree){
		for(int i = 0; i < p.length; i++){
			p[i] = tree.e_vec.getFeature(i);
		}
		
		keyword_p = new double [tree.e_vec.keywords_length];
		for(int i = 0; i < tree.e_vec.keywords_length; i++){
			keyword_p[i] = tree.e_vec.getKeywordFeature(i);
		}
		
		completeMethodString = tree.toCompleteMethodString();
	}
	
	/*
	 * 変化した重み w_arr に対する評価値を計算する。
	 */
	public void calculateEvaluationValue(){
		double []w_arr = ExplanationVector.getWeights();
		sum = 0.0;
		//キーワードアレーからキーワードの特徴量を算出。
		double sum_k = 0.0;
		for(double k: keyword_p)
			sum_k += k;
		p[1] = sum_k;

		//各特徴の合計値。
		for(int i = 0; i < ExplanationVector.FEATURE_NUM; i++){
			sum += w_arr[i] * p[i];
		}
	}
	
	public double getEvaluationValue(){
		return sum;
	}
	
	public String getCompleteMethodString(){
		return completeMethodString;
	}
	
	public String toString(){
		String s = "";
		for(int i = 0; i < p.length; i++){
			s += p[i] + ", ";
		}
		for(int i = 0; i < keyword_p.length; i++){
			s += keyword_p[i] + ", ";
		}
		s += completeMethodString;
		return s;
	}
}
