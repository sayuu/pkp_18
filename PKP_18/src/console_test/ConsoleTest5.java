package console_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import experiment1.Result;

import keywordProgramming.ExplanationVector;
import keywordProgramming.Function;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;
import logging.LogControl;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;
import state.KpRunningState;
/**
 * �^�X�N��1000���ɕ�����
 * @author sayuu
 *
 */
public class ConsoleTest5 {

	static String resultFolder = "C:\\Users\\sayuu\\Desktop\\exp\\Result\\";
	static String resultAllFolder = "C:\\Users\\sayuu\\Desktop\\Result All\\";
	
	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
	
	public static ArrayList<Function> original_functions = new ArrayList<Function>();
	
	public static keywordProgramming.Params para = new keywordProgramming.Params();
	
	public static String expFolder = "D://�����܂Ƃ�//testSite//����1000";
	
	public static final boolean IsConsoleTest = true;
	
	public static List<File> resultFiles = new ArrayList<File>();
	
	
	public static void main(String[] args) throws FileNotFoundException {
//		expFolder = args[0] + File.separator;
//		resultFolder = expFolder + "Result" + File.separator;
//		resultAllFolder = expFolder + "ResultAll" + File.separator;
//		String projectName = args[1];
		
		String projectName = "cc";
		
		String outputFolder = expFolder + File.separator + projectName +"200";
		
		List<TestSite> list = new ArrayList<TestSite>();
		
		list = getItems(expFolder + File.separator + projectName);
		
		separateTasks(projectName, outputFolder, list, 700);
		
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

	public static List<ResultFile> loadResultFiles(String path){
		List<ResultFile> list = new ArrayList<ResultFile>();
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

        File[] files = dir.listFiles();
        for(File file: files){
        	ResultFile t = new ResultFile(file, false);
        	list.add(t);
        }
        
        Collections.sort(list);
        
        
        for(ResultFile r: list){
        	System.out.println(r.name);
//        	System.out.println(r.output);
        }
        
		return list;
	}
	
	//count���ɕ�����B
	public static void separateTasks(String projectName, String outputFolder, List<TestSite> tasks, int count){
		
		int c = 0;
		String folder = "";
	    for (TestSite t: tasks) {
	       if(c % count == 0){
	    	   //�`�F���W�t�H���_�[(�t�H���_�쐬)
	    	   folder = outputFolder + File.separator + projectName + "-" +c;
	    	   File newdir = new File(folder);
	    	   newdir.mkdirs();
	       }
	       //�t�H���_�ɃR�s�[
		    try {
				copyTransfer(t.getTxtFile().getAbsolutePath(), folder + File.separator + t.getTxtFile().getName());
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
	       c++;
	    }

	}
	
	/**
	 * �R�s�[���̃p�X[srcPath]����A�R�s�[��̃p�X[destPath]��
	 * �t�@�C���̃R�s�[���s���܂��B
	 * �R�s�[�����ɂ�FileChannel#transferTo���\�b�h�𗘗p���܂��B
	 * ���A�R�s�[�����I����A���́E�o�͂̃`���l�����N���[�Y���܂��B
	 * @param srcPath    �R�s�[���̃p�X
	 * @param destPath    �R�s�[��̃p�X
	 * @throws IOException    ���炩�̓��o�͏�����O�����������ꍇ
	 */
	public static void copyTransfer(String srcPath, String destPath) 
	    throws IOException {
	    
	    FileChannel srcChannel = new
	        FileInputStream(srcPath).getChannel();
	    FileChannel destChannel = new
	        FileOutputStream(destPath).getChannel();
	    try {
	        srcChannel.transferTo(0, srcChannel.size(), destChannel);
	    } finally {
	        srcChannel.close();
	        destChannel.close();
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

		//�d�݂͌Œ�
		keywordProgramming.KeywordProgramming.BEST_R = 100;
		keywordProgramming.KeywordProgramming.HEIGHT = 3;
//		para.const_freq = 0;
//		para.w_arr.add(0, -0.05);//��
//		para.w_arr.add(1, 1.0);
//		para.w_arr.add(2, -0.01);//��
//		para.w_arr.add(3, 0.001);
//		para.w_arr.add(4, 0.001);
		
		double[] tmp_w = {-0.05, 1.0, -0.01, 0.001, 0.001};
		ExplanationVector.setWeights(tmp_w);
		ExplanationVector.setConstFreq(0.0);
		
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
				for(int k = 0; k< 6; k++){

					//��΂�
					if((i==1 && j==1 && k==0)||(i==1 && j==1 && k==1)||(i==1 && j==1 && k==2)){
						continue;
					}
				
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
						para.ld_const = 1;
						
						int k_list[] = {0, 1, 2, 4, 8};
						for(int m = 0; m < k_list.length; m++){
							para.ld_delete = 1;
							para.ld_replace = 1 + k_list[m];
							para.ld_add = 1 + 2 * k_list[m];
							long start = System.currentTimeMillis();
//							runTasksUsingMultiThread(list, para, projectName, start);			
							runTasks(list,  projectName, start);			
						}
					}else{
						//���Z�b�g�@���h���̂��߁B
						para.ld_delete = 0;
						para.ld_replace = 0;
						para.ld_add = 0;
						long start = System.currentTimeMillis();
//						runTasksUsingMultiThread(list, para, projectName, start);
						runTasks(list,  projectName, start);	
					}
				}
			}
		}
	}
	
	public static void runTasks(List<TestSite> testSites,
			String projectName, long startTime){
		
		long start = System.currentTimeMillis();

		//LogControl logControl = new LogControl(LogControl.LOCAL_SEARCH);
		String resultStr = "";

		/*
		 * �e�^�X�N�ɂ��āA���p�����[�^�ɂ�����o�͌����擾���Ă����B
		 */
		//�e�^�X�N�̏o�͌��Q�̒��ŁA�������o����������
		List<Integer> answerOrders = new ArrayList<Integer>();
		
		for(int i = 0; i < testSites.size(); i++){
			System.out.println((i+1)+ "/" + testSites.size());
			testSites.get(i).initKeywordProgramming();
			testSites.get(i).runKeywordProgramming(KpRunningState.LOCAL_SEARCH_BATCH);
			answerOrders.add(testSites.get(i).getAnswerNumber(9999));
			
			if(i % 1000 == 0){
				System.gc();				
			}
		}
			
		List<Result> result_list = new ArrayList<Result>(); 
		
//		resultStr += "  > �����^�X�N�̏��� > �o�͌`���F [ID, �~�����o��, ����], (���ʂ�0�Ԃ��琔����. -1�͌��Q���ɏo�����Ȃ��������Ƃ�\��.)");
		for(int i = 0; i < testSites.size(); i++){
			resultStr += "   " + testSites.get(i).getId()+ ", "+ testSites.get(i).getSelectedString() + ", " + answerOrders.get(i) + "\n";
			
			String str = testSites.get(i).getSelectedString();
			int odr = answerOrders.get(i);
//			resultStr += str + "\t" + odr);//id�폜�B�^�u��؂�ɕύX�B
			int numKey = testSites.get(i).getNumOfKeywords();
			int numLT = testSites.get(i).getNumOfLocalTypes();
			int numLF = testSites.get(i).getNumOfLocalFunctions();
			//generics ��r��
			if(!str.contains("<"))
				result_list.add(new Result(testSites.get(i).getId(), str, odr, numKey, numLT, numLF));
		}
//		resultStr += "  < �����^�X�N�̏��� < �o�͌`���F [ID, �~�����o��, ����], (���ʂ�0�Ԃ��琔����. -1�͌��Q���ɏo�����Ȃ��������Ƃ�\��.)");
		
		int sum_zero = 0;
		int sum_m_one = 0;
		int sum_others = 0;
		int sumKey = 0;
		int sumLT = 0;
		int sumLF = 0;
		
		int sum_within_tree = 0; //���3��
		int sum_within_five = 0; //���5��
		int sum_within_ten = 0; //���10��
		
		List<Result> result_list_others = new ArrayList<Result>(); 
		
		List<Result> result_list_incorrect = new ArrayList<Result>();//�ԈႢ�̌��ʂ�ێ�����B
		try{
        for(Result result: result_list) {
//			resultStr += result.fTestSiteId + "\t" + result.fSelectedString + "\t" + result.fAnswerOrder);
			sumKey += result.fNumOfKeywords; 
			sumLT += result.fNumOfLocalTypes;
			sumLF += result.fNumOfLocalFunctions;
			

			//���3���ȓ��ɐ������o������
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 3)
				sum_within_tree++;//���ʂ�0,1,2�̂Ƃ��B
			//���5���ȓ��ɐ������o������
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 5)
				sum_within_five++;
			//���10���ȓ��ɐ������o������
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 10)
				sum_within_ten++;
			
			
			if(result.fAnswerOrder == 0)
				sum_zero++;
			else if(result.fAnswerOrder == -1){
				sum_m_one++;
				result_list_incorrect.add(result);
			}else{
				sum_others++;
				result_list_others.add(result);
			}
					
        }
		}catch(Error e){
			e.printStackTrace();
		}
        		
		String flg_input = para.shortened_input_keywords;		resultStr += "  ���̓L�[���[�h���� =" + flg_input+"\n";
		String aimai = para.common_subsequence;
		resultStr += "  �����܂��L�[���[�h �Ή� =" + aimai+"\n";
		int ld_del = para.ld_delete;
		int ld_rep = para.ld_replace;
		int ld_add = para.ld_add;
		resultStr += "  LD = " + ld_del + ", " + ld_rep + ", " + ld_add+"\n";
		String bunkatu = para.separate_keywords;
		resultStr += "  �L�[���[�h���� =" + bunkatu+"\n";
		
		resultStr += "BEST_R = " + KeywordProgramming.BEST_R+"\n";
		resultStr += "�ő�̖؂̍��� = " + KeywordProgramming.HEIGHT+"\n";
		resultStr += ("���݂̓����̏d�� = ")+"\n";		
//		for(int i = 0; i < best_w.length; i++){
//			logControl.print(String.valueOf(best_w[i]) + ", ");
//		}
		resultStr += "alfa = " + ExplanationVector.getConstFreq()+"\n";
		resultStr += ""+"\n";
		resultStr += ""+"\n";
		
		resultStr += testSites.get(0).getPackageName()+"\n";
		resultStr += testSites.get(0).getFullyQualifiedClassName()+"\n";
		
		
		resultStr += "����\t" + result_list.size()+"\n";
		//resultStr += "2�Ԗڈȍ~�ɏo����\t\t\t\t\t" + sum_others)+"\n";
		
		resultStr += "���1�Ԗڂɏo����\t\t" + sum_zero+"\n";
		resultStr += "���3�Ԗڈȓ��ɐ����o��\t\t\t" + sum_within_tree+"\n";
		resultStr += "���5�Ԗڈȓ��ɐ����o��\t\t\t\t" + sum_within_five+"\n";
		resultStr += "���10�Ԗڈȓ��ɐ����o��\t\t\t\t\t" + sum_within_ten+"\n";
		resultStr += "�����o����\t\t\t\t\t\t" + (result_list.size() - sum_m_one)+"\n";
		double s_r_10 =  getScoreOfAnswerAppearancedOrderLimitX(answerOrders, 10);
				double s_r_30 =  getScoreOfAnswerAppearancedOrderLimitX(answerOrders, 30);
		resultStr += "���10�Ԗڈȓ��̋t���X�R�A\t\t\t\t\t\t\t" + s_r_10+"\n";
		resultStr += "���30�Ԗڈȓ��̋t���X�R�A\t\t\t\t\t\t\t\t" + s_r_30+"\n";


		resultStr += "�f�[�^"+"\n";
		
		String output = result_list.size() +","+sum_zero+","+sum_within_tree+","+sum_within_five+","+sum_within_ten+","+(result_list.size() - sum_m_one)+","+s_r_10+","+s_r_30;
		
		resultStr += output+"\n";
		
		resultStr += "�o�����Ȃ�������\t" + sum_m_one+"\n";
//		resultStr += "  > 2�Ԗڈȍ~�ɏo������")+"\n";
//		for(Result result: result_list_others){
//			resultStr += result.fSelectedString + "\t" + result.fAnswerOrder)+"\n";
//		}
//		resultStr += "  > 2�Ԗڈȍ~�ɏo������")+"\n";

		resultStr += "���σL�[���[�h��\t" + ((double)sumKey/result_list.size())+"\n";
		resultStr += "���σ��[�J���^��\t" + ((double)sumLT/result_list.size())+"\n";
		resultStr += "���σ��[�J���֐���\t" + ((double)sumLF/result_list.size())+"\n";
		resultStr += "���L�[���[�h��\t" + (sumKey)+"\n";
		resultStr += "�����[�J���^��\t" + (sumLT)+"\n";
		resultStr += "�����[�J���֐���\t" + (sumLF)+"\n";
		
		resultStr += ""+"\n";
		resultStr += "�����̑g�ݍ��킹x�ȉ��őS�T��. x=\t" + KeywordProgramming.COMBINATION_SIZE+"\n";

		resultStr += ""+"\n";
		
		long stop = System.currentTimeMillis();
		
		resultStr += " �����̎��s�ɂ�����������= " + (stop-start) + " �~���b�BLocalSearch.run"+"\n";
		
		
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
				export_pw.print(output + "\n" +resultStr);
				//�I������
				export_pw.flush();
				export_pw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		    
		
	}
	
	public static double getScoreOfAnswerAppearancedOrderLimitX(List<Integer> order_list, int x){
		double score = 0.0;
		for(Integer i: order_list){
			if(i != -1 && i <= x)	//�o�����Ȃ���΃X�R�A��0 , i > x�͕]�����Ȃ��B
				score += 1.0 / (i + 1);
		}
		return score;
	}
	
//	public static void runTasksUsingMultiThread(List<TestSite> list,
//			keywordProgramming.exp.Params para, String projectName, long startTime) {
//		int max_thread_num = 3;
//		if(list.size() < max_thread_num){
//			max_thread_num = list.size();
//		}
//	    TsThread thread[];
//	    TsSync sync;
//	    
//	    // �����I�u�W�F�N�g�̍쐬
//	    sync = new TsSync();
//
//	    // �q�X���b�h���z��̍쐬
//	    thread = new TsThread[max_thread_num];
//
//	    // �X���b�h�̋N��
//	    for (int i = 0; i < list.size(); ) {
//	    	
//	    	for(int j = 0; j < max_thread_num; j++){
//	    		if(i >= list.size()){
//	    			break;
//	    		}
//		    	thread[j] = new TsThread(sync, para, list.get(i), projectName, startTime);
//			    thread[j].start();
//			    			    
//			    i++;
//	    	}
//		      
//		    try {
//			 Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO �����������ꂽ catch �u���b�N
//			 e.printStackTrace();
//			}
//		      
//		    // �q�̏I����҂�
//		    sync.waitSync();
//		    
//	    }
//	   String name = "";
//			name += projectName;
//			name += ",";
//			name += startTime;//�J�n���ԁ@
//			name += ",";
//			name += para.shortened_input_keywords;
//			name += ",";
//			name += para.separate_keywords;
//			name += ",";
//			name += para.common_subsequence;
//			name += ",";
//			name += para.ld_delete;
//			name += ",";
//			name += para.ld_replace;
//			name += ",";
//			name += para.ld_add;
//			
//		//�o�̓e�L�X�g�t�@�C���̍쐬
//    	String savefilename = resultFolder + name + ".txt";
//    	
//    	//�t�@�C����������΍쐬����
//    	File txtFile = new File(savefilename);
//    	if (!txtFile.exists()){
//			try {
//				txtFile.createNewFile();
//			} catch (IOException e1) {
//				// TODO �����������ꂽ catch �u���b�N
//				e1.printStackTrace();
//			}
//    	}
//    	
//		//�t�H���_���Ȃ���΍쐬����B
//		File dir = txtFile.getParentFile();
//		if (!dir.exists()) {  
//		    dir.mkdirs();
//		}
//		
//		try{
//			PrintWriter export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
//			//��������
//			export_pw.print(sync.output);
//			//�I������
//			export_pw.flush();
//			export_pw.close();
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//	    
//	}
}

