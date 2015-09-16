package termFreqCount;
import java.io.File;


public class FileSearchMain {

	/**
	 * @param args
	 */
	  public static void main(String[] args) {
	      String path = "C:\\sourcefiles\\commons-codec-1.4-src";
	      FileSearch search = new FileSearch();

	      System.out.println("\n���S�Ẵt�@�C�����擾");
	      File[] files = search.listFiles(path, null);
	      printFileList(files);
	      search.clear();

	      System.out.println("\n���g���qjava�̃t�@�C�����擾");
	      files = search.listFiles(path, "*.java");
	      printFileList(files);
	      search.clear();
	      
	      System.out.println("\n���S�Ẵt�@�C���ƃf�B���N�g�����擾");
	      files = search.listFiles(path, null,search.TYPE_FILE_OR_DIR, true, 0);
	      printFileList(files);
	      search.clear();

	      System.out.println("\n�����݂̓��t����A2���O�ȍ~�ɍX�V���ꂽ�t�@�C�����擾");
	      files = search.listFiles(path, null,search.TYPE_FILE, true, 2);
	      printFileList(files);
	      search.clear();

	      System.out.println("\n�����݂̓��t����A30���ȑO�̌Â��t�@�C�����擾");
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
