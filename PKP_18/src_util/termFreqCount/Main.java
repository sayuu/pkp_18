package termFreqCount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	static final String[] project_names = {
		"Buddi",
		"Jmol",
		"Vuze",
		"carol",
		"dnsjava",
		"jMemorize",
		"jakartaCC",
		"jedit",
		"jruby",
		"rssowl",
		"sphinx",
		"tvbrowser",
		"zimbra",
	};
	
	static final String[] project_paths = {
		"/home/sayuu/source/Buddi-3.4.0.11.src/src",
		"/home/sayuu/source/Jmol/src",
		"/home/sayuu/source/Vuze_4604_source",
		"/home/sayuu/source/carol-2.0.5-src/src",
		"/home/sayuu/source/dnsjava/trunk/org",
		"/home/sayuu/source/jMemorize-1.3.0/src",
		"/home/sayuu/source/commons-codec-1.4-src",
		"/home/sayuu/source/jedit4.4.1source",
		"/home/sayuu/source/jruby-1.6.3/src",
		"/home/sayuu/source/rssowl",
		"/home/sayuu/source/cmusphinx",
		"/home/sayuu/source/tvbrowser-3.0.1/src",
		"/home/sayuu/source/zcs-6.0.7_GA_2483-src",
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
		//String path = "/home/sayuu/source/commons-codec-1.4-src";
		//String path = "/home/sayuu/source/jMemorize-1.3.0/src";
		//String path = "/home/sayuu/source/Vuze_4604_source";
		//String path = "/home/sayuu/source/jedit4.4.1source";
		//String path = "/home/sayuu/source/Buddi-3.4.0.11.src/src";
		//String path = "/home/sayuu/source/carol-2.0.5-src/src";
		//String path = "/home/sayuu/source/tvbrowser-3.0.1/src";
		//String path = "/home/sayuu/source/Jmol/src";
		//String path = "/home/sayuu/source/dnsjava/trunk/org";
		//String path = "/home/sayuu/source/rssowl";
		//String path = "/home/sayuu/source/jruby-1.6.3/src";
		 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
		
		//String path = "/home/sayuu/source/zcs-6.0.7_GA_2483-src";
		//String path = "/home/sayuu/source/cmusphinx";

		//CommentGetter.getComment(path);
		
		
		
		//methodtermfrequency(path);
		//allProjects();
		
		//MethodNameGetter.getMethodName3(path, false, false);
		//MethodNameGetter.getMethodLine(path);
		
		/*
		for(int i = 0; i < project_names.length; i++){
			methodtermfrequency(project_names[i], project_paths[i]);
		}
		*/
		
		String path = "C://Users//sayuu//Desktop//srcjar//src";
		
		methodtermfrequency("all", path);
		//allProjects();

	}

	public static void methodtermfrequency(String project_name, String path) {
		MethodNameGetter.getMethodName3(path, true, false);
		MethodNameTermCount.MethodNameTermCountForSimpleTxt(project_name);
	}
	

	/*
	 * �S�v���W�F�N�g�̒P��p�x���W�v�������B
	 */
	public static void allProjects(){
		String path = "/home/sayuu/source/�o�͌���/term";
		
		 FileSearch search = new FileSearch();

		 File[] files = search.listFiles(path, "*.txt");
	     search.clear();
	     
	     
	     File outfile = new File(path + "/term_total.txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
		     HashMap<String, Integer> termMap = new HashMap<String, Integer>();

		     
		     //1�t�@�C�����ǂݍ���Œ��ׂ�
		     for(File f : files){
		    	if(f.getName().equals("term_total.txt")){
		    		continue;	//��΂�
		    	}
		    	 try {
		             FileReader in = new FileReader(f);
		             BufferedReader br = new BufferedReader(in);
		             String line;
		    
		             while ((line = br.readLine()) != null) {
		            	 line = line.trim();	//�O��̋󔒕����폜
		            	
		                String[] arr = line.split(":");
		                String term = arr[0].trim();
		                Integer count = Integer.valueOf(arr[1].trim());	//�擾�����l
			            if(termMap.containsKey(term)){
			            	 count += termMap.get(term);	//���ɕێ����Ă���l�𑫂��B
			            }
			            termMap.put(term, count);
			         	
		             }
		             
		             br.close();
		             in.close();
		         } catch (IOException e) {
		             System.out.println(e);
		         }
		     }
		     
		  // ��r�̂��߂̃N���X���w�肵�� TreeMap �𐶐�
		    TreeMap treeMap = new TreeMap(new IntegerMapComparator(termMap));
		    // TreeMap �ɑS���̑g���R�s�[(���̂Ƃ��Ƀ\�[�g�����)
		    treeMap.putAll(termMap);
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
		     //System.out.println("java�t�@�C����: " + f_num + ", ���s��: " + line_sum + ", //�R�����g���s��: " + com_sum + ", �R�����g��(%):" + (double)com_sum/line_sum*100);
		     //pw.println("java�t�@�C����: " + f_num + ", ���s��: " + line_num + ", //���\�b�h����: " + method_num);
			
			pw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


}
