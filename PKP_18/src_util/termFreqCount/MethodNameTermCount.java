package termFreqCount;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import keywordProgramming.Function;
import keywordProgramming.Type;


public class MethodNameTermCount {
	
	static final String SUB_CLASS_FILE_NAME = "sub_class.txt";
	
	//static final String FUNCTION_FILE_NAME = "function.txt";
	//static final String FUNCTION_FILE_NAME = "javaio/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javalang/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javautil/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javabeans/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javalangref/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "javalangreflect/kypg_mdl_func.txt";
	static final String FUNCTION_FILE_NAME = "javaxxml/kypg_mdl_func.txt";
	//static final String FUNCTION_FILE_NAME = "coreApi/coreApi_func.txt";
	
	static ArrayList<Type> types = new ArrayList<Type>();
	static ArrayList<Function> functions = new ArrayList<Function>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args){
		
		//MethodNameTermCountForSimpleTxt();
	}

//
//	public static void main(String[] args){
//		
//		String a = "inputURLSafeIOmodeURL";
//		String b = splitName2(a, ", ");
//		System.out.println(b);
//		/*
//		 * �Ⴊ�����̂�
//		 * IOmode����
//		 * I, O, mode
//		 * �ɕ�����Ă��܂����A
//		 * URLSafe���ƁA
//		 * URL, Safe
//		 * �Ə�肭�����A
//		 * 
//		 * IOMode
//		 * �ƂȂ邩�Ȃ�Ȃ���
//		 * ����͂킩��Ȃ��B
//		 * ���������Ă݃i�C�g�ˁB
//		 * 
//		 */
//	}
	
	/*
	 * MethodNameGetter.GetMethodName2 �őn����
	 * ���\�b�h���̗��񂾂��������ꂽm_name_output.txt�t�@�C����ǂݍ��� 
	 */
	
	public static void MethodNameTermCountForSimpleTxt(String project_name){
		ArrayList<String> list = new ArrayList<String>();

		//�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
		readFileAndAddList(MethodNameGetter.filename, list);
		//printClassList(classes);
		//printFunctionsList(functions);
		/*
		for(Function f: functions){
			System.out.println(f.toString());
		}
		*/
        HashMap<String, Integer> termMap = new HashMap<String, Integer>();

       
    	for(String l: list){
            // �P��̏o���񐔂�1���₷
            int count = 1;
            if(termMap.containsKey(l)){
                count += termMap.get(l);
            }
            termMap.put(l, count);
		}
        
        
        //System.out.println("=========================================================");
        
        // ��r�̂��߂̃N���X���w�肵�� TreeMap �𐶐�
    	TreeMap treeMap = new TreeMap(new IntegerMapComparator(termMap));
    	// TreeMap �ɑS���̑g���R�s�[(���̂Ƃ��Ƀ\�[�g�����)
    	treeMap.putAll(termMap);
/*
        // �P�ꐔ�̕\�� (�\�[�g�����j
        Iterator<String> iterator = termMap.keySet().iterator();
		while(iterator.hasNext()) {
		    String key = iterator.next();
		    Integer value = termMap.get(key);
		    System.out.println(key + ": " + value);
		}
*/
    	
//    	File outfile = new File("/home/sayuu/source/�o�͌���/term/term_" + project_name + ".txt");
    	File outfile = new File("C://Users//sayuu//Desktop//" + project_name + ".txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			// TreeMap �̕\���@// �P�ꐔ�̕\�� (�\�[�g�L��j
			Set termSet = treeMap.keySet();  // �\�[�g����Ă���
			Iterator iterator1 = termSet.iterator();
			while(iterator1.hasNext()) {
			    String key = (String)iterator1.next();
			    Integer value = (Integer)treeMap.get(key);
			    if(key.length() > 1 && !key.endsWith("_")){//�ςȕ������o���Ȃ��B
			    	//System.out.println(key + ": " + value);
			    	pw.println(key + ": " + value);
			    }
			}
			pw.close();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
	    	e1.printStackTrace();
		}

	}
	
	/*
	 * KP�pfunction.txt��ǂݍ��� 
	 */

	public static void MethodNameTermCountForKPFunctionTxt(){
		/*
		 * function���A�܂�label���\������P���
		 * �o���p�x�𒲂ׂ�B2011/07/10
		 * 
		 */
		//�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
		readClassFileAndAddList(MethodNameTermCount.SUB_CLASS_FILE_NAME, types);
		readFunctionFileAndAddList(MethodNameTermCount.FUNCTION_FILE_NAME, functions);
		//printClassList(classes);
		//printFunctionsList(functions);
		/*
		for(Function f: functions){
			System.out.println(f.toString());
		}
		*/
        HashMap<String, Integer> termMap = new HashMap<String, Integer>();

        for(Function f: functions){
        	for(String l:f.getLabels()){
                // �P��̏o���񐔂�1���₷
                int count = 1;
                if(termMap.containsKey(l)){
                    count += termMap.get(l);
                }
                termMap.put(l, count);
    		}
            
        }
        
        //System.out.println("=========================================================");
        
        // ��r�̂��߂̃N���X���w�肵�� TreeMap �𐶐�
    	TreeMap treeMap = new TreeMap(new IntegerMapComparator(termMap));
    	// TreeMap �ɑS���̑g���R�s�[(���̂Ƃ��Ƀ\�[�g�����)
    	treeMap.putAll(termMap);
/*
        // �P�ꐔ�̕\�� (�\�[�g�����j
        Iterator<String> iterator = termMap.keySet().iterator();
		while(iterator.hasNext()) {
		    String key = iterator.next();
		    Integer value = termMap.get(key);
		    System.out.println(key + ": " + value);
		}
*/
		// TreeMap �̕\���@// �P�ꐔ�̕\�� (�\�[�g�L��j
		Set termSet = treeMap.keySet();  // �\�[�g����Ă���
		Iterator iterator1 = termSet.iterator();
		while(iterator1.hasNext()) {
		    String key = (String)iterator1.next();
		    Integer value = (Integer)treeMap.get(key);
		    if(key.length() > 1 && !key.endsWith("_")){//�ςȕ������o���Ȃ��B
		    	System.out.println(key + ": " + value);
		    }
		}

	}

	//Classes�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
	public static void readClassFileAndAddList(String input_txt_file_name, ArrayList<Type> types){
		FileReader fr;

		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				types.add(new Type(s));
			}
			br.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}

	//Functions�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
	public static void readFunctionFileAndAddList(String input_txt_file_name, ArrayList<Function> functions){
		FileReader fr;
		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				functions.add(new Function(s));
			}
			br.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}
	
	//Functions�t�@�C����ǂݍ���ŁA���X�g�Ɋi�[
	public static void readFileAndAddList(String input_txt_file_name, ArrayList<String> methodNames){
		FileReader fr;
		try {
			fr = new FileReader(input_txt_file_name);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				methodNames.add(s);
			}
			br.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}
}