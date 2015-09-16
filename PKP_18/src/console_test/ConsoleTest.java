package console_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import keywordProgramming.BestRoots;
import keywordProgramming.ExplanationVector;
import keywordProgramming.Function;
import keywordProgramming.FunctionTree;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;
import keywordProgramming.exp.Params;

public class ConsoleTest {
	/*
	 * x: -1.0 ~ -2.0
	 * y: < -1.0
	 * z: 1.0 ~ 2.5
	 * が良いらしい。
	 * w = {-1.5, 1.0, -3.0, 1.5}
	 */

	//今現在の重みの組み合わせ
	private static double [] start_w;

	//重みの更新幅
	private static double [] step_w;

	//選択した候補が最上位に来る重みの組み合わせ
	private static double [] best_w;


	static PrintWriter pwL;

	static int count = 0;
	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) {
		String file_path = "C:\\Users\\sayuu\\Desktop\\deta.txt";
		List<Integer> result_order_list = new ArrayList<Integer>();
		readFile2(file_path, result_order_list);
		
		HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
		
		System.out.println(result_order_list.size());
		for(Integer i: result_order_list){
			if(!map.containsKey(i)){
				map.put(i, 1);
			}else{
				int count = map.get(i);
				map.put(i, ++count);
			}
			
		}
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()){
			Integer i = it.next();
			System.out.println(i +","+ map.get(i));
		}
		
		//printResult();

	}

	public class Result{
		Params para = new Params();
		int order;
		String id;
	}
	
	static List<Double> list_score_10 = new ArrayList<Double>();
	static List<Double> list_score_30 = new ArrayList<Double>();
			
	public static void printResult() {
		String file_path = "C:\\Users\\sayuu\\Desktop\\0.txt";
		
		List<Result> result_order_list = new ArrayList<Result>();
		
		readFile(file_path, result_order_list);
		
		calcResult(result_order_list);
		
		for(int i = 0; i < list_score_10.size(); i++){
			System.out.print(list_score_10.get(i) + ", ");
		}
		System.out.println();
		for(int i = 0; i < list_score_30.size(); i++){
			System.out.print(list_score_30.get(i) + ", ");
		}
		System.out.println();
	}

	private static void calcResult(List<Result> result_order_list) {
		int count_appear = 0;
		int count_not_appear = 0;
		int count_no1 = 0;
		int count_no3 = 0;
		int count_no5 = 0;
		int count_no10 = 0;
		int count_no30 = 0;
		int total_num = 0;
		double reverse_score_10 = 0;
		double reverse_score_30 = 0;
		
		Result start = result_order_list.get(0);
		
		for(Result r:result_order_list){
			if(!start.para.equals(r.para)){
				start.para.print();
				list_score_10.add(reverse_score_10);
				list_score_30.add(reverse_score_30);
				showResult(total_num, count_appear, count_no1,
						count_no3, count_no5, count_no10, count_no30,
						reverse_score_10, reverse_score_30);
				//リセット
				total_num = 0;
				count_appear = 0;
				count_not_appear = 0;
				count_no1 = 0;
				count_no3 = 0;
				count_no5 = 0;
				count_no10 = 0;
				count_no30 = 0;
				
				reverse_score_10 = 0;
				reverse_score_30 = 0;
				start = r;
			}
			
			total_num++;
			int order = r.order;
			if(order == -1){
				count_not_appear++;
			}else{
				count_appear++;
			
				if(order == 0)
					count_no1++;
				if(order < 3)
					count_no3++;
				if(order < 5)
					count_no5++;
				if(order < 10)
					count_no10++;
				if(order < 30)
					count_no30++;
				
				if(order < 10)
					reverse_score_10 += (double)1/(order+1);
				
				if(order < 30)
					reverse_score_30 += (double)1/(order+1);
			}
		}
		start.para.print();
		list_score_10.add(reverse_score_10);
		list_score_30.add(reverse_score_30);
		showResult(total_num, count_appear, count_no1,
				count_no3, count_no5, count_no10, count_no30,
				reverse_score_10, reverse_score_30);
	}

	private static void showResult(int total_num,
			int count_appear, int count_no1, int count_no3, int count_no5,
			int count_no10, int count_no30, double reverse_score_10,
			double reverse_score_30) {
		System.out.println("総数, " + total_num);
		System.out.println("正解出現数, " + count_appear);
		System.out.println("上位1位以内出現数, " + count_no1);
		System.out.println("上位3位以内出現数, " + count_no3);
		System.out.println("上位5位以内出現数, " + count_no5);
		System.out.println("上位10位以内出現数, " + count_no10);
		System.out.println("上位30位以内出現数, " + count_no30);
		System.out.println("上位10位以内逆数スコア, " + reverse_score_10);
		System.out.println("上位30位以内逆数スコア, " + reverse_score_30);
	}

	private static void readFile(String file_path, List<Result> result_order_list){
		FileReader in;
		try {
			in = new FileReader(file_path);
			BufferedReader br = new BufferedReader(in);
			String line;
			
			while ((line = br.readLine()) != null) {
				String[] s1 = line.split(",");
				Result r = new ConsoleTest().new Result();
				r.para.w_arr.add(Double.valueOf(s1[0].trim()));
				r.para.w_arr.add(Double.valueOf(s1[1].trim()));
				r.para.w_arr.add(Double.valueOf(s1[2].trim()));
				r.para.w_arr.add(Double.valueOf(s1[3].trim()));
				r.para.w_arr.add(Double.valueOf(s1[4].trim()));
				r.para.const_freq = Double.valueOf(s1[5].trim());
				r.id = s1[6].trim();
				r.order = Integer.valueOf(s1[7].trim());
				result_order_list.add(r);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private static void readFile2(String file_path, List<Integer> result_order_list){
		FileReader in;
		try {
			in = new FileReader(file_path);
			BufferedReader br = new BufferedReader(in);
			String line;
			
			while ((line = br.readLine()) != null) {
				String[] s1 = line.split(",");
				Integer r = Integer.valueOf(s1[0].trim());
				result_order_list.add(r);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private static void test15tasks(int output_size) {
		long start = System.currentTimeMillis();
		
		for(int i = 1; i <= 15; i++){
			long st = System.currentTimeMillis();
			read_and_test_task(i, output_size, false);
			long sp = System.currentTimeMillis();
//			System.out.println("task "+ i + " 実行にかかった時間は " + (sp - st) + " ミリ秒です。");
//			System.out.println();
		}
		
//		
//		
//		read_and_test_task(8, output_size, false);
//		
		long stop = System.currentTimeMillis();
		System.out.println("平均時間は " + (stop - start) / 15.0 + " ミリ秒です。");
//		
	}
	
	private static double evecSum(double[] weight, double[] score){
		double sum = 0;
		for(int i = 0; i < 4; i++){
			sum += weight[i] * score[i];
		}
		return sum;
	}
	
	
	private static void originalKeywordProgramming(int output_size, int best_r) {
		double[] original_w = {-0.05, 1, -0.01, 0.001};
		ExplanationVector.setWeights(original_w);
		KeywordProgramming.BEST_R = best_r;
		oudan_1(output_size, true);
	}
	public static boolean gridSearch_top_output;
/*
 * taskを
 * start_task から
 * end_task まで
 * Grid Search する。
 */
	public static void gridSearchTasks(int start_task, int end_task, int output_size, boolean top_output) {
		gridSearch_top_output = top_output;
		for(int i = start_task; i <= end_task; i++){
		//task情報の読み込み
		readTask(i);

		String Type_FILE_NAME = "./gridSearch 5/gridSearch_" + i + "_" + wToString() + "_" + output_size + ".txt";
		//String Function_FILE_NAME = "./function.txt";


			File fileL = new File(Type_FILE_NAME);
			//File fileC = new File(Function_FILE_NAME);


			try{


				fileL.createNewFile();
				//fileC.createNewFile();
				pwL = new PrintWriter(new BufferedWriter(new FileWriter(fileL)));
				//PrintWriter pwC = new PrintWriter(new BufferedWriter(new FileWriter(fileC)));


				GridSearch0(i, output_size, top_output);

				pwL.close();
			}catch(IOException e){
				e.printStackTrace();
			}

			KeywordProgramming.clearStaticFields(false);
//			System.out.println(KeywordProgramming.types.size() +", "+ KeywordProgramming.functions.size());
		}
	}
/*
 * gridSearchTasks
 * の結果ファイルを使って、
 * GraphR228
 * のためのデータを作成する。
 */
	public static void makeGraphData(int output_size, boolean top_output, boolean ni_chi, boolean save_csv_file, boolean eval_inverse_order) {
		gridSearch_top_output = top_output;

		HashMap<Integer, ArrayList<String>> h = new HashMap<Integer, ArrayList<String>>();

		for(int i = 2; i <= 15; i++){
			ArrayList<String> arr = new ArrayList<String>();

			String input_txt_file_name = "./gridSearch 5/gridSearch_" + i + "_" + wToString() + "_" + output_size + ".txt";

			FileReader fr;
			try {
				fr = new FileReader(input_txt_file_name);
				BufferedReader br = new BufferedReader(fr);
				String s;
				while ((s = br.readLine()) != null) {
					arr.add(s);
				}
				br.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			h.put(i, arr);
		}

		String outputfilename = "./graph_data/GraphR228/csv_data/graph_data_" + wToString() +  "_" + output_size + "_" + Boolean.toString(ni_chi) + "_" + Boolean.toString(eval_inverse_order) + ".csv";

		File fileL = new File(outputfilename);
		try{
			fileL.createNewFile();
			pwL = new PrintWriter(new BufferedWriter(new FileWriter(fileL)));
			pwL.println("format,3");
			pwL.println("memo");
			pwL.println("memo");
			int text_len =  h.get(2).size();
			for(int j = 0; j < text_len; j++){
				int ans_count = 0;
				double score = 0;	// 正解順位の逆数を用いたスコアリング手法。
				for(int i = 1; i <= 15; i++){
					if(i != 1){	//1は不正解なのでそれ以外をやる。
						ArrayList<String> arr = h.get(i);
						String line = arr.get(j);
						String[] sp = line.split(",");
						int distance = Integer.parseInt(sp[6].trim());
						int order = Integer.parseInt(sp[7].trim()) + 1;	//0始まりを1始まりに変更する。
						String ans = sp[8].trim();

						if(i == 10 && ans.equals("out.println(f.getName())")){	//10だけはout.から始まる。
							ans_count++;
						}else if(distance == 0){	//答えならば、
							ans_count++;
							score += (double)1.0/order;
						}
					}
				}

				String[] out = h.get(2).get(j).split(",");
				double[] i_arr = new double[6];
				for(int k = 0; k < 6; k++){
					i_arr[k] =roundOfBigDecimal(Double.parseDouble(out[k].trim()));

				}

				if(ni_chi == false){
					System.out.println(j + ", " + i_arr[2] + ", " + i_arr[3] + ", " + i_arr[4] + ", " + i_arr[5] + ", " + ans_count + ", " + score);
					if(save_csv_file){
						if(eval_inverse_order)
							pwL.println(i_arr[2] + ", " + i_arr[4] + ", " + i_arr[5] + ", " + score);
						else
							pwL.println(i_arr[2] + ", " + i_arr[4] + ", " + i_arr[5] + ", " + ans_count);
					}
				}else{
					int ni_chi_count = 0;
					if(ans_count == 14)	//13??
						ni_chi_count = 1;
					System.out.println(j + ", " + i_arr[2] + ", " + i_arr[3] + ", " + i_arr[4] + ", " + i_arr[5] + ", " + ni_chi_count);
					if(save_csv_file)
						pwL.println(i_arr[2] + ", " + i_arr[4] + ", " + i_arr[5] + ", " + ni_chi_count);
				}
			}

			pwL.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * 小数第2位を四捨五入。（BigDecimalクラスを使用）
	 */
	public static double roundOfBigDecimal(double num) {
		return new BigDecimal(num).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/*
	 *  前処理
	 */
	static void init(){
		KeywordProgramming.readClassFileAndAddList(KeywordProgramming.SUB_CLASS_FILE_NAME);
		KeywordProgramming.readFunctionFileAndAddList(KeywordProgramming.FUNCTION_FILE_NAME);
	}

	//特徴の重みベクトル
//	private static double[] w_arr = {-0.5, 0.5, -0.1, 0.1};		//とりあえず初めは固定で。
	private static double[] w_arr = {-0.5, 1.0, -0.1, 0.1};		//[1]を1.0固定で、それ以外だけを動かす。

	//重みの更新幅
	private static double[] w_step_arr10 = {0.1, 0.2, 0.05, 0.1}; // 範囲の1/10
	private static double[] w_step_arr5 = {0.2, 0.4, 0.1, 0.2};	// 範囲の1/5
	private static double[] w_step_arr2 = {0.4, 0.8, 0.2, 0.4};	// 範囲の 1/2

	private static double[] w_step_arr_same1 = {0.1, 0.1, 0.1, 0.1};	// 全て同じ。
	private static double[] w_step_arr_same2 = {0.2, 0.2, 0.2, 0.2};	// 全て同じ。
	private static double[] w_step_arr_same3 = {0.5, 0.5, 0.5, 0.5};	// 全て同じ。

	private static double[] w_step_arr = w_step_arr_same3;

//	private static double[] p0_se = {-1.0, -0.1};
//	private static double[] p1_se = {0.2, 2.0};
//	private static double[] p2_se = {-1.0, -0.1};
//	private static double[] p3_se = {0.1, 1.0};

//	private static double[] p0_se = {-1.5, -0.5};	//上の方を狙う。
//	private static double[] p1_se = {0.2, 2.0};
//	private static double[] p2_se = {-1.5, -0.5};
//	private static double[] p3_se = {0.5, 1.5};

	private static double[] p0_se = {-5.0, -0.0};		//0.0 ~ 3.0まで。
	private static double[] p2_se = {-5.0, -0.0};
	private static double[] p3_se = {0.0, 5.0};

	private static double[] p4_se = {,};

	public static String wToString(){
		String s = "";
		for(double d: w_step_arr){
			s += d + "_";
		}
		for(double d: p0_se){
			s += d + "_";
		}
		for(double d: p2_se){
			s += d + "_";
		}
		for(double d: p3_se){
			s += d + "_";
		}
		return s;
	}

	public static void GridSearch0(int i, int output_size, boolean top_output) {
		double tmp_p0_e = p0_se[0];
		while(tmp_p0_e <= p0_se[1]){
			w_arr[0] = roundOfBigDecimal(tmp_p0_e);
			GridSearch2(i,output_size, top_output);
			tmp_p0_e += w_step_arr[0];
		}
	}

	//固定
//	public static void GridSearch1(int i) {
//		double tmp_p1_e = p1_se[0];
//		while(tmp_p1_e < p1_se[1]){
//			w_arr[1] = tmp_p1_e;
//			GridSearch2(i);
//			tmp_p1_e += w_step_arr[1];
//		}
//	}

	public static void GridSearch2(int i, int output_size, boolean top_output) {
		double tmp_p2_e = p2_se[0];
		while(tmp_p2_e <= p2_se[1]){
			w_arr[2] = roundOfBigDecimal(tmp_p2_e);
			GridSearch3(i, output_size, top_output);
			tmp_p2_e += w_step_arr[2];
		}
	}

	public static void GridSearch3(int i, int output_size, boolean top_output) {
		double tmp_p3_e = p3_se[0];
		while(tmp_p3_e <= p3_se[1]){
			w_arr[3] = roundOfBigDecimal(tmp_p3_e);
			ExplanationVector.setWeights(w_arr);
			testTask1_nearest_LD(i, output_size, top_output);
			tmp_p3_e += w_step_arr[3];
		}
	}

	/*
	 * 1つのタスクについて、テストして選んで重みを更新する簡数。
	 */
	public static void testAndEvalTask(int i, int output_size, boolean top_output) {
		FunctionTree[] ts = testTask1_nearest_LD(i, output_size, top_output);
		if(ts != null){
			ArrayList<FunctionTree> tl = new ArrayList<FunctionTree>();
			for(FunctionTree tree: ts){
				if(tree != null)
					tl.add(tree);
			}
			reEvalTask(tl);
		}
	}


	public static void read_and_test_task(int i, int output_size, boolean top_output){
		readTask(i);
		testTask1_nearest_LD(i, output_size, top_output);
		KeywordProgramming.clearStaticFields(false);
		//		System.out.println(KeywordProgramming.types.size() +", "+ KeywordProgramming.functions.size());
	}
	
	public static void read_and_test_task2(int i, int output_size, boolean top_output){
		readTask(i);
//		KeywordProgramming.execute3(keywords, desiredTypeTask(i), null, null);
		KeywordProgramming.clearStaticFields(false);
		//		System.out.println(KeywordProgramming.types.size() +", "+ KeywordProgramming.functions.size());
	}

	/*
	 * 1つのタスクについて1回だけやる。
	 */
	public static FunctionTree[] testTask1_nearest_LD(int i, int output_size, boolean top_output){

		KeywordProgramming.setBestRoots(new BestRoots(KeywordProgramming.getTypes()));

		//表(BestRoots)を作る
		KeywordProgramming.DynamicProgram();

		//望ましい返り値の型の取得
		String t_desired = desiredTypeTask(i);

		//高さ1～3までの表にあるものをまとめてソートしてから出す。
		FunctionTree[] ts = KeywordProgramming.ExtractTree(t_desired);
		
		/*
		
		FunctionTree[] ts_tmp = KeywordProgramming.getBestRoots().getRoots("java.lang.Object", KeywordProgramming.HEIGHT-1);
		int count_f = 0;
		for(FunctionTree f: ts_tmp){
			if(f != null)
				if(count_f < 100){
		//	System.out.println(count_f++ + ", "+ f.toCompleteMethodString() +", " + f.getEvaluationValue());
				}
		}
		*/
		
		String s = i + ", "+ count++ + ", ";
		for(double d : ExplanationVector.getWeights()){
			s += d + ", ";
		}
		int min_d = 99999;

		if(ts == null){
			s += min_d + ", " + ", 回答なし ts == null";
			System.out.println(i + ", 回答なし ts == null, "+ t_desired);
		}else{

			FunctionTree best_tree = null;
			int best_tree_order = -1;

			//pop-upに表示された候補の中のうち、
			//output_size 以内の中で、
			//一番答えに近い距離の候補を探す
			if(output_size > ts.length)
				output_size = ts.length;//長さがoverしてたら、配列サイズにする。
			if(top_output == false){
				for(int j = 0; j < output_size; j++){
					FunctionTree t = ts[j];
					if(t != null){
						
						LevenshteinDistance ld = new LevenshteinDistance(1,1,1) ;
						int d = ld.edit(t.toCompleteMethodString(), ansTask(i));
						
						if(d < min_d){
							min_d = d;
							best_tree = t;
							best_tree_order = j;
						}
//						System.out.println(j + ", eval=" + t.getEvaluationValue() + ", dist=" + d + ", " + t.toCompleteMethodString());
					}
				}
			}else{
				//pop-upに表示された最上位の候補と、解答との距離を比較する。
				//tsはソート済なので、ts[0]が最上位である。
				best_tree = ts[0];
				best_tree_order = 0;
				LevenshteinDistance ld = new LevenshteinDistance(1,1,1) ;
				min_d = ld.edit(ts[0].toCompleteMethodString(), ansTask(i));
			}

			if(best_tree == null){
				s += min_d + ", " + best_tree_order + ", " + ", 回答なし best_tree == null";
				System.out.println(i + ", " + count++ + ", 回答なし best_tree == null, "+ t_desired);
			}else{
				//System.out.println(i + ", " + min_d + ", " + best_tree.toCompleteMethodString());

				s += min_d + ", " + best_tree_order + ", " + best_tree.toCompleteMethodString() + ", " + best_tree.getEvaluationValue();
				System.out.println(s);
				best_tree.setSelectedFlg();	//選んだフラグ。

			}

		}
		if(pwL != null)
			pwL.println(s);
		return ts;
	}

	/*
	 * 重みを更新する簡数。
	 */
	public static void reEvalTask(ArrayList<FunctionTree> tl) {
		//再評価
		start_w = ExplanationVector.getWeights();			//特徴の重みベクトル
		step_w = ExplanationVector.getSteps();		//特徴の重みの更新幅
		best_w = Arrays.copyOf(start_w, start_w.length);	//選択した候補に最小の順位を与える重みの組
		double[] tmp_w = Arrays.copyOf(start_w, start_w.length);

		//最小順位
		int min_order = 99999;	//初期値は選択したときの順位

		reEvaluation(4, tl, min_order, tmp_w);

		//重みを更新
		ExplanationVector.setWeights(best_w);
	}

	public static void reEvaluation(int feature_num, ArrayList<FunctionTree> treeList, int min_order, double[] tmp_w) {

		//計算
		if(feature_num == 0){

			//重みを更新
			ExplanationVector.setWeights(tmp_w);
			//計算
			for(FunctionTree tree: treeList){
				tree.getEvaluationValue();
			}
			//ソート
			Collections.sort(treeList, new Comparator<FunctionTree>(){
				//rootsを評価値の降順にソートする。
				public int compare(FunctionTree r1, FunctionTree r2) {
					if(r1.getEvaluationValue() < r2.getEvaluationValue())
						return 1;
					else if(r1.getEvaluationValue() > r2.getEvaluationValue())
						return -1;
					return 0;
				}
			});

			int tmp_order = -1;

			//double tmp_eval = -99999;

			//selectedFlg==trueの木を探す
			for(int s = 0; s < treeList.size(); s++){
				if(treeList.get(s).getSelectedFlg() == true){
					tmp_order = s;//選択された木の順位を保持
					//tmp_eval = treeList.get(s).getEvaluationValue();
				}
			}

			/*
			 * 同じ順位でも、違う評価値の場合がある。(初めは計算が間違っているのかと思っていたが。)
			 * 同じ順位2位でも
			 * 評価値が、
			 *  2, -1.1800000000000002
				2, -0.9800000000000004
				のように異なる場合、どうすべきか。
				「分布」を用いた方が良いのか。
			 */

			//System.out.println(tmp_order + ", " +tmp_eval);

			/*
			 * 最小順位よりも順位が上（小さい）とき最小順位を更新し、
			 * そのときの重みを保持する。
			 */
			if(tmp_order < min_order){
				min_order = tmp_order;
				//配列のコピー
				best_w = Arrays.copyOf(tmp_w, tmp_w.length);
			}

			return;
		}

		//分岐
		feature_num--;

		//そのまま
		tmp_w[feature_num] = start_w[feature_num];
		reEvaluation(feature_num, treeList, min_order, tmp_w);

		//増やす
		tmp_w[feature_num] = start_w[feature_num] + step_w[feature_num];
		reEvaluation(feature_num, treeList, min_order, tmp_w);

		//減らす
		tmp_w[feature_num] = start_w[feature_num] - step_w[feature_num];
		reEvaluation(feature_num, treeList, min_order, tmp_w);

		return;
	}

	/*
	 * task 横断的に1回だけやる。
	 */

	public static void oudan_1(int output_size, boolean top_output){

		for(int i = 1; i <= 15; i++){
			readTask(i);
			testTask1_nearest_LD(i, output_size, top_output);
			//追加したtask独自情報のクリア
			KeywordProgramming.clearStaticFields(false);
		}
	}

	public static int numTypeTask(int i){
		int x = 0;
		switch(i){
		case 1:
			 x = 2; break;
		case 2:
			 x = 5; break;
		case 3:
			 x = 10; break;
		case 4:
			 x = 21; break;
		case 5:
			 x = 4; break;
		case 6:
			 x = 19; break;
		case 7:
			 x = 3; break;
		case 8:
			 x = 19; break;
		case 9:
			 x = 5; break;
		case 10:
			 x = 19; break;
		case 11:
			 x = 3; break;
		case 12:
			 x = 20; break;
		case 13:
			 x = 25; break;
		case 14:
			 x = 3; break;
		case 15:
			 x = 11; break;
		}

		return x;
	}

	public static int numFuncTask(int i){
		int x = 0;
		switch(i){
		case 1:
			 x = 5; break;
		case 2:
			 x = 5; break;
		case 3:
			 x = 29; break;
		case 4:
			 x = 108; break;
		case 5:
			 x = 4; break;
		case 6:
			 x = 76; break;
		case 7:
			 x = 3; break;
		case 8:
			 x = 85; break;
		case 9:
			 x = 6; break;
		case 10:
			 x = 87; break;
		case 11:
			 x = 6; break;
		case 12:
			 x = 117; break;
		case 13:
			 x = 67; break;
		case 14:
			 x = 3; break;
		case 15:
			 x = 34; break;
		}

		return x;
	}

	public static String desiredTypeTask(int i){
		String s = "";
		switch(i){
		case 1:
			 s = "java.lang.String"; break;
		case 2:
			 s = "java.lang.Integer"; break;
		case 3:
			 s = "java.lang.Object"; break;
		case 4:
			 s = "java.lang.Boolean"; break;
		case 5:
			 s = "java.lang.Integer"; break;	//返り値はBoolean じゃない。taskの書き方が特殊なせいで、プラグインがうまく機能していなかった。
		case 6:
			 s = "java.lang.Object"; break;
		case 7:
			 s = "java.lang.Integer"; break;
		case 8:
			 s = "java.lang.Object"; break;
		case 9:
			 s = "java.lang.Character"; break;
		case 10:
			 s = "java.lang.Object"; break;
		case 11:
			 s = "java.lang.Object"; break;
		case 12:
			 s = "java.lang.Object"; break;
		case 13:
			 s = "java.lang.Object"; break;
		case 14:
			 s = "java.lang.String"; break;
		case 15:
			 s = "java.io.BufferedReader"; break;
		}

		return s;
	}

	public static String ansTask(int i){
		String s = "";
		switch(i){
		case 1:
			 s = "message.replaceAll(space, comma)"; break;
		case 2:
			 s = "new Integer(input)"; break;
		case 3:

			 //s = "list.length()"; break;
			 s = "list.size()"; break;
		case 4:
			 s = "fruits.contains(food)";
			 break;
		case 5:
			 s = "vowels.indexOf(c)"; break;
		case 6:
			 s = "numberNames.put(key, value)"; break;
		case 7:
			 s = "Math.abs(x)"; break;
		case 8:
			 s = "tokens.add(st.nextToken())"; break;
		case 9:
			 s = "message.charAt(i)"; break;
		case 10:
			 s = "System.out.println(f.getName())"; break;
		case 11:
			 s = "buf.append(s)"; break;
		case 12:
			 s = "lines.add(in.readLine())"; break;
		case 13:
			 s = "log.println(message)"; break;
		case 14:
			 s = "input.toLowerCase()"; break;
		case 15:
			 s = "new BufferedReader(new FileReader(filename))"; break;
		}

		return s;
	}

	public static List<String> methodStringToList(String s){
		String[] ss = s.split("[\\.\\(\\),]");
		String k[] = new String[ss.length];
		for(int i = 0; i < ss.length; i++)
	    	if(!ss[i].equals(""))
	    		k[i] = ss[i];
		return (List<String>)Arrays.asList(k);
	}

	public static ArrayList<String> ansTaskLabel(int i){
		ArrayList<String> s = new ArrayList<String>();
		switch(i){
		case 1:
			 //"message.replaceAll(space, comma)";
			s.add("message");
			s.add("replaceAll");
			s.add("space");
			s.add("comma");
			 break;
		case 2:
			//"new Integer(input)";
			s.add("new Integer");
			s.add("input");
			 break;
		case 3:
			//"list.length()";
			s.add("list");
			s.add("length");
			break;
		case 4:
			//"fruits.contains(food)";
			s.add("fruits");
			s.add("contains");
			s.add("food");
			break;
		case 5:
			//s = "vowels.indexOf(c)"; break;
		case 6:
			//s = "numberNames.put(key, value)"; break;
		case 7:
			//s = "Math.abs(x)"; break;
		case 8:
			//s = "tokens.add(st.nextToken())"; break;
		case 9:
			//s = "message.charAt(i)"; break;
		case 10:
			// s = "System.out.println(f.getName())"; break;
		case 11:
			//s = "buf.append(s)"; break;
		case 12:
			//s = "lines.add(in.readLine())"; break;
		case 13:
			// s = "log.println(message)"; break;
		case 14:
			// s = "input.toLowerCase()"; break;
		case 15:
			// s = "new BufferedReader(new FileReader(filename))"; break;
		}

		return s;
	}

	public static void readTask(int i){
		switch(i){
		case 1:
			readTask01(); break;
		case 2:
			readTask02(); break;
		case 3:
			readTask03(); break;
		case 4:
			readTask04(); break;
		case 5:
			readTask05(); break;
		case 6:
			readTask06(); break;
		case 7:
			readTask07(); break;
		case 8:
			readTask08(); break;
		case 9:
			readTask09(); break;
		case 10:
			readTask10(); break;
		case 11:
			readTask11(); break;
		case 12:
			readTask12(); break;
		case 13:
			readTask13(); break;
		case 14:
			readTask14(); break;
		case 15:
			readTask15(); break;
		}
	}


	public static void readTask00(){
		KeywordProgramming.addFunction(new Function(""));

		KeywordProgramming.addType(new Type(""));

		KeywordProgramming.inputKeywords("");
	}

	public static void readTask01(){
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,comma,comma,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task01,new;task01,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,message,message,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,space,space,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.String,spacesToCommas,spaces;to;commas,this,java.lang.String"));

		KeywordProgramming.addType(new Type("this"));

		KeywordProgramming.inputKeywords("message replace all space comma");
	}

	public static void readTask02(){
		KeywordProgramming.addFunction(new Function("this,static,nonfinal,method,java.lang.Void,main,mainthis,,java.lang.String[]"));
		KeywordProgramming.addFunction(new Function("this,static,nonfinal,method,java.lang.Void,main,main,this,java.lang.String[]"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task02,new;task02,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,input,input,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String[],args,args,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,output,output,"));

		KeywordProgramming.addType(new Type("java.lang.String[]"));
		KeywordProgramming.addType(new Type("this"));

		KeywordProgramming.inputKeywords("new integer input");
	}

	public static void readTask03(){
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,trimSize,trim;size,this,java.util.List,int"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.List,list,list,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.List,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.List,subList,sub;list,java.util.List,int,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,int,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,noMoreThan,no;more;than,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task03,new;task03,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.List,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.List,java.lang.Object"));

		KeywordProgramming.addType(new Type("java.lang.Object[]"));
		KeywordProgramming.addType(new Type("java.util.Iterator"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.util.List"));
		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("java.util.Collection"));
		KeywordProgramming.addType(new Type("java.util.ListIterator"));

		//KeywordProgramming.inputKeywords("list length");
		KeywordProgramming.inputKeywords("list size");
	}

	public static void readTask04(){
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.HashSet"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.List"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.ArrayList"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.ArrayList,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.ArrayList"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Set,java.lang.Object[]"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.List,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.ArrayList,int,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.ArrayList,int,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Set,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.List,int,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.ArrayList,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.HashSet"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.ArrayList,java.lang.Object[]"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.HashSet"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.ArrayList,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Set"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Set"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.ArrayList,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,int,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,int,"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.HashSet,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.Set"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.ArrayList,int"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.Set,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.ArrayList,int"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,int,float,"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.Set,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.Set,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.List,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.ArrayList"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,constructor,java.util.ArrayList,ArrayList,new;array;list,java.util.Collection,"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.List"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.List,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,ensureCapacity,ensure;capacity,java.util.ArrayList,int"));
//		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task04,new;task04,"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Set"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,trimToSize,trim;to;size,java.util.ArrayList"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.List,int"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.List,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.ArrayList,int,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.ArrayList"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.List,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.ArrayList,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List,int"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.Set,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Set"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.List,int"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.List"));
//		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.util.List,getFruitList,get;fruit;list,this"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Set,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,constructor,java.util.ArrayList,ArrayList,new;array;list,"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Set"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.HashSet"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List"));
//		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,food,food,"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,constructor,java.util.ArrayList,ArrayList,new;array;list,int,"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.HashSet"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.List,subList,sub;list,java.util.List,int,int"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.HashSet,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.ArrayList"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List,java.lang.Object[]"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.List,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.ArrayList,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,java.util.Collection,"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.List,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.Set,fruits,fruits,"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.List"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.Set,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.List,int,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.List,java.util.Collection"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.HashSet,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.List,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,"));
//		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Boolean,isFruit,is;fruit,this,java.lang.String"));
//		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.Set,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.List"));
//
//		KeywordProgramming.addType(new Type("java.util.ArrayList"));
//		KeywordProgramming.addType(new Type("java.lang.Object[]"));
//		KeywordProgramming.addType(new Type("java.util.Iterator"));
//		KeywordProgramming.addType(new Type("java.lang.Object"));
//		KeywordProgramming.addType(new Type("int"));
//		KeywordProgramming.addType(new Type("java.util.List"));
//		KeywordProgramming.addType(new Type("java.util.HashSet"));
//		KeywordProgramming.addType(new Type("java.util.Set"));
//		KeywordProgramming.addType(new Type("this"));
//		KeywordProgramming.addType(new Type("float"));
//		KeywordProgramming.addType(new Type("java.util.Collection"));
//		KeywordProgramming.addType(new Type("java.util.ListIterator"));

		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.HashSet"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.ArrayList"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.ArrayList,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.ArrayList"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,T[],toArray,to;array,java.util.Set<java.lang.String>,T[]"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Set,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.Set<java.lang.String>,java.util.Collection<?>"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.ArrayList,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.ArrayList,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.util.Iterator<java.lang.String>,iterator,iterator,java.util.Set<java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Set<java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Set,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.List,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.ArrayList,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.HashSet"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.ArrayList,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.HashSet"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Set<java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.Set<java.lang.String>,java.util.Collection<?>"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.Set<java.lang.String>,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.util.List<java.lang.String>,getFruitList,get;fruit;list,this"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.ArrayList,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Set"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Set"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.ArrayList,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,int,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,int,"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.HashSet,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.Set"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.ArrayList,int"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Set<java.lang.String>,java.util.Collection<? extends java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.Set,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.ArrayList,int"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,int,float,"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.Set,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.Set,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.ArrayList"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,constructor,java.util.ArrayList,ArrayList,new;array;list,java.util.Collection,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,ensureCapacity,ensure;capacity,java.util.ArrayList,int"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task04,new;task04,"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Set"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Set<java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,trimToSize,trim;to;size,java.util.ArrayList"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.ArrayList,int,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.ArrayList"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.Set<java.lang.String>,java.util.Collection<?>"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.ArrayList,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.Set<java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.Set,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Set"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Set,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,constructor,java.util.ArrayList,ArrayList,new;array;list,"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Set"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.HashSet"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,food,food,"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.Set<java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,constructor,java.util.ArrayList,ArrayList,new;array;list,int,"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.HashSet"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.List,subList,sub;list,java.util.List,int,int"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.HashSet,java.lang.Object"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.Set<java.lang.String>,fruits,fruits,"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.ArrayList"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.ArrayList,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.ArrayList,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Set<java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,java.util.Collection,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Set<java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.Set,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.List,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Set<java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Set<java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.HashSet,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashSet,nonstatic,nonfinal,constructor,java.util.HashSet,HashSet,new;hash;set,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Boolean,isFruit,is;fruit,this,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.util.Set,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.Set,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.List"));

		KeywordProgramming.addType(new Type("java.util.ArrayList"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("T[]"));
		KeywordProgramming.addType(new Type("java.util.List"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.util.Set<java.lang.String>"));
		KeywordProgramming.addType(new Type("java.util.Iterator<java.lang.String>"));
		KeywordProgramming.addType(new Type("float"));
		KeywordProgramming.addType(new Type("java.util.ListIterator"));
		KeywordProgramming.addType(new Type("java.util.Collection<? extends java.lang.String>"));
		KeywordProgramming.addType(new Type("java.util.Collection<?>"));
		KeywordProgramming.addType(new Type("java.lang.Object[]"));
		KeywordProgramming.addType(new Type("java.util.Iterator"));
		KeywordProgramming.addType(new Type("java.util.HashSet"));
		KeywordProgramming.addType(new Type("java.util.List<java.lang.String>"));
		KeywordProgramming.addType(new Type("java.util.Set"));
		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("java.util.Collection"));

		KeywordProgramming.inputKeywords("fruits contains food");
	}

	public static void readTask05(){
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task05,new;task05,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Boolean,isVowel,is;vowel,this,char"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Character,c,c,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,vowels,vowels,"));
		KeywordProgramming.addType(new Type("char"));
		KeywordProgramming.addType(new Type("this"));
		/*
		char c = 'c';
		String s = "abc";
		s.indexOf(c);

		//char は int としても認識されなくちゃいけないらしい。
		*/
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,c,c,"));

		KeywordProgramming.inputKeywords("vowels index of c");
	}

	public static void readTask06(){
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.util.Set,entrySet,entry;set,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,containsKey,contains;key,java.util.Map,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.HashMap,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Void,putAll,put;all,java.util.HashMap,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.util.Collection,values,values,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Boolean,containsValue,contains;value,java.util.HashMap,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,put,put,java.util.HashMap,java.lang.Object,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,int,"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,int,float,"));
//		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,value,value,"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.util.Set,keySet,key;set,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.util.Set,keySet,key;set,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,containsValue,contains;value,java.util.Map,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Boolean,containsKey,contains;key,java.util.HashMap,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.Map,numberNames,number;names,"));
//		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task06,new;task06,"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,java.util.Map,"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.Map,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.Map,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.util.Set,entrySet,entry;set,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Map,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Object,put,put,java.util.Map,java.lang.Object,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Void,putAll,put;all,java.util.Map,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.HashMap,java.lang.Object"));
//		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,key,key,"));
//		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.HashMap"));
//		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.util.Collection,values,values,java.util.Map"));
//		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,test,test,this"));
//
//		KeywordProgramming.addType(new Type("java.lang.Object"));
//		KeywordProgramming.addType(new Type("java.util.Map"));
//		KeywordProgramming.addType(new Type("int"));
//		KeywordProgramming.addType(new Type("java.util.Set"));
//		KeywordProgramming.addType(new Type("this"));
//		KeywordProgramming.addType(new Type("float"));
//		KeywordProgramming.addType(new Type("java.util.Collection"));
//		KeywordProgramming.addType(new Type("java.util.HashMap"));

		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.util.Set,entrySet,entry;set,java.util.Map"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.Map<java.lang.Integer|java.lang.String>,numberNames,number;names,"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,containsKey,contains;key,java.util.Map,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.HashMap,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.util.Collection,values,values,java.util.HashMap"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Boolean,containsValue,contains;value,java.util.HashMap,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,put,put,java.util.HashMap,java.lang.Object,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Map"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.util.Set,keySet,key;set,java.util.HashMap"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,value,value,"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Boolean,containsKey,contains;key,java.util.HashMap,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,containsKey,contains;key,java.util.Map<java.lang.Integer|java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.String,remove,remove,java.util.Map<java.lang.Integer|java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.util.Set<java.util.Map.Entry<java.lang.Integer|java.lang.String>>,entrySet,entry;set,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task06,new;task06,"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,java.util.Map,"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Map"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.Map,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.String,get,get,java.util.Map<java.lang.Integer|java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.util.Set,entrySet,entry;set,java.util.HashMap"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.HashMap"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Map,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Map"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.util.Set<java.lang.Integer>,keySet,key;set,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.HashMap"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Void,putAll,put;all,java.util.Map,java.util.Map"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,key,key,"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.util.Collection,values,values,java.util.Map"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,test,test,this"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Void,putAll,put;all,java.util.HashMap,java.util.Map"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.HashMap"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Map"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,int,"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,constructor,java.util.HashMap,HashMap,new;hash;map,int,float,"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Void,putAll,put;all,java.util.Map<java.lang.Integer|java.lang.String>,java.util.Map<? extends java.lang.Integer|? extends java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.util.Collection<java.lang.String>,values,values,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Boolean,containsValue,contains;value,java.util.Map,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.util.Set,keySet,key;set,java.util.Map"));
//		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,number,names,names,"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.Map,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map,nonstatic,nonfinal,method,java.lang.Object,put,put,java.util.Map,java.lang.Object,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,containsValue,contains;value,java.util.Map<java.lang.Integer|java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.String,put,put,java.util.Map<java.lang.Integer|java.lang.String>,java.lang.Integer,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.HashMap,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Map<java.lang.Integer|java.lang.String>,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Map<java.lang.Integer|java.lang.String>,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.HashMap,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.HashMap"));

		KeywordProgramming.addType(new Type("java.util.Map"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.util.Map<java.lang.Integer|java.lang.String>"));
		KeywordProgramming.addType(new Type("number"));
		KeywordProgramming.addType(new Type("java.util.Map<? extends java.lang.Integer|? extends java.lang.String>"));
		KeywordProgramming.addType(new Type("float"));
		KeywordProgramming.addType(new Type("java.util.HashMap"));
		KeywordProgramming.addType(new Type("java.util.Collection<java.lang.String>"));
		KeywordProgramming.addType(new Type("java.util.Set<java.util.Map.Entry<java.lang.Integer|java.lang.String>>"));
		KeywordProgramming.addType(new Type("java.util.Set"));
		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("java.util.Collection"));
		KeywordProgramming.addType(new Type("java.util.Set<java.lang.Integer>"));

		KeywordProgramming.inputKeywords("number Names put key value");
	}

	public static void readTask07(){
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,Task07,new Task07,task07,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Integer,absoluteValue,absolute;value,this,int"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,x,x,"));

		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("int"));

		KeywordProgramming.inputKeywords("Math abs x");
	}

	public static void readTask08(){
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,removeAllElements,remove;all;elements,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,removeElementAt,remove;element;at,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,firstElement,first;element,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.util.Enumeration,elements,elements,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,method,java.lang.Object,nextElement,next;element,java.util.StringTokenizer"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,copyInto,copy;into,java.util.Vector,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,method,java.lang.String,nextToken,next;token,java.util.StringTokenizer,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,insertElementAt,insert;element;at,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.Vector,tokens,tokens,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task08,new;task08,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.String,toString,to;string,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,method,java.lang.Integer,countTokens,count;tokens,java.util.StringTokenizer"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,constructor,java.util.StringTokenizer,StringTokenizer,new;string;tokenizer,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,constructor,java.util.StringTokenizer,StringTokenizer,new;string;tokenizer,java.lang.String,java.lang.String,boolean,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,setElementAt,set;element;at,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,elementAt,element;at,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Vector,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.Vector,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,java.util.Collection,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,capacity,capacity,java.util.Vector"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.StringTokenizer,st,st,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.util.Vector,getTokens,get;tokens,this,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.Vector,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,method,java.lang.Boolean,hasMoreElements,has;more;elements,java.util.StringTokenizer"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,lastElement,last;element,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,int,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.util.List,subList,sub;list,java.util.Vector,int,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,constructor,java.util.StringTokenizer,StringTokenizer,new;string;tokenizer,java.lang.String,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,addElement,add;element,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,ensureCapacity,ensure;capacity,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,removeElement,remove;element,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Vector,int,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,method,java.lang.String,nextToken,next;token,java.util.StringTokenizer"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,trimToSize,trim;to;size,java.util.Vector"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,message,message,"));
		KeywordProgramming.addFunction(new Function("java.util.StringTokenizer,nonstatic,nonfinal,method,java.lang.Boolean,hasMoreTokens,has;more;tokens,java.util.StringTokenizer"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,setSize,set;size,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,int,int,"));

		KeywordProgramming.addType(new Type("java.util.Enumeration"));
		KeywordProgramming.addType(new Type("java.lang.Object[]"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("java.util.Vector"));
		KeywordProgramming.addType(new Type("java.util.List"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.util.StringTokenizer"));
		KeywordProgramming.addType(new Type("boolean"));
		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("java.util.Collection"));

        //<<<tokens.add(st.nextToken())>>>;
		KeywordProgramming.inputKeywords("tokens add st next Token");
	}

	public static void readTask09(){
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task09,new;task09,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,message,message,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,count,count,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,test,test,this"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,i,i,"));

		KeywordProgramming.addType(new Type("this"));
	    //char c = <<<message.charAt(i)>>>;

		KeywordProgramming.inputKeywords("message char At i");
	}


	public static void readTask10(){
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.String,separator,separator,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,canExecute,can;execute,java.io.File"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.io.File,dir,dir,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,isFile,is;file,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setExecutable,set;executable,java.io.File,boolean,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.io.File,getParentFile,get;parent;file,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,nonfinal,method,java.io.File,createTempFile,create;temp;file,java.io.File,java.lang.String,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.io.File[],listFiles,list;files,java.io.File,java.io.FileFilter"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.Character,separatorChar,separator;char,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setWritable,set;writable,java.io.File,boolean,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setWritable,set;writable,java.io.File,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Void,deleteOnExit,delete;on;exit,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String,getPath,get;path,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String[],list,list,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,canRead,can;read,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setLastModified,set;last;modified,java.io.File,long"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.String,pathSeparator,file;path;separator,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setExecutable,set;executable,java.io.File,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String[],list,list,java.io.File,java.io.FilenameFilter"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.Character,separatorChar,file;separator;char,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setReadable,set;readable,java.io.File,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,mkdirs,mkdirs,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,isDirectory,is;directory,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.io.File[],listFiles,list;files,java.io.File,java.io.FilenameFilter"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,renameTo,rename;to,java.io.File,java.io.File"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.io.File,f,f,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,constructor,java.io.File,File,new;file,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.net.URL,toURL,to;u;r;l,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,canWrite,can;write,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String,toString,to;string,java.io.File"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,out,println,println,"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.Character,pathSeparatorChar,file;path;separator;char,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Long,getTotalSpace,get;total;space,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setReadOnly,set;read;only,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,nonfinal,method,java.io.File,createTempFile,create;temp;file,java.io.File,java.lang.String,java.lang.String,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Integer,compareTo,compare;to,java.io.File,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.String,separator,file;separator,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,mkdir,mkdir,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String,getCanonicalPath,get;canonical;path,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,nonfinal,method,java.io.File,createTempFile,create;temp;file;file,,java.lang.String,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.net.URI,toURI,to;u;r;i,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.String,pathSeparator,path;separator,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.io.File,getCanonicalFile,get;canonical;file,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String,getName,get;name,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Long,getUsableSpace,get;usable;space,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,isHidden,is;hidden,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,constructor,java.io.File,File,new;file,java.lang.String,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,createNewFile,create;new;file,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,nonfinal,method,java.io.File[],listRoots,list;roots;file,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,exists,exists,java.io.File"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task10,new;task10,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String,getAbsolutePath,get;absolute;path,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,isAbsolute,is;absolute,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,nonfinal,method,java.io.File,createTempFile,create;temp;file;file,,java.lang.String,java.lang.String,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Long,length,length,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.io.File[],listFiles,list;files,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.io.File,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,final,field,java.lang.Character,pathSeparatorChar,path;separator;char,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,setReadable,set;readable,java.io.File,boolean,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Boolean,delete,delete,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,static,nonfinal,method,java.io.File[],listRoots,list;roots,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.io.File,getAbsoluteFile,get;absolute;file,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.String,getParent,get;parent,java.io.File"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,ls,ls,this,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,constructor,java.io.File,File,new;file,java.net.URI,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,constructor,java.io.File,File,new;file,java.io.File,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Long,lastModified,last;modified,java.io.File"));
		KeywordProgramming.addFunction(new Function("java.io.File,nonstatic,nonfinal,method,java.lang.Long,getFreeSpace,get;free;space,java.io.File"));


		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,append,append,java.io.PrintStream,java.lang.CharSequence"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.io.File,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.io.OutputStream,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,char"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,format,format,java.io.PrintStream,java.util.Locale,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.io.File,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,char"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,double"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintStream,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,format,format,java.io.PrintStream,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.lang.String,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,float"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,double"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,long"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,append,append,java.io.PrintStream,char"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.io.OutputStream,boolean,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,printf,printf,java.io.PrintStream,java.util.Locale,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,flush,flush,java.io.PrintStream"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,close,close,java.io.PrintStream"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,append,append,java.io.PrintStream,java.lang.CharSequence,int,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintStream,byte[],int,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Boolean,checkError,check;error,java.io.PrintStream"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintStream,char[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.io.PrintStream,printf,printf,java.io.PrintStream,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,float"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,constructor,java.io.PrintStream,PrintStream,new;print;stream,java.io.OutputStream,boolean,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,char[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintStream,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintStream,long"));


		KeywordProgramming.addType(new Type("java.io.FileFilter"));
		KeywordProgramming.addType(new Type("java.io.FilenameFilter"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("java.net.URL"));
		KeywordProgramming.addType(new Type("java.io.File[]"));
		KeywordProgramming.addType(new Type("java.lang.String[]"));
		KeywordProgramming.addType(new Type("java.net.URI"));
		KeywordProgramming.addType(new Type("java.io.File"));
		KeywordProgramming.addType(new Type("boolean"));
		KeywordProgramming.addType(new Type("long"));
		KeywordProgramming.addType(new Type("this"));

		KeywordProgramming.addType(new Type("char[]"));
		KeywordProgramming.addType(new Type("char"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.io.PrintStream"));
		KeywordProgramming.addType(new Type("PrintWriter"));
		KeywordProgramming.addType(new Type("double"));
		KeywordProgramming.addType(new Type("float"));
		KeywordProgramming.addType(new Type("java.lang.CharSequence"));
		KeywordProgramming.addType(new Type("java.lang.Object[]"));
		KeywordProgramming.addType(new Type("java.util.Locale"));
		KeywordProgramming.addType(new Type("java.io.OutputStream"));
		KeywordProgramming.addType(new Type("byte[]"));

        //<<<System.out.println(f.getName())>>>;
//		KeywordProgramming.addFunction(new Function("java.io.System.out,static,nonfinal,method,java.lang.Void,println,system;out;println,java.lang.String"));


		KeywordProgramming.inputKeywords("system out println f get name");
	}


	public static void readTask11(){
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.String,repeatString,repeat;string,this,java.lang.String,int"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task11,new;task11,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.StringBuffer,buf,buf,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.Integer,thisManyTimes,this;many;times,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,s,s,"));

		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("this"));
        //<<<buf.append(s)>>>;

		KeywordProgramming.inputKeywords("buf append s");
	}


	public static void readTask12(){
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,removeAllElements,remove;all;elements,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,removeElementAt,remove;element;at,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,firstElement,first;element,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.util.Enumeration,elements,elements,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,size,size,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,copyInto,copy;into,java.util.Vector,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,insertElementAt,insert;element;at,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.String,toString,to;string,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.List,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,constructor,java.io.BufferedReader,BufferedReader,new;buffered;reader,java.io.Reader,"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.String,readLine,read;line,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.List,int,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,setElementAt,set;element;at,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,elementAt,element;at,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.Vector,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,constructor,java.io.BufferedReader,BufferedReader,new;buffered;reader,java.io.Reader,int,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,add,add,java.util.Vector,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,java.util.Collection,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.util.List,getLines,get;lines,this,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,capacity,capacity,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Boolean,ready,ready,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.Vector,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,lastIndexOf,last;index;of,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,get,get,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Integer,read,read,java.io.BufferedReader,char[],int,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,remove,remove,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,lastElement,last;element,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.Vector,java.lang.Object,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,containsAll,contains;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Integer,read,read,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,int,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task12,new;task12,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.util.List,subList,sub;list,java.util.Vector,int,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,removeAll,remove;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,remove,remove,java.util.List,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.io.BufferedReader,in,in,"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Void,reset,reset,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Vector,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Object,clone,clone,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.ListIterator,listIterator,list;iterator,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,addElement,add;element,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Void,close,close,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,ensureCapacity,ensure;capacity,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,removeElement,remove;element,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Long,skip,skip,java.io.BufferedReader,long"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.List,subList,sub;list,java.util.List,int,int"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Void,mark,mark,java.io.BufferedReader,int"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,addAll,add;all,java.util.Vector,int,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,contains,contains,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object[],toArray,to;array,java.util.List,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,isEmpty,is;empty,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Integer,hashCode,hash;code,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,trimToSize,trim;to;size,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Integer,indexOf,index;of,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.util.List,lines,lines,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.List"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Object,set,set,java.util.List,int,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,retainAll,retain;all,java.util.List,java.util.Collection"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,clear,clear,java.util.Vector"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Void,setSize,set;size,java.util.Vector,int"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.lang.Boolean,equals,equals,java.util.List,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Boolean,markSupported,mark;supported,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,method,java.lang.Boolean,add,add,java.util.Vector,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.util.Vector,nonstatic,nonfinal,constructor,java.util.Vector,Vector,new;vector,int,int,"));
		KeywordProgramming.addFunction(new Function("java.util.List,nonstatic,nonfinal,method,java.util.Iterator,iterator,iterator,java.util.List"));

		KeywordProgramming.addType(new Type("char[]"));
		KeywordProgramming.addType(new Type("java.util.Enumeration"));
		KeywordProgramming.addType(new Type("java.io.BufferedReader"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("java.util.List"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("long"));
		KeywordProgramming.addType(new Type("java.util.ListIterator"));
		KeywordProgramming.addType(new Type("java.lang.Object[]"));
		KeywordProgramming.addType(new Type("java.util.Iterator"));
		KeywordProgramming.addType(new Type("java.util.Vector"));
		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("java.io.Reader"));
		KeywordProgramming.addType(new Type("java.util.Collection"));
		//	        //<<<lines.add(in.readLine())>>>;


		KeywordProgramming.inputKeywords("lines add in read Line");
	}


	public static void readTask13(){
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Boolean,checkError,check;error,java.io.PrintWriter"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.io.Writer,"));
		KeywordProgramming.addFunction(new Function("java.io.FileWriter,nonstatic,nonfinal,constructor,java.io.FileWriter,FileWriter,new;file;writer,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task13,new;task13,"));
		KeywordProgramming.addFunction(new Function("java.io.IOException,nonstatic,nonfinal,constructor,java.io.IOException,IOException,new;i;o;exception,java.lang.Throwable,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,long"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintWriter,char[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.lang.String,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintWriter,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,double"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,float"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,close,close,java.io.PrintWriter"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.io.File,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.io.OutputStream,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,long"));
		KeywordProgramming.addFunction(new Function("java.io.FileWriter,nonstatic,nonfinal,constructor,java.io.FileWriter,FileWriter,new;file;writer,java.lang.String,boolean,"));
		KeywordProgramming.addFunction(new Function("java.io.IOException,nonstatic,nonfinal,constructor,java.io.IOException,IOException,new;i;o;exception,java.lang.String,java.lang.Throwable,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,printf,printf,java.io.PrintWriter,java.util.Locale,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,printf,printf,java.io.PrintWriter,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,char[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,format,format,java.io.PrintWriter,java.lang.String,java.lang.Object[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,char[]"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintWriter,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,flush,flush,java.io.PrintWriter"));
		KeywordProgramming.addFunction(new Function("java.io.FileWriter,nonstatic,nonfinal,constructor,java.io.FileWriter,FileWriter,new;file;writer,java.io.FileDescriptor,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,java.lang.Object"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,char"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,append,append,java.io.PrintWriter,char"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.IOException,nonstatic,nonfinal,constructor,java.io.IOException,IOException,new;i;o;exception,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,char"));
		KeywordProgramming.addFunction(new Function("java.io.IOException,nonstatic,nonfinal,constructor,java.io.IOException,IOException,new;i;o;exception,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.io.File,"));
		KeywordProgramming.addFunction(new Function("java.io.FileWriter,nonstatic,nonfinal,constructor,java.io.FileWriter,FileWriter,new;file;writer,java.io.File,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintWriter,char[],int,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,println,println,java.io.PrintWriter,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,float"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.io.Writer,boolean,"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.io.PrintWriter,log,log,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,append,append,java.io.PrintWriter,java.lang.CharSequence,int,int"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,message,message,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,constructor,java.io.PrintWriter,PrintWriter,new;print;writer,java.io.OutputStream,boolean,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,write,write,java.io.PrintWriter,java.lang.String,int,int"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,append,append,java.io.PrintWriter,java.lang.CharSequence"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,logMessage,log;message,this,java.lang.String"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,boolean"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.lang.Void,print,print,java.io.PrintWriter,double"));
		KeywordProgramming.addFunction(new Function("java.io.FileWriter,nonstatic,nonfinal,constructor,java.io.FileWriter,FileWriter,new;file;writer,java.io.File,boolean,"));
		KeywordProgramming.addFunction(new Function("java.io.PrintWriter,nonstatic,nonfinal,method,java.io.PrintWriter,format,format,java.io.PrintWriter,java.util.Locale,java.lang.String,java.lang.Object[]"));

		KeywordProgramming.addType(new Type("char[]"));
		KeywordProgramming.addType(new Type("java.lang.Object"));
		KeywordProgramming.addType(new Type("char"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.io.Writer"));
		KeywordProgramming.addType(new Type("double"));
		KeywordProgramming.addType(new Type("long"));
		KeywordProgramming.addType(new Type("float"));
		KeywordProgramming.addType(new Type("java.io.IOException"));
		KeywordProgramming.addType(new Type("java.lang.CharSequence"));
		KeywordProgramming.addType(new Type("java.lang.Object[]"));
		KeywordProgramming.addType(new Type("java.io.PrintWriter"));
		KeywordProgramming.addType(new Type("java.util.Locale"));
		KeywordProgramming.addType(new Type("java.lang.Throwable"));
		KeywordProgramming.addType(new Type("java.io.File"));
		KeywordProgramming.addType(new Type("java.io.FileDescriptor"));
		KeywordProgramming.addType(new Type("boolean"));
		KeywordProgramming.addType(new Type("java.io.OutputStream"));
		KeywordProgramming.addType(new Type("java.io.FileWriter"));
		KeywordProgramming.addType(new Type("this"));
	    //<<<log.println(message)>>>;

		KeywordProgramming.inputKeywords("log println message ");
	}


	public static void readTask14(){
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,Task14,new Task14,task14,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,void,test,test,this"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,input,input,"));

		KeywordProgramming.addType(new Type("Task14"));
		KeywordProgramming.addType(new Type("void"));
		KeywordProgramming.addType(new Type("this"));
		//String output = <<<input.toLowerCase()>>>;

		KeywordProgramming.inputKeywords("input to Lower Case");
	}


	public static void readTask15(){
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,constructor,java.io.BufferedReader,BufferedReader,new;buffered;reader,java.io.Reader,"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,constructor,java.io.BufferedReader,BufferedReader,new;buffered;reader,java.io.Reader,int,"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Void,close,close,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Void,mark,mark,java.io.BufferedReader,int"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Boolean,markSupported,mark;supported,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Integer,read,read,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Integer,read,read,java.io.BufferedReader,char[],int,int"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.String,readLine,read;line,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Boolean,ready,ready,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Void,reset,reset,java.io.BufferedReader"));
		KeywordProgramming.addFunction(new Function("java.io.BufferedReader,nonstatic,nonfinal,method,java.lang.Long,skip,skip,java.io.BufferedReader,long"));
		KeywordProgramming.addFunction(new Function("java.io.FileReader,nonstatic,nonfinal,constructor,java.io.FileReader,FileReader,new;file;reader,java.lang.String,"));
		KeywordProgramming.addFunction(new Function("java.io.FileReader,nonstatic,nonfinal,constructor,java.io.FileReader,FileReader,new;file;reader,java.io.File,"));
		KeywordProgramming.addFunction(new Function("java.io.FileReader,nonstatic,nonfinal,constructor,java.io.FileReader,FileReader,new;file;reader,java.io.FileDescriptor,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,constructor,this,Task15,new;task15,"));
		KeywordProgramming.addFunction(new Function("this,nonstatic,nonfinal,method,java.lang.Void,test,test,this"));
		KeywordProgramming.addFunction(new Function(",nonstatic,nonfinal,localvariable,java.lang.String,filename,filename,"));

		KeywordProgramming.addType(new Type("char[]"));
		KeywordProgramming.addType(new Type("java.io.BufferedReader"));
		KeywordProgramming.addType(new Type("java.io.FileReader"));
		KeywordProgramming.addType(new Type("int"));
		KeywordProgramming.addType(new Type("java.io.File"));
		KeywordProgramming.addType(new Type("java.io.FileDescriptor"));
		KeywordProgramming.addType(new Type("long"));
		KeywordProgramming.addType(new Type("this"));
		KeywordProgramming.addType(new Type("java.io.Reader,java.io.BufferedReader,java.io.FileReader"));
		//BufferedReader in = <<<new BufferedReader(new FileReader(filename))>>>;

		KeywordProgramming.inputKeywords("new Buffered Reader new File Reader filename");
	}

}
