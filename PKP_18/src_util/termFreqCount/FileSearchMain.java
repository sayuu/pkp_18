package termFreqCount;
import java.io.File;


public class FileSearchMain {

	/**
	 * @param args
	 */
	  public static void main(String[] args) {
	      String path = "C:\\sourcefiles\\commons-codec-1.4-src";
	      FileSearch search = new FileSearch();

	      System.out.println("\n●全てのファイルを取得");
	      File[] files = search.listFiles(path, null);
	      printFileList(files);
	      search.clear();

	      System.out.println("\n●拡張子javaのファイルを取得");
	      files = search.listFiles(path, "*.java");
	      printFileList(files);
	      search.clear();
	      
	      System.out.println("\n●全てのファイルとディレクトリを取得");
	      files = search.listFiles(path, null,search.TYPE_FILE_OR_DIR, true, 0);
	      printFileList(files);
	      search.clear();

	      System.out.println("\n●現在の日付から、2日前以降に更新されたファイルを取得");
	      files = search.listFiles(path, null,search.TYPE_FILE, true, 2);
	      printFileList(files);
	      search.clear();

	      System.out.println("\n●現在の日付から、30日以前の古いファイルを取得");
	      files = search.listFiles(path, null,search.TYPE_FILE, true, -30);
	      printFileList(files);
	      search.clear();
	  }

	  private static void printFileList(File[] files) {
	      for (int i = 0; i < files.length; i++) {
	          File file = files[i];
	          System.out.println((i + 1) + ":    " + file);
	      }
	  }


}
