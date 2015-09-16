package methodFreqCount;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/*
 * ファイルパスに置いた.classファイルに記述されている、
 * 　　各関数の使用頻度を計算するクラス。
 * 
 * 実行は、このファイルではなく、
 * BcelReadClassSample を実行する。
 * 
 */
public class Test {
	
	//file_path に解析したい、.classファイルを置く。
	static String file_path = "C:\\Users\\sayuu\\Desktop\\jar";
	static List<File> file_list = new ArrayList<File>();
	
	public static final void main(final String[] args) {
		getFiles();

		for(File file: file_list){
			System.out.println(file);
		}
    }
	

	public static void getFiles(){
	    File dir = new File(file_path);
	    if (!dir.exists()) {  
		    return;
		}
	    search(dir);
	}
	
	public static void search(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i=0; i<files.length; i++)
				search(files[i]);
			}
		else {
		// ここに各ファイルに対する処理を記述
			if(file.getName().endsWith(".class") == false){
				return;
			}
			//パターンマッチ：クラス名に'$'が含まれていれば除外
			if(file.getName().indexOf('$') > -1){
				return;
			}
			file_list.add(file);
		}
	}
}
