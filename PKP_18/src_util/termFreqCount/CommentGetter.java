package termFreqCount;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class CommentGetter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//getComment();
	}



	public static void getComment(String path) {
		// TODO Auto-generated method stub
		 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
		//String path = "/home/sayuu/source/commons-codec-1.4-src/src";
		//String path = "/home/sayuu/source/Vuze_4604_source";
		//String path = "/home/sayuu/source/jedit4.4.1source";
		//String path = "/home/sayuu/source/Buddi-3.4.0.11.src/src";
		//String path = "/home/sayuu/source/carol-2.0.5-src/src";
		//String path = "/home/sayuu/source/tvbrowser-3.0.1/src";
		//String path = "/home/sayuu/source/Jmol/src";
		//String path = "/home/sayuu/source/dnsjava/trunk/org";
		//String path = "/home/sayuu/source/rssowl";
		//String path = "/home/sayuu/source/jruby-1.6.3/src";
		//String path = "/home/sayuu/source/zcs-6.0.7_GA_2483-src";
		//String path = "/home/sayuu/source/jMemorize-1.3.0/src";
		

		 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
		 FileSearch search = new FileSearch();

		 //System.out.println("\n●拡張子javaのファイルを取得");
		 File[] files = search.listFiles(path, "*.java");
	     //printFileList(files);
	     search.clear();
	     
	     
	     File outfile = new File("/home/sayuu/source/loc_output.txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			//System.out.println("ファイル番号：コメント番号：コメント/コード");
			pw.println("ファイル番号：コメント番号：コメント/コード");
		   
		     int line_sum = 0;
		     int com_sum = 0;
		     int f_num=1;
		     //1ファイルずつ読み込んで調べる
		     for(File f : files){
		    	//1行ずつ読み込んでその行がコメント行かどうか調べる
		    	 int com_num=0;
		    	 try {
		             FileReader in = new FileReader(f);
		             BufferedReader br = new BufferedReader(in);
		             String line;
		             boolean is_prev_line_com = false;//1行前の行がコメントかどうか
		             while ((line = br.readLine()) != null) {
		            	 line_sum++;
		            	 //その行のどこかにコメントがあったら、その行を表示する
		            	 if(line.indexOf("//") != -1 && line.indexOf("http://") == -1 && line.indexOf("https://") == -1){
		            		 is_prev_line_com = true;
		            		 com_num++;
		            		 //System.out.println(f_num + ": " + com_num + ":Com    " + line);
		            		 pw.println(f_num + ": " + com_num + ":Com    " + line);
		            	 }
		            	 //現在行がコメント行ではなく、前の行がコメント行なら現在行を表示する。
		            	 else if(is_prev_line_com == true){
		            		 is_prev_line_com = false;
		            		 //System.out.println(f_num + ": " + com_num + ":Exe    " + line);
		            		 //pw.println(f_num + ": " + com_num + ":Exe    " + line);
		            	 }
		             }
		             br.close();
		             in.close();
		         } catch (IOException e) {
		             System.out.println(e);
		         }
		         f_num++;
		         com_sum += com_num;
		     }
		     //System.out.println("javaファイル数: " + f_num + ", 総行数: " + line_sum + ", //コメント総行数: " + com_sum + ", コメント率(%):" + (double)com_sum/line_sum*100);
		     pw.println("javaファイル数: " + f_num + ", 総行数: " + line_sum + ", //コメント総行数: " + com_sum + ", コメント率(%):" + (double)com_sum/line_sum*100);
			
			pw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	     
	    
	
	private static void printFileList(File[] files) {
	      for (int i = 0; i < files.length; i++) {
	          File file = files[i];
	          System.out.println((i + 1) + ":    " + file);
	      }
	}

}
