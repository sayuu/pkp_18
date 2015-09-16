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
 * ���O�����̂��߂̃N���X
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
	
	private static boolean export_flg = false;	//�t�@�C���o��
	private static String export_folder = 	PreferenceInitializer.LogFileFolder;//�t�@�C���o�̓t�H���_
	
	//�o�̓e�L�X�g�t�@�C���p
	private File txtFile;
	
	//�o�̓e�L�X�g�t�@�C���pPrintWriter
  	private PrintWriter export_pw;
  	
	private String place;
	
	//�R���\�[��
	private static MessageConsole console;
	//�R���\�[���}�l�[�W���[
	private static IConsoleManager consoleManager;
	//�R���\�[���X�g���[��
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
			keywordProgramming = false;//static�ϐ��������xtrue�ɂ���Ǝ��ɃI�u�W�F�N�g�𐶐������Ƃ���true�̂܂܂ł��邩�炱�̕��K�v

		//�o�̓e�L�X�g�t�@�C���̍쐬
		if(export_flg){
			long millis = System.currentTimeMillis();
			String path = export_folder + "/" + millis + ".txt";//���Ԃŋ��
			//�ꏊ��path���Ɋ܂߂�.
	  		if(place.equals(this.KP) && keywordProgramming){
	  			path = export_folder + "/" + millis + "_" + this.KP + ".txt";
	  		}else if(place.equals(this.LOCAL_SEARCH) && localSearch){
	  			path = export_folder + "/" + millis + "_" + this.LOCAL_SEARCH + ".txt";
	  		}else if(place.equals(this.GRID_SEARCH) && gridSearch){
	  			path = export_folder + "/" + millis + "_" + this.GRID_SEARCH + ".txt";
	  		}
	  		
			txtFile = new File(path);
			
			//�t�H���_���Ȃ���΍쐬����B
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
			//������t�@�C���̃T�C�Y��0�ł���΁A�폜����.
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
		//�o�̓R���\�[���̗p��
		console = new MessageConsole("�L�[���[�h�v���O���~���O", null);
		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		consoleManager.addConsoles(new IConsole[]{console});
		consoleStream = console.newMessageStream();
	}
	
	/*
	 * �ǂ̃��O���o�͂��A�ǂ̃��O���o�͂��Ȃ�����\������B
	 */
	public void printLogState(){
		String s = ">>> ���O�o�͂̐ݒ� >>>\n";
		
		s += " ���[�J���T�[�`: ";
		s += onOffString(localSearch);
		
		s += " ���[�J���T�[�` 1�X�e�b�v���̏��: ";
		s += onOffString(localSearch_stepbystep);
		
		s += " ���[�J���T�[�` 1�X�e�b�v���̊e�ߖT�̏��: ";
		s += onOffString(localSearch_neighbours);
		
		s += " �O���b�h�T�[�`: ";
		s += onOffString(gridSearch);
		
		s += " �L�[���[�h�v���O���~���O ��{���: ";
		s += onOffString(kp_basic);

		s += " �L�[���[�h�v���O���~���O �^�ꗗ: ";
		s += onOffString(kp_types);

		s += " �L�[���[�h�v���O���~���O �֐��ꗗ: ";
		s += onOffString(kp_functions);
		
		s += " �L�[���[�h�v���O���~���O �o�͌��Q�ꗗ: ";
		s += onOffString(kp_results);
		
		s += "<<< ���O�o�͂̐ݒ� <<<\n\n";
		
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
