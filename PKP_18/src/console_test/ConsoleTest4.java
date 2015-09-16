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
import keywordProgramming.Params;
import keywordProgramming.Type;
import logging.LogControl;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;
import state.KpRunningState;
/**
 * ���ʐ���
 * @author sayuu
 *
 */
public class ConsoleTest4 {

	static String resultFolder = "C:\\Users\\sayuu\\Desktop\\exp\\Result\\";
	static String resultAllFolderLCS = "C:\\Users\\sayuu\\Desktop\\Result All lcs\\";
	static String resultAllFileLCS = "C:\\Users\\sayuu\\Desktop\\Result All lcs\\all.txt";
	static String resultAllFolderLD = "C:\\Users\\sayuu\\Desktop\\Result All ld\\";
	static String resultAllFileLD = "C:\\Users\\sayuu\\Desktop\\Result All ld\\all.txt";
	static String resultAllFileLD2 = "C:\\Users\\sayuu\\Desktop\\Result All ld\\all2.txt";
	
	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
	
	public static ArrayList<Function> original_functions = new ArrayList<Function>();
	
	public static keywordProgramming.Params para = new keywordProgramming.Params();
	
	public static String expFolder = "C:\\Users\\sayuu\\Desktop\\Result All lcs\\";
	
	public static final boolean IsConsoleTest = true;
	
	public static boolean flg_percent = true;
	
	public static boolean flg_raw_data = true;
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		//getResult("j60", true);
		
//		flg_percent = false;
//		getAllResultLD();
		
//		
//		flg_percent = false;
//		getFinalResult("all", true);
		getExcelResult();
	}
	
	public static void getExcelResult(){
		List<ResultFile> list = new ArrayList<ResultFile>();
		list = loadResultAllFile(resultAllFileLD2);
		//���10���ڂ̌��ʂ�����k,�c��C�ŕ��ׂ�B
		
		//���͕��@5���*�����Q���
		for(int i = 0; i < 10; i++){
			int result_num = 5+30*i;	//0~4��LCS�Ȃ̂ŃX�L�b�v����B
			double c_list[] = {1.0, 1.5, 2.33, 4.0, 9.0};
			for(int n = 0; n < c_list.length; n++){
				int k_list[] = {0, 1, 2, 4, 8};
				for(int m = 0; m < k_list.length; m++){
					String sum10 = list.get(result_num).output.outputPerSum10();
					System.out.print(sum10 + ",");
					result_num++;
				}
				System.out.println();
			}
		}
	}
	
	private static void getAllResultLD() {
		String projectName = "";
		projectName = "cc0";
		getResult(projectName, true);
		
		projectName = "carol";
		for(int i = 0; i <= 2000; i+=500){
			getResult(projectName + String.valueOf(i), true);
		}
		
		projectName = "junit";
		for(int i = 0; i <= 3000; i+=500){
			getResult(projectName + String.valueOf(i), true);
		}
		
		projectName = "s";
		for(int i = 0; i <= 9500; i+=500){
			getResult(projectName + String.valueOf(i), true);
		}
		
		String j_name = "jMemorize";
		String[] j_arr = {"j10","j11","j12","j13","j14","j15","j20","j21","j30","j31","j40","j41","j50","j51","j60","j61"};
		
		for(int i = 0; i < j_arr.length; i++){
			projectName = j_arr[i];
			getResult(projectName, true);
		}
	}

	private static void getAllResultLCS() {
		String projectName = "";
		projectName = "cc\\cc";
		getResult(projectName, false);
		projectName = "carol\\carol";
		getResult(projectName, false);
		projectName = "junit\\junit";
		getResult(projectName, false);
		
		String j_name = "jMemorize";
		String[] j_arr = {"j10","j11","j12","j13","j14","j15","j20","j21","j30","j31","j40","j41","j50","j51","j60","j61"};
		
		for(int i = 0; i < j_arr.length; i++){
			projectName = j_name + File.separator + j_arr[i];
			getResult(projectName, false);
		}
		
		String s_name = "sphinx";
		String[] s_arr = {"s10","s11","s12","s13","s20","s21","s22","s23","s30","s31","s32"};
		
		for(int i = 0; i < s_arr.length; i++){
			projectName = s_name + File.separator + s_arr[i];
			getResult(projectName, false);
		}
	}
	
	private static void getResult(String projectName, boolean isLd) {
		List<ResultFile> list = new ArrayList<ResultFile>();
		if(isLd)
			list = loadResultFiles(resultAllFolderLD + projectName);
		else
			list = loadResultFiles(resultAllFolderLCS + projectName);
		
		if(list != null)
			run(list, projectName);
	}
	
	private static void getFinalResult(String projectName, boolean isLd) {
		List<ResultFile> list = new ArrayList<ResultFile>();
		if(isLd)
			list = loadResultAllFile(resultAllFileLD);
		else
			list = loadResultAllFile(resultAllFileLCS);
		run(list, projectName);
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

	public static List<ResultFile> loadResultAllFile(String path){
		
	    File file = new File(path);
	    if (!file.exists() || !file.isFile()) {  
	    	return null;
		}

	    List<ResultFile> rlist = new ArrayList<ResultFile>();
	    
		try {
			FileReader fr = new FileReader(file);
			
			List<List<ResultFile>> listlist = new ArrayList<List<ResultFile>>();
			BufferedReader br = new BufferedReader(fr);
			String s;
			List<ResultFile> list = new ArrayList<ResultFile>();
			while ((s = br.readLine()) != null) {
				ResultFile r = new ResultFile(s);
				list.add(r);
				if(list.size() == 300){
					listlist.add(new ArrayList<ResultFile>(list));
					list.clear();
				}
			}
			
			for(int i=0; i<300;i++){
				ResultFile all_r = new ResultFile(null, false);
				for(int j=0; j< listlist.size(); j++){
					ResultFile r = listlist.get(j).get(i);
//					System.out.println(r.output.toString());
					if(j==0){
						all_r.output = r.output;
						all_r.para = r.para;
						
					}else{
						all_r.output.plus(r.output);
					}
				}
				rlist.add(all_r);
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return rlist;
	}
	
	public static List<ResultFile> loadResultFiles(String path){
		List<ResultFile> list = new ArrayList<ResultFile>();
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

        File[] files = dir.listFiles();
        for(File file: files){
        	if(file.isDirectory()){
        		continue;
        	}
        	ResultFile t = new ResultFile(file, true);
        	list.add(t);
        }
        
        Collections.sort(list);
        
        
        for(ResultFile r: list){
//        	System.out.println(r.name);
//        	System.out.println(r.output);
        }
        
		return list;
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
	
	public static void run(List<ResultFile> list, String projectName){

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
				
				if(flg_raw_data == false)
				System.out.println("���̓L�[���[�h����,�L�[���[�h����,�����܂��L�[���[�h�Ή�,�폜,�u��,�}��,LDC,LDK,����,1�Ԗ�,3�Ԗ�,5�Ԗ�,10�Ԗ�,�Ō���܂�,���10�Ԗ�,���30�Ԗ�");
				//�ގ��x(6��� ld�͕ϐ��𓮂���)
				// �Ȃ��BLCS1,2,3,4�B 5�ʂ�B
				// LD (�폜�A�ǉ��A�u��)�R�X�g�𓮂����B(k=0 1 2 4 8)�Ȃ�5�ʂ� * (c=5�ʂ�)
				// �v�A25�ʂ�B
				//5+25=30�ʂ�B
				for(int k = 0; k< 6; k++){
	
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
						double c_list[] = {1.0, 1.5, 2.33, 4.0, 9.0};
						
						for(int n = 0; n < c_list.length; n++){
							
							para.ld_const = c_list[n];
							int k_list[] = {0, 1, 2, 4, 8};
							for(int m = 0; m < k_list.length; m++){
								para.ld_delete = 1;
								para.ld_replace = 1 + k_list[m];
								para.ld_add = 1 + 2 * k_list[m];
								para.ld_k = k_list[m];
								boolean flg = printResults(list, para, projectName);
								
							}
						}
					}else{
						//���Z�b�g
						para.ld_delete = 0;
						para.ld_replace = 0;
						para.ld_add = 0;
						para.ld_const = 0.0;
						boolean flg = printResults(list,  para, projectName);
						if(flg_raw_data == false)
						if(!flg)
						System.out.println("false!");
					}
				}
				if(flg_raw_data == false)
				System.out.println("==============================");
			}
		}
	}
	
	public static boolean printResults(List<ResultFile> list, Params para, String projectName){
		int flg = 0;
		List<ResultFile> list2 = new ArrayList<ResultFile>();
		
		for(ResultFile r:list){
			if(r.matchParams(para)){
				para.print();
				if(flg_percent)
					System.out.println("," + r.output.outputPercentPer100());
				else
					System.out.println("," + r.output.toString() );
				flg++;
				list2.add(r);
			}
		}
		
		if(flg > 1){
			System.out.println("�d������!");
			for(ResultFile r:list2){
				para.print();
				if(flg_percent)
					System.out.println("," + r.output.outputPercentPer100());
				else
					System.out.println("," + r.output.toString());
			}
			return false;
		}
		
		if(flg > 0)
			return true;
		
		para.print();
		System.out.println(",�Y���t�@�C���Ȃ��B");
		return false;
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

