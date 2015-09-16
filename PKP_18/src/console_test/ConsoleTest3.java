package console_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;



import experiment1.Result;

import keywordProgramming.ExplanationVector;
import keywordProgramming.Function;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Params;
import keywordProgramming.Type;

import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;
import state.KpRunningState;
/*
 * 実行
 */
public class ConsoleTest3 {

	
	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
	
	public static ArrayList<Function> original_functions = new ArrayList<Function>();
	
	public static keywordProgramming.Params para = new keywordProgramming.Params();
	
	public static String taskFolder; 	//"C:\\Users\\sayuu\\Desktop\\exp\\";
	static String resultFolder;			//"C:\\Users\\sayuu\\Desktop\\exp\\Result\\";
	
	public static final boolean IsConsoleTest = true;
	
	public static int[] start_end = {0,5,0,2,0,6};
	
	public static List<ResultFile> resultFiles = new ArrayList<ResultFile>();
	
	public static String lcs_ld;
	
	public static void main(String[] args) throws FileNotFoundException {
		List<TestSite> list = new ArrayList<TestSite>();
		
		taskFolder = args[0] + File.separator;
		resultFolder = args[1] + File.separator;
		String projectName = args[2];
		lcs_ld = args[3];
		
//		taskFolder = "C:\\Users\\sayuu\\Desktop\\exp\\";
//		resultFolder = "C:\\Users\\sayuu\\Desktop\\exp\\Result\\";		
//		String projectName = "carol0";
		
		
		//基本関数と型はロードしておく。
		
		FileReader in = new FileReader(taskFolder + "sub_class.txt");
        BufferedReader c_r = new BufferedReader(in);
        FileReader in1 = new FileReader(taskFolder + "function.txt");
		BufferedReader f_r = new BufferedReader(in1);
		
		//ファイルからのロードは一回だけやる。
		loadOriginalFiles(c_r, f_r);
		
		resultFiles = loadResultFiles(resultFolder);
		list = getItems(taskFolder + File.separator + projectName);
		run(list, projectName);
		
		if(args.length < 8){
			return;
		}
		
		taskFolder = args[4] + File.separator;
		resultFolder = args[5] + File.separator;
		projectName = args[6];
		lcs_ld = args[7];
		
		resultFiles = loadResultFiles(resultFolder);
		list = getItems(taskFolder + File.separator + projectName);
		run(list, projectName);
	}

	public static List<ResultFile> loadResultFiles(String path){
		List<ResultFile> list = new ArrayList<ResultFile>();
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

        File[] files = dir.listFiles();
        for(File file: files){
        	ResultFile t = new ResultFile(file, false);
        	list.add(t);
        }
        
        Collections.sort(list);
        
        
        for(ResultFile r: list){
        	System.out.println(r.name);
//        	System.out.println(r.output);
        }
        
		return list;
	}
	
	//ファイルの読み込み(型、関数)
	public static void loadOriginalFiles(BufferedReader c_r, BufferedReader f_r){
		String s;

		//classesファイルの読み込み
		try {
			while ((s = c_r.readLine()) != null) {
				Type t = new Type(s);
				original_types.put(t.getName(), t);
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
				original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public static List<TestSite> getItems(String path){
		List<TestSite> list = new ArrayList<TestSite>();
				
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File file: classFolders) {
	    	if(file.isFile()){
	    		TestSite t = new TestSite(file, false);
	        	list.add(t);
//		        File[] files = folder.listFiles();
//		        for(File file: files){
//		        	TestSite t = new TestSite(file, false);
//		        	list.add(t);
//		        }
	    	}
	    }
	    
	  //時間の降順にソートする。
	    TestSiteComparator comp = new TestSiteComparator();
	    Collections.sort(list, comp);
	    
//	    for(TestSite ts :list){
//	    	System.out.println((ts.getTxtFile().getAbsolutePath()));
//	    }
	    
		return list;
	}
	
	public static void run(List<TestSite> list, String projectName){

		//重みは固定
		keywordProgramming.KeywordProgramming.BEST_R = 100;
		keywordProgramming.KeywordProgramming.HEIGHT = 3;
//		para.const_freq = 0;
//		para.w_arr.add(0, -0.05);//負
//		para.w_arr.add(1, 1.0);
//		para.w_arr.add(2, -0.01);//負
//		para.w_arr.add(3, 0.001);
//		para.w_arr.add(4, 0.001);
		
		double[] tmp_w = {-0.05, 1.0, -0.01, 0.001, 0.001};
		ExplanationVector.setWeights(tmp_w);
		ExplanationVector.setConstFreq(0.0);
		
		//キーワード改変(5種類)
		//なし
		//母音削除, 先頭３文字、１文字置換、１文字挿入
		for(int i = 0; i< 5; i++){
		
			
			switch(i){
			case 0:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_OFF;
				break;
			case 1:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL;
				break;
			case 2:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3;
				break;
			case 3:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE;
				break;
			case 4:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD;
				break;
			}
			
			//分割入力(2種類)
			//off, on
			for(int j = 0; j< 2; j++){
				
				
				switch(j){	
				case 0:
					para.separate_keywords = "false";
					break;
				case 1:
					para.separate_keywords = "true";
					break;
				}
				
				//類似度(6種類 ldは変数を動かす)
				// なし。
				// LCS1,2,3,4
				// LD (削除、追加、置換)コストを動かす。(k=0 1 2 4 8)なら5通り
				// 計、10種類
				int k_start = 0;
				int k_end = 0;
				if(lcs_ld.equals("lcs")){
					k_start = 1;
					k_end = 5;
				}else{
					k_start = 5;
					k_end = 6;
				}
					
				for(int k = k_start; k < k_end; k++){
				
					switch(k){
					case 0:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_OFF;
						break;
					case 1:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1;
						break;
					case 2:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2;
						break;
					case 3:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3;
						break;
					case 4:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4;
						break;
					case 5:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LD;
						break;
					}
					
					if(k == 5){
						double c_list[] = {1.0, 1.5, 2.33, 4.0, 9.0};
						
						for(int n = 0; n < c_list.length; n++){
							
							para.ld_const = c_list[n];
							
							int k_list[] = {0, 1, 2, 4, 8};
							for(int m = 0; m < k_list.length; m++){
								para.ld_delete = 1;
								para.ld_replace = 1 + k_list[m];
								para.ld_add = 1 + 2 * k_list[m];
								long start = System.currentTimeMillis();
	//							runTasksUsingMultiThread(list, para, projectName, start);
								if(!existsResult(para, resultFiles)){
									System.out.println(projectName);
									para.println();
									runTasks(list,  projectName, start);
								}
							}
						}
					}else{
						//リセット　見栄えのため。
						para.ld_delete = 0;
						para.ld_replace = 0;
						para.ld_add = 0;
						long start = System.currentTimeMillis();
//						runTasksUsingMultiThread(list, para, projectName, start);
						if(!existsResult(para, resultFiles)){
							System.out.println(projectName);
							para.println();
							runTasks(list,  projectName, start);
						}
					}
				}
			}
		}
	}
	
	public static boolean existsResult(Params para, List<ResultFile> list){
		if(list == null || list.size() == 0)
			return false;
		
		for(ResultFile r: list){
			
//			r.param.print();
//			if(para.ld_delete == 1 && para.ld_replace == 2 && para.ld_add == 3)
//				System.out.println();
//			if(r.param.ld_delete == 1 && r.param.ld_replace == 2 && r.param.ld_add == 3)
//				System.out.println();
			if(r.matchParams(para)){
				return true;//1個でも同じパラメータの組み合わせがあれば、存在した！
			}
		}
		
		return false;
	}
	
	public static void runTasks(List<TestSite> testSites,
			String projectName, long startTime){
		
		long start = System.currentTimeMillis();

		//LogControl logControl = new LogControl(LogControl.LOCAL_SEARCH);
		String resultStr = "";

		/*
		 * 各タスクについて、現パラメータにおける出力候補を取得しておく。
		 */
		//各タスクの出力候補群の中で、正解が出現した順位
		List<Integer> answerOrders = new ArrayList<Integer>();
		
		for(int i = 0; i < testSites.size(); i++){
			System.out.println((i+1)+ "/" + testSites.size() + ", " + testSites.get(i).getId());
			testSites.get(i).initKeywordProgramming();
			testSites.get(i).runKeywordProgramming(KpRunningState.LOCAL_SEARCH_BATCH);
			answerOrders.add(testSites.get(i).getAnswerNumber(9999));
			
			if(i % 1000 == 0){
				System.gc();				
			}
		}
			
		List<Result> result_list = new ArrayList<Result>(); 
		
//		resultStr += "  > 正解タスクの順位 > 出力形式： [ID, 欲しい出力, 順位], (順位は0番から数える. -1は候補群中に出現しなかったことを表す.)");
		for(int i = 0; i < testSites.size(); i++){
			resultStr += "   " + testSites.get(i).getId()+ ", "+ testSites.get(i).getSelectedString() + ", " + answerOrders.get(i) + "\n";
			
			String str = testSites.get(i).getSelectedString();
			int odr = answerOrders.get(i);
//			resultStr += str + "\t" + odr);//id削除。タブ区切りに変更。
			int numKey = testSites.get(i).getNumOfKeywords();
			int numLT = testSites.get(i).getNumOfLocalTypes();
			int numLF = testSites.get(i).getNumOfLocalFunctions();
			//generics を排除
			if(!str.contains("<"))
				result_list.add(new Result(testSites.get(i).getId(), str, odr, numKey, numLT, numLF));
		}
//		resultStr += "  < 正解タスクの順位 < 出力形式： [ID, 欲しい出力, 順位], (順位は0番から数える. -1は候補群中に出現しなかったことを表す.)");
		
		int sum_zero = 0;
		int sum_m_one = 0;
		int sum_others = 0;
		int sumKey = 0;
		int sumLT = 0;
		int sumLF = 0;
		
		int sum_within_tree = 0; //上位3件
		int sum_within_five = 0; //上位5件
		int sum_within_ten = 0; //上位10件
		
		List<Result> result_list_others = new ArrayList<Result>(); 
		
		List<Result> result_list_incorrect = new ArrayList<Result>();//間違いの結果を保持する。
		try{
        for(Result result: result_list) {
//			resultStr += result.fTestSiteId + "\t" + result.fSelectedString + "\t" + result.fAnswerOrder);
			sumKey += result.fNumOfKeywords; 
			sumLT += result.fNumOfLocalTypes;
			sumLF += result.fNumOfLocalFunctions;
			

			//上位3件以内に正解が出現した
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 3)
				sum_within_tree++;//順位が0,1,2のとき。
			//上位5件以内に正解が出現した
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 5)
				sum_within_five++;
			//上位10件以内に正解が出現した
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 10)
				sum_within_ten++;
			
			
			if(result.fAnswerOrder == 0)
				sum_zero++;
			else if(result.fAnswerOrder == -1){
				sum_m_one++;
				result_list_incorrect.add(result);
			}else{
				sum_others++;
				result_list_others.add(result);
			}
					
        }
		}catch(Error e){
			e.printStackTrace();
		}
        		
		String flg_input = para.shortened_input_keywords;		resultStr += "  入力キーワード改変 =" + flg_input+"\n";
		String aimai = para.common_subsequence;
		resultStr += "  あいまいキーワード 対応 =" + aimai+"\n";
		int ld_del = para.ld_delete;
		int ld_rep = para.ld_replace;
		int ld_add = para.ld_add;
		resultStr += "  LD = " + ld_del + ", " + ld_rep + ", " + ld_add+"\n";
		String bunkatu = para.separate_keywords;
		resultStr += "  キーワード分割 =" + bunkatu+"\n";
		
		resultStr += "BEST_R = " + KeywordProgramming.BEST_R+"\n";
		resultStr += "最大の木の高さ = " + KeywordProgramming.HEIGHT+"\n";
		resultStr += ("現在の特徴の重み = ")+"\n";		
//		for(int i = 0; i < best_w.length; i++){
//			logControl.print(String.valueOf(best_w[i]) + ", ");
//		}
		resultStr += "alfa = " + ExplanationVector.getConstFreq()+"\n";
		resultStr += ""+"\n";
		resultStr += ""+"\n";
		
		//resultStr += testSites.get(0).getPackageName()+"\n";
		//resultStr += testSites.get(0).getFullyQualifiedClassName()+"\n";
		
		
		resultStr += "総数\t" + result_list.size()+"\n";
		//resultStr += "2番目以降に出た数\t\t\t\t\t" + sum_others)+"\n";
		
		resultStr += "上位1番目に出た数\t\t" + sum_zero+"\n";
		resultStr += "上位3番目以内に正解出現\t\t\t" + sum_within_tree+"\n";
		resultStr += "上位5番目以内に正解出現\t\t\t\t" + sum_within_five+"\n";
		resultStr += "上位10番目以内に正解出現\t\t\t\t\t" + sum_within_ten+"\n";
		resultStr += "正解出現数\t\t\t\t\t\t" + (result_list.size() - sum_m_one)+"\n";
		double s_r_10 =  getScoreOfAnswerAppearancedOrderLimitX(answerOrders, 10);
				double s_r_30 =  getScoreOfAnswerAppearancedOrderLimitX(answerOrders, 30);
		resultStr += "上位10番目以内の逆数スコア\t\t\t\t\t\t\t" + s_r_10+"\n";
		resultStr += "上位30番目以内の逆数スコア\t\t\t\t\t\t\t\t" + s_r_30+"\n";


		resultStr += "データ"+"\n";
		
		String output = result_list.size() +","+sum_zero+","+sum_within_tree+","+sum_within_five+","+sum_within_ten+","+(result_list.size() - sum_m_one)+","+s_r_10+","+s_r_30;
		
		resultStr += output+"\n";
		
		resultStr += "出現しなかった数\t" + sum_m_one+"\n";
//		resultStr += "  > 2番目以降に出たもの")+"\n";
//		for(Result result: result_list_others){
//			resultStr += result.fSelectedString + "\t" + result.fAnswerOrder)+"\n";
//		}
//		resultStr += "  > 2番目以降に出たもの")+"\n";

		resultStr += "平均キーワード数\t" + ((double)sumKey/result_list.size())+"\n";
		resultStr += "平均ローカル型数\t" + ((double)sumLT/result_list.size())+"\n";
		resultStr += "平均ローカル関数数\t" + ((double)sumLF/result_list.size())+"\n";
		resultStr += "総キーワード数\t" + (sumKey)+"\n";
		resultStr += "総ローカル型数\t" + (sumLT)+"\n";
		resultStr += "総ローカル関数数\t" + (sumLF)+"\n";
		
		resultStr += ""+"\n";
		resultStr += "引数の組み合わせx以下で全探索. x=\t" + KeywordProgramming.COMBINATION_SIZE+"\n";

		resultStr += ""+"\n";
		
		long stop = System.currentTimeMillis();
		
		resultStr += " 実験の実行にかかった時間= " + (stop-start) + " ミリ秒。LocalSearch.run"+"\n";
		
		
		   String name = "";
				name += projectName;
				name += ",";
				name += startTime;//開始時間　
				name += ",";
				name += para.shortened_input_keywords;
				name += ",";
				name += para.separate_keywords;
				name += ",";
				name += para.common_subsequence;
				name += ",";
				name += para.ld_delete;
				name += ",";
				name += para.ld_replace;
				name += ",";
				name += para.ld_add;
				name += ",";
				name += para.ld_const;
			//出力テキストファイルの作成
	    	String savefilename = resultFolder + name + ".txt";
	    	
	    	//ファイルが無ければ作成する
	    	File txtFile = new File(savefilename);
	    	if (!txtFile.exists()){
				try {
					txtFile.createNewFile();
				} catch (IOException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
	    	}
	    	
			//フォルダがなければ作成する。
			File dir = txtFile.getParentFile();
			if (!dir.exists()) {  
			    dir.mkdirs();
			}
			
			try{
				PrintWriter export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
				//書き込み
				export_pw.print(output + "\n" +resultStr);
				//終了処理
				export_pw.flush();
				export_pw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		    
		
	}
	
	public static double getScoreOfAnswerAppearancedOrderLimitX(List<Integer> order_list, int x){
		double score = 0.0;
		for(Integer i: order_list){
			if(i != -1 && i <= x)	//出現しなければスコアは0 , i > xは評価しない。
				score += 1.0 / (i + 1);
		}
		return score;
	}
	
//	public static void runTasksUsingMultiThread(List<TestSite> list,
//			keywordProgramming.exp.Params para, String projectName, long startTime) {
//		int max_thread_num = 3;
//		if(list.size() < max_thread_num){
//			max_thread_num = list.size();
//		}
//	    TsThread thread[];
//	    TsSync sync;
//	    
//	    // 同期オブジェクトの作成
//	    sync = new TsSync();
//
//	    // 子スレッド情報配列の作成
//	    thread = new TsThread[max_thread_num];
//
//	    // スレッドの起動
//	    for (int i = 0; i < list.size(); ) {
//	    	
//	    	for(int j = 0; j < max_thread_num; j++){
//	    		if(i >= list.size()){
//	    			break;
//	    		}
//		    	thread[j] = new TsThread(sync, para, list.get(i), projectName, startTime);
//			    thread[j].start();
//			    			    
//			    i++;
//	    	}
//		      
//		    try {
//			 Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO 自動生成された catch ブロック
//			 e.printStackTrace();
//			}
//		      
//		    // 子の終了を待つ
//		    sync.waitSync();
//		    
//	    }
//	   String name = "";
//			name += projectName;
//			name += ",";
//			name += startTime;//開始時間　
//			name += ",";
//			name += para.shortened_input_keywords;
//			name += ",";
//			name += para.separate_keywords;
//			name += ",";
//			name += para.common_subsequence;
//			name += ",";
//			name += para.ld_delete;
//			name += ",";
//			name += para.ld_replace;
//			name += ",";
//			name += para.ld_add;
//			
//		//出力テキストファイルの作成
//    	String savefilename = resultFolder + name + ".txt";
//    	
//    	//ファイルが無ければ作成する
//    	File txtFile = new File(savefilename);
//    	if (!txtFile.exists()){
//			try {
//				txtFile.createNewFile();
//			} catch (IOException e1) {
//				// TODO 自動生成された catch ブロック
//				e1.printStackTrace();
//			}
//    	}
//    	
//		//フォルダがなければ作成する。
//		File dir = txtFile.getParentFile();
//		if (!dir.exists()) {  
//		    dir.mkdirs();
//		}
//		
//		try{
//			PrintWriter export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
//			//書き込み
//			export_pw.print(sync.output);
//			//終了処理
//			export_pw.flush();
//			export_pw.close();
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//	    
//	}
}

