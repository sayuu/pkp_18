package gridSearch;

import java.util.ArrayList;
import java.util.List;

import keywordProgramming.ExplanationVector;
import keywordProgramming.KeywordProgramming;

import localSearch.LocalSearch;
import logging.LogControl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import state.KpRunningState;

/*
 * GridSearchに関するクラス
 * 
 * 固定する要素を指定できる
 * CSVを出力できる
 * 
 *
 * 
 */
public class GridSearch {

	private TestSite[] testSites;
	
	//各タスクの出力候補群の中で、正解が出現した順位
	private List<Integer> answerOrders = new ArrayList<Integer>();
	
	//特徴２の値
	private double f2_const;//正

	//各特徴の開始と終了の値 (特徴２以外）
	private double f1_start;//負
	private double f3_start;//負
	private double f4_start;//正
	
	private double f1_end;
	private double f3_end;
	private double f4_end;

	private double f1_step;
	private double f3_step;
	private double f4_step;
	
	private int one_work;
	private int totalwork;
	
	private int numOfKpExec;//何回に1回KPを実行するか。
	/*
	 * コンストラクタ
	 */
	public GridSearch(TestSite[] sites, int totalwork){
		testSites = sites;
		this.totalwork = totalwork;
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		numOfKpExec = store.getInt(PreferenceInitializer.GRID_COUNT_FOR_KP);
		
		f2_const = store.getDouble(PreferenceInitializer.GRID_WEIGHT_1_CONSTANT);
		
		f1_start = -store.getDouble(PreferenceInitializer.GRID_START_WEIGHT_0);
		f3_start = -store.getDouble(PreferenceInitializer.GRID_START_WEIGHT_2);
		f4_start = store.getDouble(PreferenceInitializer.GRID_START_WEIGHT_3);
		f1_end = -store.getDouble(PreferenceInitializer.GRID_END_WEIGHT_0);
		f3_end = -store.getDouble(PreferenceInitializer.GRID_END_WEIGHT_2);
		f4_end = store.getDouble(PreferenceInitializer.GRID_END_WEIGHT_3);
		
		//絶対値の大きい方をendにする。
		if(f1_start < f1_end){
			double tmp = f1_start;
			f1_start = f1_end;
			f1_end = tmp;
		}
		if(f3_start < f3_end){
			double tmp = f3_start;
			f3_start = f3_end;
			f3_end = tmp;
		}	
		if(f4_start > f4_end){
			double tmp = f4_start;
			f4_start = f4_end;
			f4_end = tmp;
		}
		
		f1_step = store.getDouble(PreferenceInitializer.GRID_STEP_WIDTH_0);
		f3_step = store.getDouble(PreferenceInitializer.GRID_STEP_WIDTH_2);
		f4_step = store.getDouble(PreferenceInitializer.GRID_STEP_WIDTH_3);
		
		one_work = (int) Math.abs( (f1_start - f1_end)/f1_step * (f3_start - f3_end)/f3_step * (f4_start - f4_end)/f4_step / totalwork);
		
		//BEST_Rの設定
		KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.GRID_BEST_R);
	}

	public void run(IProgressMonitor monitor){
		
		LogControl logControl = new LogControl(LogControl.GRID_SEARCH);
		logControl.printLogState();

		logControl.println(">>> グリッドサーチ >>>");
		
		logControl.println(" 特徴1 start= " + f1_start + ", end= " + f1_end + ", 刻み幅= " + f1_step);
		logControl.println(" 特徴2 値= " + f2_const + " で固定");
		logControl.println(" 特徴3 start= " + f3_start + ", end= " + f3_end + ", 刻み幅= " + f3_step);
		logControl.println(" 特徴4 start= " + f4_start + ", end= " + f4_end + ", 刻み幅= " + f4_step);
		logControl.println(" KPを行う回数: " + numOfKpExec + " 回に1回行う.");
		logControl.println(" 入力タスク数: " + testSites.length );
		
		logControl.println(" >> 入力タスク一覧 >> 出力形式： [ID, 欲しい出力] ");

		for(TestSite ts: testSites){
			logControl.println("  " + ts.getId()+ ", "+ ts.getSelectedString());
		}
		
		logControl.println(" << 入力タスク一覧 << 出力形式： [ID, 欲しい出力] ");
		
		logControl.println("[カウンタ, 特徴量(4つを順に表示), 合計タスク数, 正解が存在したタスクの数, 逆数スコア, 正解タスクの順位の配列(順位は0番始まり. 出現しない場合は-1.　配列の順番はタスク一覧ビューの上から順), (KP実行or時間短縮)]");
		
		int worked = 1; //モニタ用
		int counter_for_kp = 0;
		int counter_for_grobal = 0;
		 
		//値がdoubleだからfor文ではなくてwhile文を使う
		/*
		 * キーワードとラベルの一致の特徴２は1.0で固定する。
		 */
		double f1_now = f1_start; 
		while(f1_now > f1_end){
			double f3_now = f3_start; 
			while(f3_now > f3_end){
				double f4_now = f4_start; 
				while(f4_now < f4_end){
					//キャンセルされたら終了
					if (monitor.isCanceled()) {
						logControl.println("<<< グリッドサーチ <<<");
						logControl.close();
					    return;
					}

					
					//重みを更新
					double[] tmp_w = {f1_now, f2_const, f3_now, f4_now};	//2番目の重みを指定値で固定.
					ExplanationVector.setWeights(tmp_w);
					
					
					double score;
					
					boolean flg_kp = false;//KPを実行する回か否か。
					String flg_kp_string = "時間短縮";
					if(counter_for_kp % numOfKpExec == 0){
						flg_kp = true;
						flg_kp_string = "KP実行";
					}
					
					/*
					 * 指定回数ごとにKPを行う。
					 */
					if(flg_kp){
						LocalSearch.runKeywordProgrammingForAllTasks(monitor, testSites, answerOrders, KpRunningState.GRID_SEARCH);
						//スコアの計算
						score = LocalSearch.getScoreOfAnswerAppearancedOrder(answerOrders);
					}else{
						if(monitor != null)
							monitor.setTaskName("時間短縮の方法によってスコアを計算。");
						//KPを実行しないとき。
						//再計算
						score = LocalSearch.reCalculateScore(testSites, tmp_w, answerOrders);
					}
					
					
					//正解が存在したタスク数
					int ans_num = LocalSearch.getScoreOfAnswerAppearancedTaskNumbar(answerOrders);
					/*
					 * 表示
					 * 
					 * カウンタ、特徴量、合計タスク数、正解が存在したタスク数、逆数スコア、正解タスクの順位
					 */
					logControl.println(counter_for_grobal + ", " + f1_now + ", " + f2_const + ", " + f3_now + ", " + f4_now + ", " + 
					 testSites.length + ", " + ans_num + ", " + score + ", " + answerOrders + ", " + flg_kp_string);
					
					//リストのクリア
					answerOrders.clear();
					//モニタを動かす
					worked += one_work;
					monitor.worked(worked);
					
					counter_for_grobal++;
					counter_for_kp++;
					f4_now += f4_step;	//プラスする。
				}
				f3_now -= f3_step;	//マイナスする。
			}
			f1_now -= f1_step;	//マイナスする。
		}
		
		logControl.close();
	}

	/*
	 * ひっくり返す。
	 */
	private void change(double f1, double f2){
		double tmp = f1;
		f1 = f2;
		f2 = tmp;
	}
}
