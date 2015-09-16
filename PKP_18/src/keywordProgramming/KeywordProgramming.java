package keywordProgramming;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.preference.IPreferenceStore;

import ast.Import;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import search.Node;
import search.State;
import state.KpRunningState;

import logging.LogControl;

/**
 * �L�[���[�h�v���O���~���O�̎�v�ȂR�̃A���S���Y��
 *  ���I�v��@(DynamicProgram)�A
 *  GetBestExplTreeForFunc�A
 *  ExtractTree
 * ���L�q�����N���X�B
 * 
 * @author sayuu
 *
 */
public class KeywordProgramming {

	//���I�v��@�̕\�̍���
	public static int HEIGHT = 3;
	
	//���I�v��@�̕\�̂P�̌�_�ɕێ�����u���v�֐��؂̌�
	public static int BEST_R = 25;

	//�����T���̕��@
	public static String SEARCH_MODE_FUNCTION_PARAMETER = "SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL";
	
	//�S�T����������ƂȂ�A�֐��̈����̑g�ݍ��킹�̐�
	public static int COMBINATION_SIZE = 5;
	//�S�T����������ƂȂ�A�֐��̈����̐�
	public static int MAX_ARGUMETNT_SIZE = 5;
	
	//�ŗǗD��T�����s����
	public static int BEST_FIRST_SIZE = 5;

	//�����f�[�^�x�[�X�̃t�@�C�����B�@�^�B
	public static final String SUB_CLASS_FILE_NAME = "sub_class.txt";
	
	//�����f�[�^�x�[�X�̃t�@�C�����B�@�֐��B
	public static final String FUNCTION_FILE_NAME = "function.txt";
	
	//�����f�[�^�x�[�X�̃t�@�C�����B�@�p�x
	public static final String FREQUENCY_FILE_NAME = "frequency.txt";

	//���I�v��@�ɂ���č쐬�����\��\���I�u�W�F�N�g�AbestRoots
	private BestRoots bestRoots;
	
	//�A���S���Y���Ɏg�p�����S�Ă̌^��ێ�����TreeMap
	private TreeMap<String, Type> types = new TreeMap<String, Type>();
	
	//�A���S���Y���Ɏg�p�����S�Ă̊֐���ێ�����ArrayList
	private ArrayList<Function> functions = new ArrayList<Function>();
		
//	//�����f�[�^�x�[�X�ɑ��݂���S�Ă̌^��ێ�����TreeMap
//	public static TreeMap<String, Type> original_types = new TreeMap<String, Type>();
//	
//	//�����f�[�^�x�[�X�ɑ��݂���S�Ă̊֐���ێ�����ArrayList
//	public static ArrayList<Function> original_functions = new ArrayList<Function>(1);

	//�C���|�[�g���̌^��ێ�����TreeMap
	private TreeMap<String, Type> imported_types = new TreeMap<String, Type>();
		
	//�C���|�[�g���̊֐���ێ�����ArrayList
	private ArrayList<Function> imported_functions = new ArrayList<Function>();

	//�p�x
	public ArrayList<Frequency> frequencies = new ArrayList<Frequency>();
	
	public HashMap<String, List<FunctionTree>> sorted_ftree_map = new HashMap<String, List<FunctionTree>>();
	
	/*
	 * �ŏI�I�ȏo�͊֐��؂̗�B
	 * �L�[�����̊֐��؂̏��ʁB
	 * �l�����̊֐��؁B
	 * �]���l���Ƀ\�[�g����Ă���B
	 */
	private FunctionTree[] outputFunctionTrees;

	//���̓L�[���[�h��List
	private List<String> input_keywords;
	
	//���̓L�[���[�h��List
	private List<Word> input_keywords_word;
	
	//���̓L�[���[�h�̎�ނ�List (���\�b�h���Ƀ}�b�`�A�ϐ����Ƀ}�b�`)
	private List<String> input_keyword_types;
		
	//input�̒P��̓��A����̒P�ꂻ�ꂼ��̌�
	private HashMap<String, Integer> numOfSameWords = new HashMap<String, Integer>();

	//�ގ��x�v�Z�ɗv���鎞��
	static long time_consumed_getting_similarity = 0;
	
	public BestRoots getBestRoots(){
		return bestRoots;
	}

	public void setBestRoots(BestRoots br){
		bestRoots = br;
	}

	public synchronized void addType(Type t){
		types.put(t.getName(), t);
	}

	public TreeMap<String, Type> getTypes(){
		return types;
	}

	public synchronized void addFunction(Function f){
		functions.add(f);
	}
	public ArrayList<Function> getFunctions(){
        return functions;
    }
	//Classes�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
	public synchronized void readClassFileAndAddList(String input_txt_file_name){
		FileReader fr;

		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				Type t = new Type(s);
				types.put(t.getName(), t);
//				original_types.put(t.getName(), t);
			}
			br.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}

	//Functions�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
	public synchronized void readFunctionFileAndAddList(String input_txt_file_name){
		FileReader fr;
		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				Function f = new Function(s);
				functions.add(f);
//				original_functions.add(f);
			}
			br.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}
	
	//�t�@�C���̓ǂݍ��� (�^�A�֐��A�p�x)
	public void loadFiles(BufferedReader class_r, BufferedReader function_r, BufferedReader freq_r){
		loadFreqFiles(freq_r);
		loadClassFiles(class_r);
		loadFunctionFilesWithFrequency(function_r);
	}
	
	//�t�@�C���̓ǂݍ���(�^)
	public synchronized void loadClassFiles(BufferedReader c_r){
		String s;

		//classes�t�@�C���̓ǂݍ���
		try {
			while ((s = c_r.readLine()) != null) {
				Type t = new Type(s);
				types.put(t.getName(), t);
//				original_types.put(t.getName(), t);
			}
			//c_r.close();
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
	}
	
	//�t�@�C���̓ǂݍ���(�֐�)
	public synchronized void loadFunctionFiles(BufferedReader f_r){
		String s;

		//functions�t�@�C���̓ǂݍ���
		try {
			while ((s = f_r.readLine()) != null) {
				Function f = new Function(s);
				functions.add(f);
//				original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
	}
	
	//�t�@�C���̓ǂݍ���(�֐��ɕp�x���ǉ�)
	public synchronized void loadFunctionFilesWithFrequency(BufferedReader f_r){
		String s;

		//functions�t�@�C���̓ǂݍ���
		try {
			while ((s = f_r.readLine()) != null) {
				
				Function f = new Function(s);
				//�p�x�ǉ�
				f.setFrequency(frequencies);
				
				functions.add(f);
//				original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
	}
	
	//�t�@�C���̓ǂݍ���(�^�A�֐�)
	//original�͂�߂āAtypes ��fuctions�ɒ��ړ����B
	public synchronized void loadFiles(BufferedReader c_r, BufferedReader f_r){
		String s;

		//classes�t�@�C���̓ǂݍ���
		try {
			while ((s = c_r.readLine()) != null) {
				Type t = new Type(s);
				types.put(t.getName(), t);
				//original_types.put(t.getName(), t);
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
				functions.add(f);
				//original_functions.add(f);
				
			}
			//f_r.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
	}
	
	//�t�@�C���̓ǂݍ��� �p�x
	public void loadFreqFiles(BufferedReader fr_r){
		String s;

		//�t�@�C���̓ǂݍ���
		try {
			while ((s = fr_r.readLine()) != null) {
				
				Frequency fr = new Frequency(s);
				frequencies.add(fr);
			}
			//fr_r.close();
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
		
		
	}
	
	/*
	 * �w��񐔈ȏ�̕p�x�����֐����𒲂ׂ�
	 */
	public boolean IsFunctionWithXtimesFrequency(String func_s, int x_times){
		
		for(Function f: functions){
			if(f.toDBString().equals(func_s)){
				if (f.getFrequency() >= x_times)
					return true;
				else
					return false;
			}
		}
		
		//�Y������֐������݂��Ȃ��I
		//throw new Error();
		//System.out.println("�p�x�t�@�C���ɑ��݂��Ȃ�: " + func_s);
		return false;
	}
	
//	public static int import_function_size = 0;
//	public static int import_type_size = 0;
	
	//�N���A����B
	public synchronized void clearStaticTypeAndFunctionFields(){
		types.clear();
		for(Function f:functions){
			f.setIsDummy(false);
		}
		functions.clear();		
	}
	
	//�N���A����B
	public void clearImportStaticFields(){
		imported_types.clear();
		imported_functions.clear();		
	}
	
	//�ǉ�����B
	public synchronized void putAllImportStaticFields(){
		types.putAll(imported_types);
		functions.addAll(imported_functions);	
	}
	
	//�ǉ�����B
	public synchronized void putAllOriginalStaticFields(){
//		types.putAll(original_types);
//		functions.addAll(original_functions);	
	}
	
	public List<String> getImportedFunctions(){
		List<String> list = new ArrayList<String>();
		
		for(Function f :imported_functions){
			list.add(f.toDBString());
		}
		return list;
	}
	
	public List<String> getImportedTypes(){
		List<String> list = new ArrayList<String>();
		
		for(Type t :imported_types.values()){
			list.add(t.toDBString());
		}
		return list;
	}
	
	//�p�x��Function�ɃZ�b�g����
	public void setFreqToFunction(List<String> functions){
		List<Function> localFunctions = new ArrayList<Function>();
		for(String s: functions){
			Function f = new Function(s);
			
			f.setFrequency(frequencies);
			
			localFunctions.add(f);
			//System.out.println(f.getFrequency() + "," + f.toDBString());
		}
		
		
		ArrayList<Function> new_funcs = new ArrayList<Function>();
		TreeMap<String, Type> new_types = new TreeMap<String, Type>();
		addNewFunctions2(localFunctions, new_types , new_funcs);
		
//		import_type_size = new_types.size();
//		import_function_size = new_funcs.size();
		
//		System.out.println("type: " + new_types.size());
//		System.out.println("func: " + new_funcs.size());
	}
	
	//�p�x��Function�ɃZ�b�g����
	public synchronized void setImportedClassAndFunction(List<String> classes, List<String> functions){
		
		//�N���X�̒ǉ� (�d���𒲂ׂ�)
		for(String s: classes){
			Type t = new Type(s);
			if(!types.containsKey(t.getName())){
				types.put(t.getName(), t);
				imported_types.put(t.getName(), t);
			}else{
				//�L�[������Ƃ��A
				//subtype��ǉ�����B
				types.get(t.getName()).addSubTypes(t.getSubTypes());
			}
		}
		
		List<Function> tmp_imported_functions = new ArrayList<Function>();
		
		//�֐��ɕp�x����t������B
		for(String s: functions){
			Function f = new Function(s);
			
			f.setFrequency(frequencies);
			
//			if(f.getName().contains("addResultListener")){
//				System.out.println();
//			}
			
			tmp_imported_functions.add(f);
			//System.out.println(f.getFrequency() + "," + f.toDBString());
		}

		//functions �� imported_functions��ǉ�����B
		//�֐��̈����̌^�𒲂ׂāAtypes�̊Ԃ̏d�����Ȃ��Ȃ������̂��Aimported_types �ɓ���B
		//tmp_imported_functions ��functions�̊Ԃ̏d�����Ȃ��Ȃ������̂��Aimported_functions �ɓ���B
		addNewFunctions2(tmp_imported_functions, imported_types, imported_functions);

	}
	
	private boolean containsType(String appear){
		Collection<Type> values = types.values();
		for(Type type: values){
			if(type.getName().equals(appear)){
				return true;
			}
		}
		return false;
	}

//	public static FunctionTree getOutputFunctionTree(int i){
//		return outputFunctionTrees.get(i);
//	}

	public FunctionTree[] getOutputFunctionTrees(){
		return outputFunctionTrees;
	}


	//�v���O�C���̒�����L�[���[�h�v���O���~���O�𓮂����B
	public FunctionTree[] execute(List<Word> keywords, String desiredType, List<String> localTypes, List<String> localFunctions, String state){

		long start = System.currentTimeMillis();
		time_consumed_getting_similarity = 0;
		
		ArrayList<Type> new_types = new ArrayList<Type>();
		ArrayList<Function> new_funcs = new ArrayList<Function>();
//types = 146
//functions = 2919
//		for(Type t: types.values()){
//			System.out.println("  " + t.toDBString());
//		}
//localTypes = 49
//localfunctions = 0
		if(localTypes != null)
		for(String s: localTypes){
//			System.out.println(s);
			Type t = new Type(s);
			if(!types.containsKey(t.getName())){
				types.put(t.getName(), t);
				new_types.add(t);
			}else{
				//�L�[������Ƃ��A
				//subtype��ǉ�����B
				types.get(t.getName()).addSubTypes(t.getSubTypes());
			}
		}
//new_types = 14
		//�V�����֐��̒ǉ�
		addNewFunctions(localFunctions, new_types, new_funcs);

		//�L�[���[�h�̏����B
		inputKeywords(keywords);
		
		//�L�[���[�h�ƃ��x���̃}�b�`�ɂ��_��������B
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		boolean flg_compact = store.getBoolean(PreferenceInitializer.GROUPING_FUNCTIONS);
//
//		if(flg_compact){
//			compactFunctions();
//		}
		
		//bestRoots�𐶐�����B
		bestRoots = new BestRoots(types);

		//�����T�����@�̎擾
		//SEARCH_MODE_FUNCTION_PARAMETER = store.getString(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER);
		SEARCH_MODE_FUNCTION_PARAMETER = PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL;
		
		//DynamicProgram �A���S���Y�������s
		DynamicProgram();
		
		//ExtractTree �A���S���Y�������s
		outputFunctionTrees = ExtractTree(desiredType);

		long stop = System.currentTimeMillis();
		
		
//		for(Function f: functions){
//			System.out.println(f.getFrequency() + "," + f.toDBString());
//		}
		
		//���ʂ��R���\�[���ɕ\������B
		printResult(desiredType, new_types, new_funcs, stop-start, state);
		
		//bestRoots�̒��g��\��
//		printBestRoots();
//		printFunctionsList(functions);
		//���O�o�͗p�t�@�C��
		//createLogFiles(new_types, new_funcs);
	
		//static�ȃt�B�[���h��S�ăN���A����
		clearStaticFields(true);

		return outputFunctionTrees;
	}
	
	private static TreeMap<String, List<Function>> map_param_func = new TreeMap<String, List<Function>>();
	
	
//	private void compactFunctions(){
//		List<Function> list_score_0 = new ArrayList<Function>();
//		List<Function> list_score_over0 = new ArrayList<Function>();
//		
//		
//		for(Function f: functions){
//			
//			int score = 0;
//			for(String s: f.getLabels()){
//				
//				for(String k: input_keywords){
//					if(s.equals(k)){
//						score++;
//					}
//					
//				}
//			}
//			f.setWordScore(score);
//			
//			if(score == 0){
//				list_score_0.add(f);
//				
//				String para_type = f.getParaTypeName();
//				
//				if(!map_param_func.containsKey(para_type)){
//					List<Function> list = new ArrayList<Function>();
//					list.add(f);
//					map_param_func.put(para_type, list);
//				}else{
//					List<Function> list = map_param_func.get(para_type);
//					list.add(f);
//				}
//						
//			}else{
//				list_score_over0.add(f);
//			}
//		}
//		
//		boolean flg_print = false;
//		System.out.println("func size =" + functions.size());
//		System.out.println("0    size =" + list_score_0.size());
//		System.out.println("over0size =" + list_score_over0.size());
//		System.out.println("map  size =" + map_param_func.size());
//		
//		if(flg_print){
//			
//			//TreeMap�̐擪���珇�Ԃɉ�ʕ\������
//	        Iterator<String> it = map_param_func.keySet().iterator();
//	        while (it.hasNext()) {
//	            String key = it.next();
//	            System.out.println(key + ", " + map_param_func.get(key).size());
//	        }
//		}
//		
//        functions.clear();
//        functions.addAll(list_score_over0);
//        
//        //�e�L�[�̐擪�̂P��Function���_�~�[�Ƃ��Ēǉ��B
//        Iterator<String> it = map_param_func.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            Function f = map_param_func.get(key).get(0);
//            f.setIsDummy(true);
//            functions.add(f);
//        }
//        System.out.println("func size =" + functions.size());
//		
//	}
	
	private void addNewFunctions(List<String> localFunctions,
			ArrayList<Type> new_types, ArrayList<Function> new_funcs) {
		if(localFunctions != null)
		for(String s: localFunctions){
			Function f = new Function(s);
			//�V�����֐����������ꍇ�̂݁A
			if(functions.add(f) == true){
				new_funcs.add(f);
				//�V�����o�������N���X�̃`�F�b�N

				//�Ԃ�l�̃`�F�b�N
				String ret = f.getReturnType();
				if(ret != null){
					Type t = new Type(ret);
					if(!types.containsKey(t.getName())){
						types.put(t.getName(), t);
						new_types.add(t);
					}else{
						//�L�[������Ƃ��A
						//subtype��ǉ�����B
						types.get(t.getName()).addSubTypes(t.getSubTypes());
					}
				}
				
				//�p�����[�^�̃`�F�b�N
				if(f.getParameters() != null)
				for(String param: f.getParameters()){//������Βǉ�
					Type tt = new Type(param);
					if(!types.containsKey(tt.getName())){
						types.put(tt.getName(), tt);
						new_types.add(tt);
					}else{
						//�L�[������Ƃ��A
						//subtype��ǉ�����B
						types.get(tt.getName()).addSubTypes(tt.getSubTypes());
					}
				}
			}
		}
	}

	/*
	 * localFunctions�̈��� List<String> --> List<Function>
	 */
	private void addNewFunctions2(List<Function> localFunctions,
			TreeMap<String, Type> new_types, ArrayList<Function> new_funcs) {
		if(localFunctions != null)
		for(Function f: localFunctions){
			//�V�����֐����������ꍇ�̂݁A
			if(functions.add(f) == true){
				new_funcs.add(f);
				//�V�����o�������N���X�̃`�F�b�N

				//�Ԃ�l�̃`�F�b�N
				String ret = f.getReturnType();
				Type t = new Type(ret);
				if(!types.containsKey(t.getName())){
					types.put(t.getName(), t);
					new_types.put(t.getName(), t);
				}else{
					//�L�[������Ƃ��A
					//subtype��ǉ�����B
					types.get(t.getName()).addSubTypes(t.getSubTypes());
				}

				//�p�����[�^�̃`�F�b�N
				if(f.getParameters() != null)
				for(String param: f.getParameters()){//������Βǉ�
					Type tt = new Type(param);
					if(!types.containsKey(tt.getName())){
						types.put(tt.getName(), tt);
						new_types.put(tt.getName(), tt);
					}else{
						//�L�[������Ƃ��A
						//subtype��ǉ�����B
						types.get(tt.getName()).addSubTypes(tt.getSubTypes());
					}
				}
			}
		}
	}
		
	/*
	 * Type��Function�̃��O���o�͂���B
	 */
	private void createLogFiles(HashSet<Type> new_types, HashSet<Function> new_funcs) {
		String Type_FILE_NAME = "./type.txt";
		String Function_FILE_NAME = "./function.txt";


			File fileL = new File(Type_FILE_NAME);
			File fileC = new File(Function_FILE_NAME);


			try{


				fileL.createNewFile();
				fileC.createNewFile();
				PrintWriter pwL = new PrintWriter(new BufferedWriter(new FileWriter(fileL)));
				PrintWriter pwC = new PrintWriter(new BufferedWriter(new FileWriter(fileC)));

				for(Type t: new_types){
					//System.out.println(t.toDBString());
					pwL.println(t.toDBString());
				}

				for(Function f: new_funcs){
					//System.out.println(t.toDBString());
					pwC.println(f.toDBString());
				}
				pwL.close();
				pwC.close();


			}catch(IOException e){
				e.printStackTrace();
			}
	}

	/*
	 * �f�o�b�O�p ���ʕ\�����\�b�h
	 */
	
	private void printResult(String desiredType, ArrayList<Type> new_types, ArrayList<Function> new_funcs, long time, String state) {
		LogControl logControl = new LogControl(LogControl.KP);
		
		//�R�[�h�R���v���[�V�����̂Ƃ��̓��O��Ԃ̕\�����s���B�T�[�`���[�h�̂Ƃ��̓��O��Ԃ̕\���𖈉�s��Ȃ��B�ז��Ȃ̂ŁB
		if(state.equals(KpRunningState.CODE_COMPLETION))
			logControl.printLogState();
		
		//new_types�Ɋ܂܂��󕶎���폜
		new_types.remove(new Type(""));
		
		logControl.println(">>> start keyword programming >>>");
		logControl.println("");

		logControl.println(" >> ��{��� >>", LogControl.KP_BASIC);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String flg_input = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
		logControl.println("  ���̓L�[���[�h���� =" + flg_input);
		String aimai = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
		logControl.println("  �����܂��L�[���[�h �Ή� =" + aimai);
		int ld_del = store.getInt(PreferenceInitializer.LD_DELETE);
		int ld_rep = store.getInt(PreferenceInitializer.LD_REPLACE);
		int ld_add = store.getInt(PreferenceInitializer.LD_ADD);
		logControl.println("  LD = " + ld_del + ", " + ld_rep + ", " + ld_add);
		String bunkatu = store.getString(PreferenceInitializer.SEPARATE_KEYWORDS);
		logControl.println("  �L�[���[�h���� =" + bunkatu);
	
		logControl.println("  ���s�ɂ�����������= " + time + " �~���b�BKeywordProgramming.printResult", LogControl.KP_BASIC);
		logControl.println("  �ގ��x�v�Z�ɂ�����������= " + time_consumed_getting_similarity + " �~���b�B", LogControl.KP_BASIC);
		logControl.println("  �ގ��x�v�Z�ɂ�����������= " + (double) time_consumed_getting_similarity / time, LogControl.KP_BASIC);
		
		
		logControl.println("  BEST_R =" + BEST_R);
		logControl.println("  �֐��؂̍ő�̍��� =" + HEIGHT);
		logControl.println("  �S�T������g�ݍ��킹����臒l=" + COMBINATION_SIZE);
		logControl.print("  ���̓L�[���[�h= ", LogControl.KP_BASIC);
		for(String w: input_keywords)
			logControl.print(w + ", ", LogControl.KP_BASIC);
		logControl.println("");
		logControl.println("  �o�͌��̖]�܂����Ԃ�l�̌^= " + desiredType, LogControl.KP_BASIC);
		logControl.println("  ���̓L�[���[�h��=" + input_keywords.size());
		
		if(outputFunctionTrees != null)
			logControl.println("  �������ꂽ�o�͂̑���= " + outputFunctionTrees.length, LogControl.KP_BASIC);
		
		logControl.println("  �����̏d�݂̑g= (" + ExplanationVector.getWeightString() + ")", LogControl.KP_BASIC);
		
		logControl.println(" === �^�� === ", LogControl.KP_BASIC);
		logControl.println("  ���^��= " + types.size(), LogControl.KP_BASIC);
//		logControl.println("  �I���W�i��= " + original_types.size(), LogControl.KP_BASIC);
		if(new_types != null){
			logControl.println("  ���[�J���̌^��= " + (new_types.size() + imported_types.size()), LogControl.KP_BASIC);
			logControl.println("  �C���|�[�g��= " + (imported_types.size()), LogControl.KP_BASIC);
			logControl.println("  ����ȊO= " + (new_types.size()), LogControl.KP_BASIC);
		}
		
		logControl.println(" === �֐��� === ", LogControl.KP_BASIC);
		logControl.println("  ���֐���= " + functions.size(), LogControl.KP_BASIC);
//		logControl.println("  �I���W�i��= " + original_functions.size(), LogControl.KP_BASIC);
		if(new_funcs != null){
			logControl.println("  ���[�J���̊֐���= " + (new_funcs.size() + imported_functions.size()), LogControl.KP_BASIC);
			logControl.println("  �C���|�[�g��= " + (imported_functions.size()), LogControl.KP_BASIC);
			logControl.println("  ����ȊO= " + (new_funcs.size()), LogControl.KP_BASIC);
		}
		
		int num_f_with_freq = 0; 
		for(Function f:functions){
			if(f.getFrequency() > 0){
				num_f_with_freq++;
			}
		}
		
		logControl.println(" �p�x0�ȏ�̊֐���= " + num_f_with_freq, LogControl.KP_BASIC);
		
		logControl.println(" << ��{��� <<", LogControl.KP_BASIC);
		logControl.println("", LogControl.KP_BASIC);
		
		if(new_types != null)
			logControl.println("  ���[�J���̌^��= " + new_types.size(), LogControl.KP_TYPES);
		logControl.println(" >> ���[�J���̌^�ꗗ >>", LogControl.KP_TYPES);
		if(new_types != null){
//			for(Type t: new_types){
//			logControl.println("  " + t.toDBString(), LogControl.KP_TYPES);
//		}
			for(Type t: types.values()){
				logControl.println("  " + t.toDBString(), LogControl.KP_TYPES);
			}
		}
		logControl.println(" << ���[�J���̌^�ꗗ <<", LogControl.KP_TYPES);
		logControl.println("", LogControl.KP_TYPES);

		
		if(new_funcs != null)
			logControl.println("  ���[�J���̊֐���= " + new_funcs.size(), LogControl.KP_FUNCTIONS);
		logControl.println(" >> ���[�J���̊֐��ꗗ >> �o�͌`���F[�e�N���X��, isStatic, isFinal, type(field or constructor or method or localvariable), �Ԃ�l�̌^, ���O, ���x��(��؂蕶��;), �����̌^(���ł�) ]", LogControl.KP_FUNCTIONS);
		if(new_funcs != null){
//			for(Function f: new_funcs){
//			logControl.println("  " + f.toDBString(), LogControl.KP_FUNCTIONS);
//		}
			for(Function f: functions){
				logControl.println("  " + f.toDBString(), LogControl.KP_FUNCTIONS);
			}
		}
		logControl.println(" << ���[�J���̊֐��ꗗs << �o�͌`���F[�e�N���X��, isStatic, isFinal, type(field or constructor or method or localvariable), �Ԃ�l�̌^, ���O, ���x��(��؂蕶��;), �����̌^(���ł�) ]", LogControl.KP_FUNCTIONS);
		logControl.println("", LogControl.KP_FUNCTIONS);

		logControl.println(" >> �o�͌��ꗗ >> �o�͌`���F[�]���l, p(4�̓�����), e_i(�����̓L�[���[�h���ɓ�����), �o�͕�����]", LogControl.KP_RESULTS);
		for(FunctionTree t:outputFunctionTrees){
			if(t != null){
				logControl.println("  " + t.toEvalString() + t.toCompleteMethodString(), LogControl.KP_RESULTS);
//					logControl.out(t.toLogDBString());
			}
		}
		logControl.println(" << �o�͌��ꗗ << �o�͌`���F[�]���l, p(4�̓�����), e_i(�����̓L�[���[�h���ɓ�����), �o�͕�����]", LogControl.KP_RESULTS);
		logControl.println("", LogControl.KP_RESULTS);

		logControl.println("<<< end keyword programming <<<");
		logControl.close();
	}

	/**
	 
	 * 1:types���X�g��functions���X�g�̃N���A
	 *   �V���ɒǉ��������[�J�����̍폜
	 *   original_types, original_functions�́AEclipse�̍ŏ��œǂݍ��܂�ォ��ύX����Ȃ��B
	 *   imported_types, imported_functions�́AImport.getImportDeclaration�̒����炾���A�ύX����A�ق��̃^�C�~���O�ł͕ύX����Ȃ��B
	 *�@�@�@�����ł́A����ȊO�ɒǉ����ꂽ�A���Ӄ\�[�X�R�[�h�󋵂݂̂��N���A����邱�ƂɂȂ�B
	 *
	 * 2:numOfSameWords�̃N���A
	 *
	 * @param putImport �C���|�[�g���̏����ǉ����邩
	 */

	public void clearStaticFields(boolean putImport){
		
		//types��functions�̃N���A
		clearStaticTypeAndFunctionFields();
		
		//original����types��functions�ɒǉ��B
//		putAllOriginalStaticFields();
		copyOriginalFiles_consoleTest3();
		
		//�C���|�[�g����types��functions�ɒǉ��B
		if(putImport)
			putAllImportStaticFields();
		
		numOfSameWords.clear();
		if(bestRoots != null)
			bestRoots.clearTable();//�������Ă����Ȃ��Ă��ς��Ȃ��悤���B
		
//		System.gc();
		//used memory�̑���(�P�ʁFKByte)
//		Runtime rt = Runtime.getRuntime();
//		long usedMemory = rt.totalMemory()/1024 - rt.freeMemory()/1024;
//		System.out.println("�g�p��������= " + usedMemory + "KByte");

	}

	public void copyOriginalFiles_consoleTest3(){
		//�f�B�[�v�R�s�[
		Set keySet = console_test.ConsoleTest3.original_types.keySet();
		Iterator iteKey = keySet.iterator();
		while(iteKey.hasNext()){
            String key = String.valueOf(iteKey.next());// Key���擾
            types.put(key, console_test.ConsoleTest3.original_types.get(key).clone());// Key��tmpMap����l�����o���A
        }
		
		for(Function f:console_test.ConsoleTest3.original_functions)
			functions.add(f.clone());	
	}
	
	//Classes���X�g�̕\��
	public static void printClassList(ArrayList<String> list){
		for(String s: list){
			System.out.println(s);
		}
	}
	//Functions���X�g�̕\��
	public static void printFunctionsList(ArrayList<Function> list){
		for(Function f: list){
			System.out.println(f.toString());
		}
	}

	public void printBestRoots(){
		for(int i = 0; i < HEIGHT; i++){

			for(Type t : types.values()) {
				FunctionTree[] roots = bestRoots.getRoots(t.getName(), i);
				for(FunctionTree r: roots){
					if(r != null){//����null�`�F�b�N�͕K�v
						System.out.print("i= "+ i + ", ");
						System.out.print("ret_t_of_f= "+ t.getName() + ", ");
						System.out.print("f= "+ r.getRoot().getFunction().getName() + ", ");
						System.out.print("parent_t_of_f= "+ r.getRoot().getFunction().getParentClass() + ", ");
						System.out.print("tree= "+ r.toCompleteMethodString() + ", ");
						System.out.print("e= "+ r.getEvaluationValue());
						System.out.println("");
					}
				}
//				int count = bestRoots.getSizeOfRoots(t.getName(), i);
//				if(roots.length > 0)
//					System.out.println(i + ", "+ t.getName() + ", " + count);
			}
			System.out.println("");
		}
	}


	/*
	 * �L�[���[�h�̂��Ă̏����B
	 */
	public void inputKeywords(List<Word> words){
		
		input_keywords_word = words;
		input_keywords = new ArrayList<String>();
		
		for(Word word:words){
			input_keywords.add(word.getWord());
		}
		
		
//		if(s.equals("")){	
//			return;
//		}
			
		//�������������������
//		String s_lowerCase = s.toLowerCase();
		
		//�R���}����A�Ȃ��ŏꍇ�킯
//		if(s_lowerCase.contains(",")){
//			//�R���}�ŕ���
//			List<String> func_val = Arrays.asList(s_lowerCase.split(","));
//			//�O�����֐�
//			List<String> funcs = Arrays.asList(func_val.get(0).split("[ �@\t]"));
//
//			//������ϐ�
//			List<String> vals = Arrays.asList(func_val.get(1).split("[ �@\t]"));
//		
//			//�֐��́A�O��"@"������
//			for(int i= 0; i < funcs.size(); i++){
//				if(!funcs.get(i).equals("") && !funcs.get(i).matches("[ �@\t]"))
//					input_keywords.add("@" + funcs.get(i));
//			}
//			
//			//�ϐ��́A���̂܂�
//			for(int i= 0; i < vals.size(); i++){
//				if(!vals.get(i).equals("") && !vals.get(i).matches("[ �@\t]"))
//					input_keywords.add(vals.get(i));
//			}
//			
//		}else{
//			//keyword�ɕ���
//			input_keywords = Arrays.asList(s_lowerCase.split("[ �@\t]"));
//		}
		
		//keyword�𓯈�P�ꂲ�Ƃɂ��̌����J�E���g����
		for(int i= 0; i < input_keywords.size(); i++){
			String word = input_keywords.get(i);
			int count = 1;
			if(numOfSameWords.containsKey(word)){
				//�L�[�����łɂ���Ƃ��A�ȑO�܂ł̐��ƂP�𑫂�
				count += numOfSameWords.get(word);
			}
			numOfSameWords.put(word, count);
		}
	}

	/**
	 * procedure DynamicProgram
	 *
	 * this function modifies bestRoots
	 */
	public synchronized void DynamicProgram(){
		/*
		 * "java.lang.Object"��subType���쐬�B
		 */
		Type t_obj = types.get("java.lang.Object");
		for(Type t_sub: types.values())
			if(!t_sub.getName().equals("java.lang.Object"))
				t_obj.addSubType(t_sub.getName());
		
		//for each 1 <= i <= h
		for(int i = 0; i < HEIGHT; i++){
			//for each t in T
			for(Type t : types.values()) {
				// bestRoots(t,i) = 0
				//for each f in F where ret(f) in sub(t)
				for(String subt: t.getSubTypes()){
					for(Function f: functions){
						if(f.getReturnType().equals(subt)){
							// e = GetBestExplForFunc(f, i-1)
							List<FunctionTree> treeList = GetBestExplTreeForFunc(f, i);
							if(treeList != null)
							for(FunctionTree tree: treeList){							
								// if e > -�� then bestRoots(t, i) = bestRoots(t, i)U(e, f)
								if(tree != null && tree.getEvaluationValue() > -9999.0){
									bestRoots.addRoot(tree);
								}
							}
						}
					}
	            }
				//keep the best r roots
				bestRoots.keepBestRoots(t.getName(), i);
            }
		}
	}
	
//	public static synchronized void DynamicProgram(){
//
//		/*
//		 * "java.lang.Object"��subType���쐬�B
//		 */
////		Type t_obj = types.get("java.lang.Object");
////		for(Type t_sub: types.values())
////			if(!t_sub.getName().equals("java.lang.Object"))
////				t_obj.addSubType(t_sub.getName());
//
//		//for each 1 <= i <= h
//		for(int i = 0; i < HEIGHT; i++){
//			//for each t in T
//			for(Type t : types.values()) {
//
//
//				// bestRoots(t,i) = 0
//				//for each f in F where ret(f) in sub(t)
//				for(String subt: t.getSubTypes()){
//
//
//					for(Function f: functions){
//						if(f.getReturnType().equals(subt)){
//							
////							if(i == 1 && f.getName().equals("Integer")){
////								if(f.getParameters() != null && f.getParameters().get(0).equals("java.lang.Integer")){
////								System.out.println(f.getName());
////								}
////							}
//							
////							if(i == 2 && f.getName().equals("add") && f.getParentClass().equals("java.util.List<java.lang.Integer>")
////							&& f.getReturnType().equals("java.lang.Boolean")){
////								System.out.println(f.getName());
////							}
//							
//							// e = GetBestExplForFunc(f, i-1)
//							List<FunctionTree> treeList = GetBestExplTreeForFunc(f, i);
//							
//
////							if(i == 1 && f.getReturnType().equals("java.lang.Integer")){
////								if(treeList != null)
////								for(FunctionTree ftree: treeList){
////									System.out.println(ftree.getEvaluationValue() + ", " + ftree.toCompleteMethodString());
////								}
////							
////							}
//							if(treeList != null)
//							for(FunctionTree tree: treeList){
//									
//								// if e > -�� then bestRoots(t, i) = bestRoots(t, i)U(e, f)
//								if(tree != null && tree.getEvaluationValue() > -9999.0){
//									bestRoots.addRoot(tree);
////									if(tree.toCompleteMethodString().equals("fFull.add(new Integer(i))")){
////									System.out.println(tree);
////									}
//								}
//							}
//						}
//					}
//	            }
//				//keep the best r roots
//				bestRoots.keepBestRoots(t.getName(), i);
//				
//				//sorted_ftree_map �́A�l�ׂ��T���̎��Ɏg�p����B
////				List<FunctionTree> roots = Arrays.asList(bestRoots.getRoots(t.getName(), i));
////				
////				List<FunctionTree> sorted_ftree = sorted_ftree_map.get(t.getName());
////				if(sorted_ftree != null){
////					for(FunctionTree root: roots)
////						if(root != null)
////							sorted_ftree.add(root);
////
////					Collections.sort(sorted_ftree);
////					sorted_ftree_map.put(t.getName(), sorted_ftree);
////				}else{
////					sorted_ftree = new ArrayList<FunctionTree>();
////					for(FunctionTree root: roots)
////						if(root != null)
////							sorted_ftree.add(root);
////					
////					sorted_ftree_map.put(t.getName(), sorted_ftree);
////				}
////				
////				if(t.getName().equals("java.lang.Integer")){
////					for(FunctionTree ft :sorted_ftree){
////						System.out.println(ft.getEvaluationValue() +", "+ft.toCompleteMethodString());
////					}
////				}
//				
//            }
//		}
//	}
	/**
	 * procedure GetBestExplForFunc
	 *
	 *	�Œ�ł�Root�݂̂�Tree�͎擾�ł���
	 *
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFunc(Function f, int h_max){
		//return GetBestExplTreeForFuncGreedy(f, h_max);//�I���W�i�����[�h
		if(SEARCH_MODE_FUNCTION_PARAMETER.equals(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL)){
			return GetBestExplTreeForFuncGreedy(f, h_max);//�I���W�i�����[�h
		}else if(SEARCH_MODE_FUNCTION_PARAMETER.equals(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_EXHAUSTIVE)){
			//return GetBestExplTreeForFunc_combinationSize(f, h_max);//�S�T�����[�h(�g�ݍ��킹���ł��؂�)
			return GetBestExplTreeForFunc_pamamLength(f, h_max);//�S�T�����[�h(�������ł��؂�)
		}else{
			//return GetBestExplTreeForFuncGreedyBestFirst(f, h_max);//�ŗǗD��T�����[�h
			return GetBestExplTreeForFuncGreedy(f, h_max);//�I���W�i�����[�h
		}
	}

	/**
	 * procedure GetBestExplForFunc
	 *
	 *	�Œ�ł�Root�݂̂�Tree�͎擾�ł���
	 *
	 * �g�ݍ��킹�̐��ɂ���ďꍇ����
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFunc_combinationSize(Function f, int h_max){
		
		//�S�T��VER
		if(f.getParameters() != null){
			int combination_size = 1;
			for(String param: f.getParameters()){//�����̏��Ԓʂ�Ƀ��[�v����
				int tree_size = 0;
				for(int i = 0; i < h_max; i++){
					FunctionTree[] trees = bestRoots.getRoots(param, i);
					if(trees != null)
						tree_size += trees.length;
				}
				combination_size *= tree_size;//tree_size��0�Ȃ�A�g�ݍ��킹������0�B
			}
			
			if(f.getParameters().size() > 1 && combination_size > 0 && combination_size <= COMBINATION_SIZE){
				//���������S�ȏ�̊֐������o��
//				System.out.println(h_max + ", "+ f.toDBString());
				return GetBestExplTreeForFuncExhaustive(f, h_max);
			}
		}
		//�I���W�i��VER �×~�@
		return GetBestExplTreeForFuncGreedy(f, h_max);
	}
	
	/**
	 * procedure GetBestExplForFunc
	 *
	 *	�Œ�ł�Root�݂̂�Tree�͎擾�ł���
	 *
	 * �����̐��ɂ���ďꍇ����
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFunc_pamamLength(Function f, int h_max){
	//		if(f.getParameters() != null){
	//		System.out.println(f.getParameters().size());
	//	}
			
			
		//��������x�̂Ƃ��S�T��VER
		if(f.getParameters() != null)
		if(f.getParameters().size() > 1 && f.getParameters().size() <= MAX_ARGUMETNT_SIZE){
			//��������MAX_ARGUMETNT_SIZE�ȏ�̊֐������o��
	//		System.out.println(h_max + ", "+ f.toDBString());
	//		System.out.println(f.getParameters().size());
			return GetBestExplTreeForFuncExhaustive(f, h_max);
		}
		
		//�I���W�i��VER �×~�@
		return GetBestExplTreeForFuncGreedy(f, h_max);
	}


	/**
	 * �͔C���T���A�l�ׂ��T��VER�B
	 *
	 * @param f
	 * @param h_max
	 * @return
	 */
	public List<FunctionTree> GetBestExplTreeForFuncExhaustive(Function f, int h_max){

		//System.out.println(h_max + ", "+ f.toDBString());
		
		//�e�N���X��"this"���A��static�̏ꍇ�́A���V�[�o�����ł��N���ł���悤�ɂ���B
//		boolean this_flg = false;
//		if(f.getParentClass().equals("this") && f.isStatic() == false){
//			this_flg = true;
//		}
		
		//�֐������N���X���Astatic�łȂ��ꍇ�́A���V�[�o�����ŋN���ł���悤�ɂ���B
		boolean this_nonStatic_flg = false;
		if(f.isInThisClass() == true && f.isStatic() == false){
			this_nonStatic_flg = true;
		}
		
		List<FunctionTree> ret_list = new ArrayList<FunctionTree>();
		
		
		//for each p in params(f)
		
		if(f.getParameters() == null){
			FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//Root�݂̂�Tree
			ret_list.add(best_tree);
			return ret_list;	//�֐��Ɉ������Ȃ��Ƃ��Aroot�݂̂�Ԃ�
		}else if(h_max == 0 && this_nonStatic_flg == false)
			return null;	//�֐��Ɉ����͂���A������0�ŁA���N���X�̊֐��ł͂Ȃ��Ƃ��Anull��Ԃ�
		
		//this_flg=true�A���A����1�̂Ƃ��A���̈����̓��V�[�o�ł���B
		if(this_nonStatic_flg == true && f.getParameters().size() == 1){
			FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//Root�݂̂�Tree
			ret_list.add(best_tree);
			return ret_list;
		}
		
		//this_flg=true�̂Ƃ��́A����0�A�������V�[�o�݂̂̂Ƃ�������B
//		if(this_flg == true && f.getParameters().length == 1)
//			return best_tree;
		
//		int candidate_num = COMBINATION_SIZE / f.getParameters().size();
		int candidate_num = COMBINATION_SIZE;
		
//		if(h_max == 2 && f.getName().equals("add") && f.getParentClass().equals("java.util.List<java.lang.Integer>")
//				&& f.getReturnType().equals("java.lang.Boolean")){
//					System.out.println("=============================================================");
//				}
		
		HashMap<Integer, ArrayList<FunctionTree>> param_list_map = new HashMap<Integer, ArrayList<FunctionTree>>();
		
		boolean flg = false;
		if(flg){
			//�Ԃ�l�̌^�������ƈ�v����֐��؂�S��treeList�ɑ}�����A
			//���̃��X�g�������̌^�ƃ��X�g�̃}�b�vparam_list_map�ɕێ�����
			int param_num = 0;
			for(String param: f.getParameters()){//�����̏��Ԓʂ�Ƀ��[�v����
				ArrayList<FunctionTree> treeList = new ArrayList<FunctionTree>();
				
				for(int i = 0; i < h_max; i++){
					FunctionTree[] trees = bestRoots.getRoots(param, i);
					if(trees != null){
	
	
						int max = candidate_num;
						if(max > trees.length)
							max = trees.length;
						
						for(int j = 0; j < max; j++){
						
							if(trees[j] != null){
								treeList.add(trees[j]);
							}
						}
					}
						
				}
					
				param_list_map.put(param_num++, treeList);
	//			System.out.println(param_num + ", "+ param + ", " + treeList.size());
			}
		}else{
			//�Ԃ�l�̌^�������ƈ�v����֐��؂�S��treeList�ɑ}�����A
			//���̃��X�g�������̌^�ƃ��X�g�̃}�b�vparam_list_map�ɕێ�����
			int param_num = 0;
			for(String param: f.getParameters()){//�����̏��Ԓʂ�Ƀ��[�v����
				ArrayList<FunctionTree> treeList = new ArrayList<FunctionTree>();
				List<FunctionTree> trees = sorted_ftree_map.get(param);
				
//				if(h_max == 0 && param.equals("java.lang.Integer")){
//					for(FunctionTree ft :trees){
//						System.out.println(ft.getEvaluationValue() +", "+ft.toCompleteMethodString());
//					}
//				}
				
				for(int i = 0; i < h_max; i++){
					
					
					
					
					
					if(trees != null){
						int max = candidate_num;
						if(max > trees.size())
							max = trees.size();
						for(int j = 0; j < max; j++){
							if(trees.get(j)!= null){
								treeList.add(trees.get(j));
							}
						}
					}
				}
				param_list_map.put(param_num++, treeList);
	//			System.out.println(param_num + ", "+ param + ", " + treeList.size());
			}
		}
		
		//�����̑g�ݍ��킹�̏����S�ċ��߂�B
		ParameterCombinations pc = new ParameterCombinations();
		ArrayList<ArrayList<FunctionTree>> allCombinations = pc.getAllCombinations(param_list_map);
		
		if(allCombinations.size() == 0)
			return null;
		

		 /*
		  *  �����̑g�ݍ��킹�̏����S�Ă��g�p���āA�֐��؂��쐬����B
		  *  �g�ݍ��킹������6�Ȃ�A�o�͂����֐��؂�6�ƂȂ�Blist�̒��Ɋ֐��؂�6����
		 */
		for(int i = 0; i < allCombinations.size(); i++){
			ArrayList<FunctionTree> comb = allCombinations.get(i);
			FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//Root�݂̂�Tree
			
			for(FunctionTree tree: comb){
				best_tree.addChild(tree);
			}
			
			ret_list.add(best_tree);
		}
		return ret_list;
		
		 /* �����̑g�ݍ��킹�̏����S�Ď����A
		 * ���̒��ň�ԗǂ��g�ݍ��킹�ƂȂ�֐��؂̗���q�֐��،Q�Ƃ����֐���best_tree�����߂�B
		 * (��s�����ł�1�ʂ肵�������Ȃ��B)
		 */
		
//		ExplanationVector e_best = new ExplanationVector(input_keywords.size(), -ExplanationVector.INFINITE_VALUE);//�����
//		ArrayList<FunctionTree> best_param_comb = null;
//		for(int i = 0; i < allCombinations.size(); i++){
//			ExplanationVector tmp_e_best = new ExplanationVector(input_keywords.size(), -ExplanationVector.INFINITE_VALUE);//�����
//			ArrayList<FunctionTree> comb = allCombinations.get(i);
//			for(FunctionTree tree: comb){
//				//System.out.println(i + ", " + tree.toCompleteMethodString());
//				tmp_e_best.add(tree.e_vec);
//			}
//			//tmp_e_best��e_best�����傫���Ƃ��A
//			if(tmp_e_best.compareTo(e_best) == 1){
//				e_best.substitution(tmp_e_best);//���
//				best_param_comb = comb;
//			}
//			
//		}
//		
//		for(FunctionTree tree: best_param_comb){
//			best_tree.addChild(tree);
//		}
//		
//		return best_tree;
	}
	
	/**
	 * ���g�p�B
	 * 
	 * @param combination
	 * @param param_num
	 * @param param_list_map
	 */
	public void tree_combination(ArrayList<FunctionTree> combination, int param_num, HashMap<Integer, ArrayList<FunctionTree>> param_list_map){
		if(combination.size() == param_list_map.size()){
			System.out.println(combination);
			return;
		}
		ArrayList<FunctionTree> list = param_list_map.get(param_num);
		for(FunctionTree tree: list){
			combination.add(tree);
			param_num++;
			tree_combination(combination, param_num, param_list_map);
			combination.remove(tree);
			param_num--;
		}
	}
	
	/**
	 * �I���W�i���o�[�W����
	 * �×~�@�B
	 * 
	 * @param f
	 * @param h_max
	 * @return
	 */

	public List<FunctionTree> GetBestExplTreeForFuncGreedy(Function f, int h_max){

//		if(f.getName().equals("rules") && f.getParentClass().equals("this")){
//			System.out.println("aaa");
//		}
//		
//		if(f.getName().equals("TRUE")){
//			System.out.println("aaa");
//		}
		
		//�֐������N���X���Astatic�łȂ��ꍇ�́A���V�[�o�����ŋN���ł���悤�ɂ���B
		boolean this_nonStatic_flg = false;
		if(f.isInThisClass() == true && f.isStatic() == false){
			this_nonStatic_flg = true;
		}
			
		FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//Root�݂̂�Tree
		ExplanationVector e_cumulative = new ExplanationVector(input_keywords.size(), best_tree.e_vec);//best_tree��e_vec���R�s�[����new
		
		List<FunctionTree> ret_list = new ArrayList<FunctionTree>();
		
		//for each p in params(f)
		
		if(f.getParameters() == null){
			ret_list.add(best_tree);
			return ret_list;	//�֐��Ɉ������Ȃ��Ƃ��Aroot�݂̂�Ԃ�
		}else if(h_max == 0 && this_nonStatic_flg == false)
			return null;	//�֐��Ɉ����͂���A������0�ŁA���N���X�̊֐��ł͂Ȃ��Ƃ��Anull��Ԃ�
		
		//this_flg=true�A���A����1�̂Ƃ��A���̈����̓��V�[�o�ł���B
		if(this_nonStatic_flg == true && f.getParameters().size() == 1){
			ret_list.add(best_tree);
			return ret_list;
		}
		
		for(int i = 0; i < f.getParameters().size(); i++){//�����̏��Ԓʂ�Ƀ��[�v����
		
			//e_best = (-��, 0, 0, 0, ...) 
			ExplanationVector e_best = new ExplanationVector(input_keywords.size(), -ExplanationVector.INFINITE_VALUE);//�����	
			FunctionTree param_tree = null;
			//for each 1 <= i <= h
			for(int j = 0; j < h_max; j++){//h_max=0�̏ꍇ�͂��̃��[�v�͎��s����Ȃ�
				//for each (e', f') in bestRoots(p, i)
				FunctionTree[] trees = bestRoots.getRoots(f.getParameters().get(i), j);
				if(trees != null){
					for(FunctionTree t: trees){
						if(t != null){
							//if e_cumulative + e' > e_best
							//    e_best = e_cumulative + e'
							
							ExplanationVector tmp = ExplanationVector.add(e_cumulative, t.e_vec);
							if(tmp.compareTo(e_best) == 1){
								e_best.substitution(tmp);//���
								param_tree = t;
							}

						}
					}
				}
			}
			//e_cumulative = e_best
			e_cumulative.substitution(e_best);
			
			if(param_tree == null)
				return null;	//�������P�ł����܂�Ȃ����,����root�֐��͑I�����Ȃ��Bnull��Ԃ��B
			else
				best_tree.addChild(param_tree);
		}

		ret_list.add(best_tree);
		return ret_list;
	}

	
	/**
	 * �~����ŗǗD��T��
	 * 
	 * �q�֐��؂����]���l�̒l���q���[���X�e�B�b�N�֐��ƌ��Ȃ��āA
	 * ���̎q�֐��؂̕]���l�̑��a���������̂�D�悵�ĒT������B
	 * 
	 * �T���͉񐔂��w�肵�ēr���őł��؂�B
	 * 
	 * @param f
	 * @param h_max
	 * @return
	 */

	public FunctionTree GetBestExplTreeForFuncGreedyBestFirst(Function f, int h_max){

		FunctionTree best_tree = new FunctionTree(f, input_keywords_word, numOfSameWords);//Root�݂̂�Tree
		
		if(f.getParameters() == null)
			return best_tree;	//�֐��Ɉ������Ȃ��Ƃ��Aroot�݂̂�Ԃ�
		else if(h_max == 0)
			return null;	//�֐��Ɉ����͂��邪�A������0�̂Ƃ��Anull��Ԃ�
		
		/*
		 * param_list_map��
		 * param_num
		 * ���쐬����B
		 */
		
		//�Ԃ�l�̌^�������ƈ�v����֐��؂�S��treeList�ɑ}�����A
		//���̃��X�g��TreeSet�ɂ��āA�l�̏d����r�����A�\�[�g�����̂��A
		//�����̌^�ƃ��X�g�̃}�b�vparam_list_map�ɕێ�����
		HashMap<Integer, ArrayList<FunctionTree>> param_list_map = new HashMap<Integer, ArrayList<FunctionTree>>();
		int param_num = 0;
		for(String param: f.getParameters()){//�����̏��Ԓʂ�Ƀ��[�v����
			ArrayList<FunctionTree> treeList = new ArrayList<FunctionTree>();
			for(int i = 0; i < h_max; i++){
				FunctionTree[] trees = bestRoots.getRoots(param, i);
				if(trees != null){
					for(FunctionTree t: trees){
						if(t != null){
							treeList.add(t);
						}
					}
				}
			}
			/*
			 * TreeSet���g�p���邱�Ƃɂ��A
			 * �d���𖳂����A�\�[�g���s���B
			 */
			TreeSet<FunctionTree> treeSet = new TreeSet<FunctionTree>(treeList);
			treeList = new ArrayList<FunctionTree>(treeSet);

			param_list_map.put(param_num++, treeList);
		}
		
		
		/*
		 * ��������~����ŗǗD��T��
		 */
		Node.param_list_map = param_list_map;
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		List<Integer> firstNumList = new ArrayList<Integer>();
		//������Ԃ͊e�����Ƃ��ɁATreeSet�̐擪�i��ԕ]���l�̑傫�����́j.
		for(int i = 0; i < param_num; i++){
			firstNumList.add(0);//������Ԃ͑S�Đ擪�A�܂�0.
		}
		State firstState = new State(param_num, firstNumList);
		openList.add(new Node(firstState, 0));//������Ԃ�����B�[��0.
		
		int endDepth = 50;
		
		/*
		 * closedList��
		 * �w�肵���[��endDepth�܂ŁA
		 * �\�z�R�X�g���Ƀm�[�h�i�������֐��̑g�ݍ��킹�j��
		 * �}������B
		 */
		while(true){
			if(openList.size() == 0)
				break;
			//������openList���\�[�g���Ă���A���B
			Collections.sort(openList);
			Node node = openList.remove(0);//�擪�m�[�h���擾����
			closedList.add(node);
			if(node.getDepth() == endDepth){//�I������
				break;
			}
			for(Node child: node.getChildren()){
				child.setParent(node);
				openList.add(child);
			}
			
		}
		
		/*
		 * closedList�ɓ����Ă���������֐��̑g�ݍ��킹�ɂ��Ă̂݁A
		 * �]���l���v�Z���A��ԕ]���l���傫���Ȃ�g�ݍ��킹��
		 * best_tree�Ƃ��ĕԂ��B
		 */
		for(Node node: closedList){
			List<Integer> numList = node.getState().getNumberList();
			for(int i = 0; i < param_num; i++){
				int num = numList.get(i);
				FunctionTree ft = param_list_map.get(i).get(num);
			}
		}
		return best_tree;
	}

	
	
	/**
	 *
	 * ���I�v��@�̕\��
	 * �]�܂����Ԃ�l�̌^��Ԃ��֐��؂�
	 * ����1����HEIGHT�܂őS�Ď擾���A
	 * �]���l�Ń\�[�g���ĕԂ��֐�
	 *
	 * @param desiredType �]�܂����Ԃ�l�̌^
	 * @return outputTrees �]���l�Ń\�[�g���ꂽ�֐��؂̃��X�g
	 */
	public FunctionTree[] ExtractTree(String desiredType){

		Set<FunctionTree> outputTrees = new HashSet<FunctionTree>(BEST_R * HEIGHT * 4 / 3 + 1);

		for(int i=0; i< HEIGHT; i++){
			FunctionTree[] roots = bestRoots.getRoots(desiredType, i);
			if(roots != null)
				for(FunctionTree t: roots){
					//null�v�f�͍폜
					//�����񂪏d�����Ȃ��悤�ɂ���B
					if(t != null){
						outputTrees.add(t);
					}
				}
		}
		outputTrees = new TreeSet<FunctionTree>(outputTrees);
		return outputTrees.toArray(new FunctionTree[0]);
	}

}
