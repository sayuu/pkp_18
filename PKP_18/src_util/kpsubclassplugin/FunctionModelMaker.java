package kpsubclassplugin;
/**
 * 09/07/12(日)
*モデルで、メソッドとフィールドの部分、
レシーバを書くのを忘れていたので追加。ModelMaker

*配列はそのままでも大丈夫なのかな？まあほっとこう。

*入れ子クラスとりあえず無視。

* $1 $2 って一体なんなんだ？ 無視。
クラス名に$が付いているものは除外することにする。

* 修正した結果、ioとlangだけで、
全Function 数：4539
全Class 数：193
となった。

*Classオンリーの表も作らなくちゃ。
作成。
ModelMakerだけはしっかりできているけど、
他のClassには反映されていません。
注意。というか使わないんだったら消してもいいんだけれども。
間違わないように。

*classes.txt functions.txt の2つを作成できるようになった。

 * 09/07/02（木）
 *
 * 注意：
 * public static String splitName(String name,  String with)
 * において、
 * IO, URL, UTFなどもすべてバラバラに分割されてしまう！
 * まあいいやあとで治そう。
 * 一文字ずつバラバラに分割されてしまうModelもあるが、
 * 大部分はフィールドのようである。
 * パスしてしまおう。
 *
 * とりあえず
 * Modelはできた、ということで。
 *
*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;


public class FunctionModelMaker {
	//付録より、『核になるAPI』のみ。
	static final String [] packages = {
//		"java.beans",
		"java.lang",
//		"java.lang.ref",
//		"java.lang.reflect",
//		"java.math",
//		"java.net",
//		"javax.net", //jsse.jarの中
//		"java.io",
//		"java.nio",
//		"java.text",
//		"java.util",
//		"java.util.jar",
//		"java.util.logging",
//		"java.util.prefs",
//		"java.util.regex",
//		"java.util.zip",
//		"javax.crypto",
//		"java.security",
//		"javax.security",
//		"javax.xml",
//		"org.w3c.dom",
//		"org.xml.sax",
	};

	//基本データ型
	static final String[] basic_type = {
		"void",
		"boolean",
		"char",
		"byte",
		"short",
		"int",
		"long",
		"float",
		"double",
	};
	
	static final String[][] basic_type_simbols = {
		{"boolean$", "java.lang.Boolean"},
		{"byte$", "java.lang.Byte"      },
		{"char$", "java.lang.Character" },
		{"double$", "java.lang.Double"  },
		{"float$", "java.lang.Float"    },
		{"int$", "java.lang.Integer"    },
		{"long$", "java.lang.Long"      },
		{"short$", "java.lang.Short"    },
		{"void$", "java.lang.Void"      },
		{"Z$", "java.lang.Boolean"      },
		{"B$", "java.lang.Byte"         },
		{"C$", "java.lang.Character"    },
		{"D$", "java.lang.Double"       },
		{"F$", "java.lang.Float"        },
		{"I$", "java.lang.Integer"      },
		{"J$", "java.lang.Long"         },
		{"S$", "java.lang.Short"        },
		{"V$", "java.lang.Void"         },
		{"^\\[L", "["                   }
	};
	
	//修飾子を表現する文字列
	public final String MOD_STRING_NONSTATIC_FIELD = "nonstatic_field";
	public final String MOD_STRING_STATIC_FIELD = "static_field";
	public final String MOD_STRING_NONSTATIC_METHOD = "nonstatic_method";
	public final String MOD_STRING_STATIC_METHOD = "static_method";
	public final String MOD_STRING_CONSTRUCTOR = "constructor";

	/*
	 * static コンストラクタはC#にはあるが、java には無い。
	 * */


	//enumで表現
	enum ModString {
		NONSTATIC_FIELD("nonstatic_field"),
		STATIC_FIELD("static_field"),
		NONSTATIC_METHOD("nonstatic_method"),
		STATIC_METHOD("static_method"),
		CONSTRUCTOR("constructor");
	    private final String mode;
	    private ModString(String m) {
	        this.mode = m;
	    }
	    String modName(){return mode;}
	}


	//出力ファイル
	static PrintWriter printWriter_Func;	//Function ファイル用
	static PrintWriter printWriter_Clas;	//Class ファイル用

	static List<String> AllClasses = new ArrayList<String>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		//出力ファイル
		//Class ファイル用
		File file_clas = new File("D:\\kypg_mdl_clas.txt");
		FileWriter filewriter_clas = new FileWriter(file_clas);
		BufferedWriter bw_clas = new BufferedWriter(filewriter_clas);
		printWriter_Clas = new PrintWriter(bw_clas);

		for(String s: basic_type){
			printWriter_Clas.println(s);
		}

		//Function ファイル用
		File file_func = new File("D:\\kypg_mdl_func.txt");
		FileWriter filewriter_func = new FileWriter(file_func);
		BufferedWriter bw_func = new BufferedWriter(filewriter_func);
		printWriter_Func = new PrintWriter(bw_func);


		// TODO 自動生成されたメソッド・スタブ
		//File input_file = new File("C:\\Program Files\\Java\\jre1.6.0_05\\lib\\rt.jar");//家
		File input_file = new File("C:\\Program Files\\Java\\jre6\\lib\\rt.jar");//学校
		JarFile jf = new JarFile(input_file);

		int numOfAllFunctions = 0;
		int numOfAllClasses = 0;
		for(String packageName : packages){
			String packageLocation = packageName.replace(".", "/");
			//パターンマッチ：クラスの先頭文字が大文字で始まり".class"で終わるのをチェック
			Pattern pattern1 = Pattern.compile("^"+ packageLocation + "/[A-Z].*\\.class");

			//パターンマッチ：クラス名に数字が含まれていれば除外
			Pattern pattern2 = Pattern.compile("[^0-9]+");

			//パターンマッチ：クラス名がImplで終わる
			Pattern pattern3 = Pattern.compile(".*Impl\\.class$");

			for (Enumeration e = jf.entries(); e.hasMoreElements();) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				String zipEntryLocation = ze.getName();
				//パターンマッチ：クラス名に'$'が含まれていれば除外
				if(zipEntryLocation.indexOf('$') > -1){
					continue;
				}
				//java.lang.Objectは除外
//				if(zipEntryLocation.equals("java/lang/Object.class")){
//					continue;
//				}
				//java.lang.ApplicationShutdownHooksは除外
				if(zipEntryLocation.equals("java/lang/ApplicationShutdownHooks.class")){
					continue;
				}
				//パターンマッチ：対象：jarファイル
				Matcher matcher1 = pattern1.matcher(zipEntryLocation);
				Matcher matcher2 = pattern2.matcher(zipEntryLocation);
				Matcher matcher3 = pattern3.matcher(zipEntryLocation);
				if(matcher1.matches() && matcher2.matches() && ! matcher3.matches()){
					//className作成
					/*
					 * zipEntryLocation から末尾の".class"を除いた文字列を作成する　
					 * 6は".class"の文字長である。
					 */
					String className = zipEntryLocation.substring(0, zipEntryLocation.length() - 6);
					className = className.replace("/", ".");

					//テキストにModelを出力する
					numOfAllFunctions += printModel(className);
					numOfAllClasses++;
				}
			}
		}
		printWriter_Func.close();
		printWriter_Clas.close();
		System.out.println("全Function 数：" + numOfAllFunctions);
		System.out.println("全Class 数：" + numOfAllClasses);
	}

	public static int printModel(String className) {
		//return printModelStatic2(className);
		//static の時1個しか出さない。
		return printModelStatic1(className);
	}
	//テキストにModelを出力する
	public static int printModelStatic2(String className) {
		int numOfFunctions = -1;
	    try {
	      int i,j;

	      //Classesファイルへの書き出し
	      printWriter_Clas.println(className);

	      //以下、Functionsファイルへの書き出し

	      // クラスオブジェクトの取得
	      Class cls = Class.forName(className);
	      className = formatTypeName(className);
	      
	      // フィールドの分析
	      Field[] fieldList = cls.getFields();
	      for (i=0;i<fieldList.length;i++) {
	        Field fld = fieldList[i];
	        int mod = fld.getModifiers();

	        //publicのみ
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//staticの時のみ2回

	        	for(int c = 0; c < count; c++){
		        	//クラス名の書き出し
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//修飾子の書き出し
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",field,");
		  	      	// 返り値型を表示
			        printWriter_Func.print(formatTypeName(fld.getType().getName())+",");
		  	      	//Function名の書き出し
		  	      	printWriter_Func.print(fld.getName()+",");
			        //ラベル(フィールド名)を表示
		  	      	if(c==0)
		  	      		printWriter_Func.print(splitName(fld.getName(), ";"));
		  	      	else	// ラベル(クラス名;フィールド名)を表示(staticのみ)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(fld.getName(), ";"));
		  	      	//フィールドにパラメータは無いから要らない。
//		  	      	//パラメーター
//	  	      			//レシーバあるいはstaticの所属クラス
//	  	      			if(c == 0)
//	  	      				printWriter_Func.print(","+className);
			        printWriter_Func.println();
	        	}

	        }

	      }

	      // コンストラクタの分析
	      Constructor[] ctorList = cls.getConstructors();
	      for (i=0;i<ctorList.length;i++) {
	        Constructor ct = ctorList[i];
	        int mod = ct.getModifiers();
	        //publicのみ
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	//所属クラス名の書き出し
	  	      	printWriter_Func.print(className+",");
	  	      	//inthisclass = false
	  	      	printWriter_Func.print("false,");
	  	      	//修飾子の書き出し
	  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",constructor,");
		        //返り値型（クラス名）を表示
		        printWriter_Func.print( ct.getDeclaringClass().getName()+",");
		        //Function名の書き出し
	  	      	printWriter_Func.print(ct.getDeclaringClass().getSimpleName()+",");
		        // ラベル(new;コンストラクタ名)を表示
		        printWriter_Func.print( "new;" + splitName(ct.getDeclaringClass().getSimpleName(), ";"));

		        // 引数の型を表示
		        Class[] cparamList = ct.getParameterTypes();
		        for (j=0;j<cparamList.length;j++) {
		          printWriter_Func.print( "," + formatTypeName(cparamList[j].getName()));
		        }
		        printWriter_Func.println("");
	        }
	      }

	      // メソッドの分析
	      Method[] methList = cls.getMethods();
	      for (i=0;i<methList.length;i++) {
	        Method m = methList[i];
	        int mod = m.getModifiers();

	        //publicのみ
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//staticの時のみ2回
	        	for(int c = 0; c < count; c++){
		        	//所属クラス名の書き出し
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//修飾子の書き出し
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",method,");
		  	      	// 返り値の型を表示
			        printWriter_Func.print(formatTypeName(m.getReturnType().getName())+",");
		  	      	//Function名の書き出し
		  	      	printWriter_Func.print(m.getName()+",");
			        // ラベル(メソッド名)を表示
		  	      	if(c == 0)
		  	      		printWriter_Func.print(splitName(m.getName(), ";"));
		  	      	else// ラベル(クラス名;フィールド名)を表示(staticのみ)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(m.getName(), ";"));
		  	      	//パラメーター
		  	      		//レシーバあるいはstaticの所属クラス
		  	      		if(c == 0)
		  	      			printWriter_Func.print(","+className);
				        // 引数の型を表示
				        Class[] mparamList = m.getParameterTypes();
				        for (j=0;j<mparamList.length;j++) {
				          printWriter_Func.print( "," + formatTypeName(mparamList[j].getName()));
				        }
			        printWriter_Func.println("");
	        	}

	        }
	      }
	      // function 数
	      numOfFunctions = fieldList.length + ctorList.length + methList.length;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return numOfFunctions;
	  }
	
	public static int printModelStatic1(String className) {
		int numOfFunctions = -1;
	    try {
	      int i,j;

	      //Classesファイルへの書き出し
	      printWriter_Clas.println(className);

	      //以下、Functionsファイルへの書き出し

	      // クラスオブジェクトの取得
	      Class cls = Class.forName(className);
	      className = formatTypeName(className);
	      
	      // フィールドの分析
	      Field[] fieldList = cls.getFields();
	      for (i=0;i<fieldList.length;i++) {
	        Field fld = fieldList[i];
	        int mod = fld.getModifiers();

	        //publicのみ
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//staticの時のみ2回

	        	for(int c = 0; c < 1; c++){
		        	//クラス名の書き出し
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//修飾子の書き出し
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",field,");
		  	      	// 返り値型を表示
			        printWriter_Func.print(formatTypeName(fld.getType().getName())+",");
		  	      	//Function名の書き出し
		  	      	printWriter_Func.print(fld.getName()+",");
			        //ラベル(フィールド名)を表示
		  	      	if(count == 1)
		  	      		printWriter_Func.print(splitName(fld.getName(), ";"));
		  	      	else	// ラベル(クラス名;フィールド名)を表示(staticのみ)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(fld.getName(), ";"));
		  	      	//フィールドにパラメータは無いから要らない。
//		  	      	//パラメーター
//	  	      			//レシーバあるいはstaticの所属クラス
//	  	      			if(c == 0)
//	  	      				printWriter_Func.print(","+className);
			        printWriter_Func.println();
	        	}

	        }

	      }

	      // コンストラクタの分析
	      Constructor[] ctorList = cls.getConstructors();
	      for (i=0;i<ctorList.length;i++) {
	        Constructor ct = ctorList[i];
	        int mod = ct.getModifiers();
	        //publicのみ
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	//所属クラス名の書き出し
	  	      	printWriter_Func.print(className+",");
	  	      	//inthisclass = false
	  	      	printWriter_Func.print("false,");
	  	      	//修飾子の書き出し
	  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",constructor,");
		        //返り値型（クラス名）を表示
		        printWriter_Func.print( ct.getDeclaringClass().getName()+",");
		        //Function名の書き出し
	  	      	printWriter_Func.print(ct.getDeclaringClass().getSimpleName()+",");
		        // ラベル(new;コンストラクタ名)を表示
		        printWriter_Func.print( "new;" + splitName(ct.getDeclaringClass().getSimpleName(), ";"));

		        // 引数の型を表示
		        Class[] cparamList = ct.getParameterTypes();
		        for (j=0;j<cparamList.length;j++) {
		          printWriter_Func.print( "," + formatTypeName(cparamList[j].getName()));
		        }
		        printWriter_Func.println("");
	        }
	      }

	      // メソッドの分析
	      Method[] methList = cls.getMethods();
	      for (i=0;i<methList.length;i++) {
	        Method m = methList[i];
	        int mod = m.getModifiers();

	        //publicのみ
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//staticの時のみ2回
	        	for(int c = 0; c < 1; c++){
		        	//所属クラス名の書き出し
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//修飾子の書き出し
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",method,");
		  	      	// 返り値の型を表示
			        printWriter_Func.print(formatTypeName(m.getReturnType().getName())+",");
		  	      	//Function名の書き出し
		  	      	printWriter_Func.print(m.getName()+",");
			        // ラベル(メソッド名)を表示
		  	      	if(count == 1)
		  	      		printWriter_Func.print(splitName(m.getName(), ";"));
		  	      	else// ラベル(クラス名;フィールド名)を表示(staticのみ)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(m.getName(), ";"));
		  	      	//パラメーター
		  	      		//レシーバあるいはstaticの所属クラス
		  	      		if(count == 1)
		  	      			printWriter_Func.print(","+className);
				        // 引数の型を表示
				        Class[] mparamList = m.getParameterTypes();
				        for (j=0;j<mparamList.length;j++) {
				          printWriter_Func.print( "," + formatTypeName(mparamList[j].getName()));
				        }
			        printWriter_Func.println("");
	        	}

	        }
	      }
	      // function 数
	      numOfFunctions = fieldList.length + ctorList.length + methList.length;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return numOfFunctions;
	  }
	
	//名前の大文字の部分の前に文字列withを挿入し、大文字を小文字に変換する
	//最後の1文字が大文字の場合は切らない。
	//大文字が連続している場合は、切らない。
	// "_" で 切る。
	public static String splitName(String name,  String with){
		String ret = name;
		// "_" で区切り。
		if(name.contains("_")){
			ret = splitName_splitByUnderBar(name, with);
		}else if(isAllUpperCase(name)){
			//Void.TYPE のような全て大文字の時
			ret = name.toLowerCase().trim();
		}else{
			// 大文字で区切り。
			ret = splitName_splitByUpperCase(name, with);
		}
		return ret;
	}

	public static String splitName_splitByUnderBar(String name,  String with){
		name = name.replaceAll("_", with);
		return name.toLowerCase().trim();
	}
	
	//文字列が全て大文字かを確かめる
	public static boolean isAllUpperCase(String name){
		/*
		 * 一文字でも小文字があれば、false
		 */
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length(); i++){
			//大文字（位置i）を見つける
			if(Character.isLowerCase(sb.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	public static String splitName_splitByUpperCase(String name,  String with){
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length() -1; i++){
			//大文字（位置i）を見つける
			if(Character.isUpperCase(sb.charAt(i))){
				
				//次も連続して大文字なら、続ける。
				if(Character.isUpperCase(sb.charAt(i+1))){
					continue;
				}
				//1つ前の文字が大文字なら、続ける。
				if(i > 0 && Character.isUpperCase(sb.charAt(i-1))){
					continue;
				}
				//その大文字の前に文字列withを挿入
				if(i >= 1){
					sb.insert(i, with);
					//文字列withの長さ分だけiを進める
					i += with.length();
				}
			}
		}
		return sb.toString().toLowerCase().trim();
	}
	
	  // 省略文字を戻す。
	  // 基本データ型は全てオブジェクト型に変換する。
	  //（配列記号"["はそのまま残す。）
//	  public static String formatTypeName(String name){
//		  boolean flg_changed = false;
//		  
//		  for(int i = 0; i < basic_type_simbols.length; i++){
//			  String old = new String(name);
//			  name = replaceName(name, basic_type_simbols[i][0], basic_type_simbols[i][1]);
//			  if(!old.equals(name))
//				  flg_changed = true;
//		  }
//		
//		  if(flg_changed){//長さが違えば、変更あり。
//			  name = "1\"" + name;	//基本データ型のフラグを立てる。 
//		  }else{
//			  name = "0\"" + name; 	//立てない。
//		  }
//			  return name;
//	  }
//	  

	  // 省略文字だけを戻す。
	  // 基本データ型はオブジェクト型に変換しない。
	  //（配列記号"["はそのまま残す。）
	  public static String formatTypeName(String name){
		  boolean flg_changed = false;
		  
		  for(int i = 9; i < basic_type_simbols.length; i++){
			  String old = new String(name);
			  name = replaceName(name, basic_type_simbols[i][0], basic_type_simbols[i][1]);
			  if(!old.equals(name))
				  flg_changed = true;
		  }
		  return name;
	  }
	  
	  public static String replaceName(String name, String from, String to){
		  return name.replaceFirst(from, to);
	  }
	  
//	  
//	  public static String formatTypeName(String name){
//		  int len_start = name.length();
//		  
//		// 基本データ型は全てオブジェクト型に変換する。
//			//		  B            byte
//		  name = name.replaceFirst("byte$", "java.lang.Byte");
//			//		  C            char
//		  name = name.replaceFirst("char$", "java.lang.Character");
//			//		  D            double
//		  name = name.replaceFirst("double$", "java.lang.Double");
//			//		  F            float
//		  name = name.replaceFirst("float$", "java.lang.Float");
//			//		  I            int
//		  name = name.replaceFirst("int$", "java.lang.Integer");
//			//		  J            long
//		  name = name.replaceFirst("long$", "java.lang.Long");
//			//		  S            short
//		  name = name.replaceFirst("short$", "java.lang.Short");
//			//		  Z            boolean
//		  name = name.replaceFirst("boolean$", "java.lang.Boolean");
//			//		  V            void
//		  name = name.replaceFirst("void$", "java.lang.Void");
//
//		// 省略文字を戻す。
//			//		  B            byte
//		  name = name.replaceFirst("B$", "java.lang.Byte");
//			//		  C            char
//		  name = name.replaceFirst("C$", "java.lang.Character");
//			//		  D            double
//		  name = name.replaceFirst("D$", "java.lang.Double");
//			//		  F            float
//		  name = name.replaceFirst("F$", "java.lang.Float");
//			//		  I            int
//		  name = name.replaceFirst("I$", "java.lang.Integer");
//			//		  J            long
//		  name = name.replaceFirst("J$", "java.lang.Long");
//		  //		  Lクラス名;   クラスまたはインタフェース
//		  //（とりあえず１次元配列のみ考慮）
//		  name = name.replaceFirst("^\\[L", "[");
//			//		  S            short
//		  name = name.replaceFirst("S$", "java.lang.Short");
//			//		  Z            boolean
//		  name = name.replaceFirst("Z$", "java.lang.Boolean");
//			//		  V            void
//		  name = name.replaceFirst("V$", "java.lang.Void");
//		  
//		  if(len_start != name.length()){//長さが違えば、変更あり。
//			  name = "1\"" + name;	//基本データ型のフラグを立てる。 
//		  }else{
//			  name = "0\"" + name; 	//立てない。
//		  }
//			  return name;
//	  }
//	  
//	  //省略文字を戻す。（配列記号"["はそのまま残す。）
//	  public static String formatTypeName(String name){
//			//		  B            byte
//		  name = name.replaceFirst("B$", "byte");
//			//		  C            char
//		  name = name.replaceFirst("C$", "char");
//			//		  D            double
//		  name = name.replaceFirst("D$", "double");
//			//		  F            float
//		  name = name.replaceFirst("F$", "float");
//			//		  I            int
//		  name = name.replaceFirst("I$", "int");
//			//		  J            long
//		  name = name.replaceFirst("J$", "long");
//		  //		  Lクラス名;   クラスまたはインタフェース
//		  //（とりあえず１次元配列のみ考慮）
//		  name = name.replaceFirst("^\\[L", "[");
//			//		  S            short
//		  name = name.replaceFirst("S$", "short");
//			//		  Z            boolean
//		  name = name.replaceFirst("Z$", "boolean");
//			//		  V            void
//		  name = name.replaceFirst("V$", "void");
//			  return name;
//	  }

	  public static String isStaticString(boolean isStatic){
		if(isStatic)
      		return "static";
      	else
      		return "nonstatic";
	  }

	  public static String isFinalString(boolean isFinal){
			if(isFinal)
	      		return "final";
	      	else
	      		return "nonfinal";
	  }
	  
	  public static String isPrimitiveString(boolean isPrim){
			if(isPrim)
	      		return "primitive";
	      	else
	      		return "nonprimitive";
	  }
}
