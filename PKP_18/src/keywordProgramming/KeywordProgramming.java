package keywordProgramming;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.preference.IPreferenceStore;

import ast.Import;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import search.Node;
import search.State;
import state.KpRunningState;

import logging.LogControl;

/**
 * キーワードプログラミングの主要な３つのアルゴリズム
 *  動的計画法(DynamicProgram)、
 *  GetBestExplTreeForFunc、
 *  ExtractTree
 * を記述したクラス。
 * 
 * @author sayuu
 *
 */
public class KeywordProgramming {

	//動的計画法の表の高さ
	public static int HEIGHT = 3;
	
	//動的計画法の表の１つの交点に保持する「根」関数木の個数
	public static int BEST_R = 25;

	//引数探索の方法
	public static String SEARCH_MODE_FUNCTION_PARAMETER = "SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL";
	
	//全探索する条件となる、関数の引数の組み合わせの数
	public static int COMBINATION_SIZE = 5;
	//全探索する条件となる、関数の引数の数
	public static int MAX_ARGUMETNT_SIZE = 5;
	
	//最良優先探索を行う回数
	public static int BEST_FIRST_SIZE = 5;

	//内部データベースのファイル名。　型。
	public static final String SUB_CLASS_FILE_NAME = "sub_class.txt";
	
	//内部データベースのファイル名。　関数。
	public static final String FUNCTION_FILE_NAME = "function.txt";
	
	//内部データベースのファイル名。　頻度
	public static final String FREQUENCY_FILE_NAME = "frequency.txt";

	//動的計画法によって作成される表を表すオブジェクト、bestRoots
	private BestRoots bestRoots;
	
	//アルゴリズムに使用される全ての型を保持するTreeMap
	private TreeMap<String, Type> types = new TreeMap<String, Type>();
	
	//アルゴリズムに使用される全ての関数を保持するArrayList
	private ArrayList<Function> functions = new ArrayList<Function>();
		
//	//内部データベースに存在する全ての型を保持するTreeMap
//	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
//	
//	//内部データベースに存在する全ての関数を保持するArrayList
//	public static ArrayList<Function> original_functions = new ArrayList<Function>(1);

	//インポート分の型を保持するTreeMap
	private TreeMap<String, Type> imported_types = new TreeMap<String, Type>();
		
	//インポート分の関数を保持するArrayList
	private ArrayList<Function> imported_functions = new ArrayList<Function>();

	//頻度
	public ArrayList<Frequency> frequencies = new ArrayList<Frequency>();
	
	public HashMap<String, List<FunctionTree>> sorted_ftree_map = new HashMap<String, List<FunctionTree>>();
	
	/*
	 * 最終的な出力関数木の列。
	 * キーがその関数木の順位。
	 * 値がその関数木。
	 * 評価値順にソートされている。
	 */
	private FunctionTree[] outputFunctionTrees;

	//入力キーワードのList
	private List<String> input_keywords;
	
	//入力キーワードのList
	private List<Word> input_keywords_word;
	
	//入力キーワードの種類のList (メソッド名にマッチ、変数名にマッチ)
	private List<String> input_keyword_types;
		
	//inputの単語の内、同一の単語それぞれの個数
	private HashMap<String, Integer> numOfSameWords = new HashMap<String, Integer>();

	//類似度計算に要する時間
	static long time_consumed_getting_similarity = 0;
	
	public BestRoots getBestRoots(){
		return bestRoots;
	}

	public void setBestRoots(BestRoots br){
		bestRoots = br;
	}

	public synchronized void addType(Type t){
		types.put(t.getName(), t);
	}

	public TreeMap<String, Type> getTypes(){
		return types;
	}

	public synchronized void addFunction(Function f){
		functions.add(f);
	}
	public ArrayList<Function> getFunctions(){
        return functions;
    }
	//Classesファイルを読み込んで、リストに格納
	public synchronized void readClassFileAndAddList(String input_txt_file_name){
		FileReader fr;

		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				Type t = new Type(s);
				types.put(t.getName(), t);
//				original_types.put(t.getName(), t);
			}
			br.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	//Functionsファイルを読み込んで、リストに格納
	public synchronized void readFunctionFileAndAddList(String input_txt_file_name){
		FileReader fr;
		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				Function f = new Function(s);
				functions.add(f);
//				original_functions.add(f);
			}
			br.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
	
	//ファイルの読み込み (型、関数、頻度)
	public void loadFiles(BufferedReader class_r, BufferedReader function_r, BufferedReader freq_r){
		loadFreqFiles(freq_r);
		loadClassFiles(class_r);
		loadFunctionFilesWithFrequency(function_r);
	}
	
	//ファイルの読み込み(型)
	public synchronized void loadClassFiles(BufferedReader c_r){
		String s;

		//classesファイルの読み込み
		try {
			while ((s = c_r.readLine()) != null) {
				Type t = new Type(s);
				types.put(t.getName(), t);
//				original_types.put(t.getName(), t);
			}
			//c_r.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}
	
	//ファイルの読み込み(関数)
	public synchronized void loadFunctionFiles(BufferedReader f_r){
		String s;

		//functionsファイルの読み込み
		try {
			while ((s = f_r.readLine()) != null) {
				Function f = new Function(s);
				functions.add(f);
//				original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
	}
	
	//ファイルの読み込み(関数に頻度も追加)
	public synchronized void loadFunctionFilesWithFrequency(BufferedReader f_r){
		String s;

		//functionsファイルの読み込み
		try {
			while ((s = f_r.readLine()) != null) {
				
				Function f = new Function(s);
				//頻度追加
				f.setFrequency(frequencies);
				
				functions.add(f);
//				original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
	}
	
	//ファイルの読み込み(型、関数)
	//originalはやめて、types とfuctionsに直接入れる。
	public synchronized void loadFiles(BufferedReader c_r, BufferedReader f_r){
		String s;

		//classesファイルの読み込み
		try {
			while ((s = c_r.readLine()) != null) {
				Type t = new Type(s);
				types.put(t.getName(), t);
				//original_types.put(t.getName(), t);
			}
			//c_r.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		//functionsファイルの読み込み
		try {
			while ((s = f_r.readLine()) != null) {
				Function f = new Function(s);
				functions.add(f);
				//original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
	}
	
	//ファイルの読み込み 頻度
	public void loadFreqFiles(BufferedReader fr_r){
		String s;

		//ファイルの読み込み
		try {
			while ((s = fr_r.readLine()) != null) {
				
				Frequency fr = new Frequency(s);
				frequencies.add(fr);
			}
			//fr_r.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		
	}
	
	/*
	 * 指定回数以上の頻度を持つ関数かを調べる
	 */
	public boolean IsFunctionWithXtimesFrequency(String func_s, int x_times){
		
		for(Function f: functions){
			if(f.toDBString().equals(func_s)){
				if (f.getFrequency() >= x_times)
					return true;
				else
					return false;
			}
		}
		
		//該当する関数が存在しない！
		//throw new Error();
		//System.out.println("頻度ファイルに存在しない: " + func_s);
		return false;
	}
	
//	public static int import_function_size = 0;
//	public static int import_type_size = 0;
	
	//クリアする。
	public synchronized void clearStaticTypeAndFunctionFields(){
		types.clear();
		for(Function f:functions){
			f.setIsDummy(false);
		}
		functions.clear();		
	}
	
	//クリアする。
	public void clearImportStaticFields(){
		imported_types.clear();
		imported_functions.clear();		
	}
	
	//追加する。
	public synchronized void putAllImportStaticFields(){
		types.putAll(imported_types);
		functions.addAll(imported_functions);	
	}
	
	//追加する。
	public synchronized void putAllOriginalStaticFields(){
//		types.putAll(original_types);
//		functions.addAll(original_functions);	
	}
	
	public List<String> getImportedFunctions(){
		List<String> list = new ArrayList<String>();
		
		for(Function f :imported_functions){
			list.add(f.toDBString());
		}
		return list;
	}
	
	public List<String> getImportedTypes(){
		List<String> list = new ArrayList<String>();
		
		for(Type t :imported_types.values()){
			list.add(t.toDBString());
		}
		return list;
	}
	
	//頻度をFunctionにセットする
	public void setFreqToFunction(List<String> functions){
		List<Function> localFunctions = new ArrayList<Function>();
		for(String s: functions){
			Function f = new Function(s);
			
			f.setFrequency(frequencies);
			
			localFunctions.add(f);
			//System.out.println(f.getFrequency() + "," + f.toDBString());
		}
		
		
		ArrayList<Function> new_funcs = new ArrayList<Function>();
		TreeMap<String, Type> new_types = new TreeMap<String, Type>();
		addNewFunctions2(localFunctions, new_types , new_funcs);
		
//		import_type_size = new_types.size();
//		import_function_size = new_funcs.size();
		
//		System.out.println("type: " + new_types.size());
//		System.out.println("func: " + new_funcs.size());
	}
	
	//頻度をFunctionにセットする
	public synchronized void setImportedClassAndFunction(List<String> classes, List<String> functions){
		
		//クラスの追加 (重複を調べる)
		for(String s: classes){
			Type t = new Type(s);
			if(!types.containsKey(t.getName())){
				types.put(t.getName(), t);
				imported_types.put(t.getName(), t);
			}else{
				//キーがあるとき、
				//subtypeを追加する。
				types.get(t.getName()).addSubTypes(t.getSubTypes());
			}
		}
		
		List<Function> tmp_imported_functions = new ArrayList<Function>();
		
		//関数に頻度情報を付加する。
		for(String s: functions){
			Function f = new Function(s);
			
			f.setFrequency(frequencies);
			
//			if(f.getName().contains("addResultListener")){
//				System.out.println();
//			}
			
			tmp_imported_functions.add(f);
			//System.out.println(f.getFrequency() + "," + f.toDBString());
		}

		//functions に imported_functionsを追加する。
		//関数の引数の型を調べて、typesの間の重複がなくなったものが、imported_types に入る。
		//tmp_imported_functions とfunctionsの間の重複がなくなったものが、imported_functions に入る。
		addNewFunctions2(tmp_imported_functions, imported_types, imported_functions);

	}
	
	private boolean containsType(String appear){
		Collection<Type> values = types.values();
		for(Type type: values){
			if(type.getName().equals(appear)){
				return true;
			}
		}
		return false;
	}

//	public static FunctionTree getOutputFunctionTree(int i){
//		return outputFunctionTrees.get(i);
//	}

	public FunctionTree[] getOutputFunctionTrees(){
		return outputFunctionTrees;
	}


	//プラグインの中からキーワードプログラミングを動かす。
	public FunctionTree[] execute(List<Word> keywords, String desiredType, List<String> localTypes, List<String> localFunctions, String state){

		long start = System.currentTimeMillis();
		time_consumed_getting_similarity = 0;
		
		ArrayList<Type> new_types = new ArrayList<Type>();
		ArrayList<Function> new_funcs = new ArrayList<Function>();
//types = 146
//functions = 2919
//		for(Type t: types.values()){
//			System.out.println("  " + t.toDBString());
//		}
//localTypes = 49
//localfunctions = 0
		if(localTypes != null)
		for(String s: localTypes){
//			System.out.println(s);
			Type t = new Type(s);
			if(!types.containsKey(t.getName())){
				types.put(t.getName(), t);
				new_types.add(t);
			}else{
				//キーがあるとき、
				//subtypeを追加する。
				types.get(t.getName()).addSubTypes(t.getSubTypes());
			}
		}
//new_types = 14
		//新しい関数の追加
		addNewFunctions(localFunctions, new_types, new_funcs);

		//キーワードの処理。
		inputKeywords(keywords);
		
		//キーワードとラベルのマッチにより点数をつける。
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		boolean flg_compact = store.getBoolean(PreferenceInitializer.GROUPING_FUNCTIONS);
//
//		if(flg_compact){
//			compactFunctions();
//		}
		
		//bestRootsを生成する。
		bestRoots = new BestRoots(types);

		//引数探索方法の取得
		//SEARCH_MODE_FUNCTION_PARAMETER = store.getString(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER);
		SEARCH_MODE_FUNCTION_PARAMETER = PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL;
		
		//DynamicProgram アルゴリズムを実行
		DynamicProgram();
		
		//ExtractTree アルゴリズムを実行
		outputFunctionTrees = ExtractTree(desiredType);

		long stop = System.currentTimeMillis();
		
		
//		for(Function f: functions){
//			System.out.println(f.getFrequency() + "," + f.toDBString());
//		}
		
		//結果をコンソールに表示する。
		printResult(desiredType, new_types, new_funcs, stop-start, state);
		
		//bestRootsの中身を表示
//		printBestRoots();
//		printFunctionsList(functions);
		//ログ出力用ファイル
		//createLogFiles(new_types, new_funcs);
	
		//staticなフィールドを全てクリアする
		clearStaticFields(true);

		return outputFunctionTrees;
	}
	
	private static TreeMap<String, List<Function>> map_param_func = new TreeMap<String, List<Function>>();
	
	
//	private void compactFunctions(){
//		List<Function> list_score_0 = new ArrayList<Function>();
//		List<Function> list_score_over0 = new ArrayList<Function>();
//		
//		
//		for(Function f: functions){
//			
//			int score = 0;
//			for(String s: f.getLabels()){
//				
//				for(String k: input_keywords){
//					if(s.equals(k)){
//						score++;
//					}
//					
//				}
//			}
//			f.setWordScore(score);
//			
//			if(score == 0){
//				list_score_0.add(f);
//				
//				String para_type = f.getParaTypeName();
//				
//				if(!map_param_func.containsKey(para_type)){
//					List<Function> list = new ArrayList<Function>();
//					list.add(f);
//					map_param_func.put(para_type, list);
//				}else{
//					List<Function> list = map_param_func.get(para_type);
//					list.add(f);
//				}
//						
//			}else{
//				list_score_over0.add(f);
//			}
//		}
//		
//		boolean flg_print = false;
//		System.out.println("func size =" + functions.size());
//		System.out.println("0    size =" + list_score_0.size());
//		System.out.println("over0size =" + list_score_over0.size());
//		System.out.println("map  size =" + map_param_func.size());
//		
//		if(flg_print){
//			
//			//TreeMapの先頭から順番に画面表示する
//	        Iterator<String> it = map_param_func.keySet().iterator();
//	        while (it.hasNext()) {
//	            String key = it.next();
//	            System.out.println(key + ", " + map_param_func.get(key).size());
//	        }
//		}
//		
//        functions.clear();
//        functions.addAll(list_score_over0);
//        
//        //各キーの先頭の１つのFunctionをダミーとして追加。
//        Iterator<String> it = map_param_func.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            Function f = map_param_func.get(key).get(0);
//            f.setIsDummy(true);
//            functions.add(f);
//        }
//        System.out.println("func size =" + functions.size());
//		
//	}
	
	private void addNewFunctions(List<String> localFunctions,
			ArrayList<Type> new_types, ArrayList<Function> new_funcs) {
		if(localFunctions != null)
		for(String s: localFunctions){
			Function f = new Function(s);
			//新しい関数があった場合のみ、
			if(functions.add(f) == true){
				new_funcs.add(f);
				//新しく出現したクラスのチェック

				//返り値のチェック
				String ret = f.getReturnType();
				if(ret != null){
					Type t = new Type(ret);
					if(!types.containsKey(t.getName())){
						types.put(t.getName(), t);
						new_types.add(t);
					}else{
						//キーがあるとき、
						//subtypeを追加する。
						types.get(t.getName()).addSubTypes(t.getSubTypes());
					}
				}
				
				//パラメータのチェック
				if(f.getParameters() != null)
				for(String param: f.getParameters()){//無ければ追加
					Type tt = new Type(param);
					if(!types.containsKey(tt.getName())){
						types.put(tt.getName(), tt);
						new_types.add(tt);
					}else{
						//キーがあるとき、
						//subtypeを追加する。
						types.get(tt.getName()).addSubTypes(tt.getSubTypes());
					}
				}
			}
		}
	}

	/*
	 * localFunctionsの引数 List<String> --> List<Function>
	 */
	private void addNewFunctions2(List<Function> localFunctions,
			TreeMap<String, Type> new_types, ArrayList<Function> new_funcs) {
		if(localFunctions != null)
		for(Function f: localFunctions){
			//新しい関数があった場合のみ、
			if(functions.add(f) == true){
				new_funcs.add(f);
				//新しく出現したクラスのチェック

				//返り値のチェック
				String ret = f.getReturnType();
				Type t = new Type(ret);
				if(!types.containsKey(t.getName())){
					types.put(t.getName(), t);
					new_types.put(t.getName(), t);
				}else{
					//キーがあるとき、
					//subtypeを追加する。
					types.get(t.getName()).addSubTypes(t.getSubTypes());
				}

				//パラメータのチェック
				if(f.getParameters() != null)
				for(String param: f.getParameters()){//無ければ追加
					Type tt = new Type(param);
					if(!types.containsKey(tt.getName())){
						types.put(tt.getName(), tt);
						new_types.put(tt.getName(), tt);
					}else{
						//キーがあるとき、
						//subtypeを追加する。
						types.get(tt.getName()).addSubTypes(tt.getSubTypes());
					}
				}
			}
		}
	}
		
	/*
	 * TypeとFunctionのログを出力する。
	 */
	private void createLogFiles(HashSet<Type> new_types, HashSet<Function> new_funcs) {
		String Type_FILE_NAME = "./type.txt";
		String Function_FILE_NAME = "./function.txt";


			File fileL = new File(Type_FILE_NAME);
			File fileC = new File(Function_FILE_NAME);


			try{


				fileL.createNewFile();
				fileC.createNewFile();
				PrintWriter pwL = new PrintWriter(new BufferedWriter(new FileWriter(fileL)));
				PrintWriter pwC = new PrintWriter(new BufferedWriter(new FileWriter(fileC)));

				for(Type t: new_types){
					//System.out.println(t.toDBString());
					pwL.println(t.toDBString());
				}

				for(Function f: new_funcs){
					//System.out.println(t.toDBString());
					pwC.println(f.toDBString());
				}
				pwL.close();
				pwC.close();


			}catch(IOException e){
				e.printStackTrace();
			}
	}

	/*
	 * デバッグ用 結果表示メソッド
	 */
	
	private void printResult(String desiredType, ArrayList<Type> new_types, ArrayList<Function> new_funcs, long time, String state) {
		LogControl logControl = new LogControl(LogControl.KP);
		
		//コードコンプリーションのときはログ状態の表示を行う。サーチモードのときはログ状態の表示を毎回行わない。邪魔なので。
		if(state.equals(KpRunningState.CODE_COMPLETION))
			logControl.printLogState();
		
		//new_typesに含まれる空文字列削除
		new_types.remove(new Type(""));
		
		logControl.println(">>> start keyword programming >>>");
		logControl.println("");

		logControl.println(" >> 基本情報 >>", LogControl.KP_BASIC);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String flg_input = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
		logControl.println("  入力キーワード改変 =" + flg_input);
		String aimai = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
		logControl.println("  あいまいキーワード 対応 =" + aimai);
		int ld_del = store.getInt(PreferenceInitializer.LD_DELETE);
		int ld_rep = store.getInt(PreferenceInitializer.LD_REPLACE);
		int ld_add = store.getInt(PreferenceInitializer.LD_ADD);
		logControl.println("  LD = " + ld_del + ", " + ld_rep + ", " + ld_add);
		String bunkatu = store.getString(PreferenceInitializer.SEPARATE_KEYWORDS);
		logControl.println("  キーワード分割 =" + bunkatu);
	
		logControl.println("  実行にかかった時間= " + time + " ミリ秒。KeywordProgramming.printResult", LogControl.KP_BASIC);
		logControl.println("  類似度計算にかかった時間= " + time_consumed_getting_similarity + " ミリ秒。", LogControl.KP_BASIC);
		logControl.println("  類似度計算にかかった割合= " + (double) time_consumed_getting_similarity / time, LogControl.KP_BASIC);
		
		
		logControl.println("  BEST_R =" + BEST_R);
		logControl.println("  関数木の最大の高さ =" + HEIGHT);
		logControl.println("  全探索する組み合わせ数の閾値=" + COMBINATION_SIZE);
		logControl.print("  入力キーワード= ", LogControl.KP_BASIC);
		for(String w: input_keywords)
			logControl.print(w + ", ", LogControl.KP_BASIC);
		logControl.println("");
		logControl.println("  出力候補の望ましい返り値の型= " + desiredType, LogControl.KP_BASIC);
		logControl.println("  入力キーワード数=" + input_keywords.size());
		
		if(outputFunctionTrees != null)
			logControl.println("  生成された出力の総数= " + outputFunctionTrees.length, LogControl.KP_BASIC);
		
		logControl.println("  特徴の重みの組= (" + ExplanationVector.getWeightString() + ")", LogControl.KP_BASIC);
		
		logControl.println(" === 型数 === ", LogControl.KP_BASIC);
		logControl.println("  総型数= " + types.size(), LogControl.KP_BASIC);
//		logControl.println("  オリジナル= " + original_types.size(), LogControl.KP_BASIC);
		if(new_types != null){
			logControl.println("  ローカルの型数= " + (new_types.size() + imported_types.size()), LogControl.KP_BASIC);
			logControl.println("  インポート分= " + (imported_types.size()), LogControl.KP_BASIC);
			logControl.println("  それ以外= " + (new_types.size()), LogControl.KP_BASIC);
		}
		
		logControl.println(" === 関数数 === ", LogControl.KP_BASIC);
		logControl.println("  総関数数= " + functions.size(), LogControl.KP_BASIC);
//		logControl.println("  オリジナル= " + original_functions.size(), LogControl.KP_BASIC);
		if(new_funcs != null){
			logControl.println("  ローカルの関数数= " + (new_funcs.size() + imported_functions.size()), LogControl.KP_BASIC);
			logControl.println("  インポート分= " + (imported_functions.size()), LogControl.KP_BASIC);
			logControl.println("  それ以外= " + (new_funcs.size()), LogControl.KP_BASIC);
		}
		
		int num_f_with_freq = 0; 
		for(Function f:functions){
			if(f.getFrequency() > 0){
				num_f_with_freq++;
			}
		}
		
		logControl.println(" 頻度0以上の関数数= " + num_f_with_freq, LogControl.KP_BASIC);
		
		logControl.println(" << 基本情報 <<", LogControl.KP_BASIC);
		logControl.println("", LogControl.KP_BASIC);
		
		if(new_types != null)
			logControl.println("  ローカルの型数= " + new_types.size(), LogControl.KP_TYPES);
		logControl.println(" >> ローカルの型一覧 >>", LogControl.KP_TYPES);
		if(new_types != null){
//			for(Type t: new_types){
//			logControl.println("  " + t.toDBString(), LogControl.KP_TYPES);
//		}
			for(Type t: types.values()){
				logControl.println("  " + t.toDBString(), LogControl.KP_TYPES);
			}
		}
		logControl.println(" << ローカルの型一覧 <<", LogControl.KP_TYPES);
		logControl.println("", LogControl.KP_TYPES);

		
		if(new_funcs != null)
			logControl.println("  ローカルの関数数= " + new_funcs.size(), LogControl.KP_FUNCTIONS);
		logControl.println(" >> ローカルの関数一覧 >> 出力形式：[親クラス名, isStatic, isFinal, type(field or constructor or method or localvariable), 返り値の型, 名前, ラベル(区切り文字;), 引数の型(何個でも) ]", LogControl.KP_FUNCTIONS);
		if(new_funcs != null){
//			for(Function f: new_funcs){
//			logControl.println("  " + f.toDBString(), LogControl.KP_FUNCTIONS);
//		}
			for(Function f: functions){
				logControl.println("  " + f.toDBString(), LogControl.KP_FUNCTIONS);
			}
		}
		logControl.println(" << ローカルの関数一覧s << 出力形式：[親クラス名, isStatic, isFinal, type(field or constructor or method or localvariable), 返り値の型, 名前, ラベル(区切り文字;), 引数の型(何個でも) ]", LogControl.KP_FUNCTIONS);
		logControl.println("", LogControl.KP_FUNCTIONS);

		logControl.println(" >> 出力候補一覧 >> 出力形式：[評価値, p(4つの特徴量), e_i(長さはキーワード数に等しい), 出力文字列]", LogControl.KP_RESULTS);
		for(FunctionTree t:outputFunctionTrees){
			if(t != null){
				logControl.println("  " + t.toEvalString() + t.toCompleteMethodString(), LogControl.KP_RESULTS);
//					logControl.out(t.toLogDBString());
			}
		}
		logControl.println(" << 出力候補一覧 << 出力形式：[評価値, p(4つの特徴量), e_i(長さはキーワード数に等しい), 出力文字列]", LogControl.KP_RESULTS);
		logControl.println("", LogControl.KP_RESULTS);

		logControl.println("<<< end keyword programming <<<");
		logControl.close();
	}

	/**
	 
	 * 1:typesリストとfunctionsリストのクリア
	 *   新たに追加したローカル情報の削除
	 *   original_types, original_functionsは、Eclipseの最初で読み込まれ後から変更されない。
	 *   imported_types, imported_functionsは、Import.getImportDeclarationの中からだけ、変更され、ほかのタイミングでは変更されない。
	 *　　　ここでは、それ以外に追加された、周辺ソースコード状況のみがクリアされることになる。
	 *
	 * 2:numOfSameWordsのクリア
	 *
	 * @param putImport インポート文の情報も追加するか
	 */

	public void clearStaticFields(boolean putImport){
		
		//typesとfunctionsのクリア
		clearStaticTypeAndFunctionFields();
		
		//original分をtypesとfunctionsに追加。
//		putAllOriginalStaticFields();
		copyOriginalFiles_consoleTest3();
		
		//インポート分もtypesとfunctionsに追加。
		if(putImport)
			putAllImportStaticFields();
		
		numOfSameWords.clear();
		if(bestRoots != null)
			bestRoots.clearTable();//これやってもやらなくても変わらないようだ。
		
//		System.gc();
		//used memoryの測定(単位：KByte)
//		Runtime rt = Runtime.getRuntime();
//		long usedMemory = rt.totalMemory()/1024 - rt.freeMemory()/1024;
//		System.out.println("使用メモリ量= " + usedMemory + "KByte");

	}

	public void copyOriginalFiles_consoleTest3(){
		//ディープコピー
		Set keySet = console_test.ConsoleTest3.original_types.keySet();
		Iterator iteKey = keySet.iterator();
		while(iteKey.hasNext()){
            String key = String.valueOf(iteKey.next());// Keyを取得
            types.put(key, console_test.ConsoleTest3.original_types.get(key).clone());// KeyでtmpMapから値を取り出し、
        }
		
		for(Function f:console_test.ConsoleTest3.original_functions)
			functions.add(f.clone());	
	}
	
	//Classesリストの表示
	public static void printClassList(ArrayList<String> list){
		for(String s: list){
			System.out.println(s);
		}
	}
	//Functionsリストの表示
	public static void printFunctionsList(ArrayList<Function> list){
		for(Function f: list){
			System.out.println(f.toString());
		}
	}

	public void printBestRoots(){
		for(int i = 0; i < HEIGHT; i++){

			for(Type t : types.values()) {
				FunctionTree[] roots = bestRoots.getRoots(t.getName(), i);
				for(FunctionTree r: roots){
					if(r != null){//このnullチェックは必要
						System.out.print("i= "+ i + ", ");
						System.out.print("ret_t_of_f= "+ t.getName() + ", ");
						System.out.print("f= "+ r.getRoot().getFunction().getName() + ", ");
						System.out.print("parent_t_of_f= "+ r.getRoot().getFunction().getParentClass() + ", ");
						System.out.print("tree= "+ r.toCompleteMethodString() + ", ");
						System.out.print("e= "+ r.getEvaluationValue());
						System.out.println("");
					}
				}
//				int count = bestRoots.getSizeOfRoots(t.getName(), i);
//				if(roots.length > 0)
//					System.out.println(i + ", "+ t.getName() + ", " + count);
			}
			System.out.println("");
		}
	}


	/*
	 * キーワードのついての処理。
	 */
	public void inputKeywords(List<Word> words){
		
		input_keywords_word = words;
		input_keywords = new ArrayList<String>();
		
		for(Word word:words){
			input_keywords.add(word.getWord());
		}
		
		
//		if(s.equals("")){	
//			return;
//		}
			
		//文字列を小文字化する
//		String s_lowerCase = s.toLowerCase();
		
		//コンマあり、なしで場合わけ
//		if(s_lowerCase.contains(",")){
//			//コンマで分割
//			List<String> func_val = Arrays.asList(s_lowerCase.split(","));
//			//前方が関数
//			List<String> funcs = Arrays.asList(func_val.get(0).split("[ 　\t]"));
//
//			//後方が変数
//			List<String> vals = Arrays.asList(func_val.get(1).split("[ 　\t]"));
//		
//			//関数は、前に"@"をつける
//			for(int i= 0; i < funcs.size(); i++){
//				if(!funcs.get(i).equals("") && !funcs.get(i).matches("[ 　\t]"))
//					input_keywords.add("@" + funcs.get(i));
//			}
//			
//			//変数は、そのまま
//			for(int i= 0; i < vals.size(); i++){
//				if(!vals.get(i).equals("") && !vals.get(i).matches("[ 　\t]"))
//					input_keywords.add(vals.get(i));
//			}
//			
//		}else{
//			//keywordに分割
//			input_keywords = Arrays.asList(s_lowerCase.split("[ 　\t]"));
//		}
		
		//keywordを同一単語ごとにその個数をカウントする
		for(int i= 0; i < input_keywords.size(); i++){
			String word = input_keywords.get(i);
			int count = 1;
			if(numOfSameWords.containsKey(word)){
				//キーがすでにあるとき、以前までの数と１を足す
				count += numOfSameWords.get(word);
			}
			numOfSameWords.put(word, count);
		}
	}

	/**
	 * procedure DynamicProgram
	 *
	 * this function modifies bestRoots
	 */
	public synchronized void DynamicProgram(){
		/*
		 * "java.lang.Object"のsubTypeを作成。
		 */
		Type t_obj = types.get("java.lang.Object");
		for(Type t_sub: types.values())
			if(!t_sub.getName().equals("java.lang.Object"))
				t_obj.addSubType(t_sub.getName());
		
		//for each 1 <= i <= h
		for(int i = 0; i < HEIGHT; i++){
			//for each t in T
			for(Type t : types.values()) {
				// bestRoots(t,i) = 0
				//for each f in F where ret(f) in sub(t)
				for(String subt: t.getSubTypes()){
					for(Function f: functions){
						if(f.getReturnType().equals(subt)){
							// e = GetBestExplForFunc(f, i-1)
							List<FunctionTree> treeList = GetBestExplTreeForFunc(f, i);
							if(treeList != null)
							for(FunctionTree tree: treeList){							
								// if e > -∞ then bestRoots(t, i) = bestRoots(t, i)U(e, f)
								if(tree != null && tree.getEvaluationValue() > -9999.0){
									bestRoots.addRoot(tree);
								}
							}
						}
					}
	            }
				//keep the best r roots
				bestRoots.keepBestRoots(t.getName(), i);
            }
		}
	}
	
//	public static synchronized void DynamicProgram(){
//
//		/*
//		 * "java.lang.Object"のsubTypeを作成。
//		 */
////		Type t_obj = types.get("java.lang.Object");
////		for(Type t_sub: types.values())
////			if(!t_sub.getName().equals("java.lang.Object"))
////				t_obj.addSubType(t_sub.getName());
//
//		//for each 1 <= i <= h
//		for(int i = 0; i < HEIGHT; i++){
//			//for each t in T
//			for(Type t : types.values()) {
//
//
//				// bestRoots(t,i) = 0
//				//for each f in F where ret(f) in sub(t)
//				for(String subt: t.getSubTypes()){
//
//
//					for(Function f: functions){
//						if(f.getReturnType().equals(subt)){
//							
////							if(i == 1 && f.getName().equals("Integer")){
////								if(f.getParameters() != null && f.getParameters().get(0).equals("java.lang.Integer")){
////								System.out.println(f.getName());
////								}
////							}
//							
////							if(i == 2 && f.getName().equals("add") && f.getParentClass().equals("java.util.List<java.lang.Integer>")
////							&& f.getReturnType().equals("java.lang.Boolean")){
////								System.out.println(f.getName());
////							}
//							
//							// e = GetBestExplForFunc(f, i-1)
//							List<FunctionTree> treeList = GetBestExplTreeForFunc(f, i);
//							
//
////							if(i == 1 && f.getReturnType().equals("java.lang.Integer")){
////								if(treeList != null)
////								for(FunctionTree ftree: treeList){
////									System.out.println(ftree.getEvaluationValue() + ", " + ftree.toCompleteMethodString());
////								}
////							
////							}
//							if(treeList != null)
//							for(FunctionTree tree: treeList){
//									
//								// if e > -∞ then bestRoots(t, i) = bestRoots(t, i)U(e, f)
//								if(tree != null && tree.getEvaluationValue() > -9999.0){
//									bestRoots.addRoot(tree);
////									if(tree.toCompleteMethodString().equals("fFull.add(new Integer(i))")){
////									System.out.println(tree);
////									}
//								}
//							}
//						}
//					}
//	            }
//				//keep the best r roots
//				bestRoots.keepBestRoots(t.getName(), i);
//				
//				//sorted_ftree_map は、虱潰し探索の時に使用する。
////				List<FunctionTree> roots = Arrays.asList(bestRoots.getRoots(t.getName(), i));
////				
////				List<FunctionTree> sorted_ftree = sorted_ftree_map.get(t.getName());
////				if(sorted_ftree != null){
////					for(FunctionTree root: roots)
////						if(root != null)
////							sorted_ftree.add(root);
////
////					Collections.sort(sorted_ftree);
////					sorted_ftree_map.put(t.getName(), sorted_ftree);
////				}else{
////					sorted_ftree = new ArrayList<FunctionTree>();
////					for(FunctionTree root: roots)
////						if(root != null)
////							sorted_ftree.add(root);
////					
////					sorted_ftree_map.put(t.getName(), sorted_ftree);
////				}
////				
////				if(t.getName().equals("java.lang.Integer")){
////					for(FunctionTree ft :sorted_ftree){
////						System.out.println(ft.getEvaluationValue() +", "+ft.toCompleteMethodString());
////					}
////				}
//				
//            }
//		}
//	}
	/**
	 * procedure GetBestExplForFunc
	 *
	 *	最低でもRootのみのTreeは取得できる
	 *
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFunc(Function f, int h_max){
		//return GetBestExplTreeForFuncGreedy(f, h_max);//オリジナルモード
		if(SEARCH_MODE_FUNCTION_PARAMETER.equals(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL)){
			return GetBestExplTreeForFuncGreedy(f, h_max);//オリジナルモード
		}else if(SEARCH_MODE_FUNCTION_PARAMETER.equals(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_EXHAUSTIVE)){
			//return GetBestExplTreeForFunc_combinationSize(f, h_max);//全探索モード(組み合わせ数打ち切り)
			return GetBestExplTreeForFunc_pamamLength(f, h_max);//全探索モード(引数数打ち切り)
		}else{
			//return GetBestExplTreeForFuncGreedyBestFirst(f, h_max);//最良優先探索モード
			return GetBestExplTreeForFuncGreedy(f, h_max);//オリジナルモード
		}
	}

	/**
	 * procedure GetBestExplForFunc
	 *
	 *	最低でもRootのみのTreeは取得できる
	 *
	 * 組み合わせの数によって場合分け
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFunc_combinationSize(Function f, int h_max){
		
		//全探索VER
		if(f.getParameters() != null){
			int combination_size = 1;
			for(String param: f.getParameters()){//引数の順番通りにループする
				int tree_size = 0;
				for(int i = 0; i < h_max; i++){
					FunctionTree[] trees = bestRoots.getRoots(param, i);
					if(trees != null)
						tree_size += trees.length;
				}
				combination_size *= tree_size;//tree_sizeが0なら、組み合わせ総数も0。
			}
			
			if(f.getParameters().size() > 1 && combination_size > 0 && combination_size <= COMBINATION_SIZE){
				//引数数が４以上の関数名を出力
//				System.out.println(h_max + ", "+ f.toDBString());
				return GetBestExplTreeForFuncExhaustive(f, h_max);
			}
		}
		//オリジナルVER 貪欲法
		return GetBestExplTreeForFuncGreedy(f, h_max);
	}
	
	/**
	 * procedure GetBestExplForFunc
	 *
	 *	最低でもRootのみのTreeは取得できる
	 *
	 * 引数の数によって場合分け
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFunc_pamamLength(Function f, int h_max){
	//		if(f.getParameters() != null){
	//		System.out.println(f.getParameters().size());
	//	}
			
			
		//引数数がxのとき全探索VER
		if(f.getParameters() != null)
		if(f.getParameters().size() > 1 && f.getParameters().size() <= MAX_ARGUMETNT_SIZE){
			//引数数がMAX_ARGUMETNT_SIZE以上の関数名を出力
	//		System.out.println(h_max + ", "+ f.toDBString());
	//		System.out.println(f.getParameters().size());
			return GetBestExplTreeForFuncExhaustive(f, h_max);
		}
		
		//オリジナルVER 貪欲法
		return GetBestExplTreeForFuncGreedy(f, h_max);
	}


	/**
	 * 力任せ探索、虱潰し探索VER。
	 *
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFuncExhaustive(Function f, int h_max){

		//System.out.println(h_max + ", "+ f.toDBString());
		
		//親クラスが"this"かつ、非staticの場合は、レシーバ無しでも起動できるようにする。
//		boolean this_flg = false;
//		if(f.getParentClass().equals("this") && f.isStatic() == false){
//			this_flg = true;
//		}
		
		//関数が自クラスかつ、staticでない場合は、レシーバ無しで起動できるようにする。
		boolean this_nonStatic_flg = false;
		if(f.isInThisClass() == true && f.isStatic() == false){
			this_nonStatic_flg = true;
		}
		
		List<FunctionTree> ret_list = new ArrayList<FunctionTree>();
		
		
		//for each p in params(f)
		
		if(f.getParameters() == null){
			FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//RootのみのTree
			ret_list.add(best_tree);
			return ret_list;	//関数に引数がないとき、rootのみを返す
		}else if(h_max == 0 && this_nonStatic_flg == false)
			return null;	//関数に引数はあり、高さが0で、自クラスの関数ではないとき、nullを返す
		
		//this_flg=true、かつ、引数1個のとき、その引数はレシーバである。
		if(this_nonStatic_flg == true && f.getParameters().size() == 1){
			FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//RootのみのTree
			ret_list.add(best_tree);
			return ret_list;
		}
		
		//this_flg=trueのときは、高さ0、引数レシーバのみのときがある。
//		if(this_flg == true && f.getParameters().length == 1)
//			return best_tree;
		
//		int candidate_num = COMBINATION_SIZE / f.getParameters().size();
		int candidate_num = COMBINATION_SIZE;
		
//		if(h_max == 2 && f.getName().equals("add") && f.getParentClass().equals("java.util.List<java.lang.Integer>")
//				&& f.getReturnType().equals("java.lang.Boolean")){
//					System.out.println("=============================================================");
//				}
		
		HashMap<Integer, ArrayList<FunctionTree>> param_list_map = new HashMap<Integer, ArrayList<FunctionTree>>();
		
		boolean flg = false;
		if(flg){
			//返り値の型が引数と一致する関数木を全てtreeListに挿入し、
			//そのリストを引数の型とリストのマップparam_list_mapに保持する
			int param_num = 0;
			for(String param: f.getParameters()){//引数の順番通りにループする
				ArrayList<FunctionTree> treeList = new ArrayList<FunctionTree>();
				
				for(int i = 0; i < h_max; i++){
					FunctionTree[] trees = bestRoots.getRoots(param, i);
					if(trees != null){
	
	
						int max = candidate_num;
						if(max > trees.length)
							max = trees.length;
						
						for(int j = 0; j < max; j++){
						
							if(trees[j] != null){
								treeList.add(trees[j]);
							}
						}
					}
						
				}
					
				param_list_map.put(param_num++, treeList);
	//			System.out.println(param_num + ", "+ param + ", " + treeList.size());
			}
		}else{
			//返り値の型が引数と一致する関数木を全てtreeListに挿入し、
			//そのリストを引数の型とリストのマップparam_list_mapに保持する
			int param_num = 0;
			for(String param: f.getParameters()){//引数の順番通りにループする
				ArrayList<FunctionTree> treeList = new ArrayList<FunctionTree>();
				List<FunctionTree> trees = sorted_ftree_map.get(param);
				
//				if(h_max == 0 && param.equals("java.lang.Integer")){
//					for(FunctionTree ft :trees){
//						System.out.println(ft.getEvaluationValue() +", "+ft.toCompleteMethodString());
//					}
//				}
				
				for(int i = 0; i < h_max; i++){
					
					
					
					
					
					if(trees != null){
						int max = candidate_num;
						if(max > trees.size())
							max = trees.size();
						for(int j = 0; j < max; j++){
							if(trees.get(j)!= null){
								treeList.add(trees.get(j));
							}
						}
					}
				}
				param_list_map.put(param_num++, treeList);
	//			System.out.println(param_num + ", "+ param + ", " + treeList.size());
			}
		}
		
		//引数の組み合わせの順列を全て求める。
		ParameterCombinations pc = new ParameterCombinations();
		ArrayList<ArrayList<FunctionTree>> allCombinations = pc.getAllCombinations(param_list_map);
		
		if(allCombinations.size() == 0)
			return null;
		

		 /*
		  *  引数の組み合わせの順列を全てを使用して、関数木を作成する。
		  *  組み合わせ総数が6個なら、出力される関数木も6個となる。listの中に関数木が6つ入る
		 */
		for(int i = 0; i < allCombinations.size(); i++){
			ArrayList<FunctionTree> comb = allCombinations.get(i);
			FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//RootのみのTree
			
			for(FunctionTree tree: comb){
				best_tree.addChild(tree);
			}
			
			ret_list.add(best_tree);
		}
		return ret_list;
		
		 /* 引数の組み合わせの順列を全て試し、
		 * その中で一番良い組み合わせとなる関数木の列を子関数木群とした関数木best_treeを求める。
		 * (先行研究では1通りしか試さない。)
		 */
		
//		ExplanationVector e_best = new ExplanationVector(input_keywords.size(), -ExplanationVector.INFINITE_VALUE);//空っぽ
//		ArrayList<FunctionTree> best_param_comb = null;
//		for(int i = 0; i < allCombinations.size(); i++){
//			ExplanationVector tmp_e_best = new ExplanationVector(input_keywords.size(), -ExplanationVector.INFINITE_VALUE);//空っぽ
//			ArrayList<FunctionTree> comb = allCombinations.get(i);
//			for(FunctionTree tree: comb){
//				//System.out.println(i + ", " + tree.toCompleteMethodString());
//				tmp_e_best.add(tree.e_vec);
//			}
//			//tmp_e_bestがe_bestよりも大きいとき、
//			if(tmp_e_best.compareTo(e_best) == 1){
//				e_best.substitution(tmp_e_best);//代入
//				best_param_comb = comb;
//			}
//			
//		}
//		
//		for(FunctionTree tree: best_param_comb){
//			best_tree.addChild(tree);
//		}
//		
//		return best_tree;
	}
	
	/**
	 * 未使用。
	 * 
	 * @param combination
	 * @param param_num
	 * @param param_list_map
	 */
	public void tree_combination(ArrayList<FunctionTree> combination, int param_num, HashMap<Integer, ArrayList<FunctionTree>> param_list_map){
		if(combination.size() == param_list_map.size()){
			System.out.println(combination);
			return;
		}
		ArrayList<FunctionTree> list = param_list_map.get(param_num);
		for(FunctionTree tree: list){
			combination.add(tree);
			param_num++;
			tree_combination(combination, param_num, param_list_map);
			combination.remove(tree);
			param_num--;
		}
	}
	
	/**
	 * オリジナルバージョン
	 * 貪欲法。
	 * 
	 * @param f
	 * @param h_max
	 * @return
	 */

	public List<FunctionTree> GetBestExplTreeForFuncGreedy(Function f, int h_max){

//		if(f.getName().equals("rules") && f.getParentClass().equals("this")){
//			System.out.println("aaa");
//		}
//		
//		if(f.getName().equals("TRUE")){
//			System.out.println("aaa");
//		}
		
		//関数が自クラスかつ、staticでない場合は、レシーバ無しで起動できるようにする。
		boolean this_nonStatic_flg = false;
		if(f.isInThisClass() == true && f.isStatic() == false){
			this_nonStatic_flg = true;
		}
			
		FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//RootのみのTree
		ExplanationVector e_cumulative = new ExplanationVector(input_keywords.size(), best_tree.e_vec);//best_treeのe_vecをコピーしてnew
		
		List<FunctionTree> ret_list = new ArrayList<FunctionTree>();
		
		//for each p in params(f)
		
		if(f.getParameters() == null){
			ret_list.add(best_tree);
			return ret_list;	//関数に引数がないとき、rootのみを返す
		}else if(h_max == 0 && this_nonStatic_flg == false)
			return null;	//関数に引数はあり、高さが0で、自クラスの関数ではないとき、nullを返す
		
		//this_flg=true、かつ、引数1個のとき、その引数はレシーバである。
		if(this_nonStatic_flg == true && f.getParameters().size() == 1){
			ret_list.add(best_tree);
			return ret_list;
		}
		
		for(int i = 0; i < f.getParameters().size(); i++){//引数の順番通りにループする
		
			//e_best = (-∞, 0, 0, 0, ...) 
			ExplanationVector e_best = new ExplanationVector(input_keywords.size(), -ExplanationVector.INFINITE_VALUE);//空っぽ	
			FunctionTree param_tree = null;
			//for each 1 <= i <= h
			for(int j = 0; j < h_max; j++){//h_max=0の場合はこのループは実行されない
				//for each (e', f') in bestRoots(p, i)
				FunctionTree[] trees = bestRoots.getRoots(f.getParameters().get(i), j);
				if(trees != null){
					for(FunctionTree t: trees){
						if(t != null){
							//if e_cumulative + e' > e_best
							//    e_best = e_cumulative + e'
							
							ExplanationVector tmp = ExplanationVector.add(e_cumulative, t.e_vec);
							if(tmp.compareTo(e_best) == 1){
								e_best.substitution(tmp);//代入
								param_tree = t;
							}

						}
					}
				}
			}
			//e_cumulative = e_best
			e_cumulative.substitution(e_best);
			
			if(param_tree == null)
				return null;	//引数が１つでも埋まらなければ,そのroot関数は選択しない。nullを返す。
			else
				best_tree.addChild(param_tree);
		}

		ret_list.add(best_tree);
		return ret_list;
	}

	
	/**
	 * 欲張り最良優先探索
	 * 
	 * 子関数木が持つ評価値の値をヒューリスティック関数と見なして、
	 * その子関数木の評価値の総和が高いものを優先して探索する。
	 * 
	 * 探索は回数を指定して途中で打ち切る。
	 * 
	 * @param f
	 * @param h_max
	 * @return
	 */

	public FunctionTree GetBestExplTreeForFuncGreedyBestFirst(Function f, int h_max){

		FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//RootのみのTree
		
		if(f.getParameters() == null)
			return best_tree;	//関数に引数がないとき、rootのみを返す
		else if(h_max == 0)
			return null;	//関数に引数はあるが、高さが0のとき、nullを返す
		
		/*
		 * param_list_mapと
		 * param_num
		 * を作成する。
		 */
		
		//返り値の型が引数と一致する関数木を全てtreeListに挿入し、
		//そのリストをTreeSetにして、値の重複を排除かつ、ソートしたのち、
		//引数の型とリストのマップparam_list_mapに保持する
		HashMap<Integer, ArrayList<FunctionTree>> param_list_map = new HashMap<Integer, ArrayList<FunctionTree>>();
		int param_num = 0;
		for(String param: f.getParameters()){//引数の順番通りにループする
			ArrayList<FunctionTree> treeList = new ArrayList<FunctionTree>();
			for(int i = 0; i < h_max; i++){
				FunctionTree[] trees = bestRoots.getRoots(param, i);
				if(trees != null){
					for(FunctionTree t: trees){
						if(t != null){
							treeList.add(t);
						}
					}
				}
			}
			/*
			 * TreeSetを使用することにより、
			 * 重複を無くしつつ、ソートも行う。
			 */
			TreeSet<FunctionTree> treeSet = new TreeSet<FunctionTree>(treeList);
			treeList = new ArrayList<FunctionTree>(treeSet);

			param_list_map.put(param_num++, treeList);
		}
		
		
		/*
		 * ここから欲張り最良優先探索
		 */
		Node.param_list_map = param_list_map;
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		List<Integer> firstNumList = new ArrayList<Integer>();
		//初期状態は各引数ともに、TreeSetの先頭（一番評価値の大きいもの）.
		for(int i = 0; i < param_num; i++){
			firstNumList.add(0);//初期状態は全て先頭、つまり0.
		}
		State firstState = new State(param_num, firstNumList);
		openList.add(new Node(firstState, 0));//初期状態を入れる。深さ0.
		
		int endDepth = 50;
		
		/*
		 * closedListに
		 * 指定した深さendDepthまで、
		 * 予想コスト順にノード（引数候補関数の組み合わせ）を
		 * 挿入する。
		 */
		while(true){
			if(openList.size() == 0)
				break;
			//ここでopenListをソートしてから、取る。
			Collections.sort(openList);
			Node node = openList.remove(0);//先頭ノードを取得する
			closedList.add(node);
			if(node.getDepth() == endDepth){//終了判定
				break;
			}
			for(Node child: node.getChildren()){
				child.setParent(node);
				openList.add(child);
			}
			
		}
		
		/*
		 * closedListに入っている引数候補関数の組み合わせについてのみ、
		 * 評価値を計算し、一番評価値が大きくなる組み合わせを
		 * best_treeとして返す。
		 */
		for(Node node: closedList){
			List<Integer> numList = node.getState().getNumberList();
			for(int i = 0; i < param_num; i++){
				int num = numList.get(i);
				FunctionTree ft = param_list_map.get(i).get(num);
			}
		}
		return best_tree;
	}

	
	
	/**
	 *
	 * 動的計画法の表で
	 * 望ましい返り値の型を返す関数木を
	 * 高さ1からHEIGHTまで全て取得し、
	 * 評価値でソートして返す関数
	 *
	 * @param desiredType 望ましい返り値の型
	 * @return outputTrees 評価値でソートされた関数木のリスト
	 */
	public FunctionTree[] ExtractTree(String desiredType){

		Set<FunctionTree> outputTrees = new HashSet<FunctionTree>(BEST_R * HEIGHT * 4 / 3 + 1);

		for(int i=0; i< HEIGHT; i++){
			FunctionTree[] roots = bestRoots.getRoots(desiredType, i);
			if(roots != null)
				for(FunctionTree t: roots){
					//null要素は削除
					//文字列が重複しないようにする。
					if(t != null){
						outputTrees.add(t);
					}
				}
		}
		outputTrees = new TreeSet<FunctionTree>(outputTrees);
		return outputTrees.toArray(new FunctionTree[0]);
	}

}
