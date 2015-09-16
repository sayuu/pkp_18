package keywordProgramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import keywordProgramming.Word;

import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import console_test.LevenshteinDistance;

/**
 * ExplanationVectorを表すクラス
 * @author sayuu
 *
 */
public class ExplanationVector{
	
	//関数を評価する特徴の数
	public static final int FEATURE_NUM = 5;

	//特徴の重みベクトル
	private static double[] w_arr = {-0.05, 1, -0.01, 0.001, 0.001};		//先行研究初期値。
	
	//頻度の定数
	private static double const_freq = 0.001;
	
	//重みの更新幅
	private static double[] w_step_arr = {0.04, 0.2, 0.02, 0.02, 0.02};

	//キーワードの個数
	public static int keywords_length;
	
	//キーワードごとの特徴量
	private double[] keyword_p;
	
	//全ての要素(e0とe_arr)を足したもの.大小比較にはこれを使う.	
	private double sum;
	
	//特徴ベクトル
	private double p[] = new double [FEATURE_NUM];	

	public static final double INFINITE_VALUE = 9999.9;
	
	/**
	 * コンストラクタ
	 * @param keywords_length キーワード数
	 */
	public ExplanationVector(int keywords_length){
		this.keywords_length = keywords_length;
		this.keyword_p = new double[keywords_length];
	}

	/**
	 * コンストラクタ
	 * @param keywords_length キーワード数
	 * @param sum ExplanationVectorの要素の合計値
	 */
	public ExplanationVector(int keywords_length, double sum){
		this.keywords_length = keywords_length;
		this.keyword_p = new double[keywords_length];
		this.sum = sum;
	}

	/**
	 * コンストラクタ
	 * @param e コピーしたいExplanationVector
	 */
	public ExplanationVector(int keywords_length, ExplanationVector e){
		this.keyword_p = new double[keywords_length];
		substitution(e);
	}

	/**
	 * 比較のための
	 * ExplanationVectorの各要素の合計値を計算する
	 * 
	 */
	public void calcSum(){
		sum = 0.0;
		//キーワードアレーからキーワードの特徴量を算出。
		double sum_k = 0.0;
		for(double k: keyword_p)
			sum_k += k;
		p[1] = sum_k;

		//各特徴の合計値。
		for(int i = 0; i < FEATURE_NUM; i++){
			sum += w_arr[i] * p[i];
		}

	}

	/**
	 * 比較のための
	 * ExplanationVectorの各要素の合計値を
	 * 計算し、取得する。
	 * 
	 * @return ExplanationVectorの各要素の合計値
	 */
	public double calcAndGetSum(){
		calcSum();
		return sum;
	}
	
	public double getSum(){
		return sum;
	}

	/**
	 * ExplanationVectorに子ノード付け加えた際の値を計算する。
	 * 
	 * @param child　付け加えられる子ノード
	 */
	public void add(ExplanationVector child){
		ExplanationVector new_e = ExplanationVector.add(this, child);
		this.substitution(new_e);
	}
	
//	public void add(ExplanationVector child){
//		
//		//キーワードアレーからキーワードの特徴量を算出。
//		double sum_k = 0.0;
//		for(int i = 0; i < keyword_p.length; i++){
//			
////			keyword_p[i] += child.keyword_p[i];
////			if(keyword_p[i] > 1)
////				keyword_p[i] = 1;
//			keyword_p[i] = Math.max(keyword_p[i], child.keyword_p[i]);
//			
//			sum_k += keyword_p[i];
//		}
//
//		//各特徴量を合算(単純な親子の合計)
//		for(int i = 0; i < FEATURE_NUM; i++){
//			p[i] += child.p[i];
//		}
//		p[1] = sum_k;
//
//
//		calcSum();
//	}

	/**
	 * 親のExplanationVectorに子を付け加えた際の値を計算して
	 * その結果を保持した新しいExplanationVectorを返す。
	 * 
	 * @param parent　親のExplanationVector
	 * @param child　子のExplanationVector
	 * @return 計算結果を保持した新しいExplanationVector
	 */
	public static ExplanationVector add(ExplanationVector parent,  ExplanationVector child){
		ExplanationVector new_e = new ExplanationVector(parent.keywords_length, parent);
		//キーワードアレーからキーワードの特徴量を算出。
		double sum_k = 0.0;
		for(int i = 0; i < new_e.keyword_p.length; i++){
			try{
			//max を取るように変更する。(類似度は1以上行かないので)
			new_e.keyword_p[i] = Math.max(parent.keyword_p[i], child.keyword_p[i]);
			}catch(Exception e){
				e.printStackTrace();
			}
			//足し算したあと、1でcapする。
//			new_e.keyword_p[i] = parent.keyword_p[i] + child.keyword_p[i];
//			if(new_e.keyword_p[i] > 1)
//				new_e.keyword_p[i] = 1;
			
			sum_k += new_e.keyword_p[i];
		}

		//各特徴量を合算(単純な親子の合計)
		for(int i = 0; i < FEATURE_NUM; i++){
			new_e.p[i] = parent.p[i] + child.p[i];
		}
		new_e.p[1] = sum_k;
		new_e.calcSum();
		return new_e;
	}

	/**
	 * このオブジェクトに引数で与えられたExplanationVectorの
	 * 各要素の値を全てコピーする。
	 * 
	 * @param e 内容をコピーしたいExplanationVector
	 */
	public void substitution(ExplanationVector e){
		sum = e.sum;
		for(int i = 0; i < FEATURE_NUM; i++){
			p[i] = e.p[i];
		}
		for(int i = 0; i < keyword_p.length; i++){
			keyword_p[i] = e.keyword_p[i];
		}
	}

	/**
	 * 入力キーワードからExplanationVectorを計算する。
	 * 
	 * @param input_words 入力キーワードのList
	 * @param numOfSameWordsIn 個々のキーワードがそれぞれ何個入力クエリに含まれているかを表すHashMap
	 * @param ft ExplanationVectorを計算したいFunctionTree
	 * @return 各要素に値が入力され、各要素の合計値の計算がなされたExplanationVector
	 */
	public static ExplanationVector calcExplanationVector(List<Word> input_words, HashMap<String, Integer> numOfSameWordsIn, FunctionTree ft){
		ExplanationVector e = new ExplanationVector(input_words.size());
		Function f = ft.getRoot().getFunction();
		
		/*
		 * 評価値の計算は、
		 * 木のrootの評価値だけを計算して、
		 * すでに求まっている子の値に足せばよい。
		 */

		//初期化
		for(int i = 0; i < FEATURE_NUM; i++){
			e.p[i] = 0;
		}

		//木全体のノード数
		e.p[0] = 1;	//root1つなので、1.

//		if(f.getName().contains("result")){
//			System.out.println();
//		}
		
		long start_word_match = System.currentTimeMillis();
		
		//キーワードに一致するラベルの数

		//f.labelにinputと同一のキーワードがある時の計算
		//同一単語の出現個数をそれぞれx, yとするとe_i = max(x/y, 1)とする.
		for(int i=0; i < input_words.size(); i++){
			//String word = input_words.get(i);
//			Word word = new Word(input_words.get(i));
			
			//ワード分割ON かつ、ワードと関数のタイプが等しく無い時は、使用しない。
			if(Word.flg_separate.equals("true") && !judgeWordType(input_words.get(i), f)){
				continue;
			}
			
			for(String flab: f.getLabels()){
				if(input_words.get(i).getWord().equals(flab)){
					e.keyword_p[i] = (double)(ft.numOfSameWordsFunc.get(flab)) / numOfSameWordsIn.get(flab);

//					if(input_words.get(i).getType().equals(Word.METHOD))
//						e.keyword_p[i] = (double)(ft.numOfSameWordsFunc.get(flab)) / numOfSameWordsIn.get("@"+flab);
//					//バグ VARIABLE に - はつけていない。ので
////					else if(word.getType().equals(Word.VARIABLE))
////						e.keyword_p[i] = (double)(ft.numOfSameWordsFunc.get(flab)) / numOfSameWordsIn.get("-"+flab);
//					else
//						e.keyword_p[i] = (double)(ft.numOfSameWordsFunc.get(flab)) / numOfSameWordsIn.get(flab);

					if(e.keyword_p[i] > 1)
						e.keyword_p[i] = 1;
				}
			}
		}
		long end_word_match = System.currentTimeMillis();

		KeywordProgramming.time_consumed_getting_similarity += end_word_match - start_word_match;
		
		//キーワードに一致しないラベルの数
		for(String flab: f.getLabels()){
			if(numOfSameWordsIn.containsKey(flab) == false){
				e.p[2]++;
			}
		}

		//このfがローカル変数、メンバ変数（関数）ならばe_0を+0.001する(fはコンテキストに近い方が望ましいため)
		//（後で書く）
		if( f.getParentClass().equals("") || f.getParentClass().equals("this")){
			e.p[3]++;
		}

		//頻度の特徴量の計算
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean flg = store.getBoolean(PreferenceInitializer.INCLUDE_FREQUENCY);
		if(flg)
			calcFrequencyFeatures(e, f); 
		
		e.calcSum();
		return e;
	}

	/*
	 * 頻度の特徴量の計算を行う。
	 */
	private static void calcFrequencyFeatures(ExplanationVector e, Function f) {
		//式１．単純 w*f
		//e.p[4] = f.getFrequency();
		
		//式２．頻度を恣意的に3回5回で分けで、0, 0.5, 1.0 の値を入れる
//		if(f.getFrequency() < 3){
//			e.p[4] = 0;
//		}else if(f.getFrequency() < 5){
//			e.p[4] = 0.5;
//		}else{
//			e.p[4] = 1;
//		}
		
		//式３．1-exp(-a*f) a = 0.3程度。
		//頻度の影響があまりにも大きくなり、他の特徴（特にキーワードとの一致よりも頻度の影響は低くなってほしい）
		//とのバランスを欠いてしまうのを防ぐため
		//expを使用して最大値を１で抑えた関数。
		e.p[4] = 1 - Math.exp(-const_freq * f.getFrequency());
		
	}
	
	/**
	 * ワードwordと関数fのタイプがマッチしているか調べる。
	 * 
	 * @param word
	 * @param f
	 * @return
	 */
	public static boolean judgeWordType(Word word, Function f){
		
		if(word.getType().equals(Word.NORMAL))
			return true;
		
		boolean is_method = word.getType().equals(Word.FUNCTION);
		
		if(f.getFunctionType().equals(Function.METHOD) || f.getFunctionType().equals(Function.CONSTRUCTOR)){
			if(is_method){
				return true;
			}else{
				return false;
			}
		}else{
			if(is_method){
				return false;
			}else{
				return true;
			}
		}
	}
	
	/*
	 * キーワードとラベルのペア
	 * と
	 * その類似度
	 */
	private static class PairOfKeywordLabel{
		String keyword;
		String label;
		double similarity;
		
		public PairOfKeywordLabel(String keyword, String label, double similarity){
			this.keyword = keyword;
			this.label = label;
			this.similarity = similarity;
		}
	}

	/**
	 * 入力キーワードからExplanationVectorを計算する。
	 * 
	 * @param input_words 入力キーワードのList
	 * @param numOfSameWordsIn 個々のキーワードがそれぞれ何個入力クエリに含まれているかを表すHashMap
	 * @param ft ExplanationVectorを計算したいFunctionTree
	 * @return 各要素に値が入力され、各要素の合計値の計算がなされたExplanationVector
	 */
	public static ExplanationVector calcExplanationVector2(List<Word> input_words, HashMap<String, Integer> numOfSameWordsIn, FunctionTree ft){
		ExplanationVector e = new ExplanationVector(input_words.size());
		Function f = ft.getRoot().getFunction();
		
		/*
		 * 評価値の計算は、
		 * 木のrootの評価値だけを計算して、
		 * すでに求まっている子の値に足せばよい。
		 */

		//初期化
		for(int i = 0; i < FEATURE_NUM; i++){
			e.p[i] = 0;
		}

		//木全体のノード数
		e.p[0] = 1;	//root1つなので、1.

		//キーワードに一致するラベルの数
//		List<Word> word_list = new ArrayList<Word>();
//		for(int i=0; i < input_words.size(); i++){
//			word_list.add(new Word(input_words.get(i)));
//		}
		
		
		long start_similarity = System.currentTimeMillis();
		//f.labelにinputと同一のキーワードがある時の計算
		//同一単語の出現個数をそれぞれx, yとするとe_i = max(x/y, 1)とする.
		
		// キーワード x in K(入力クエリに含まれるキーワードの集合),
		// ラベル y in L(ラベルの集合)
		// 類似度 f(x,y) 
		//全ての(x,y)について類似度fを計算する。
		List<PairOfKeywordLabel> similarity_list = new ArrayList<PairOfKeywordLabel>();
		
		for(int i=0; i < input_words.size(); i++){
//			Word word = new Word(input_words.get(i));
			
			//ワード分割ON かつ、ワードと関数のタイプが等しく無い時は、使用しない。
			if(Word.flg_separate.equals("true") && !judgeWordType(input_words.get(i), f)){
				continue;
			}
//			if(f.getName().equals("getMessage")){
//				System.out.println();
//			}
			for(String flabel: f.getLabels()){
				String key = input_words.get(i).getWord();
				//ここにバグ
				double s = similarity(key, flabel);
				similarity_list.add(new PairOfKeywordLabel(key, flabel, s));
			}
		}

		Collections.sort(similarity_list, new Comparator<PairOfKeywordLabel>() {
			public int compare( PairOfKeywordLabel a, PairOfKeywordLabel b ) {
				// compare メソッドで、負の数、0、正の数 (int) を返すことによって、その大小を定義する。
				// 第一引数を小さいとする場合は負の数。大きいとする場合は正の数を返す。
				
				if(a.similarity == b.similarity)
					return 0;
				else
					return  a.similarity > b.similarity ? -1 : 1;
			}
		} );

		
		//どのキーワードとどのラベルが対応するかの対応表
		List<PairOfKeywordLabel> kl_map = new ArrayList<PairOfKeywordLabel>();
		//すでに確保されたラベルのリスト
		List<String> keeped_label_list = new ArrayList<String>();
		//すでに確保されたキーワード
		List<String> keeped_word_list = new ArrayList<String>();

		for(PairOfKeywordLabel p: similarity_list){
			
			//すでにワードとラベルの両方が決定されていないときのみ、
			//組み合わせを追加する。
			if(!keeped_word_list.contains(p.keyword) && !keeped_label_list.contains(p.label)){
				//マップに追加する。
				kl_map.add(p);
				//決定済みキーワードとラベルを保持する。
				keeped_word_list.add(p.keyword);
				keeped_label_list.add(p.label);
			}
		}
	
		long end_similarity = System.currentTimeMillis();
		
		//類似度計算に掛かった時間
		KeywordProgramming.time_consumed_getting_similarity += end_similarity - start_similarity;
		
		//計算された各得点を、e_pに代入する。
		for(int i=0; i < input_words.size(); i++){
			for(PairOfKeywordLabel kl: kl_map){
				//ここにバグ
				if(input_words.get(i).getWord().equals(kl.keyword)){
					e.keyword_p[i] = kl.similarity;
					if(e.keyword_p[i] > 1)
						e.keyword_p[i] = 1;
				}
			}
		}
		
//		if(f.getName().contains("addResultListener")){
//			System.out.println("==========");
//			for(PairOfKeywordLabel p: similarity_list){
//				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
//			}
//			System.out.println("==========");
//			for(PairOfKeywordLabel p: kl_map){
//				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
//			}
//			System.out.println("==========");
//			for(int i=0; i < input_words.size(); i++){
//				System.out.println(input_words.get(i) + ", " + e.keyword_p[i]);
//			}
//			System.out.println("==========");
//		}
		
		//{(pnl,new,0.3), (lbl,j,0), (img,frame,0.2)}
		// 0.3 + 0.0 + 0.2 = 0.5 
		//これが、new JFrame が pnl ad img lbl と、どの程度類似しているかを示す得点となる。 
		
		
		
		//キーワードに一致しないラベルの数
		e.p[2] = f.getLabels().size() - keeped_label_list.size();
//		for(String flab: f.getLabels()){
//			if(numOfSameWordsIn.containsKey(flab) == false){
//				e.p[2]++;
//			}
//		}

		//このfがローカル変数、メンバ変数（関数）ならばe_0を+0.001する(fはコンテキストに近い方が望ましいため)
		//（後で書く）
		if( f.getParentClass().equals("") || f.getParentClass().equals("this")){
			e.p[3]++;
		}

		//頻度の特徴量の計算
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean flg = store.getBoolean(PreferenceInitializer.INCLUDE_FREQUENCY);
		if(flg)
			calcFrequencyFeatures(e, f); 
		
		e.calcSum();
		return e;
	}
	
//	public static ExplanationVector calcExplanationVector2(List<String> input_words, HashMap<String, Integer> numOfSameWordsIn, FunctionTree ft){
//		ExplanationVector e = new ExplanationVector(input_words.size());
//		Function f = ft.getRoot().getFunction();
//		
//		/*
//		 * 評価値の計算は、
//		 * 木のrootの評価値だけを計算して、
//		 * すでに求まっている子の値に足せばよい。
//		 */
//
//		//初期化
//		for(int i = 0; i < FEATURE_NUM; i++){
//			e.p[i] = 0;
//		}
//
//		//木全体のノード数
//		e.p[0] = 1;	//root1つなので、1.
//
//		//キーワードに一致するラベルの数
//
//		//f.labelにinputと同一のキーワードがある時の計算
//		//同一単語の出現個数をそれぞれx, yとするとe_i = max(x/y, 1)とする.
//		
//		// キーワード x in K(入力クエリに含まれるキーワードの集合),
//		// ラベル y in L(ラベルの集合)
//		// 類似度 f(x,y) 
//		//全ての(x,y)について類似度fを計算する。
//		List<PairOfKeywordLabel> similarity_list = new ArrayList<PairOfKeywordLabel>();
//		
//		for(int i=0; i < input_words.size(); i++){
//			for(String flabel: f.getLabels()){
//				String key = input_words.get(i);
//				double s = similarity(key, flabel);
//				similarity_list.add(new PairOfKeywordLabel(key, flabel, s));
//			}
//		}
//		
//		Collections.sort(similarity_list, new Comparator<PairOfKeywordLabel>() {
//			public int compare( PairOfKeywordLabel a, PairOfKeywordLabel b ) {
//				// 引数1と引数2はそれぞれリスト中の値
//				
//				// compare メソッドで、負の数、0、正の数 (int) を返すことによって、その大小を定義します。 
//				// 第一引数を小さいとする場合は負の数。大きいとする場合は正の数を返せば OK です。
//				return  a.similarity > b.similarity ? -1 : 1;
//			}
//		} );
//		
//		
//		//どのキーワードとどのラベルが対応するかの対応表
//		List<PairOfKeywordLabel> kl_map = new ArrayList<PairOfKeywordLabel>();
//		//すでに確保されたラベルのリスト
//		List<String> keeped_label_list = new ArrayList<String>();
//		//すでに確保されたキーワード
//		List<String> keeped_word_list = new ArrayList<String>();
//
//		for(PairOfKeywordLabel p: similarity_list){
//			//すでにワードとラベルの両方が決定されていないときのみ、
//			//組み合わせを追加する。
//			if(!keeped_word_list.contains(p.keyword) && !keeped_label_list.contains(p.label)){
//				//マップに追加する。
//				kl_map.add(p);
//				//決定済みキーワードとラベルを保持する。
//				keeped_word_list.add(p.keyword);
//				keeped_label_list.add(p.label);
//			}
//		}
//	
//		//計算された各得点を、e_pに代入する。
//		for(int i=0; i < input_words.size(); i++){
//			for(PairOfKeywordLabel kl: kl_map){
//				if(input_words.get(i).equals(kl.keyword)){
//					e.keyword_p[i] = kl.similarity;
//					if(e.keyword_p[i] > 1)
//						e.keyword_p[i] = 1;
//				}
//			}
//		}
//		
////		if(f.getName().contains("addResultListener")){
////			System.out.println("==========");
////			for(PairOfKeywordLabel p: similarity_list){
////				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
////			}
////			System.out.println("==========");
////			for(PairOfKeywordLabel p: kl_map){
////				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
////			}
////			System.out.println("==========");
////			for(int i=0; i < input_words.size(); i++){
////				System.out.println(input_words.get(i) + ", " + e.keyword_p[i]);
////			}
////			System.out.println("==========");
////		}
//		//{(pnl,new,0.3), (lbl,j,0), (img,frame,0.2)}
//		// 0.3 + 0.0 + 0.2 = 0.5 
//		//これが、new JFrame が pnl ad img lbl と、どの程度類似しているかを示す得点となる。 
//		
//		
//		
//		//キーワードに一致しないラベルの数
//		e.p[2] = f.getLabels().size() - keeped_label_list.size();
////		for(String flab: f.getLabels()){
////			if(numOfSameWordsIn.containsKey(flab) == false){
////				e.p[2]++;
////			}
////		}
//
//		//このfがローカル変数、メンバ変数（関数）ならばe_0を+0.001する(fはコンテキストに近い方が望ましいため)
//		//（後で書く）
//		if( f.getParentClass().equals("") || f.getParentClass().equals("this")){
//			e.p[3]++;
//		}
//
//		//頻度
//		e.p[4] = f.getFrequency();
//				
//		e.calcSum();
//		return e;
//	}
//	
	
	public static double[] getWeights(){
		return w_arr;
	}
	
	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * の形式の文字列を返す。
	 */
	public static String getWeightString(){
		String str = "";
		for(int i = 0; i < w_arr.length; i++){
			str += String.valueOf(w_arr[i]);
			if(i < w_arr.length-1)
				str += ", ";
		}
		return str;
	}

	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * の形式の文字列を設定する。
	 */
	public static void setWeightString(String input){
		String[] s_arr = input.split(",");
		for(int i = 0; i < w_arr.length; i++){
			w_arr[i] = Double.parseDouble(s_arr[i].trim());
		}
	}
	
	/**
	 * 1つの特徴の重みの設定
	 * iは0始まり
	 * @param w
	 * @param i
	 */
	public static void setWeight(double w, int i){
		w_arr[i] = w;
	}

	
	/*
	 * 1つの特徴の重みの設定
	 */
	public static void setConstFreq(double c){
		const_freq = c;
	}
	
	public static double getConstFreq(){
		return const_freq;
	}
	
	public static void setWeights(double []w){
		w_arr = Arrays.copyOf(w, w.length);
	}

	public static double[] getSteps(){
		return w_step_arr;
	}
	
	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * の形式の文字列を返す。
	 */
	public static String getStepString(){
		String str = "";
		for(int i = 0; i < w_step_arr.length; i++){
			str += String.valueOf(w_step_arr[i]);
			if(i < w_step_arr.length-1)
				str += ", ";
		}
		return str;
	}
	
	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * の形式の文字列を設定する。
	 */
	public static void setStepString(String input){
		String[] s_arr = input.split(",");
		for(int i = 0; i < w_step_arr.length; i++){
			w_step_arr[i] = Double.parseDouble(s_arr[i].trim());
		}
	}
	
	/*
	 * 1つの特徴の重みの更新幅(ステップ幅)の設定
	 */
	public static void setStep(double w, int i){
		w_step_arr[i] = w;
	}

	public double getFeature(int i){
		return p[i];
	}

	public double getKeywordFeature(int i){
		return keyword_p[i];
	}

	//thisのsumが大きければ1、小さければ-1.
	public int compareTo(ExplanationVector o) {
		if(o.getSum() != -INFINITE_VALUE)
			o.calcSum();
		// TODO 自動生成されたメソッド・スタブ
		if(this.sum > o.getSum())
			return 1;
		else if(this.sum < o.getSum())
			return -1;
		return 0;
	}


	//各特長が正(1)の特徴か、負(-1)の特徴か調べるメソッド
	/*
	 * 各特徴は、
	 * 0: 木を構成するノードの数					 Negative
	 * 1: キーワードに一致するラベルの数（もしくは関数の数） Positive
	 * 2: キーワードに一致しないラベルの数 			 Negative
	 * 3: コンテキストに近い関数の数 				 Positive
	 */
	public static boolean isPositive(int i){
		if(i == 0)
			return false;
		else if(i == 1)
			return true;
		else if(i == 2)
			return false;
		else 
			return true;
	}
	
	public static double similarity(String keyword, String label){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String flg = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);

		if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1)){
			return similarityWithLCS(keyword, label, 1);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2)){
			return similarityWithLCS(keyword, label, 2);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3)){
			return similarityWithLCS(keyword, label, 3);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4)){
			return similarityWithLCS(keyword, label, 4);
		}else{
			return similarityWithLD(keyword, label);
		}
	}
	
	public static double similarity2(String keyword, String label, String flg){
		if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1)){
			return similarityWithLCS(keyword, label, 1);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2)){
			return similarityWithLCS(keyword, label, 2);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3)){
			return similarityWithLCS(keyword, label, 3);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4)){
			return similarityWithLCS(keyword, label, 4);
		}else{
			return similarityWithLD(keyword, label);
		}
	}
	
//	public static double similarity(String keyword, String label){
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		String flg = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
//		if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1)){
//			return similarityWithLCS(keyword, label, false);
//		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2)){
//			return similarityWithLCS(keyword, label, true);
//		}else{		
//			return similarityWithLD(keyword, label);
//		}
//	}
	
	/**
	 * キーワードとラベルの一致度を返す
	 * 
	 * 順番を保ったまま１文字ずつ調べて、
	 * 一致した文字の個数/ラベル長
	 * が得点となる。
	 * @param keyword
	 * @param label
	 * @param div_flg = true なら、LCS2, false なら LCS1
	 * @return
	 */
	public static double similarityWithLCS(String keyword, String label, int number){
		
		//最後にラベルの文字列長で割る. 最長共通部分系列長/ラベル長
		double ret_score = 0;
		
		try{
			switch(number){
			case 1:
				ret_score = LCS1(keyword, label);
				break;
			case 2:
				ret_score = LCS2(keyword, label);
				break;
			case 3:
				ret_score = Math.pow(LCS1(keyword, label), 2);
				break;
			case 4:
				ret_score = Math.pow(LCS2(keyword, label), 2);
				break;
			default:
				throw new Exception();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret_score;
	}
	
	/**
	 * キーワードとラベルの一致度を返す
	 * 
	 * 順番を保ったまま１文字ずつ調べて、
	 * 一致した文字の個数/ラベル長
	 * が得点となる。
	 * @param keyword
	 * @param label
	 * @param number 1~4の値　LCS1 ~ 4を指定。
	 * @return
	 */

//	public static double similarityWithLCS(String keyword, String label, boolean div_flg){
//		
//		//最後にラベルの文字列長で割る. 最長共通部分系列長/ラベル長
//		double ret_score = 0;
//		try{
//			if(div_flg)
//				ret_score = (double)LongestCommonSubsequence.LCS_Length(keyword, label) / (keyword.length() + label.length());//LCS2
//			else
//				ret_score = (double)LongestCommonSubsequence.LCS_Length(keyword, label) / (label.length());//LCS1
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return ret_score;
//	}
	public static double LCS1(String keyword, String label){
		return (double)LongestCommonSubsequence.LCS_Length(keyword, label) / (label.length());//LCS1
	}
	
	public static double LCS2(String keyword, String label){
		return 2 * (double)LongestCommonSubsequence.LCS_Length(keyword, label) / (keyword.length() + label.length());//LCS2
	}
	/**
	 * キーワードとラベルの一致度を返す
	 * 
	 * 類似度  =  1/(編集距離 + 1)
	 * 類似度  =  1/編集距離  （これだと、距離=0のとき発散してしまう）
	 * 
	 * 
	 * @param keyword
	 * @param label
	 * @return
	 */
	public static double similarityWithLD(String keyword, String label){
		double ret_score = 0;

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		int del = store.getInt(PreferenceInitializer.LD_DELETE);
		int rep = store.getInt(PreferenceInitializer.LD_REPLACE);
		int add = store.getInt(PreferenceInitializer.LD_ADD);
		double constant = store.getDouble(PreferenceInitializer.LD_CONST);
		
//		double constant = console_test.ConsoleTest3.para.ld_const;
		
//		if(console_test.ConsoleTest3.IsConsoleTest == true){
//			del = console_test.ConsoleTest3.para.ld_delete;
//			rep = console_test.ConsoleTest3.para.ld_replace;
//			add = console_test.ConsoleTest3.para.ld_add;
//		}
		
		try{
			LevenshteinDistance ld = new LevenshteinDistance(del, rep, add);
			ret_score = 1 / (double)( ld.edit(keyword, label)/constant + 1.0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret_score;
	}
	
//	public static double similarityWithLD(String keyword, String label){
//		double ret_score = 0;
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		
//		int del = store.getInt(PreferenceInitializer.LD_DELETE);
//		int rep = store.getInt(PreferenceInitializer.LD_REPLACE);
//		int add = store.getInt(PreferenceInitializer.LD_ADD);
//		
//		try{
//			LevenshteinDistance ld = new LevenshteinDistance(del, rep, add);
//			ret_score = 1 / (double)(ld.edit(keyword, label) + 1.0);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return ret_score;
//	}
	
//	public static double similarity(String keyword, String label){
//		int match_ch_count = 0;//マッチした文字数
//		String sub_label = label;	//ラベルの部分文字列
//		for(int i = 0; i < keyword.length(); i++){
//			int idx = sub_label.indexOf(keyword.charAt(i));
//			if(idx != -1){
//				match_ch_count++;
//				sub_label = sub_label.substring(idx + 1);//発見された文字の次の文字を先頭文字とする文字列
//				if(sub_label.length() == 0){//ラベルの終端に到達した。
//					break;
//				}
//			}else{
//				//1文字でもlabelに存在しないときはすぐにループを抜ける。
//				break;
//			}
//			
//		}
//		double ret_score = (double)match_ch_count / label.length();	//最後にラベルの文字列長で割る
//		return ret_score;
//	}
	
	public static void main(String[] args){
//		System.out.println(similarityWithLCS("usg", "usage", true));
//		System.out.println(similarityWithLCS("usg", "anchor", true));
//		System.out.println(similarityWithLCS("usg", "arg", true));
//		System.out.println(similarityWithLCS("usg", "background", true));
//		System.out.println(similarityWithLCS("usg", "in", true));
//		System.out.println("========");
//		System.out.println(similarityWithLCS("usg", "usage", false));
//		System.out.println(similarityWithLCS("usg", "anchor", false));
//		System.out.println(similarityWithLCS("usg", "arg", false));
//		System.out.println(similarityWithLCS("usg", "background", false));
//		System.out.println(similarityWithLCS("usg", "in", false));
//		System.out.println("========");
//		System.out.println(similarityWithLD("usg", "usage"));
//		System.out.println(similarityWithLD("usg", "anchor"));
//		System.out.println(similarityWithLD("usg", "arg"));
//		System.out.println(similarityWithLD("usg", "background"));
//		System.out.println(similarityWithLD("usg", "in"));
//		System.out.println("========");
//		
////		LevenshteinDistance.w_delete = 1;
////		LevenshteinDistance.w_replace = 2;
////		LevenshteinDistance.w_insert = 2;
//		
//		System.out.println(similarityWithLD("usg", "usage"));
//		System.out.println(similarityWithLD("usg", "anchor"));
//		System.out.println(similarityWithLD("usg", "arg"));
//		System.out.println(similarityWithLD("usg", "background"));
//		System.out.println(similarityWithLD("usg", "in"));
//		
	}
}
