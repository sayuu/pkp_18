package console_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import keywordProgramming.exp.Function;
import keywordProgramming.exp.Type;

import plugin.preference.PreferenceInitializer;
import plugin.testSite.exp.TestSite;
import plugin.testSite.exp.TestSiteComparator;
import plugin.testSite.exp.TsSync;
import plugin.testSite.exp.TsThread;

public class ConsoleTest2 {

	static String resultFolder = "C:\\Users\\sayuu\\Desktop\\exp\\Result\\";
	
	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
	
	public static ArrayList<Function> original_functions = new ArrayList<Function>();
	
	public static void main(String[] args) throws FileNotFoundException {
		List<TestSite> list = new ArrayList<TestSite>();
		String expFolder = "C:\\Users\\sayuu\\Desktop\\exp\\";
		String cc = "cc";
		String cc1 = "cc1";
		
		

			//��{�֐��ƌ^�̓��[�h���Ă����B
			
			FileReader in = new FileReader(expFolder + "sub_class.txt");
	        BufferedReader c_r = new BufferedReader(in);
	        FileReader in1 = new FileReader(expFolder + "function.txt");
			BufferedReader f_r = new BufferedReader(in1);
			
			//�t�@�C������̃��[�h�͈�񂾂����B
			loadOriginalFiles(c_r, f_r);
			
			list = getItems(expFolder + cc);
			
			run(list, "cc");
		
	}
	
	//�t�@�C���̓ǂݍ���(�^�A�֐�)
	public static void loadOriginalFiles(BufferedReader c_r, BufferedReader f_r){
		String s;

		//classes�t�@�C���̓ǂݍ���
		try {
			while ((s = c_r.readLine()) != null) {
				Type t = new Type(s);
				original_types.put(t.getName(), t);
			}
			//c_r.close();
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
		//functions�t�@�C���̓ǂݍ���
		try {
			while ((s = f_r.readLine()) != null) {
				Function f = new Function(s);
				original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	public static List<TestSite> getItems(String path){
		List<TestSite> list = new ArrayList<TestSite>();
				
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        for(File file: files){
	        	TestSite t = new TestSite(file, false);
	        	list.add(t);
	        }
	    }
	    
	  //���Ԃ̍~���Ƀ\�[�g����B
	    TestSiteComparator comp = new TestSiteComparator();
	    Collections.sort(list, comp);
	    
		return list;
	}
	
	public static void run(List<TestSite> list, String projectName){
	
		keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
		
		//�d�݂͌Œ�
		keywordProgramming.exp.KeywordProgramming.BEST_R = 100;
		keywordProgramming.exp.KeywordProgramming.HEIGHT = 3;
		para.const_freq = 0;
		para.w_arr.add(0, -0.05);//��
		para.w_arr.add(1, 1.0);
		para.w_arr.add(2, -0.01);//��
		para.w_arr.add(3, 0.001);
		para.w_arr.add(4, 0.001);
		
		//�L�[���[�h����(5���)
		//�Ȃ�
		//�ꉹ�폜, �擪�R�����A�P�����u���A�P�����}��
		for(int i = 0; i< 5; i++){
			
			switch(i){
			case 0:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_OFF;
				break;
			case 1:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL;
				break;
			case 2:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3;
				break;
			case 3:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE;
				break;
			case 4:
				para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD;
				break;
			}
			
			//��������(2���)
			//off, on
			for(int j = 0; j< 2; j++){
				
				switch(j){
				case 0:
					para.separate_keywords = "false";
					break;
				case 1:
					para.separate_keywords = "true";
					break;
				}
				
				//�ގ��x(6��� ld�͕ϐ��𓮂���)
				// �Ȃ��B
				// LCS1,2,3,4
				// LD (�폜�A�ǉ��A�u��)�R�X�g�𓮂����B(k=0 1 2 4 8)�Ȃ�5�ʂ�
				// �v�A10���
				for(int k = 5; k< 6; k++){
					
//					switch(k){
//					case 0:
//						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_OFF;
//						break;
//					case 1:
//						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2;
//						break;
//
//					}
					
					switch(k){
					case 0:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_OFF;
						break;
					case 1:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1;
						break;
					case 2:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2;
						break;
					case 3:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3;
						break;
					case 4:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4;
						break;
					case 5:
						para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LD;
						break;
					}
					
					if(k == 5){
						int k_list[] = {0, 1, 2, 4, 8};
						for(int m = 0; m < k_list.length; m++){
							para.ld_delete = 1;
							para.ld_replace = 1 + k_list[m];
							para.ld_add = 1 + 2 * k_list[m];
							long start = System.currentTimeMillis();
							runTasksUsingMultiThread(list, para, projectName, start);			
						}
					}else{
						//���Z�b�g�@���h���̂��߁B
						para.ld_delete = 0;
						para.ld_replace = 0;
						para.ld_add = 0;
						long start = System.currentTimeMillis();
						runTasksUsingMultiThread(list, para, projectName, start);
					}
				}
			}
		}
	}
	
	public static void runTasksUsingMultiThread(List<TestSite> list,
			keywordProgramming.exp.Params para, String projectName, long startTime) {
		int max_thread_num = 3;
		if(list.size() < max_thread_num){
			max_thread_num = list.size();
		}
	    TsThread thread[];
	    TsSync sync;
	    
	    // �����I�u�W�F�N�g�̍쐬
	    sync = new TsSync();

	    // �q�X���b�h���z��̍쐬
	    thread = new TsThread[max_thread_num];

	    // �X���b�h�̋N��
	    for (int i = 0; i < list.size(); ) {
	    	
	    	for(int j = 0; j < max_thread_num; j++){
	    		if(i >= list.size()){
	    			break;
	    		}
		    	thread[j] = new TsThread(sync, para, list.get(i), projectName, startTime);
			    thread[j].start();
			    			    
			    i++;
	    	}
		      
		    try {
			 Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO �����������ꂽ catch �u���b�N
			 e.printStackTrace();
			}
		      
		    // �q�̏I����҂�
		    sync.waitSync();
		    
	    }
	   String name = "";
			name += projectName;
			name += ",";
			name += startTime;//�J�n���ԁ@
			name += ",";
			name += para.shortened_input_keywords;
			name += ",";
			name += para.separate_keywords;
			name += ",";
			name += para.common_subsequence;
			name += ",";
			name += para.ld_delete;
			name += ",";
			name += para.ld_replace;
			name += ",";
			name += para.ld_add;
			
		//�o�̓e�L�X�g�t�@�C���̍쐬
    	String savefilename = resultFolder + name + ".txt";
    	
    	//�t�@�C����������΍쐬����
    	File txtFile = new File(savefilename);
    	if (!txtFile.exists()){
			try {
				txtFile.createNewFile();
			} catch (IOException e1) {
				// TODO �����������ꂽ catch �u���b�N
				e1.printStackTrace();
			}
    	}
    	
		//�t�H���_���Ȃ���΍쐬����B
		File dir = txtFile.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			PrintWriter export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
			//��������
			export_pw.print(sync.output);
			//�I������
			export_pw.flush();
			export_pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	    
	}
}

