package termFreqCount;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import keywordProgramming.Function;
import keywordProgramming.Type;


public class MethodNameTermCount {
	
	static final String SUB_CLASS_FILE_NAME = "sub_class.txt";
	
	//static final String FUNCTION_FILE_NAME = "function.txt";
	//static final String FUNCTION_FILE_NAME = "javaio/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javalang/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javautil/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javabeans/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javalangref/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javalangreflect/kypg_mdl_func.txt";
	static final String FUNCTION_FILE_NAME = "javaxxml/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "coreApi/coreApi_func.txt";
	
	static ArrayList<Type> types = new ArrayList<Type>();
	static ArrayList<Function> functions = new ArrayList<Function>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args){
		
		//MethodNameTermCountForSimpleTxt();
	}

//
//	public static void main(String[] args){
//		
//		String a = "inputURLSafeIOmodeURL";
//		String b = splitName2(a, ", ");
//		System.out.println(b);
//		/*
//		 * 例が悪いのか
//		 * IOmodeだと
//		 * I, O, mode
//		 * に分かれてしまうが、
//		 * URLSafeだと、
//		 * URL, Safe
//		 * と上手くいく、
//		 * 
//		 * IOMode
//		 * となるかならないか
//		 * それはわからない。
//		 * 実物を見てみナイトね。
//		 * 
//		 */
//	}
	
	/*
	 * MethodNameGetter.GetMethodName2 で創った
	 * メソッド名の羅列だけが書かれたm_name_output.txtファイルを読み込む 
	 */
	
	public static void MethodNameTermCountForSimpleTxt(String project_name){
		ArrayList<String> list = new ArrayList<String>();

		//ファイルを読み込んで、リストに格納
		readFileAndAddList(MethodNameGetter.filename, list);
		//printClassList(classes);
		//printFunctionsList(functions);
		/*
		for(Function f: functions){
			System.out.println(f.toString());
		}
		*/
        HashMap<String, Integer> termMap = new HashMap<String, Integer>();

       
    	for(String l: list){
            // 単語の出現回数を1増やす
            int count = 1;
            if(termMap.containsKey(l)){
                count += termMap.get(l);
            }
            termMap.put(l, count);
		}
        
        
        //System.out.println("=========================================================");
        
        // 比較のためのクラスを指定して TreeMap を生成
    	TreeMap treeMap = new TreeMap(new IntegerMapComparator(termMap));
    	// TreeMap に全部の組をコピー(このときにソートされる)
    	treeMap.putAll(termMap);
/*
        // 単語数の表示 (ソート無し）
        Iterator<String> iterator = termMap.keySet().iterator();
		while(iterator.hasNext()) {
		    String key = iterator.next();
		    Integer value = termMap.get(key);
		    System.out.println(key + ": " + value);
		}
*/
    	
//    	File outfile = new File("/home/sayuu/source/出力結果/term/term_" + project_name + ".txt");
    	File outfile = new File("C://Users//sayuu//Desktop//" + project_name + ".txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			// TreeMap の表示　// 単語数の表示 (ソート有り）
			Set termSet = treeMap.keySet();  // ソートされている
			Iterator iterator1 = termSet.iterator();
			while(iterator1.hasNext()) {
			    String key = (String)iterator1.next();
			    Integer value = (Integer)treeMap.get(key);
			    if(key.length() > 1 && !key.endsWith("_")){//変な文字を出さない。
			    	//System.out.println(key + ": " + value);
			    	pw.println(key + ": " + value);
			    }
			}
			pw.close();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
	    	e1.printStackTrace();
		}

	}
	
	/*
	 * KP用function.txtを読み込む 
	 */

	public static void MethodNameTermCountForKPFunctionTxt(){
		/*
		 * function名、つまりlabelを構成する単語の
		 * 出現頻度を調べる。2011/07/10
		 * 
		 */
		//ファイルを読み込んで、リストに格納
		readClassFileAndAddList(MethodNameTermCount.SUB_CLASS_FILE_NAME, types);
		readFunctionFileAndAddList(MethodNameTermCount.FUNCTION_FILE_NAME, functions);
		//printClassList(classes);
		//printFunctionsList(functions);
		/*
		for(Function f: functions){
			System.out.println(f.toString());
		}
		*/
        HashMap<String, Integer> termMap = new HashMap<String, Integer>();

        for(Function f: functions){
        	for(String l:f.getLabels()){
                // 単語の出現回数を1増やす
                int count = 1;
                if(termMap.containsKey(l)){
                    count += termMap.get(l);
                }
                termMap.put(l, count);
    		}
            
        }
        
        //System.out.println("=========================================================");
        
        // 比較のためのクラスを指定して TreeMap を生成
    	TreeMap treeMap = new TreeMap(new IntegerMapComparator(termMap));
    	// TreeMap に全部の組をコピー(このときにソートされる)
    	treeMap.putAll(termMap);
/*
        // 単語数の表示 (ソート無し）
        Iterator<String> iterator = termMap.keySet().iterator();
		while(iterator.hasNext()) {
		    String key = iterator.next();
		    Integer value = termMap.get(key);
		    System.out.println(key + ": " + value);
		}
*/
		// TreeMap の表示　// 単語数の表示 (ソート有り）
		Set termSet = treeMap.keySet();  // ソートされている
		Iterator iterator1 = termSet.iterator();
		while(iterator1.hasNext()) {
		    String key = (String)iterator1.next();
		    Integer value = (Integer)treeMap.get(key);
		    if(key.length() > 1 && !key.endsWith("_")){//変な文字を出さない。
		    	System.out.println(key + ": " + value);
		    }
		}

	}

	//Classesファイルを読み込んで、リストに格納
	public static void readClassFileAndAddList(String input_txt_file_name, ArrayList<Type> types){
		FileReader fr;

		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				types.add(new Type(s));
			}
			br.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	//Functionsファイルを読み込んで、リストに格納
	public static void readFunctionFileAndAddList(String input_txt_file_name, ArrayList<Function> functions){
		FileReader fr;
		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				functions.add(new Function(s));
			}
			br.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
	
	//Functionsファイルを読み込んで、リストに格納
	public static void readFileAndAddList(String input_txt_file_name, ArrayList<String> methodNames){
		FileReader fr;
		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				methodNames.add(s);
			}
			br.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
}