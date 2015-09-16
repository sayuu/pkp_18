package methodFreqCount;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/*
 * �t�@�C���p�X�ɒu����.class�t�@�C���ɋL�q����Ă���A
 * �@�@�e�֐��̎g�p�p�x���v�Z����N���X�B
 * 
 * ���s�́A���̃t�@�C���ł͂Ȃ��A
 * BcelReadClassSample �����s����B
 * 
 */
public class Test {
	
	//file_path �ɉ�͂������A.class�t�@�C����u���B
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
		// �����Ɋe�t�@�C���ɑ΂��鏈�����L�q
			if(file.getName().endsWith(".class") == false){
				return;
			}
			//�p�^�[���}�b�`�F�N���X����'$'���܂܂�Ă���Ώ��O
			if(file.getName().indexOf('$') > -1){
				return;
			}
			file_list.add(file);
		}
	}
}
