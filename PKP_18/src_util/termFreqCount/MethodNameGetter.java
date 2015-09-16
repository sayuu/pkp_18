package termFreqCount;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * 指定したフォルダの中のファイルを全て検索し、
 * メソッド名を取り出すクラス。
 * 								2011/07/13
 * 
 * コメントは、getMethodName3が大まかに排除。
 * 
 * if と　while、forは除くべし。まだやってない。 (厳密ではなくなるが、まあ地の文だけでもいいだろう。
 * 	getMethodLine 済み
 *  getMethodName3 済み　2011/10/21
 * 
 */

public class MethodNameGetter {

	static String filename = "C://Users//sayuu//Desktop//m_name_output.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String path = "/home/sayuu/source/test";
		//String path = "/home/sayuu/source/commons-codec-1.4-src";
		String path = "/home/sayuu/source/Vuze_4604_source";

		getMethodName3(path, true, false);
	}
	//名前の大文字の部分の前に文字列withを挿入し、大文字を小文字に変換する
	  //(注意：IO, URL, UTFなどもすべてバラバラに分割されてしまう！)
	  public static String splitName(String name,  String with){
		  StringBuffer sb = new StringBuffer(name);
		  for(int i = 0; i < sb.length(); i++){
			  //大文字（位置i）を見つける
			  if(Character.isUpperCase(sb.charAt(i))){
				//その大文字の前に文字列withを挿入
				if(i >= 1){
					sb.insert(i, with);
					//文字列withの長さ分だけiを進める
					i += with.length();
				}
			  }
		  }
		  return sb.toString().toLowerCase();
	  }

		/*
		 * メソッド行を取得する。txtにoutputする。
		 * 
		 * getMethodName3を改造した。
		 * 
		 */
		public static void getMethodLine(String path){
			// TODO Auto-generated method stub
			 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
			//String path = "/home/sayuu/source/commons-codec-1.4-src";
			//String path = "/home/sayuu/source/jMemorize-1.3.0/src";
			//String path = "/home/sayuu/source/Vuze_4604_source";
			//String path = "/home/sayuu/source/jedit4.4.1source";
			//String path = "/home/sayuu/source/Buddi-3.4.0.11.src/src";
			//String path = "/home/sayuu/source/carol-2.0.5-src/src";
			//String path = "/home/sayuu/source/tvbrowser-3.0.1/src";
			//String path = "/home/sayuu/source/Jmol/src";
			//String path = "/home/sayuu/source/dnsjava/trunk/org";
			//String path = "/home/sayuu/source/rssowl";
			//String path = "/home/sayuu/source/jruby-1.6.3/src";

			
			 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
			 FileSearch search = new FileSearch();

			 //System.out.println("\n●拡張子javaのファイルを取得");
			 File[] files = search.listFiles(path, "*.java");
		     //printFileList(files);
		     search.clear();
		     
		     
		     File outfile = new File("/home/sayuu/source/m_name_output.txt");
		     try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
				
				//System.out.println("ファイル番号：コメント番号：コメント/コード");
				//pw.println("ファイル番号: 行番号: メソッド番号: メソッド名: 行");
			   
			     int line_num = 0;
			     int method_num = 0;
			     int f_num=1;
			     //1ファイルずつ読み込んで調べる
			     for(File f : files){
			    	//1行ずつ読み込んでその行がコメント行かどうか調べる
			    	 try {
			             FileReader in = new FileReader(f);
			             BufferedReader br = new BufferedReader(in);
			             String line;
			             
			             /*
	           		  * 前括弧の前に文字列がある。
	           		  * 
	           		  * .* 何でもいいから、０回以上
	           		  * 
	           		  * [\s演算子] 空白文字と演算子が 1回
	           		  *
	           		  * [a-zA-Z]+ 文字が1回以上
	           		  * 
	           		  * (はそのまま使うと特別な記号として認識されてしまうので、
	           		  * \\(とする。
	           		  * 
	           		  * 他の演算子も一応全てに\\を前につけておく。
	           		  * 
	           		  * []内の演算子または空白文字のうちのいずれかに当てはまるの意味。
	           		  * 
	           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
	           		  * 
	           		  * 取り出したいパターンは()でくくる。
	           		  * ([a-zA-Z]+)
	           		  * 
	           		  */
			             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
		            		             
			             /*
			              * これだと
			              * 1行に複数メソッド存在したときに1個しかとれないから
			              * どうしたらいいんだろう。
			              * 
			              * 例えばこれだと、
			              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
			              *  
			              *  getBytesUnchecked
			              *  encodeHexString
			              *  getCharsetName
			              *  
			              *  この３つがメソッド名。
			              */

			             /*
			              * matcher.matches();
			              * ではなくて、
			              * matcher.find();
			              * を使う。
			              * 
			              * matchesは文章全体を対象とするが、
			              * findは部分文字列を対象とする。
			              * 
			              * 文章中にパターンにマッチする文字列が複数現れる場合は
			              * こちらをつかうと便利。
			              * すると、
			              * 
			              * マッチする部分文字列のみに対応する正規表現だけを書けば良い
			              * ので簡単になる。
			              */
			    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
			    		 
		            	 boolean flg_block_comment = false;	//ブロックコメントのフラグ
  	
			             while ((line = br.readLine()) != null) {
			            	 line_num++;
			            	 
			            	//一行コメントを調べる。
			            	 line = line.trim();	//前後の空白文字削除
			            	 
			            	 //trimして長さ0なら空行。
			            	 if(line.length() == 0){
			            		 continue;
			            	 }
			            	 
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("if") == true || line.startsWith("else if") == true || line.startsWith("else") == true || line.startsWith("while") == true|| line.startsWith("for") == true){
			            		 continue;
			            	 }
			            	 

			            	 //if文の終わりと、メソッド宣言対策、
			            	 if(line.startsWith("}") == true || line.endsWith("{") == true){ 
			            		 continue;
			            	 }
			            	 
			            	 //"="があったらそのうしろだけを取る。 代入文の先頭部分を削除
			            	 if(line.contains("=") == true){
			            		 line = line.substring(line.indexOf("=")+1);
			            		 line = line.trim();
			            	 }
			            	 
		         			//System.out.println(line);
		         			//pw.println(line);

			            	 
			            	//行頭からコメントが始まっているもののみチェック。それ以外の途中から始まっているやつは面倒なのでそのまま。
		         			
		         			if(flg_block_comment == true){	//ブロックコメントの中
			         			
		         				//System.out.println("ブロックコメント　中");
			         			//pw.println("ブロックコメント　中");
			         			
			         			//ブロックコメント終了判定
			         			if(line.startsWith("*/")){
				         			//System.out.println("ブロックコメント　終了");
				         			//pw.println("ブロックコメント　終了");
				         			flg_block_comment = false;
				         		}
			         			
			         			continue;
			             	
			         			
		         			}else{	//ブロックコメントの外
		         				
			         			//1行コメントの判定
		         				if(line.startsWith("//")){
				         			//System.out.println("一行コメント");
				         			//pw.println("一行コメント");
				         			continue;
				         		}else if(line.startsWith("/*")){	//ブロックコメント開始判定
				         			//System.out.println("ブロックコメント　開始");
				         			//pw.println("ブロックコメント　開始");
				         			flg_block_comment = true;
				         			continue;
				         		}
		         			}
			            	
			        		 
			            	 
			            	 Matcher matcher = pattern.matcher(line);
		    	        	      	 
			            	 //その行のどこかにメソッドがあったら、その行を表示する
			            	 while(matcher.find()){
			            		 method_num++;
				    	         //String matchedString = matcher.group(1);
				    	         //System.out.println(matchedString);
				    	         pw.println(line);

				    			 /*
				    	         if(show_matchedString)
				    	        	 pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
				    	         if(splitted)
				    	        	 for(String s_name: splitName(matchedString, ",").split(",")){
				    	        		 pw.println(s_name);
				    	        	 }
				    	         else
				    	        	 pw.println(matchedString);
				    	         */
				    	      }
				    	      
			             }
			             
			             br.close();
			             in.close();
			         } catch (IOException e) {
			             System.out.println(e);
			         }
			         f_num++;
			     }
			     //System.out.println("javaファイル数: " + f_num + ", 総行数: " + line_sum + ", //コメント総行数: " + com_sum + ", コメント率(%):" + (double)com_sum/line_sum*100);
			     //pw.println("javaファイル数: " + f_num + ", 総行数: " + line_num + ", //メソッド総数: " + method_num);
				
				pw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	  
	  
		/*
		 * メソッド名の羅列だけが書かれたtxtファイルを出力する。 
		 * 
		 * splitted=true のとき、大文字の部分で名前を単語に分割する。> 頻度計測時はtrueにする。
		 * show_matchedString=true のとき、マッチした行を出力に書き込む。 > 頻度計測時はfalseにする。
		 * 
		 * getMethodName2 はコメント部分も取得してしまうので、
		 * コメント部分を排除するように作り直す。
		 * 
		 */
		public static void getMethodName3(String path, boolean splitted, boolean show_matchedString){
			// TODO Auto-generated method stub
			 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
			//String path = "/home/sayuu/source/commons-codec-1.4-src";
			//String path = "/home/sayuu/source/jMemorize-1.3.0/src";
			//String path = "/home/sayuu/source/Vuze_4604_source";
			//String path = "/home/sayuu/source/jedit4.4.1source";
			//String path = "/home/sayuu/source/Buddi-3.4.0.11.src/src";
			//String path = "/home/sayuu/source/carol-2.0.5-src/src";
			//String path = "/home/sayuu/source/tvbrowser-3.0.1/src";
			//String path = "/home/sayuu/source/Jmol/src";
			//String path = "/home/sayuu/source/dnsjava/trunk/org";
			//String path = "/home/sayuu/source/rssowl";
			//String path = "/home/sayuu/source/jruby-1.6.3/src";

			
			 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
			 FileSearch search = new FileSearch();

			 //System.out.println("\n●拡張子javaのファイルを取得");
			 File[] files = search.listFiles(path, "*.java");
		     //printFileList(files);
		     search.clear();
		     
		     
		     File outfile = new File(filename);
		     try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
				
				//System.out.println("ファイル番号：コメント番号：コメント/コード");
				//pw.println("ファイル番号: 行番号: メソッド番号: メソッド名: 行");
			   
			     int line_num = 0;
			     int method_num = 0;
			     int f_num=1;
			     //1ファイルずつ読み込んで調べる
			     for(File f : files){
			    	//1行ずつ読み込んでその行がコメント行かどうか調べる
			    	 try {
			             FileReader in = new FileReader(f);
			             BufferedReader br = new BufferedReader(in);
			             String line;
			             
			             /*
	           		  * 前括弧の前に文字列がある。
	           		  * 
	           		  * .* 何でもいいから、０回以上
	           		  * 
	           		  * [\s演算子] 空白文字と演算子が 1回
	           		  *
	           		  * [a-zA-Z]+ 文字が1回以上
	           		  * 
	           		  * (はそのまま使うと特別な記号として認識されてしまうので、
	           		  * \\(とする。
	           		  * 
	           		  * 他の演算子も一応全てに\\を前につけておく。
	           		  * 
	           		  * []内の演算子または空白文字のうちのいずれかに当てはまるの意味。
	           		  * 
	           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
	           		  * 
	           		  * 取り出したいパターンは()でくくる。
	           		  * ([a-zA-Z]+)
	           		  * 
	           		  */
			             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
		            		             
			             /*
			              * これだと
			              * 1行に複数メソッド存在したときに1個しかとれないから
			              * どうしたらいいんだろう。
			              * 
			              * 例えばこれだと、
			              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
			              *  
			              *  getBytesUnchecked
			              *  encodeHexString
			              *  getCharsetName
			              *  
			              *  この３つがメソッド名。
			              */

			             /*
			              * matcher.matches();
			              * ではなくて、
			              * matcher.find();
			              * を使う。
			              * 
			              * matchesは文章全体を対象とするが、
			              * findは部分文字列を対象とする。
			              * 
			              * 文章中にパターンにマッチする文字列が複数現れる場合は
			              * こちらをつかうと便利。
			              * すると、
			              * 
			              * マッチする部分文字列のみに対応する正規表現だけを書けば良い
			              * ので簡単になる。
			              */
			    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
			    		 
		            	 boolean flg_block_comment = false;	//ブロックコメントのフラグ
    	
			             while ((line = br.readLine()) != null) {
			            	 line_num++;
			            	 
			            	//一行コメントを調べる。
			            	 line = line.trim();	//前後の空白文字削除
			            	 
			            	 //trimして長さ0なら空行。
			            	 if(line.length() == 0){
			            		 continue;
			            	 }

			            	 //メソッド宣言対策、
			            	 if(line.endsWith("{") == true){ 
			            		 continue;
			            	 }

			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("if") == true ){
			            		 line = line.substring(line.indexOf("if")+1);
			            		 line = line.trim();
			            	 }
			            	 
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("else if") == true){
			            		 line = line.substring(line.indexOf("else if")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("}else if") == true){
			            		 line = line.substring(line.indexOf("}else if")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("} else if") == true){
			            		 line = line.substring(line.indexOf("} else if")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("else") == true){
			            		 line = line.substring(line.indexOf("else")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("}else") == true){
			            		 line = line.substring(line.indexOf("}else")+1);
			            		 line = line.trim();
			            	 }
			            	 

			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("} else") == true){
			            		 line = line.substring(line.indexOf("} else")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("while") == true){
			            		 line = line.substring(line.indexOf("while")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if文とwhile文、for文を除く
			            	 if(line.startsWith("for") == true){
			            		 line = line.substring(line.indexOf("for")+1);
			            		 line = line.trim();
			            	 }
			            	

			            	 
		         			//System.out.println(line);
		         			//pw.println(line);

			            	 
			            	//行頭からコメントが始まっているもののみチェック。それ以外の途中から始まっているやつは面倒なのでそのまま。
		         			
		         			if(flg_block_comment == true){	//ブロックコメントの中
			         			
		         				//System.out.println("ブロックコメント　中");
			         			//pw.println("ブロックコメント　中");
			         			
			         			//ブロックコメント終了判定
			         			if(line.startsWith("*/")){
				         			//System.out.println("ブロックコメント　終了");
				         			//pw.println("ブロックコメント　終了");
				         			flg_block_comment = false;
				         		}
			         			
			         			continue;
			             	
			         			
		         			}else{	//ブロックコメントの外
		         				
			         			//1行コメントの判定
		         				if(line.startsWith("//")){
				         			//System.out.println("一行コメント");
				         			//pw.println("一行コメント");
				         			continue;
				         		}else if(line.startsWith("/*")){	//ブロックコメント開始判定
				         			//System.out.println("ブロックコメント　開始");
				         			//pw.println("ブロックコメント　開始");
				         			flg_block_comment = true;
				         			continue;
				         		}
		         			}
			            	
			        		 
			            	 
			            	 Matcher matcher = pattern.matcher(line);
			            	 
			    			            	 
			            	 //その行のどこかにメソッドがあったら、その行を表示する
			            	 while(matcher.find()){
			            		 method_num++;
				    	         String matchedString = matcher.group(1);
				    	         //System.out.println(matchedString);
				    	         
				    	         if(show_matchedString)
				    	        	 pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
				    	         if(splitted)
				    	        	 for(String s_name: splitName(matchedString, ",").split(",")){
				    	        		 pw.println(s_name);
				    	        	 }
				    	         else
				    	        	 pw.println(matchedString);
				    	         
				    	      }
			             }
			             
			             br.close();
			             in.close();
			         } catch (IOException e) {
			             System.out.println(e);
			         }
			         f_num++;
			     }
			     //System.out.println("javaファイル数: " + f_num + ", 総行数: " + line_sum + ", //コメント総行数: " + com_sum + ", コメント率(%):" + (double)com_sum/line_sum*100);
			     //pw.println("javaファイル数: " + f_num + ", 総行数: " + line_num + ", //メソッド総数: " + method_num);
				
				pw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	
	/*
	 * メソッド名の羅列だけが書かれたtxtファイルを出力する。 
	 * 
	 * splitted=true のとき、大文字の部分で名前を単語に分割する。> 普段はtrueにする。
	 * show_matchedString=true のとき、マッチした行を出力に書き込む。 > 普段はfalseにする。
	 */
	public static void getMethodName2(boolean splitted, boolean show_matchedString){
		// TODO Auto-generated method stub
		 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
		//String path = "/home/sayuu/source/commons-codec-1.4-src";
		//String path = "/home/sayuu/source/jMemorize-1.3.0/src";
		//String path = "/home/sayuu/source/Vuze_4604_source";
		//String path = "/home/sayuu/source/jedit4.4.1source";
		//String path = "/home/sayuu/source/Buddi-3.4.0.11.src/src";
		//String path = "/home/sayuu/source/carol-2.0.5-src/src";
		//String path = "/home/sayuu/source/tvbrowser-3.0.1/src";
		//String path = "/home/sayuu/source/Jmol/src";
		//String path = "/home/sayuu/source/dnsjava/trunk/org";
		//String path = "/home/sayuu/source/rssowl";
		String path = "/home/sayuu/source/jruby-1.6.3/src";

		
		 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
		 FileSearch search = new FileSearch();

		 //System.out.println("\n●拡張子javaのファイルを取得");
		 File[] files = search.listFiles(path, "*.java");
	     //printFileList(files);
	     search.clear();
	     
	     
	     File outfile = new File("/home/sayuu/source/m_name_output.txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			//System.out.println("ファイル番号：コメント番号：コメント/コード");
			//pw.println("ファイル番号: 行番号: メソッド番号: メソッド名: 行");
		   
		     int line_num = 0;
		     int method_num = 0;
		     int f_num=1;
		     //1ファイルずつ読み込んで調べる
		     for(File f : files){
		    	//1行ずつ読み込んでその行がコメント行かどうか調べる
		    	 try {
		             FileReader in = new FileReader(f);
		             BufferedReader br = new BufferedReader(in);
		             String line;
		             
		             /*
           		  * 前括弧の前に文字列がある。
           		  * 
           		  * .* 何でもいいから、０回以上
           		  * 
           		  * [\s演算子] 空白文字と演算子が 1回
           		  *
           		  * [a-zA-Z]+ 文字が1回以上
           		  * 
           		  * (はそのまま使うと特別な記号として認識されてしまうので、
           		  * \\(とする。
           		  * 
           		  * 他の演算子も一応全てに\\を前につけておく。
           		  * 
           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
           		  * 
           		  * 取り出したいパターンは()でくくる。
           		  * ([a-zA-Z]+)
           		  * 
           		  */
		             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
	            		             
		             /*
		              * これだと
		              * 1行に複数メソッド存在したときに1個しかとれないから
		              * どうしたらいいんだろう。
		              * 
		              * 例えばこれだと、
		              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
		              *  
		              *  getBytesUnchecked
		              *  encodeHexString
		              *  getCharsetName
		              *  
		              *  この３つがメソッド名。
		              */

		             /*
		              * matcher.matches();
		              * ではなくて、
		              * matcher.find();
		              * を使う。
		              * 
		              * matchesは文章全体を対象とするが、
		              * findは部分文字列を対象とする。
		              * 
		              * 文章中にパターンにマッチする文字列が複数現れる場合は
		              * こちらをつかうと便利。
		              * すると、
		              * 
		              * マッチする部分文字列のみに対応する正規表現だけを書けば良い
		              * ので簡単になる。
		              */
		    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
		   		 
		             while ((line = br.readLine()) != null) {
		            	 line_num++;
		            	 Matcher matcher = pattern.matcher(line);
		            	 
		            	 //その行のどこかにメソッドがあったら、その行を表示する
		            	 while(matcher.find()){
		            		 method_num++;
			    	         String matchedString = matcher.group(1);
			    	         //System.out.println(matchedString);
			    	         //if(show_matchedString)
			    	         //pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
			    	         if(splitted)
			    	        	 for(String s_name: splitName(matchedString, ",").split(",")){
			    	        		 pw.println(s_name);
			    	        	 }
			    	         else
			    	        	 pw.println(matchedString);
			    	      }
		             }
		             
		             br.close();
		             in.close();
		         } catch (IOException e) {
		             System.out.println(e);
		         }
		         f_num++;
		     }
		     //System.out.println("javaファイル数: " + f_num + ", 総行数: " + line_sum + ", //コメント総行数: " + com_sum + ", コメント率(%):" + (double)com_sum/line_sum*100);
		     //pw.println("javaファイル数: " + f_num + ", 総行数: " + line_num + ", //メソッド総数: " + method_num);
			
			pw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/*
	 * 見易いtxtファイルを出力する。 
	 */
	public static void getMethodName(){
		// TODO Auto-generated method stub
		 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
		String path = "/home/sayuu/source/commons-codec-1.4-src";
		 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
		 FileSearch search = new FileSearch();

		 //System.out.println("\n●拡張子javaのファイルを取得");
		 File[] files = search.listFiles(path, "*.java");
	     //printFileList(files);
	     search.clear();
	     
	     
	     File outfile = new File("/home/sayuu/source/output.txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			//System.out.println("ファイル番号：コメント番号：コメント/コード");
			pw.println("ファイル番号: 行番号: メソッド番号: メソッド名: 行");
		   
		     int line_num = 0;
		     int method_num = 0;
		     int f_num=1;
		     //1ファイルずつ読み込んで調べる
		     for(File f : files){
		    	//1行ずつ読み込んでその行がコメント行かどうか調べる
		    	 try {
		             FileReader in = new FileReader(f);
		             BufferedReader br = new BufferedReader(in);
		             String line;
		             
		             /*
           		  * 前括弧の前に文字列がある。
           		  * 
           		  * .* 何でもいいから、０回以上
           		  * 
           		  * [\s演算子] 空白文字と演算子が 1回
           		  *
           		  * [a-zA-Z]+ 文字が1回以上
           		  * 
           		  * (はそのまま使うと特別な記号として認識されてしまうので、
           		  * \\(とする。
           		  * 
           		  * 他の演算子も一応全てに\\を前につけておく。
           		  * 
           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
           		  * 
           		  * 取り出したいパターンは()でくくる。
           		  * ([a-zA-Z]+)
           		  * 
           		  */
		             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
	            		             
		             /*
		              * これだと
		              * 1行に複数メソッド存在したときに1個しかとれないから
		              * どうしたらいいんだろう。
		              * 
		              * 例えばこれだと、
		              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
		              *  
		              *  getBytesUnchecked
		              *  encodeHexString
		              *  getCharsetName
		              *  
		              *  この３つがメソッド名。
		              */

		             /*
		              * matcher.matches();
		              * ではなくて、
		              * matcher.find();
		              * を使う。
		              * 
		              * matchesは文章全体を対象とするが、
		              * findは部分文字列を対象とする。
		              * 
		              * 文章中にパターンにマッチする文字列が複数現れる場合は
		              * こちらをつかうと便利。
		              * すると、
		              * 
		              * マッチする部分文字列のみに対応する正規表現だけを書けば良い
		              * ので簡単になる。
		              */
		    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
		   		 
		             while ((line = br.readLine()) != null) {
		            	 line_num++;
		            	 Matcher matcher = pattern.matcher(line);
		            	 
		            	 //その行のどこかにメソッドがあったら、その行を表示する
		            	 while(matcher.find()){
		            		 method_num++;
			    	         String matchedString = matcher.group(1);
			    	         //System.out.println(matchedString);
			    	         pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
			    	      }
		             }
		             
		             br.close();
		             in.close();
		         } catch (IOException e) {
		             System.out.println(e);
		         }
		         f_num++;
		     }
		     //System.out.println("javaファイル数: " + f_num + ", 総行数: " + line_sum + ", //コメント総行数: " + com_sum + ", コメント率(%):" + (double)com_sum/line_sum*100);
		     pw.println("javaファイル数: " + f_num + ", 総行数: " + line_num + ", //メソッド総数: " + method_num);
			
			pw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
