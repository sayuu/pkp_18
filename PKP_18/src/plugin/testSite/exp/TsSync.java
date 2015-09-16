package plugin.testSite.exp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import plugin.preference.PreferenceInitializer;
import keywordProgramming.ExplanationVector;
import keywordProgramming.exp.Params;

public class TsSync extends Thread{

	int cnt; // アクティブスレッド数
	
	public String output = ""; //出力文字列
	
	  public static void main(String args[]) {
		    int num = 3;
		    TsThread ths[];
		    TsSync sync;
		    int i;

		    // 同期オブジェクトの作成
		    sync = new TsSync();

		    // 子スレッド情報配列の作成
		    ths = new TsThread[num];

		    // スレッドの起動
		    for (i = 0; i < num; i++) {
		      //ths[i] = new TsThread(sync, (i+1)*1000);
		      ths[i].start();
		    }

		    // 親スレッドの処理（子スレッドと同時実行）
		    System.out.println("Hello World! at " + Thread.currentThread().getName());

		    // 子の終了を待つ
		    sync.waitSync();

		    // 親スレッドの処理（子スレッド終了後に実行）
		    System.out.println("Hello World! at " + Thread.currentThread().getName());
	  }

	  public TsSync() {
	    // 同期オブジェクトの初期化処理
	    cnt = 0;
	  }

	  public synchronized void addSync() {
	    // 同期オブジェクトの追加
	    cnt++;
	  }

	  public synchronized void delSync() {
	    // 同期オブジェクトの削除
	    cnt--;
	    try {
	      notify();
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }

	  public synchronized void waitSync() {
	    // 同期オブジェクトの待ち合わせ（子がすべて終了するまで待つ）
		
	    while (cnt > 0) {
	      try {
	        
	        wait();
	      } catch (Exception ex) {
	        ex.printStackTrace();
	      }
	    }
	  }
	  
	  public synchronized void printResult(Params para, TestSite ts, String projectName, long startTime){
//			for(int i=0;i<ExplanationVector.FEATURE_NUM;i++)
//				System.out.print(para.w_arr.get(i) + ",");
//			System.out.print(para.const_freq + ",");
		  
	  		String output = "";
	  		output += projectName;
			output += ",";
		  		output +=ts.getId();
				output += ",";
				output += para.shortened_input_keywords;
				output += ",";
				output += para.separate_keywords;
				output += ",";
				output += para.common_subsequence;
				output += ",";
				output += para.ld_delete;
				output += ",";
				output += para.ld_replace;
				output += ",";
				output += para.ld_add;
				output += ",";
				output += ts.getAnswerNumber(para, 99999);
			
				System.out.println(output);
			
				this.output += output + "\r\n";
		}
}
