package experiment3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import state.KpRunningState;

import keywordProgramming.ExplanationVector;
import keywordProgramming.FunctionTree;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;
import logging.LogControl;

/*
 * 複数タスク選択であれば、
 * そのうちの、
 * より多くのタスクについて正解が出るような重みを選ぶ.
 * FullRun()の場合。
 * 
 * 一回だけローカルサーチを行うのはどうしようか
 * 使うか使わないか。
 * 学習率がどうのこうのと言ってたな。
 * 
 * 一度KPをして、
 * そこから得られた結果だけを扱って調整していたけど、
 * そうではなくて、何回もKPするほうが良いのだろうか？
 * GridSearchではそうしたのだが。
 * 
 * 順位に着目するのか、
 * 正解数に着目するのか、
 * 同じ正解数なら、順位が上にきていたほうが良いとか。
 */

public class Experiment3{

	//評価値の順に並べた関数木の順位
	private int selected_tree_num;

	//重みの更新幅
	private double [] step_w;

	//選択した候補が最上位に来る重みの組み合わせ
	private double [] best_w;

	//計算した結果出た候補の最小順位
	private int min_order;

	private TestSite[] testSites;
	
	//正解が出現したタスク数の最大合計値
	//private int max_sum_ans_tasks;
	
	//スコアの最大値
	private double max_score;
	
	//各タスクの出力候補群のList
	//private List<FunctionTree[]> functionTreeLists = new ArrayList<FunctionTree[]>();
	
	//各タスクの出力候補群の中で、正解が出現した順位
	private List<Integer> answerOrders = new ArrayList<Integer>();
	
	private boolean isOnline;	//オンライン学習か、否か
	
	private boolean flg_log_step_by_step;	//1ステップごとにログを出すか否かのフラグ
	private boolean flg_log_neighbours;		//1ステップごとに各近傍のログを出すか否かのフラグ
		
	private int times_of_steps;	//指定ステップ回数
	
	private int counter_step;	//ステップ数カウンタ
	private int counter_neighbour;	//近傍カウンタ
	
	LogControl logControl;	//ログ出力管理オブジェクト
	
	//単語の列（頻度順）
	private List<String> term_list = new ArrayList<String>();
	public static final String TERM_FREQUENT_FILE_NAME = "term_total.txt";

	//入力に使う単語数、(残す単語数)
	int numOfKeywords;
	
	//出力に含まれる単語数
	int numOfKeywordsInLabel;
	
	//単語の切り捨て順　頻度順か、逆順か。
	boolean flg_desc;
	
	ArrayList<ArrayList<Integer>> allCombinations;
	
	ArrayList<ResultOrder> listOfResultOrder;
	
	
	/*
	 * コンストラクタ
	 */
	public Experiment3(TestSite[] sites, int numOfKeywordsInQuery, int numOfKeywordsInLabel, boolean isOnline, boolean flg_desc){
		testSites = sites;
		this.isOnline = isOnline;
		this.numOfKeywords = numOfKeywordsInQuery;
		this.numOfKeywordsInLabel = numOfKeywordsInLabel;
		this.flg_desc = flg_desc;
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		flg_log_step_by_step = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH_STEP_BY_STEP);
		flg_log_neighbours = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH_NEIGHBOURS);
		KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);

		if(isOnline){
			times_of_steps = store.getInt(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_STEPS);
		}else{
			times_of_steps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
		}
		
		//単語頻度の読み込み
		//ReadTermFrequency();
		
		//組み合わせのリストを作成
//		CombinationMaker sp = new CombinationMaker();
//		this.allCombinations = sp.getAllCombinations(numOfKeywords);
		
		Combination  cb = new Combination(this.numOfKeywordsInLabel, this.numOfKeywords);
        cb.combine(1);
        allCombinations = cb.all_combinations;
        
		listOfResultOrder = new ArrayList();
	}

	/*
	 * 単語頻度の読み込み
	 */
	public void ReadTermFrequency(){
		//プラグインの中にあるファイルの読み込み
		
		Path f_path = new Path(TERM_FREQUENT_FILE_NAME);
		URL f_fileURL = FileLocator.find(Activator.bundle, f_path, null);
		InputStream f_in;
		
		try {
			f_in = f_fileURL.openStream();
			BufferedReader f_reader = new BufferedReader(new InputStreamReader(f_in));
			String s;
			while ((s = f_reader.readLine()) != null) {
				term_list.add(s.split(":")[0]);
			}
			f_reader.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public void run(int numOfSteps, String state, IProgressMonitor monitor){
		
		long start = System.currentTimeMillis();

		//開始時の特徴の重みベクトル
		double []start_w = Arrays.copyOf(ExplanationVector.getWeights(), ExplanationVector.getWeights().length);
		this.step_w = ExplanationVector.getSteps();		//特徴の重みの更新幅
		this.best_w = Arrays.copyOf(start_w, start_w.length);	//選択した候補に最小の順位を与える重みの組

		logControl = new LogControl(LogControl.LOCAL_SEARCH);
		/*
		 * 各タスクについて、現パラメータにおける出力候補を取得しておく。
		 */
		//runKeywordProgrammingForAllTasksWithSelectedKeywords(monitor, testSites, answerOrders, state);
		runKeywordProgrammingForAllTasksForAllKeywordCombinations(monitor, testSites, state);
		
		if(monitor != null)
			monitor.worked(4);
//		
//		//スコアの計算
//		max_score = getScoreOfAnswerAppearancedOrder(answerOrders);
//	
//		List<Result> result_list = new ArrayList<Result>(); 
//		
////		logControl.println("  > 正解タスクの順位 > 出力形式： [ID, 欲しい出力, 順位], (順位は0番から数える. -1は候補群中に出現しなかったことを表す.)");
//		for(int i = 0; i < testSites.length; i++){
////			logControl.println("   " + testSites[i].getId()+ ", "+ testSites[i].getSelectedString() + ", " + answerOrders.get(i));
//			String str = testSites[i].getSelectedString();
//			int odr = answerOrders.get(i);
////			logControl.println(str + "\t" + odr);//id削除。タブ区切りに変更。
//			int numKey = testSites[i].getNumOfKeywords();
//			int numLT = testSites[i].getNumOfLocalTypes();
//			int numLF = testSites[i].getNumOfLocalFunctions();
//			//generics を排除
//			if(!str.contains("<"))
//				result_list.add(new Result(testSites[i].getId(), str, odr, numKey, numLT, numLF));
//		}
////		logControl.println("  < 正解タスクの順位 < 出力形式： [ID, 欲しい出力, 順位], (順位は0番から数える. -1は候補群中に出現しなかったことを表す.)");
//		
//		int sum_zero = 0;
//		int sum_m_one = 0;
//		int sum_within_tree = 0; //上位3件
//		int sum_within_five = 0; //上位5件
//		int sum_within_ten = 0; //上位10件
//		int sum_others = 0;
//		int sumKey = 0;
//		int sumLT = 0;
//		int sumLF = 0;
//		
//		List<Result> result_list_others = new ArrayList<Result>(); 
//		
//		List<Result> result_list_incorrect = new ArrayList<Result>();//間違いの結果を保持する。
//		
//        for(Result result: result_list) {
//			//logControl.println(result.fSelectedString + "\t" + result.fAnswerOrder);
//			sumKey += result.fNumOfKeywords; 
//			sumLT += result.fNumOfLocalTypes;
//			sumLF += result.fNumOfLocalFunctions;
//			
//			//上位3件以内に正解が出現した
//			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 3)
//				sum_within_tree++;//順位が0,1,2のとき。
//			//上位5件以内に正解が出現した
//			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 5)
//				sum_within_five++;
//			//上位10件以内に正解が出現した
//			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 10)
//				sum_within_ten++;
//			
//			if(result.fAnswerOrder == 0)
//				sum_zero++;
//			else if(result.fAnswerOrder == -1){
//				sum_m_one++;
//				result_list_incorrect.add(result);
//			}else{
//				sum_others++;
//				result_list_others.add(result);
//			}
//					
//        }
//		logControl.println("  > ソートして重複文字列を削除した。 >");

//		logControl.println("BEST_R = " + KeywordProgramming.BEST_R);
//		logControl.print("現在の特徴の重み = ");		
//		for(int i = 0; i < 4; i++){
//			logControl.print(String.valueOf(best_w[i]) + ", ");
//		}
		
		//全組み合わせの平均値を出す。
		int sum_sum_ans_all = 0;	//全範囲での答えの数。
		int sum_sum_within_one = 0;	//上位1件
		int sum_sum_within_three = 0; //上位3件
		int sum_sum_within_five = 0; //上位5件
		int sum_sum_within_ten = 0; //上位10件
		
		int all_tasks = listOfResultOrder.get(0).sum_all_tasks;
		
		for(ResultOrder ro: listOfResultOrder){
			sum_sum_ans_all += ro.sum_ans_all;
			sum_sum_within_one += ro.sum_within_one;
			sum_sum_within_three += ro.sum_within_three;
			sum_sum_within_five += ro.sum_within_five;
			sum_sum_within_ten += ro.sum_within_ten;
			
		}
		
		logControl.println("");
		logControl.println("");
		logControl.println("総数\t" + all_tasks);
		logControl.println("全体\t\t" + sum_sum_ans_all / (double)listOfResultOrder.size());
		logControl.println("上位1番目に出た数\t\t\t" + sum_sum_within_one / (double)listOfResultOrder.size());
		logControl.println("上位3番目に出た数\t\t\t" + sum_sum_within_three / (double)listOfResultOrder.size());
		logControl.println("上位5番目に出た数\t\t\t" + sum_sum_within_five / (double)listOfResultOrder.size());
		logControl.println("上位10番目に出た数\t\t\t" + sum_sum_within_ten / (double)listOfResultOrder.size());
		
		//logControl.println("出現しなかった数\t\t\t" + sum_m_one);
//		logControl.println("平均キーワード数\t" + ((double)sumKey/result_list.size()));
//		logControl.println("平均ローカル型数\t" + ((double)sumLT/result_list.size()));
//		logControl.println("平均ローカル関数数\t" + ((double)sumLF/result_list.size()));
//		logControl.println("総キーワード数\t" + (sumKey));
//		logControl.println("総ローカル型数\t" + (sumLT));
//		logControl.println("総ローカル関数数\t" + (sumLF));
		
		logControl.println("");
		logControl.println("引数の組み合わせx以下で全探索. x=\t" + KeywordProgramming.COMBINATION_SIZE);

		logControl.println("");
		
		long stop = System.currentTimeMillis();
		
		logControl.println(" 実験の実行にかかった時間= " + (stop-start) + " ミリ秒。LocalSearch.run");
	}


	/**
	 * 一番スコアが高くなるような特徴の重みの組合わせを探すメソッド
	 * 
	 * 3（そのまま、＋step, -stepの3つ）の4乗通り=81の
	 * 現在の特徴の重みの組み合わせの近傍について、
	 * スコアを求め、その中で最大のスコアとなる組合わせを求める。
	 * 
	 * すべての重みの組み合わせを試してみるために、
	 * このメソッドは再帰的に呼ばれる
	 *  
	 * @param feature_num 特徴番号
	 * @param start_w	ステップ開始時の重みの組合わせ
	 * @param tmp_w		現在の重みの組合わせ
	 */
	private void findBestCombination(int feature_num, double[] start_w, double[] tmp_w) {

		//計算
		if(feature_num == 0){

			//重みを更新
			ExplanationVector.setWeights(tmp_w);
			//再計算
			List<Integer> tmp_order_list = new ArrayList<Integer>();
			double tmp_score = reCalculateScore(testSites, tmp_w, tmp_order_list);
			
			boolean flg_reflesh_current_point = false;	//更新が起きたかのフラグ
			/*
			 * 現在の最大スコアよりも、
			 * 更新した結果のスコアの方が大きい場合、更新する。
			 */
			if(tmp_score > max_score){
				flg_reflesh_current_point = true;
				max_score = tmp_score;
				//配列のコピー
				best_w = Arrays.copyOf(tmp_w, tmp_w.length);
				//リストのコピー
				answerOrders.clear();
				answerOrders.addAll(tmp_order_list);
			}
			//フラグがtrueのとき、各近傍のログを出す
			if(flg_log_neighbours){
				logControl.print("各近傍毎の情報, ステップ " + counter_step + ", 近傍 " + counter_neighbour++);
				logControl.print(", 特徴の重み = (");
				for(int i = 0; i < 4; i++){
					logControl.print(String.valueOf(tmp_w[i]) + ", ");
				}
				logControl.print("), スコア = " + tmp_score);
				if(flg_reflesh_current_point){
					logControl.println(", 更新あり");
				}else{
					logControl.println(", 更新なし");
				}
			}
			return;
		}

		//分岐
		feature_num--;

		//そのまま
		tmp_w[feature_num] = start_w[feature_num];
		findBestCombination(feature_num, start_w, tmp_w);

		//正の特徴か負の特徴か
		boolean positive = ExplanationVector.isPositive(feature_num);
		
		//増やす
		tmp_w[feature_num] = start_w[feature_num] + step_w[feature_num];
		
		//ネガティブな特徴は0以上になることはない。
		if(positive == false){
			if(tmp_w[feature_num] > 0)
			tmp_w[feature_num] = 0;
		}
			
		findBestCombination(feature_num, start_w, tmp_w);

		//減らす
		tmp_w[feature_num] = start_w[feature_num] - step_w[feature_num];
		
		//ポジティブな特徴は0以下になることはない。
		if(positive == true){
			if(tmp_w[feature_num] < 0)
			tmp_w[feature_num] = 0;
		}
			
		findBestCombination(feature_num, start_w, tmp_w);

		return;
	}

	/*
	 * すべてのTestSiteに対してKPを行い、
	 * 出力候補のListを取得する。
	 * 
	 * 正解候補が出現した順位も保存する。
	 * 
	 */
	public static void runKeywordProgrammingForAllTasks(IProgressMonitor monitor, TestSite[] testSites, List<Integer> answerOrders, String state){
		for(int i = 0; i < testSites.length; i++){
			//ユーザーが処理をキャンセルした
			if(monitor != null && monitor.isCanceled()) {
			    return;
			}
			if(monitor != null)
				monitor.setTaskName("タスクId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") について、キーワードプログラミングのアルゴリズムにより出力候補群を生成中");
			testSites[i].initKeywordProgramming();
			testSites[i].runKeywordProgramming(state);
			answerOrders.add(testSites[i].getAnswerNumber(9999));
		}
	}
	

	/*
	 * すべてのTestSiteに対してKPを行い、
	 * 出力候補のListを取得する。
	 * 
	 * 正解候補が出現した順位も保存する。
	 * 
	 * キーワードは頻度の高い方から指定個数選んで入力する。
	 */
	public void runKeywordProgrammingForAllTasksWithSelectedKeywords(IProgressMonitor monitor, TestSite[] testSites, List<Integer> answerOrders, String state){
		for(int i = 0; i < testSites.length; i++){
			//ユーザーが処理をキャンセルした
			if(monitor != null && monitor.isCanceled()) {
			    return;
			}
			if(monitor != null)
				monitor.setTaskName("タスクId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") について、キーワードプログラミングのアルゴリズムにより出力候補群を生成中");
			testSites[i].initKeywordProgramming();
			
			//キーワードを入手
			List<String> keywords = testSites[i].getKeywordList();
			
			//キーワードを頻度の昇順(降順)にソートし、指定数以降は削除する。
			List<String> sorted_keywords = sortKeywordsByFrequency(keywords);
			
			//編集したキーワードをセットする
			testSites[i].setKeywordList(sorted_keywords);
			
			testSites[i].runKeywordProgramming(state);
			answerOrders.add(testSites[i].getAnswerNumber(9999));
			
			//編集したキーワードをもとに戻す。
			testSites[i].resetKeyword();
			
		}
	}

	
	/*
	 * すべてのTestSiteに対してKPを行い、
	 * 出力候補のListを取得する。
	 * 
	 * 正解候補が出現した順位も保存する。
	 * 
	 * キーワードは頻度の高い方から指定個数選んで入力する。
	 */
	public void runKeywordProgrammingForAllTasksForAllKeywordCombinations(IProgressMonitor monitor, TestSite[] testSites, String state){
		int sum_answerOrder = 0;
		
		
		for(ArrayList<Integer> combi_list :this.allCombinations){
			
			List<Integer> answerOrders = new ArrayList<Integer>();
			
			for(int i = 0; i < testSites.length; i++){
				//ユーザーが処理をキャンセルした
				if(monitor != null && monitor.isCanceled()) {
				    return;
				}
				if(monitor != null)
					monitor.setTaskName("タスクId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") について、キーワードプログラミングのアルゴリズムにより出力候補群を生成中");
				
				//generics を排除
				String str = testSites[i].getSelectedString();
				if(str.contains("<"))
					continue;
				
				testSites[i].initKeywordProgramming();
				//キーワードを入手
				List<String> keywords = testSites[i].getKeywordList();
				
				//キーワードを頻度の昇順(降順)にソートし、指定数以降は削除する。
				//List<String> sorted_keywords = sortKeywordsByFrequency(keywords);
				
				//キーワードを選定する。
				List<String> sorted_keywords = selectKeywords(keywords, combi_list);
				
				//編集したキーワードをセットする
				testSites[i].setKeywordList(sorted_keywords);
				
				testSites[i].runKeywordProgramming(state);
				
				//答えを格納。
				answerOrders.add(testSites[i].getAnswerNumber(9999));
				
				//編集したキーワードをもとに戻す。
				testSites[i].resetKeyword();
				
			}
			
			int sum_ans_all = 0;	//全範囲での答えの数。
			int sum_within_one = 0;	//上位1件
			int sum_within_tree = 0; //上位3件
			int sum_within_five = 0; //上位5件
			int sum_within_ten = 0; //上位10件
			for(Integer order: answerOrders){
				if(order != -1){
					//全体。
					sum_ans_all++;
					//上位1件に正解が出現した
					if(order == 0)
						sum_within_one++;
					//上位3件以内に正解が出現した
					if(order < 3)
						sum_within_tree++;//順位が0,1,2のとき。
					//上位5件以内に正解が出現した
					if(order < 5)
						sum_within_five++;
					//上位10件以内に正解が出現した
					if(order < 10)
						sum_within_ten++;
				}
			}
			listOfResultOrder.add(new ResultOrder(answerOrders.size(), sum_ans_all, sum_within_one, sum_within_tree, sum_within_five, sum_within_ten));
		}
	}
	
	/*
	 *  combi_list で指定されたid のキーワードだけを残す。
	 *  
	 *  combi_list は番号が、1始まりなので、注意する。
	 */
	private List<String> selectKeywords(List<String> keywords, ArrayList<Integer> combi_list) {

		List<String> result_keywords = new ArrayList<String>();
		
		for(Integer id: combi_list){
			for(int i = 0; i < keywords.size(); i++){
				if((id - 1) == i){
					result_keywords.add(keywords.get(i));
				}
			}
		}
		
		return result_keywords;		
	}
	
	//numOfKeywords キーワードの個数
	
	//出現頻度の高いもの（低いもの）を残す。
	private List<String> sortKeywordsByFrequency(List<String> keywords) {
		//treemapは、キーがソートされる。
		
		
		
		TreeMap<Integer, String> key_freq = new TreeMap();
		
		//ExmComparator 降順用のコンパレーター
		if(this.flg_desc)
			key_freq = new TreeMap(new ExmComparator());
		
		for(String s: keywords){
			int index = term_list.indexOf(s);
					
			if(index != -1){
				//在る。
				key_freq.put(index, s);
			}else{
				//無いとき。
				key_freq.put(9999999, s);
			}
		}
		
		List<String> result_keywords = new ArrayList<String>();
		Iterator<Integer> it = key_freq.keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			
			if(count >= this.numOfKeywords)
				break;
			else
				count++;
			
			Integer key = it.next();
			result_keywords.add(key_freq.get(key));
//		    System.out.println(key + "/" + key_freq.get(key));
		}
		return result_keywords;		
	}
	
	/*
	 * 選択候補の順位を取得する。
	 */
	public static void getSelectedStringOrders(TestSite[] testSites, List<Integer> answerOrders){
		for(int i = 0; i < testSites.length; i++){
			answerOrders.add(testSites[i].getSelectedStringOrder());
		}
	}
	
	/*
	 * 正解が出現したタスクの数をスコアとした
	 * スコアを返す。
	 */
	public static int getScoreOfAnswerAppearancedTaskNumbar(List<Integer> order_list){
		int score = 0;
		for(Integer i: order_list){
			if(i != -1)
				score++;
		}
		return score;
	}
	
	
	/*
	 * 正解が出現した順位の逆数を各タスク合計したものをスコアとした
	 * スコアを返す。
	 */
	public static double getScoreOfAnswerAppearancedOrder(List<Integer> order_list){
		double score = 0.0;
		for(Integer i: order_list){
			if(i != -1)	//出現しなければスコアは0
				score += 1.0 / (i + 1);
		}
		return score;
	}
	
	/*
	 * 
	 * 一度目に行ったKPによって生成された出力候補群を用いて
	 * 評価値の再計算を行う
	 * 
	 * 逆数スコアを出力する。
	 * 
	 * tmp_w 特徴の重み
	 */
	public static double reCalculateScore(TestSite[] testSites, double[] tmp_w, List<Integer> tmp_order_list){
		
		for(int i = 0; i < testSites.length; i++){
			//各Treeに関してtmp_wのときのスコアを計算。
			testSites[i].reCalculateEvaluationValue();
			//ソートする.
			testSites[i].sortFunctionTrees();
			//正解順位を得る。
//			answerOrders.add(testSites[i].getAnswerNumber(200));
			tmp_order_list.add(testSites[i].getAnswerNumber(200));
		}
		
		//逆数スコアを出力する。
		return getScoreOfAnswerAppearancedOrder(tmp_order_list);
	}
	
	//降順に並べるためのコンパレータ
	public class ExmComparator implements java.util.Comparator{
	    public int compare( Object object1, Object object2 ){
	        return ( (Comparable)object1 ).compareTo( object2 ) * -1;
	    }
	}
}
