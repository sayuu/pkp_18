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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import experiment1.Result;

import keywordProgramming.ExplanationVector;
import keywordProgramming.Function;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Params;
import keywordProgramming.Type;
import logging.LogControl;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;
import state.KpRunningState;
/**
 * 結果整理
 * @author sayuu
 *
 */
public class ConsoleTest4 {

	static String resultFolder = "C:\\Users\\sayuu\\Desktop\\exp\\Result\\";
	static String resultAllFolderLCS = "C:\\Users\\sayuu\\Desktop\\Result All lcs\\";
	static String resultAllFileLCS = "C:\\Users\\sayuu\\Desktop\\Result All lcs\\all.txt";
	static String resultAllFolderLD = "C:\\Users\\sayuu\\Desktop\\Result All ld\\";
	static String resultAllFileLD = "C:\\Users\\sayuu\\Desktop\\Result All ld\\all.txt";
	static String resultAllFileLD2 = "C:\\Users\\sayuu\\Desktop\\Result All ld\\all2.txt";
	
	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
	
	public static ArrayList<Function> original_functions = new ArrayList<Function>();
	
	public static keywordProgramming.Params para = new keywordProgramming.Params();
	
	public static String expFolder = "C:\\Users\\sayuu\\Desktop\\Result All lcs\\";
	
	public static final boolean IsConsoleTest = true;
	
	public static boolean flg_percent = true;
	
	public static boolean flg_raw_data = true;
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		//getResult("j60", true);
		
//		flg_percent = false;
//		getAllResultLD();
		
//		
//		flg_percent = false;
//		getFinalResult("all", true);
		getExcelResult();
	}
	
	public static void getExcelResult(){
		List<ResultFile> list = new ArrayList<ResultFile>();
		list = loadResultAllFile(resultAllFileLD2);
		//上位10件目の結果を横軸k,縦軸Cで並べる。
		
		//入力方法5種類*分割２種類
		for(int i = 0; i < 10; i++){
			int result_num = 5+30*i;	//0~4はLCSなのでスキップする。
			double c_list[] = {1.0, 1.5, 2.33, 4.0, 9.0};
			for(int n = 0; n < c_list.length; n++){
				int k_list[] = {0, 1, 2, 4, 8};
				for(int m = 0; m < k_list.length; m++){
					String sum10 = list.get(result_num).output.outputPerSum10();
					System.out.print(sum10 + ",");
					result_num++;
				}
				System.out.println();
			}
		}
	}
	
	private static void getAllResultLD() {
		String projectName = "";
		projectName = "cc0";
		getResult(projectName, true);
		
		projectName = "carol";
		for(int i = 0; i <= 2000; i+=500){
			getResult(projectName + String.valueOf(i), true);
		}
		
		projectName = "junit";
		for(int i = 0; i <= 3000; i+=500){
			getResult(projectName + String.valueOf(i), true);
		}
		
		projectName = "s";
		for(int i = 0; i <= 9500; i+=500){
			getResult(projectName + String.valueOf(i), true);
		}
		
		String j_name = "jMemorize";
		String[] j_arr = {"j10","j11","j12","j13","j14","j15","j20","j21","j30","j31","j40","j41","j50","j51","j60","j61"};
		
		for(int i = 0; i < j_arr.length; i++){
			projectName = j_arr[i];
			getResult(projectName, true);
		}
	}

	private static void getAllResultLCS() {
		String projectName = "";
		projectName = "cc\\cc";
		getResult(projectName, false);
		projectName = "carol\\carol";
		getResult(projectName, false);
		projectName = "junit\\junit";
		getResult(projectName, false);
		
		String j_name = "jMemorize";
		String[] j_arr = {"j10","j11","j12","j13","j14","j15","j20","j21","j30","j31","j40","j41","j50","j51","j60","j61"};
		
		for(int i = 0; i < j_arr.length; i++){
			projectName = j_name + File.separator + j_arr[i];
			getResult(projectName, false);
		}
		
		String s_name = "sphinx";
		String[] s_arr = {"s10","s11","s12","s13","s20","s21","s22","s23","s30","s31","s32"};
		
		for(int i = 0; i < s_arr.length; i++){
			projectName = s_name + File.separator + s_arr[i];
			getResult(projectName, false);
		}
	}
	
	private static void getResult(String projectName, boolean isLd) {
		List<ResultFile> list = new ArrayList<ResultFile>();
		if(isLd)
			list = loadResultFiles(resultAllFolderLD + projectName);
		else
			list = loadResultFiles(resultAllFolderLCS + projectName);
		
		if(list != null)
			run(list, projectName);
	}
	
	private static void getFinalResult(String projectName, boolean isLd) {
		List<ResultFile> list = new ArrayList<ResultFile>();
		if(isLd)
			list = loadResultAllFile(resultAllFileLD);
		else
			list = loadResultAllFile(resultAllFileLCS);
		run(list, projectName);
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

	public static List<ResultFile> loadResultAllFile(String path){
		
	    File file = new File(path);
	    if (!file.exists() || !file.isFile()) {  
	    	return null;
		}

	    List<ResultFile> rlist = new ArrayList<ResultFile>();
	    
		try {
			FileReader fr = new FileReader(file);
			
			List<List<ResultFile>> listlist = new ArrayList<List<ResultFile>>();
			BufferedReader br = new BufferedReader(fr);
			String s;
			List<ResultFile> list = new ArrayList<ResultFile>();
			while ((s = br.readLine()) != null) {
				ResultFile r = new ResultFile(s);
				list.add(r);
				if(list.size() == 300){
					listlist.add(new ArrayList<ResultFile>(list));
					list.clear();
				}
			}
			
			for(int i=0; i<300;i++){
				ResultFile all_r = new ResultFile(null, false);
				for(int j=0; j< listlist.size(); j++){
					ResultFile r = listlist.get(j).get(i);
//					System.out.println(r.output.toString());
					if(j==0){
						all_r.output = r.output;
						all_r.para = r.para;
						
					}else{
						all_r.output.plus(r.output);
					}
				}
				rlist.add(all_r);
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return rlist;
	}
	
	public static List<ResultFile> loadResultFiles(String path){
		List<ResultFile> list = new ArrayList<ResultFile>();
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

        File[] files = dir.listFiles();
        for(File file: files){
        	if(file.isDirectory()){
        		continue;
        	}
        	ResultFile t = new ResultFile(file, true);
        	list.add(t);
        }
        
        Collections.sort(list);
        
        
        for(ResultFile r: list){
//        	System.out.println(r.name);
//        	System.out.println(r.output);
        }
        
		return list;
	}
	
	public static List<TestSite> getItems(String path){
		List<TestSite> list = new ArrayList<TestSite>();
				
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        for(File file: files){
	        	TestSite t = new TestSite(file, false);
	        	list.add(t);
	        }
	    }
	    
	  //時間の降順にソートする。
	    TestSiteComparator comp = new TestSiteComparator();
	    Collections.sort(list, comp);
	    
		return list;
	}
	
	public static void run(List<ResultFile> list, String projectName){

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
				
				if(flg_raw_data == false)
				System.out.println("入力キーワード改変,キーワード分割,あいまいキーワード対応,削除,置換,挿入,LDC,LDK,総数,1番目,3番目,5番目,10番目,最後尾まで,上位10番目,上位30番目");
				//類似度(6種類 ldは変数を動かす)
				// なし。LCS1,2,3,4。 5通り。
				// LD (削除、追加、置換)コストを動かす。(k=0 1 2 4 8)なら5通り * (c=5通り)
				// 計、25通り。
				//5+25=30通り。
				for(int k = 0; k< 6; k++){
	
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
								para.ld_k = k_list[m];
								boolean flg = printResults(list, para, projectName);
								
							}
						}
					}else{
						//リセット
						para.ld_delete = 0;
						para.ld_replace = 0;
						para.ld_add = 0;
						para.ld_const = 0.0;
						boolean flg = printResults(list,  para, projectName);
						if(flg_raw_data == false)
						if(!flg)
						System.out.println("false!");
					}
				}
				if(flg_raw_data == false)
				System.out.println("==============================");
			}
		}
	}
	
	public static boolean printResults(List<ResultFile> list, Params para, String projectName){
		int flg = 0;
		List<ResultFile> list2 = new ArrayList<ResultFile>();
		
		for(ResultFile r:list){
			if(r.matchParams(para)){
				para.print();
				if(flg_percent)
					System.out.println("," + r.output.outputPercentPer100());
				else
					System.out.println("," + r.output.toString() );
				flg++;
				list2.add(r);
			}
		}
		
		if(flg > 1){
			System.out.println("重複あり!");
			for(ResultFile r:list2){
				para.print();
				if(flg_percent)
					System.out.println("," + r.output.outputPercentPer100());
				else
					System.out.println("," + r.output.toString());
			}
			return false;
		}
		
		if(flg > 0)
			return true;
		
		para.print();
		System.out.println(",該当ファイルなし。");
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
			System.out.println((i+1)+ "/" + testSites.size());
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
		
		resultStr += testSites.get(0).getPackageName()+"\n";
		resultStr += testSites.get(0).getFullyQualifiedClassName()+"\n";
		
		
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

