package logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;

/*
 * ログ処理のためのクラス
 */
public class LogControl {
	
	public static final String LOCAL_SEARCH = "LOCAL_SEARCH";
	public static final String LOCAL_SEARCH_STEP_BY_STEP = "LOCAL_SEARCH_STEP_BY_STEP";
	public static final String LOCAL_SEARCH_NEIGHBOURS = "LOCAL_SEARCH_NEIGHBOURS";
	
	public static final String GRID_SEARCH = "GRID_SEARCH";
	
	public static final String KP = "KP";
	public static final String KP_TIME = "KP_TIME";
	public static final String KP_BASIC = "KP_BASIC";
	public static final String KP_FUNCTIONS = "KP_FUNCTIONS";
	public static final String KP_TYPES = "KP_TYPES";
	public static final String KP_RESULTS = "KP_RESULTS";
	
	private static boolean localSearch = false;
	private static boolean localSearch_stepbystep = false;
	private static boolean localSearch_neighbours = false;
	
	
	private static boolean gridSearch = false;
	private static boolean keywordProgramming = false;
	
	private static boolean kp_time = false;
	private static boolean kp_basic = false;
	private static boolean kp_functions = false;
	private static boolean kp_types = false;
	private static boolean kp_results = false;
	
	private static boolean export_flg = false;	//ファイル出力
	private static String export_folder = 	PreferenceInitializer.LogFileFolder;//ファイル出力フォルダ
	
	//出力テキストファイル用
	private File txtFile;
	
	//出力テキストファイル用PrintWriter
  	private PrintWriter export_pw;
  	
	private String place;
	
	//コンソール
	private static MessageConsole console;
	//コンソールマネージャー
	private static IConsoleManager consoleManager;
	//コンソールストリーム
	public static MessageConsoleStream consoleStream;
	
	public LogControl(String place){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		export_flg = store.getBoolean(PreferenceInitializer.LOG_EXPORT_FLAG);
		
		localSearch = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH);
		localSearch_stepbystep = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH_STEP_BY_STEP);
		localSearch_neighbours = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH_NEIGHBOURS);
		
		gridSearch = store.getBoolean(PreferenceInitializer.LOG_GRID_SEARCH);
		
		kp_time = store.getBoolean(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_TIME);
		kp_basic = store.getBoolean(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_BASIC);
		kp_functions = store.getBoolean(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_FUNCTIONS);
		kp_types = store.getBoolean(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_TYPES);
		kp_results = store.getBoolean(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_RESULTS);
		
		if(kp_time || kp_basic || kp_functions || kp_types || kp_results)
			keywordProgramming = true;
		else
			keywordProgramming = false;//static変数だから一度trueにすると次にオブジェクトを生成したときもtrueのままであるからこの文必要

		//出力テキストファイルの作成
		if(export_flg){
			long millis = System.currentTimeMillis();
			String path = export_folder + "/" + millis + ".txt";//時間で区別
			//場所をpath名に含める.
	  		if(place.equals(this.KP) && keywordProgramming){
	  			path = export_folder + "/" + millis + "_" + this.KP + ".txt";
	  		}else if(place.equals(this.LOCAL_SEARCH) && localSearch){
	  			path = export_folder + "/" + millis + "_" + this.LOCAL_SEARCH + ".txt";
	  		}else if(place.equals(this.GRID_SEARCH) && gridSearch){
	  			path = export_folder + "/" + millis + "_" + this.GRID_SEARCH + ".txt";
	  		}
	  		
			txtFile = new File(path);
			
			//フォルダがなければ作成する。
			File dir = txtFile.getParentFile();
			if (!dir.exists()) {  
			    dir.mkdirs();
			}
			try{
				txtFile.createNewFile();
				export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		this.place = place;
	
		if(Activator.consoleStream == null){
			createConsoleStream();
		}
	}
	
	public void print(String s){
		print(s, place);
	}
	
	public void println(String s){
		print(s + "\n", place);
	}
	
	public void println(String s, String place){
		print(s + "\n", place);
	}
	
	public void print(String s, String place){
		if(!isRequestPlace(place))
			return;
		System.out.print(s);
		if(Activator.consoleStream != null)
			Activator.consoleStream.print(s);
		else
			this.consoleStream.print(s);
		if(export_flg){
			export_pw.print(s);
		}
	}
	
	public void close(){
		if(export_pw != null){
			export_pw.flush();
			export_pw.close();
			//作ったファイルのサイズが0であれば、削除する.
			if(txtFile.length() == 0){
				txtFile.delete();
			}
		}
	}
	
	private boolean isRequestPlace(String place){
		if(place.equals(this.LOCAL_SEARCH) && !localSearch){
			return false;
		}else if(place.equals(this.LOCAL_SEARCH_STEP_BY_STEP) && !localSearch_stepbystep){
			return false;
		}else if(place.equals(this.LOCAL_SEARCH_NEIGHBOURS) && !localSearch_neighbours){
			return false;
		}else if(place.equals(this.GRID_SEARCH) && !gridSearch){
			return false;
		}else if(place.equals(this.KP) && !keywordProgramming){
			return false;
		}else if(place.equals(this.KP_TIME) && !kp_time){
			return false;
		}else if(place.equals(this.KP_BASIC) && !kp_basic){
			return false;
		}else if(place.equals(this.KP_FUNCTIONS) && !kp_functions){
			return false;
		}else if(place.equals(this.KP_TYPES) && !kp_types){
			return false;
		}else if(place.equals(this.KP_RESULTS) && !kp_results){
			return false;
		}
		return true;
	}
	
	public void createConsoleStream(){
		//出力コンソールの用意
		console = new MessageConsole("キーワードプログラミング", null);
		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		consoleManager.addConsoles(new IConsole[]{console});
		consoleStream = console.newMessageStream();
	}
	
	/*
	 * どのログを出力し、どのログを出力しないかを表示する。
	 */
	public void printLogState(){
		String s = ">>> ログ出力の設定 >>>\n";
		
		s += " ローカルサーチ: ";
		s += onOffString(localSearch);
		
		s += " ローカルサーチ 1ステップ毎の情報: ";
		s += onOffString(localSearch_stepbystep);
		
		s += " ローカルサーチ 1ステップ毎の各近傍の情報: ";
		s += onOffString(localSearch_neighbours);
		
		s += " グリッドサーチ: ";
		s += onOffString(gridSearch);
		
		s += " キーワードプログラミング 基本情報: ";
		s += onOffString(kp_basic);

		s += " キーワードプログラミング 型一覧: ";
		s += onOffString(kp_types);

		s += " キーワードプログラミング 関数一覧: ";
		s += onOffString(kp_functions);
		
		s += " キーワードプログラミング 出力候補群一覧: ";
		s += onOffString(kp_results);
		
		s += "<<< ログ出力の設定 <<<\n\n";
		
		System.out.print(s);
		if(Activator.consoleStream != null)
			Activator.consoleStream.print(s);
		else
			this.consoleStream.print(s);
	}
	
	private String onOffString(boolean flg){
		if(flg)
			return "ON\n";
		else
			return "OFF\n";
	}
}
