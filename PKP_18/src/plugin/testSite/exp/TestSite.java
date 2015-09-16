package plugin.testSite.exp;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import ast.AstUtil;


import console_test.LevenshteinDistance;

import keywordProgramming.exp.ExplanationVector;
import keywordProgramming.exp.Function;
import keywordProgramming.exp.FunctionTree;
import keywordProgramming.exp.KeywordProgramming;
import keywordProgramming.exp.Params;
import keywordProgramming.exp.Type;
import keywordProgramming.exp.Word;

/*
 * 1つのタスクを表すクラス。
 */
public class TestSite {

	public static final String TestSiteFolder = "./KeywordProgramming/testSite/";
	public static final String LogSiteFolder = "./KeywordProgramming/logSite/";

	private static List<TestSite> logFiles = new LinkedList<TestSite>();	//オンライン学習用のログファイル
	
	private String className;//クラス名（パッケージ名も）
	private int offset;
	private int original_offset;	//変更前。
    private int line_number_start;
    private int line_number_end;
    private int selected_length;
    //選択文字列
    private String selected_string;
    //選択文字列の順位
    private int selected_string_order;
    //望ましい返り値の型
  	private String desiredReturnType;
  	//入力キーワード
  	private String keywords;
  	//入力キーワード (基に戻すとき用）
  	private String keywords_original;
  	//ロケーション
  	/*
  	 * Return文 の中
	 * If文　の中
	 * 宣言文
	 * 代入文
	 * 地の文
	 * メソッドの引数の中
	 * など。
  	 */
  	private String location;
  	
  	//重複を防ぐためにHashSetにした。
  	//現在エディタ内に存在する有効な型を入れるリスト
  	private HashSet<String> classesInActiveEditor = new HashSet<String>();
  	//現在エディタ内に存在する有効な関数を入れるリスト
  	private HashSet<String> functionsInActiveEditor = new HashSet<String>();
  	
  	//1つのTextSiteに対応する1つのテキストファイル
  	private File txtFile;
  	
  	//選択された
  	private boolean isSelectedTask = false;
  	
  	//ログファイルか否か
  	private boolean isLogFile;
  	
  	//キーワードプログラミングの出力(バッチ学習の時に使用。)
  	public List<FunctionTree> outputfunctionTrees = null;
  	
  	//キーワードプログラミングの出力(オンライン学習の時に使用。)
  	private List<OutputCandidateLog> outputLogList = new ArrayList<OutputCandidateLog>();
  	
  	private int version;	//タスク形式のバージョン
  	/*
  	 * selected_string における開始行x
  	 * と、関数名y の組み合わせ,区切り。
  	 * 複数ある場合は;区切り
  	 * 例：
  	 * 
  	 * 0,activeList;9,token
  	 *  
  	 */
  	private String recieverString;	//レシーバ(複数)を1行で表した。
  	private String localValString;	//ローカル変数(複数)を1行で表した。
  	
  	public List<String> methodList = new ArrayList<String>();	//メソッド一覧(コンストラクタも含む)
  	
  	public int type_of_shortened_keywords = 0;
  	
  	public TestSite(plugin.testSite.TestSite ts){
  		
  		this.className = ts.getFullyQualifiedClassName();
  		this.offset = ts.getOffset();
  		 original_offset = ts.getOriginalOffset();
  	     line_number_start = ts.getLineNumberStart();
  	     line_number_end = ts.getLineNumberEnd();
  	      selected_length = ts.getSelectedLength();
  	    //選択文字列
  	      selected_string = ts.getSelectedString();
  	    //選択文字列の順位
  	      selected_string_order = ts.getSelectedStringOrder();
  	    //望ましい返り値の型
  	  	  desiredReturnType = ts.getDesiredReturnType();
  	  	//入力キーワード
  	  	  keywords = ts.getKeywords();
  	  	//入力キーワード (基に戻すとき用）
  	  	  //keywords_original = ts.getk
  	  	//ロケーション

  	  	  location = ts.getLocation();
  	  	
  	  	//重複を防ぐためにHashSetにした。
  	  	//現在エディタ内に存在する有効な型を入れるリスト
  		Iterator<String> iter;
  		iter = ts.getClassesInActiveEditor().iterator();
  		while(iter.hasNext()){
  			String element = iter.next();
  			classesInActiveEditor.add(element);
  		}
  	  	//現在エディタ内に存在する有効な関数を入れるリスト
		Iterator<String> iter1;
  		iter1 = ts.getFunctionsInActiveEditor().iterator();
  		while(iter1.hasNext()){
  			String element = iter1.next();
  			functionsInActiveEditor.add(element);
  		}
  	  	//1つのTextSiteに対応する1つのテキストファイル
  	  	  txtFile = ts.getTxtFile();
  	  	
  	  	//選択された
  	  	  isSelectedTask = ts.isSelectedTask();
  	  	
  	  	 //メソッドリスト
  	  	  if(ts.methodList != null)
  	  		  if(ts.methodList.size() > 0)
  	  	  for(String m: ts.methodList){
  	  		  this.methodList.add(m);
  	  	  }

  	}
  	
  	//1つのファイルfileは１つのTestSiteに対応。
  	public TestSite(File file, boolean isLogFile){
  		txtFile = file;
  		this.isLogFile = isLogFile;
    	FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String db_str;
			int line_number = 0;
			while ((db_str = br.readLine()) != null) {
				/*
				if(line_number == 0){
					//getInteger(db_str, 1)
					//db_str をint型にして返す。
					//db_strが数値ではないときは、第2引数の1を返す。
					version = Integer.getInteger(db_str, 1);
				}
				
				if(version == 1)
					readVersion1(db_str, line_number);
				else if(version == 2)
					readVersion2(db_str, line_number);
				*/
				
				readVersion3(db_str, line_number);
				
		  		line_number++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		/*
		 * Logファイルの場合、出力候補群を保存したファイル ".*out.txt" も読み込む
		 */
		if(isLogFile){
			String name = txtFile.getName().replace(".txt", "out.txt");
			String path = txtFile.getParent() + "/" +name;
			File logFile = new File(path);
			if(logFile.exists()){
				try {
					fr = new FileReader(logFile);
					BufferedReader br = new BufferedReader(fr);

					selected_string_order = Integer.parseInt(br.readLine());//先頭行は選択候補の順位。
					
					String log_str;
					while ((log_str = br.readLine()) != null) {
						OutputCandidateLog log = new OutputCandidateLog(log_str, keywords.split("[ 　\t]").length);
						outputLogList.add(log);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
    }
  	
  	public HashSet<String> getClassesInActiveEditor() {
		return classesInActiveEditor;
	}

	public HashSet<String> getFunctionsInActiveEditor() {
		return functionsInActiveEditor;
	}
	
  	/*
  	 * タスクの形式が
  	 * Version1
  	 * のタスクからフィールドを読み込む。
  	 */
	private void readVersion1(String db_str, int line_number) {
		if(line_number == 0)
			className = db_str;
		else if(line_number == 1)
			offset = Integer.parseInt(db_str);
		else if(line_number == 2)
			original_offset = Integer.parseInt(db_str);
		else if(line_number == 3)
			line_number_start = Integer.parseInt(db_str);
		else if(line_number == 4)
			line_number_end = Integer.parseInt(db_str);
		else if(line_number == 5)
			selected_length = Integer.parseInt(db_str);
		else if(line_number == 6)
			selected_string = db_str;
		else if(line_number == 7)
			keywords = db_str;
		else if(line_number == 8)
			desiredReturnType = db_str;
		else if(line_number == 9)
			location = db_str;
		else if(line_number == 10){
		    //class (type)
			String s1[] = db_str.split(";");   // ";"が区切り。
			for(String ss1: s1){
				classesInActiveEditor.add(ss1);
			}
		}else{
			//function 1行ごと。
		    functionsInActiveEditor.add(db_str);
		}
	}
 	
  	/*
  	 * タスクの形式が
  	 * Version2
  	 * のタスクからフィールドを読み込む。
  	 */
	private void readVersion2(String db_str, int line_number) {
		//line_number == 0
		// の時は、version つまり2
		
		if(line_number == 1)
			className = db_str;
		else if(line_number == 2)
			offset = Integer.parseInt(db_str);
		else if(line_number == 3)
			original_offset = Integer.parseInt(db_str);
		else if(line_number == 4)
			line_number_start = Integer.parseInt(db_str);
		else if(line_number == 5)
			line_number_end = Integer.parseInt(db_str);
		else if(line_number == 6)
			selected_length = Integer.parseInt(db_str);
		else if(line_number == 7)
			selected_string = db_str;
		else if(line_number == 8)
			keywords = db_str;
		else if(line_number == 9)
			desiredReturnType = db_str;
		else if(line_number == 10)
			location = db_str;
		else if(line_number == 11)
			recieverString = db_str;
		else if(line_number == 12)
			localValString = db_str;
		else if(line_number == 13){
		    //class (type)
			String s1[] = db_str.split(";");   // ";"が区切り。
			for(String ss1: s1){
				classesInActiveEditor.add(ss1);
			}
		}else{
			//function 1行ごと。
		    functionsInActiveEditor.add(db_str);
		}
	}
	
  	/*
  	 * タスクの形式が
  	 * Version3
  	 * のタスクからフィールドを読み込む。
  	 */
	private void readVersion3(String db_str, int line_number) {
		if(line_number == 0)
			className = db_str;
		else if(line_number == 1)
			offset = Integer.parseInt(db_str);
		else if(line_number == 2)
			original_offset = Integer.parseInt(db_str);
		else if(line_number == 3)
			line_number_start = Integer.parseInt(db_str);
		else if(line_number == 4)
			line_number_end = Integer.parseInt(db_str);
		else if(line_number == 5)
			selected_length = Integer.parseInt(db_str);
		else if(line_number == 6)
			selected_string = db_str;
		else if(line_number == 7){
			keywords = db_str;
			keywords_original = db_str;
		}else if(line_number == 8)
			desiredReturnType = db_str;
		else if(line_number == 9)
			location = db_str;
		else if(line_number == 10)
			recieverString = db_str;
		else if(line_number == 11)
			localValString = db_str;
		else if(line_number == 12){
		    //method名
			String s1[] = db_str.split(" ");   // " "区切り
			for(String ss1: s1){
				methodList.add(ss1);
			}
		}else if(line_number == 13){
		    //class (type)
			String s1[] = db_str.split(";");   // ";"が区切り。
			for(String ss1: s1){
				getClassesInActiveEditor().add(ss1);
			}
		}else{
			//function 1行ごと。
		    getFunctionsInActiveEditor().add(db_str);
		}
	}
	
  	public static void loadLogFiles(int numOfFiles){
  		String path = TestSite.LogSiteFolder;//ログを読み取る
  		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        
	        for(File file: files){
	        	//出力文字列ログファイルは除く。
	        	if(file.getName().matches(".*out\\.txt$") == false){
		        	logFiles.add(new TestSite(file, true));
	        	}
	        }
	    }
  		
	    //時間の降順にソートする。
	    Collections.sort(logFiles, new TestSiteComparator());
	    
//	    for(TestSite site: logFiles){
//	    	System.out.println(site.getSaveTime());
//	    }
	   
  	}
  	
  	/*
  	 * ログファイルの追加。
  	 */
  	public static void addLogFile(TestSite site){
  		//先頭に追加する。
  		logFiles.add(0, site);
  	}

  	/*
  	 * ログファイルの取得
  	 * 指定した数のみ、最新の先頭から。
  	 */
  	public static TestSite[] getLogFiles(int numOfFiles){
  		ArrayList<TestSite> list = new ArrayList<TestSite>();
  		for(int i = 0; i < numOfFiles; i++){
  			list.add(logFiles.get(i));
  		}
  		return (TestSite[]) list.toArray(new TestSite[list.size()]);
  	}
  	
    /**
     * 選択テキストからキーワードを自動生成するので、
     * キーワードを引数に必要としない。
     * 
     * @param cn	クラス名
     * @param o		オフセット
     * @param o_ori	変更前のオフセット
     * @param lis	選択開始行
     * @param lie	選択終了行
     * @param len	選択テキスト長
     * @param sct	選択テキスト
     * @param drt	望ましい返り値
     * @param lo	ロケーション
     * @param cls	Typeのリスト
     * @param fus	Functionのリスト
     * @param is_log_file ログファイルか？
     */
    public TestSite(String cn, int o, int o_ori, int lis, int lie, int len, String sct, String drt, String lo, List<String> cls, List<String> fus, boolean is_log_file){
    	className = cn;
    	offset = o;
    	original_offset = o_ori;
        line_number_start = lis;
        line_number_end = lie;
        selected_length = len;
        selected_string = sct;
        keywords = output2InputKeyword(selected_string);
        desiredReturnType = drt;
        location = lo;
        classesInActiveEditor = new HashSet<String>(cls);
        functionsInActiveEditor = new HashSet<String>(fus); 
        isLogFile = is_log_file;
        //ファイル名は「パッケージ名＋クラス名/currentTimeMillis」
      	String savefilename = TestSiteFolder + className.replaceAll("[<>]", "___") + "/" + System.currentTimeMillis() + ".txt";
      	txtFile = new File(savefilename);
    }

	public String toString(){
    	String s = 
    	"className= " + className +
    	"\noffset= " + offset +
    	"\noriginal_offset= " + original_offset +
        "\nline_number_start= " + line_number_start +
        "\nline_number_end= " + line_number_end +
        "\nselected_length= " + selected_length +
        "\nselected_string= " + selected_string +
        "\nkeywords= " + keywords +
        "\ndesiredReturnType= " + desiredReturnType +
        "\nlocation= " + location +
        "\nrecieverString= " + recieverString +
        "\nlocalValString= " + localValString +
        "\nclassesInActiveEditor= " + classesInActiveEditor +
        "\nfunctionsInActiveEditor= " + functionsInActiveEditor;
    	return s;
    }
	
	private String methodListString(){
    	String mlist = "";
    	for(String m:methodList){
    		mlist += m + " ";
    	}
    	return mlist;
	}
	

    public String toDBString(){
    	String s =  className + "\n" + offset + "\n" + original_offset + "\n" +
    line_number_start + "\n" + line_number_end + "\n" +
    selected_length + "\n" + selected_string + "\n" +
    keywords + "\n" + desiredReturnType + "\n" +
    location + "\n" + recieverString + "\n" + localValString + "\n";
    	
    	String mlist = "";
    	for(String m:methodList){
    		mlist += m + " ";
    	}
    	s += mlist + "\n";
    	
    	//14行目types
        Iterator<String> it_c = getClassesInActiveEditor().iterator();
        while(it_c.hasNext()){
        	String ss = it_c.next();
        	s += ss + ";";
        }
        s += "\n";
        //15行以降 functions
        Iterator<String> it_f = getFunctionsInActiveEditor().iterator();
        while(it_f.hasNext()){
        	s += it_f.next() + "\n";
        }
        return s;
    }
    
//    
//    public String toDBString(){
//    	String s =  className + "\n" + offset + "\n" + original_offset + "\n" + line_number_start + "\n" + line_number_end + "\n" +
//    selected_length + "\n" + selected_string + "\n" + keywords + "\n" + desiredReturnType + "\n" + location + "\n";
//    	//11行目types
//        Iterator<String> it_c = classesInActiveEditor.iterator();
//        while(it_c.hasNext()){
//        	String ss = it_c.next();
//        	s += ss + ";";
//        }
//        s += "\n";
//        //12行以降 functions
//        Iterator<String> it_f = functionsInActiveEditor.iterator();
//        while(it_f.hasNext()){
//        	s += it_f.next() + "\n";
//        }
//        return s;
//    }
//    
    
    /*
     * パッケージ名を含む、クラス名。
     */
    public String getFullyQualifiedClassName(){
    	return className;
    }
    /*
     * パッケージ名を含む、クラス名。
     */
    public void setFullyQualifiedClassName(String n){
    	className = n;
    }
    public String getPackageName(){
    	int dot = className.lastIndexOf('.');
    	if(dot == -1)
    		return className;
    	return className.substring(0, dot);
    }
    public String getClassSingleName(){
    	int dot = className.lastIndexOf('.');
    	return className.substring(dot+1);
    }
	public int getOffset(){
    	return offset;
    }
    public void setOffset(int o){
    	offset = o;
    }
    public int getOriginalOffset(){
    	return original_offset;
    }
    public int getLineNumberStart(){
    	return line_number_start;
    }
    public void setLineNumberStart(int s){
    	line_number_start = s;
    }
    public int getLineNumberEnd(){
    	return line_number_end;
    }
    public void setLineNumberEnd(int e){
    	line_number_end = e;
    }
    public int getSelectedLength(){
    	return selected_length;
    }
    public void setSelectedLength(int l){
    	selected_length = l;
    }
    public String getSelectedString(){
    	return selected_string;
    }
    public void setSelectedString(String s){
    	selected_string = s;
    }
    public String getDesiredReturnType(){
    	return desiredReturnType;
    }
    public void setDesiredReturnType(String d){
    	desiredReturnType = d;
    }
    public String getLocation(){
    	return location;
    }
    public String getKeywords(){
    	return keywords;
    }
    public void setKeywords(String k){
    	keywords = k;
    }
    public int getSelectedStringOrder(){
    	return selected_string_order;
    }
    
    public List<FunctionTree> getOutputFunctionTrees(){
    	return outputfunctionTrees;
    }
    
    public long getSaveTime(){
    	String s = txtFile.getName().replace(".txt", "");
    	return Long.parseLong(s);
    }
    
    public void setOutputLogList(List<OutputCandidateLog> outputLogList){
      	this.outputLogList = outputLogList;
    }
	/*
	 * 出力文字列を入力キーワードに変換する
	 * 
	 * 引数outが
	 *  message.replaceAll(space, comma);
	 *  ならば、
	 *  返り値は
	 *   message replace all space comma
	 *  となる。
	 *  
	 *  ピリオド、カンマ、セミコロン、括弧は取り、空白文字に変換する。
	 *  message　replaceAll　space　 comma　　
	 *  
	 *  replaceAllの部分は、大文字の手前に空白文字を挿入してから、
	 *  大文字を小文字に変換する。＞AstUtil.splitNameを使う。
	 *  
	 */
	public String output2InputKeyword(String out){
		if(out == null)
			return null;
		//ピリオド、カンマ、セミコロン、括弧は取り、空白文字に変換する。
		//ダブルクオーテーションと<>も取る。
		String rep1 = out.replaceAll("[\\.\\,\\;\\(\\)\\<\\>\"]", " ");
		//大文字の手前に空白文字を挿入してから、大文字を小文字に変換する。
		String rep2 = ast.AstUtil.splitName(rep1, " ");
		//複数の空白文字が連結していた場合１つにする。
		String rep3 = rep2.replaceAll(" +", " ");
		return rep3.trim();
	}
	
	//keywordの数をカウントする。
	private int countKeywords(){
		
		if(keywords == null)
			return -1;
		else{
			//個々のkeywordに分割
			String []input_keywords = keywords.split("[ 　\t]");
			return input_keywords.length;
		}
	}
	
	public int getNumOfKeywords(){
		return countKeywords();
	}
	
	public List<String> getKeywordList(){
		List list = new ArrayList();
		//個々のkeywordに分割
		String [] input_keywords = keywords.split("[ 　\t]");
		for(String s: input_keywords){
			list.add(s);
		}
		return list;
	}
	
	public void setKeywordList(List<String> list){
		String ret = ""; 
				
		for(String s: list){
			ret += s +" ";
		}
		keywords_original = keywords;
		keywords = ret;
	}
	
	public void resetKeyword(){
		keywords = keywords_original;
	}
	
	public int getNumOfLocalFunctions(){
		if(functionsInActiveEditor == null)
			return 0;
		return functionsInActiveEditor.size();
	}
	
	public int getNumOfLocalTypes(){
		if(classesInActiveEditor == null)
			return 0;
		return classesInActiveEditor.size();
	}
	
	public void save(){
		try{
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
			pw.write(toDBString());
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * キーワードプログラミングのアルゴリズムを実行する。
	 * そして、
	 * 一番selected_stringに編集距離が近い出力文字列の順位を出力する。
	 * 
	 */
//	public int runKeywordProgrammingAndGetNearestOutputNumber(int output_size, String state){
//		//キーワードプログラミングの実行
//		FunctionTree[] ft = KeywordProgramming.execute(keywords, desiredReturnType,  new ArrayList<String>(classesInActiveEditor), new ArrayList<String>(functionsInActiveEditor), state);
//		outputfunctionTrees = (ArrayList<FunctionTree>) Arrays.asList(ft);
//		//一番selected_stringに編集距離が近い出力文字列の順位を得る。
//		return getNearestDistanceOutputNumber(output_size);
//	}

	/*
	 * 一番selected_stringに編集距離が近い出力文字列の順位を得る。
	 */
//	private int getNearestDistanceOutputNumber(int output_size) {
//		
//		int best_tree_order = -1;
//
////		String s = i + ", ";
//		String s = "";
//		for(double d : ExplanationVector.getWeights()){
//			s += d + ", ";
//		}
//		
//		int min_d = 99999;
//
//		if(outputfunctionTrees == null){
//			s += min_d + ", " + ", 回答なし ts == null";
////			System.out.println(i + ", 回答なし ts == null, "+ desiredReturnType);
//		}else{
//
//			FunctionTree best_tree = null;
//
//			//pop-upに表示された候補の中のうち、
//			//output_size 以内の中で、
//			//一番答えに近い距離の候補を探す
//			if(output_size > outputfunctionTrees.size())
//				output_size = outputfunctionTrees.size();//長さがoverしてたら、配列サイズにする。
//			for(int j = 0; j < output_size; j++){
//				FunctionTree t = outputfunctionTrees.get(j);
//				if(t != null){
//					
//					LevenshteinDistance ld = new LevenshteinDistance() ;
//					int d = ld.edit(t.toCompleteMethodString(), selected_string);
//					
//					if(d < min_d){
//						min_d = d;
//						best_tree = t;
//						best_tree_order = j;
//					}
////						System.out.println(j + ", eval=" + t.getEvaluationValue() + ", dist=" + d + ", " + t.toCompleteMethodString());
//				}
//			}
//
//			if(best_tree == null){
//				s += min_d + ", " + best_tree_order + ", " + ", 回答なし best_tree == null";
////				System.out.println(i + ", 回答なし best_tree == null, "+ desiredReturnType);
//			}else{
//				//System.out.println(i + ", " + min_d + ", " + best_tree.toCompleteMethodString());
//				s += min_d + ", " + best_tree_order + ", " + best_tree.toCompleteMethodString() + ", " + best_tree.getEvaluationValue();
////				System.out.println(s);
//				best_tree.setSelectedFlg();	//選んだフラグ。
//			}
//
//		}
//		System.out.println("=== " + selected_string + " に一番編集距離が近い文字列 ===");
//		System.out.println(s);
//		System.out.println("=== " + selected_string + " に一番編集距離が近い文字列 ===");
//		
//		return best_tree_order;
//	}


	/*
	 * キーワードプログラミングのアルゴリズムを実行する。
	 * そして、正解の順位を出力する。
	 * 正解が出なければ、
	 * -1を返す。
	 */
//	public int runKeywordProgrammingAndOutputNumber(int output_size, String state){
//		FunctionTree[] ft = KeywordProgramming.execute(keywords, desiredReturnType,  new ArrayList<String>(classesInActiveEditor), new ArrayList<String>(functionsInActiveEditor), state);
//		outputfunctionTrees = (ArrayList<FunctionTree>) Arrays.asList(ft);
//		//正解の順位を出力する。
//		return getAnswerNumber(output_size);
//	}

	//キーワードプログラミングの初期化
	public void initKeywordProgramming(){
		outputfunctionTrees = null;
	}
	
	//キーワードプログラミングの終了
	public void closeKeywordProgramming(){
		outputfunctionTrees = null;
	}
	
	/*
	 * キーワードプログラミングのアルゴリズムを実行し、
	 * 出力候補群を返す
	 */
	public List<FunctionTree> runKeywordProgramming(Params para, String state){
		KeywordProgramming kp = new KeywordProgramming(para);
		kp.clearStaticFields(false);
		
		//キーワード分割する場合。
		String flg_separate = para.separate_keywords;
		
		List<Word> word_list = new ArrayList<Word>();
		
		if(flg_separate.equals("true")){
			word_list = setSeparateKeywords();
		}else{
			word_list = setInputKeywords();
		}
		
		//キーワード改変
		String flg_short = para.shortened_input_keywords;
		
		if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL)){
			word_list = setConsonantKeywords(word_list);
		}else if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3)){
			word_list = setFirst3Keywords(word_list);
		}else if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE)){
			word_list = setReplacedKeywords(word_list);
		}else if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD)){
			word_list = setInsertedKeywords(word_list);
		}
				
		//キーワードプログラミングの実行
		FunctionTree[] ft =  kp.execute(para, word_list, desiredReturnType,  new ArrayList<String>(classesInActiveEditor), new ArrayList<String>(functionsInActiveEditor), state);
		outputfunctionTrees = Arrays.asList(ft);
		return outputfunctionTrees;
	}
	
	/*
	 * 何も改変しないでそのままのキーワード
	 */
	public List<Word> setInputKeywords(){
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(String k:separateKeyword(keywords)){
			Word word = new Word(k);
			word.setType(word.NORMAL);
			newKeywords.add(word);
		}
		return newKeywords;
	}
	
	/*
	 * キーワード分割 
	 */
	public List<Word> setSeparateKeywords(){
		
		//メソッドのラベルから、単語単位に分割する。
		ArrayList<String> methodKeyword = new ArrayList<String>();
		
		for(String s: methodList){
			
			String ss = output2InputKeyword(s);
			
			for(String sss: separateKeyword(ss))
				methodKeyword.add(sss);
		}
		
		//メソッドの単語の数だけフラグを作成。
		ArrayList<Boolean> flg = new ArrayList<Boolean>();
		for(int i = 0; i < methodKeyword.size(); i++){
			flg.add(false);
		}
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(String k:separateKeyword(keywords)){
			//メソッドの単語かどうかを確認
			boolean isMethod = false;
			for(int i = 0; i < methodKeyword.size(); i++){
				String s = methodKeyword.get(i);
				if(flg.get(i) == false && k.equals(s)){
					isMethod = true;
					flg.set(i, true);
					break;
				}
			}
			Word word = new Word(k);
			if(isMethod){
				word.setType(Word.METHOD_CONSTRUCTOR);
			}else{
				word.setType(word.VARIABLE);
			}
			newKeywords.add(word);
		}
		
		return newKeywords;
		
	}

	/*
	 * キーワードを全て3文字以下にする。
	 */
	public List<Word> setFirst3Keywords(List<Word> keywords){
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			Word new_word = new Word(word.getWord());
			new_word.setType(word.getType());
						
			//3文字より大きければ、3文字にする。
			if(new_word.getWord().length() > 3){
				new_word.setWord(new_word.getWord().substring(0,3));
			}
			
			newKeywords.add(new_word);
		}
						
		return newKeywords;
	}
	
	/*
	 * 子音だけ残して、先頭以外の母音削除する。文字数制限は無し。
	 */
	public List<Word> setConsonantKeywords(List<Word> keywords){
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			Word new_word = new Word(removeVowels(word.getWord()));
			new_word.setType(word.getType());
			newKeywords.add(new_word);
		}
	
		return newKeywords;
		
	}
	
	/*
	 * キーワードをランダムに一文字置換する。
	 */
	
	public List<Word> setReplacedKeywords(List<Word> keywords){
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			Word new_word = new Word(ReplaceWord(word.getWord()));
			new_word.setType(word.getType());
			newKeywords.add(new_word);
		}
	
		return newKeywords;
		
	}
	
	/*
	 * キーワードをランダムに一文字挿入する。
	 */
	
	public List<Word> setInsertedKeywords(List<Word> keywords){
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			Word new_word = new Word(InsertWord(word.getWord()));
			new_word.setType(word.getType());
			newKeywords.add(new_word);
		}
	
		return newKeywords;
		
	}
	
	/*
	 * ランダムに一文字挿入する。
	 * 
	 */
	public String InsertWord(String word){
		
		//挿入する文字の位置をランダムに選ぶ
		//挿入できる位置は、word.length+1 存在するので、
		//0~word.lengthまでの乱数生成 (0 <= Math.random() < 1)
		// *word.length()+1 とすると、ちょうどword.length()+1は取得できない。
		// int にキャストでword.length()以上の値は切り捨てられる。
		int place = (int)(Math.random()*word.length()+1);
        
		//挿入する文字(アルファベッド)をランダムに選ぶ
		//0~25 までの乱数生成
		int ran = (int)(Math.random()* 26);
		int a;
		char c;//置き換える文字

		//A～Zのアルファベットを文字コードに直すと６５～９０の値になる
		//６５を足して６５～９０にする
        a = 'a' + ran;
        //charに型変換
        c = (char)a;

		StringBuilder sb = new StringBuilder();
		sb.append(word);
		sb.insert(place, c);
		return sb.toString();
	}
	
	/*
	 * ランダムに一文字置換する。
	 * 
	 */
	public String ReplaceWord(String word){
		
		//置換する文字をランダムに選ぶ
		//0~word.length-1 までの乱数生成
		int place = (int)(Math.random()*word.length());
        
		char pchar = word.charAt(place);
			
		int ran;
		int a;
		char c;//置き換える文字
		
		//以前と同じ文字なら、もう一度乱数を引く。
		while(true){
			//置換する文字(アルファベッド)をランダムに選ぶ
			//0~25 までの乱数生成
			ran = (int)(Math.random()* 26);
			//A～Zのアルファベットを文字コードに直すと６５～９０の値になる
			//６５を足して６５～９０にする
	        a = 'a' + ran;
	        //charに型変換
	        c = (char)a;
	        
	        //違う文字になったら脱出。
			if(pchar != c){
				break;
			}
		}
        
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		sb.setCharAt(place, c);
		return sb.toString();
	}
	
	/*
	 * 母音を削除する。
	 * (先頭文字は除く。)
	 * 
	 */
	public String removeVowels(String word){
		String c_word = word.substring(1);
		
		c_word = c_word.replace("a", "");
		c_word = c_word.replace("i", "");
		c_word = c_word.replace("u", "");
		c_word = c_word.replace("e", "");
		c_word = c_word.replace("o", "");
		
		return word.substring(0,1) + c_word;
	}
	
	public boolean isConsonant(char s){
		if(s =='a' || s =='i' || s =='u' || s =='e' || s =='o' ){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean isConsonant(String s){
		if(s.equals("a") || s.equals("i") || s.equals("u") || s.equals("e") || s.equals("o")){
			return false;
		}else{
			return true;
		}
	}
	
	/*
	 * キーワードを空白で分割し、リストに挿入する
	 */
	public List<String> separateKeyword(String keyword){
		//文字列を小文字化する
		String s_lowerCase = keyword.toLowerCase();
		//keywordに分割
		List<String> input_keywords = Arrays.asList(s_lowerCase.split("[ 　\t]"));
		return input_keywords;
	}
	
	/*
	 * 評価値の再計算
	 */
	public void reCalculateEvaluationValue(){
		if(isLogFile == false){
			for(FunctionTree ft: outputfunctionTrees){
				ft.getEvaluationValue();
			}
		}else{
			for(OutputCandidateLog log: outputLogList){
				log.calculateEvaluationValue();
			}
		}
	}

	/*
	 * 出力候補のソート
	 */
	public void sortFunctionTrees(){
		if(isLogFile == false){
			Collections.sort(outputfunctionTrees);
		}else{
			Collections.sort(outputLogList, new OutputCanditateLogComparator());
		}
	}
	
	/*
	 * 正解の順位を求める。
	 * 順位は0番から。
	 * 
	 * 正解が出なければ、-1を返す。
	 */
	public int getAnswerNumber(Params para, int output_size) {
		
		int best_tree_order = -1;

//		String s = i + ", ";
		String s = "";
		for(double d : para.w_arr){
			s += d + ", ";
		}
		
		if(isLogFile == false){
			//バッチ処理のとき。
			if(outputfunctionTrees == null){
				s += "回答なし ts == null";
			}else{
	
				FunctionTree best_tree = null;
	
				//pop-upに表示された候補の中に正解があるか調べる。
				if(output_size > outputfunctionTrees.size())
					output_size = outputfunctionTrees.size();//長さがoverしてたら、配列サイズにする。
				for(int j = 0; j < output_size; j++){
					FunctionTree t = outputfunctionTrees.get(j);
					if(t != null){
						if(t.toCompleteMethodString().equals(selected_string.trim())){
							best_tree = t;
							best_tree_order = j;
							break;
						}
					}
				}
	
				if(best_tree == null){
					s += best_tree_order + ", 回答なし best_tree == null";
				}else{
					s += best_tree_order + ", " + best_tree.toCompleteMethodString() + ", " + best_tree.getEvaluationValue();
					best_tree.setSelectedFlg();	//選んだフラグ。
				}
	
			}
	//		System.out.println("=== " + selected_string + " が出現した順位 ===");
	//		System.out.println(s);
	//		System.out.println("=== " + selected_string + " が出現した順位 ===");
		}else{
			//オンライン処理のとき。
			if(outputLogList == null){
				s += "回答なし ts == null";
			}else{
	
				OutputCandidateLog best_log = null;
	
				//pop-upに表示された候補の中に正解があるか調べる。
				if(output_size > outputLogList.size())
					output_size = outputLogList.size();//長さがoverしてたら、配列サイズにする。
				for(int j = 0; j < output_size; j++){
					OutputCandidateLog log = outputLogList.get(j);
					if(log != null){
						if(log.getCompleteMethodString().equals(selected_string.trim())){
							best_log = log;
							best_tree_order = j;
							break;
						}
					}
				}
	
				if(best_log == null){
					s += best_tree_order + ", 回答なし best_tree == null";
				}else{
					s += best_tree_order + ", " + best_log.getCompleteMethodString() + ", " + best_log.getEvaluationValue();
				}
	
			}
		}
		return best_tree_order;
	}

	public boolean isSelectedTask() {
		return isSelectedTask;
	}

	public void setSelectedTask(boolean isSelectedTask) {
		this.isSelectedTask = isSelectedTask;
	}
	
	/*
	 * テストサイトのファイルを新規作成
	 */
	public void createNewFile() {
		//フォルダがなければ作成する。
		File dir = txtFile.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			txtFile.createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
			pw.write(this.toDBString());
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/*
	 * テストサイトのファイルを新規作成
	 */
	public void copyAndMoveFileToTestSiteFolder() {
		//ファイルがなければ
		if (!txtFile.exists()) {  
		    return;
		}
		String savefilename = TestSiteFolder + className.replaceAll("[<>]", "___") + "/" + txtFile.getName();
      	File newFile = new File(savefilename);
      	
		//フォルダがなければ作成する。
		File dir = newFile.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		//ファイルをコピーする。
		try{
			copyFile(txtFile, newFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/*
	 * テストサイトを削除する
	 */
	public void deleteFile() {
		//ファイルがなければ
		if (!txtFile.exists()) {  
		    return;
		}
		txtFile.delete();
	}
	
	/*
	 * 履歴としてテストサイトのファイルを新規作成
	 * 
	 * 保存する場所が違うだけ。
	 */
	public void createNewFileAsLog(String selected_string, int selected_length, int selected_string_order, String keywords) {
		this.selected_string = selected_string;
		this.selected_length = selected_length;
		this.selected_string_order = selected_string_order;
		this.keywords = keywords;
		
		//ファイル名は「パッケージ名＋クラス名/currentTimeMillis」
		long millis = System.currentTimeMillis();
      	String savefilename = LogSiteFolder + className + "/" + millis + ".txt";
      	//出力文字列も保存。
      	String savefilename_o = LogSiteFolder + className + "/" + millis + "out.txt";
      	File  file = new File(savefilename);
      	File  file_o = new File(savefilename_o);
		//フォルダがなければ作成する。
		File dir = file.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			file.createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.write(this.toDBString());
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			file_o.createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file_o)));
			pw.write(selected_string_order + "\n");	//先頭行は選択した候補の順位。
			for(FunctionTree ft : outputfunctionTrees){
				pw.write(ft.toLogDBString());
			} 
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * ファイル保存日時を取得
	 */
	public String getSaveDate(){
		if(txtFile == null)
			return null;
		else{
			Date date = new Date(getSaveTime());
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			return df.format(date);
		}
	}
	
	//ファイルをコピーする。
	 private void copyFile(File in, File out) throws IOException {
	    FileChannel sourceChannel = new FileInputStream(in).getChannel();
	    FileChannel destinationChannel = new FileOutputStream(out).getChannel();
	    sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
	    sourceChannel.close();
	    destinationChannel.close();
	 }
	 
	 /*
	  * このTestSiteのIDを返す。
	  */
	 public String getId(){
		 return String.valueOf(getSaveTime());
	 }
	 
	 /*
      * 入力キーワードが（タスク＋DB）中の関数のラベルに全て含まれるかを調べる関数
      */
//     public boolean verifyLabel(){
//         List<Function> fList = new ArrayList<Function>(); 
//         //DBのf追加
//         fList.addAll(KeywordProgramming.getFunctions());
//        //タスク中のf追加
//         Iterator<String> iter = functionsInActiveEditor.iterator();
//         while(iter.hasNext()){
//             String func = iter.next();
//             fList.add(new Function(func));
//         }
//
//        //keywordに分割
//         List<String> kList = Arrays.asList(keywords.split("[ 　\t]", -1));
//         for(String key: kList){
//             boolean flg = false;
//             for(Function func: fList){
//                 for(String label: func.getLabels()){
//                     if(key.equals(label)){
//                         flg = true;
//                     }
//                 }
//             }
//             //あるキーワードについて、それがラベルに出現しなかった。
//             if(flg == false){
//                 return false;
//             }
//         }
//         //全てのキーワードがラベルに出現した。
//         return true;
//     }
     
     
	 /*
	  * 選択文字列
      * selected_stringを関数ごとにバラバラにして、
      * それぞれの関数名が、ちゃんとタスク中の関数名にあるかを確かめる関数。
      * 
      */
//     public String verifyLabel2(){
//   
//         List<Function> fList = new ArrayList<Function>(); 
//         //DBのf追加
//         fList.addAll(KeywordProgramming.getFunctions());
//        //タスク中のf追加
//         Iterator<String> iter = functionsInActiveEditor.iterator();
//         while(iter.hasNext()){
//             String func = iter.next();
//             if(!func.equals("")){
//            	 //System.out.println(getId());
//            	 fList.add(new Function(func));
//             }
//         }
//         
//         //クラス名(パッケージ名つかないsimple name)
//         List<String> tList = new ArrayList<String>(); 
//
//         Iterator<String> iter2 = classesInActiveEditor.iterator();
//         while(iter2.hasNext()){
//        	 String t = iter2.next();
////        	 System.out.println("t: " + t);
//        	 String t_arr[] = t.split(",", -1);
////        	 System.out.println("tarr[]: " + t_arr);
//        	 for(String tt: t_arr){
//        		 if(!tt.equals("")){
//		        	 Type type = new Type(tt);
//		        	 tList.add(type.getSimpleName());
//        		 }
//        	 }
////        	 System.out.println(type.getSimpleName());
//         }
//         
//         TreeMap<String, Type> tMap = new TreeMap<String, Type>(KeywordProgramming.getTypes());
//         
//         Iterator<String> iter3 = tMap.keySet().iterator();
//         while(iter3.hasNext()){
//        	 String t = iter3.next();
//        	 Type type = new Type(t);
//        	 tList.add(type.getSimpleName());
//        	 //System.out.println(type.getSimpleName());
//         }
//         
//         /*
//          * まず、selected_stringを関数ごとにバラバラにするのが
//          * できるのか？
//          * ()と.と,で分割すればよいか？
//          */
//       
//    	 List<String> kList = Arrays.asList(selected_string.split("[,\\.\\(\\)]", -1));
//    	
//         for(String key: kList){
//        	 if(!key.equals("")){
//	             boolean flg = false;
//	             String keyName = key;
//	             //keyがコンストラクタ (newの次にスペース)
//	             //new を外す。
//            	 if(key.startsWith("new ")){
//            		 keyName = key.substring(4); 
//            	 }
//	             for(Function func: fList){
//	            	 String funcName = func.getName();
//	            	 
//	                 if(keyName.equals(funcName)){
//	                     flg = true;
//	                 }
//	             }
//	             //ある関数名について、それがタスク中の関数名に出現しなかった。
//	             if(flg == false){
//	            	 /*
//	            	  * その関数名が大文字から始まるなら、
//	            	  * static呼び出しのときのクラス名かもしれない。
//	            	  */
//	            	 if(Character.isUpperCase(key.charAt(0))){
//	            		 //System.out.println(key);
//	            		 if(tList.contains(key))
//	            			 return "static";
//	            		 else
//	            			 return "static?";
//	            	 }
//	                 return "false";
//	             }
//        	 }
//         }
//         //全ての関数名がタスク中の関数名に出現した。
//         return "true";
//     }
     
	 /*
	  * 選択文字列selected_string
	  * を構成できるような関数がタスク中に存在するかを確かめるメソッド。
      * 
      * すでにverify2を終えているものとする。
      * 
      * まず、selected_stringに含まれる全ての関数名を取得。
      * 
      * 次に、
      * その関数名と一致する関数名を持つ関数を、
      * タスク中から全て取得。
      * 
      * 最後に、selected_stringと同じ木の形になるように、
      * 関数を組み合わせて木を作る。
      * 
      * 一致する木が存在すればtrue
      * そうでなければfalse
      * を返す。
      */
//     public boolean verifyFunctions(){
//   
//         List<Function> fList = new ArrayList<Function>(); 
//         //DBのf追加
//         fList.addAll(KeywordProgramming.getFunctions());
//        //タスク中のf追加
//         Iterator<String> iter = functionsInActiveEditor.iterator();
//         while(iter.hasNext()){
//             String func = iter.next();
//             if(!func.equals(""))
//            	 fList.add(new Function(func));
//         }
//         
//         //クラス名(パッケージ名つかないsimple name)
//         List<String> tList = new ArrayList<String>(); 
//
//         Iterator<String> iter2 = classesInActiveEditor.iterator();
//         while(iter2.hasNext()){
//        	 String t = iter2.next();
//        	 String t_arr[] = t.split(",", -1);
//        	 for(String tt: t_arr){
//        		 if(!tt.equals("")){
//		        	 Type type = new Type(tt);
//		        	 tList.add(type.getSimpleName());
//        		 }
//        	 }
//         }
//         
//         TreeMap<String, Type> tMap = new TreeMap<String, Type>(KeywordProgramming.getTypes());
//         
//         Iterator<String> iter3 = tMap.keySet().iterator();
//         while(iter3.hasNext()){
//        	 String t = iter3.next();
//        	 Type type = new Type(t);
//        	 tList.add(type.getSimpleName());
//         }
//         
//         /*
//          * まず、selected_stringを関数ごとにバラバラにするのが
//          * できるのか？
//          * ()と.と,で分割すればよいか？
//          */
//       
//    	 List<String> kList = Arrays.asList(selected_string.split("[　,\\.\\(\\)]", -1));
//    	 List<String> kList2 = new ArrayList<String>();
//    	 for(String k: kList){
//    		 kList2.add(k);
//    	 }
//    	 for(int i = 0; i < kList.size(); i++){
//    		 if(!kList.get(i).equals("")){
//	             //keyがコンストラクタ (newの次にスペース)
//	             //new を外す。
//            	 if(kList.get(i).startsWith("new ")){
//            		 kList2.add(i, kList.get(i).substring(4)); 
//            	 }
//    		 }
//    	 }
//    	 
//    	 HashMap<Integer, ArrayList<Function>> fName_function_map = new HashMap<Integer, ArrayList<Function>>();
//    	 int key_num = 0;
//         for(String key: kList2){
//        	 if(!key.equals("")){
//	             String keyName = key;
//	             //keyがコンストラクタ (newの次にスペース)
//	             //new を外す。
//            	 if(key.startsWith("new ")){
//            		 keyName = key.substring(4); 
//            	 }
//            	 
//            	 ArrayList<Function> funcList = new ArrayList<Function>();
//            	 
//	             for(Function func: fList){
//	            	 String funcName = func.getName();
//	                 if(keyName.equals(funcName)){
//	                	 funcList.add(func);
//	                 }
//	             }
//	             //keyNameと名前が一致する関数がなければ、組み合わせ時にfalseと判明するので問題ない。
//	             
//	             fName_function_map.put(key_num, funcList);
//	             key_num++;
//        	 }
//         }
//         
//	         //関数が１つのとき
//	         if(key_num == 1){
//	        	 try{
//		        	 ArrayList<Function> list = fName_function_map.get(0);
//		        	 if(list.size() > 0 && list.get(0).getName().equals(kList2.get(0)))
//		        		 return true;
//	        	 }catch(Exception e){
//	            	 e.printStackTrace();
//	             }
//	        	 return false;
//	         }
//         
//       //関数の組み合わせの順列を全て求める。
// 		Combinations pc = new Combinations();
// 		ArrayList<ArrayList<Function>> allCombinations = pc.getAllCombinations(fName_function_map);
// 		
// 		for(int i = 0; i < allCombinations.size(); i++){
//			ArrayList<Function> comb = allCombinations.get(i);
//			for(int j=0; j< comb.size(); j++){
//				Function f1 = comb.get(j);
//				if(f1.getParameters() != null)
//				for(String p:f1.getParameters()){
//					boolean flg = false;
//					for(int k=0; k < comb.size(); k++){
//						if(j != k){
//							Function f2 = comb.get(k);
//							if(p.equals(f2.getReturnType())){
//								flg = true;
//							}
//						}
//					}
//					if(!flg)
//						return false;
//				}
//			}
// 		}
//         return true;
//     }
     
	 
	 /*
      * 
      */
//     private List<String> getLocalFunctionNameList(){
//         List<Function> fList = new ArrayList<Function>(); 
//         List<String> listOfLocalFunctionName = new ArrayList<String>();
//         
//         //DBのf追加
//         fList.addAll(KeywordProgramming.getFunctions());
//        //タスク中のf追加
//         //ローカル関数の名前を取得。
//         Iterator<String> iter = functionsInActiveEditor.iterator();
//         while(iter.hasNext()){
//             String func = iter.next();
//             Function f = new Function(func);
//             fList.add(f);
//             if(f.getFunctionType().equals("localvariable")){
//        		 listOfLocalFunctionName.add(f.getName());
//        	 }
//         }
//
//         return listOfLocalFunctionName;
//     }
     
//     public List<String> getLocalFunctionNameKeywords(){
//    	 List<String> list = new ArrayList<String>();
//    			 
//    	 for(String keyword: this.getKeywordList()){
//    		 for(String localFname: this.getLocalFunctionNameList()){
//    			 if(keyword.equals(localFname)){
//    				 list.add(keyword);
//    			 }
//    		 }
//    		 
//    	 }
//    	 return list;
//     }
     
     /*
      * タスクの正解文字列中の、
      * どのキーワードがレシーバ、ローカル変数なのかを取得する。
      * 
      * 
      */
     
     private ASTNode node;
     private boolean flgGetReciever = false;	//
     private Expression reciever;
     private List<SimpleName> simpleNameList = new ArrayList<SimpleName>();
     
     private List<String> functions = new ArrayList<String>();
     
     /*
      * 正解コード片を表す
      * ASTNode node
      * を入力して、
      * recieverString
      * と、
      * localValString
      * を得る。
      */
     public void setASTNode(ASTNode node){
    	 this.node = node;
    	 searchAST(getNodeArguments(node));
    	 
    	 if(reciever != null){
    		 this.recieverString = (reciever.getStartPosition() - this.offset) + "," + reciever.toString();
    		 this.recieverString = this.recieverString.trim();
    	 }
    	 
    	 this.localValString = "";
    	 for(SimpleName sn: simpleNameList){
    		 this.localValString += (sn.getStartPosition() - this.offset) + "," + sn.toString() + ";";
    	 }
    	 this.localValString = this.localValString.trim();
     }
     
     
     /*
      *  ASTNode の引数を取得する。
      *  レシーバがあれば、レシーバも取る。
      *  
      *  node がMethodInvocation, ClassInstanceCreation
      *  の両方に対応。
      */
     private List<Expression> getNodeArguments(ASTNode node) {

 		List<Expression> list = new ArrayList<Expression>();
 		if(node instanceof MethodInvocation){
 			MethodInvocation current = (MethodInvocation)node;
 			
 			methodList.add(current.getName().toString());
 			
 			for(Object e: current.arguments()){
 				if(e instanceof Expression){
 					list.add((Expression)e);
 				}
 			}
 			
 			//System.out.println(current.resolveMethodBinding().getDeclaringClass().getBinaryName());
 			String parentClass = current.resolveMethodBinding().getDeclaringClass().getBinaryName();
 	
 			functions.addAll(AstUtil.iMethodBindingToDbStringStatic1(current.resolveMethodBinding(), parentClass, false, false, false));
 			
 			
 			reciever = current.getExpression();
 			if(reciever != null)
			if(reciever.getNodeType() == ASTNode.FIELD_ACCESS)
				System.out.println("FIELD_ACCESS");

 			//レシーバと引数が交わらないように削除。
// 			if(flgGetReciever){
// 				if(reciever instanceof SimpleName
// 					&& Character.isLowerCase(((SimpleName) reciever).getFullyQualifiedName().charAt(0)))	
// 				list.add(current.getExpression());
// 			}
 		}else if(node instanceof ClassInstanceCreation){
 			ClassInstanceCreation current = (ClassInstanceCreation)node;
 			
 			String parentClass = current.resolveConstructorBinding().getDeclaringClass().getBinaryName();
 			methodList.add(AstUtil.getSimpleClassName(parentClass));
 			
 			for(Object e: current.arguments()){
 				if(e instanceof Expression){
 					list.add((Expression)e);
 				}
 			}
 			
 			functions.addAll(AstUtil.iMethodBindingToDbStringStatic1(current.resolveConstructorBinding(), parentClass, false, false, false));
 			
 		}
 		
 		return list;
 	}
 	
     /*
      * ASTNode を再帰的に探索して、
      * 引数を取得し、そのリストを返す。
      */
     private void searchAST(List<Expression> list){
 		if(list == null)
 			return;
 		if(list.size() == 0)
 			return;
 		for(Expression e: list){
 			if(e == null)
 				continue;
 			
// 			System.out.println(e.toString());
 			if(e instanceof SimpleName){
 				simpleNameList.add((SimpleName)e);
 			}else{
 				searchAST(getNodeArguments(e));
 			}
 		}
 		return;
 	}
 	
 	public void printSimpleNameList(){
 		for(SimpleName s: simpleNameList){
 			System.out.println(s.toString());
 		}
 		return;
 	}
 	
 	public void printFunctionList(){
 		for(String s: functions){
 			System.out.println(s);
 		}
 		return;
 	}
 	
 	/*
 	 * 選択範囲に含まれる関数が、指定回数回の頻度で出現する関数かどうかを調べる 
 	 */
// 	public boolean isSelectedFunctionWithXtimesFrequency(int x){
// 		for(String s: functions){
// 			if(KeywordProgramming.IsFunctionWithXtimesFrequency(s, x))
// 				return true;
// 		}
// 		return false;
// 	}
 	
	
 	/*
 	 * このTestSiteにある関数が、指定回数回の頻度で出現する関数かどうかを調べる 
 	 */
// 	public boolean isFunctionWithXtimesFrequency(int x){
// 		Iterator<String> iter = functionsInActiveEditor.iterator();
//        while(iter.hasNext()){
//            String func = iter.next();
//            if(KeywordProgramming.IsFunctionWithXtimesFrequency(func, x))
// 				return true;
//        }
// 		return false;
// 	}
 	
 	public void openTextFile(){
 	    try{
 	       FileReader filereader = new FileReader(txtFile);

 	       int ch;
 	       while((ch = filereader.read()) != -1){
 	         System.out.print((char)ch);
 	       }

 	       filereader.close();
 	     }catch(FileNotFoundException e){
 	       System.out.println(e);
 	     }catch(IOException e){
 	       System.out.println(e);
 	     }
 	}
}
