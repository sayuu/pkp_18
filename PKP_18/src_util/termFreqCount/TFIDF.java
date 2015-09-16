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

public class TFIDF {

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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		String path = "/home/sayuu/source/�o�͌���/term/";

		for(String target_name: project_names){
			tf(target_name, path);
		}
		
		
	}

	public static void tf(String target_name, String path){
		String path_out = "/home/sayuu/source/�o�͌���/TFIDF/tf_" + target_name + ".txt";    
	     File outfile = new File(path_out);
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			HashMap<String, Double> tfMap = new HashMap<String, Double>();
			 
			HashMap<String, Integer> term_count_Map_t = new HashMap<String, Integer>();

	    	 try {
	            
	             FileReader in_t = new FileReader(path + "term_total.txt");
	             BufferedReader br_t = new BufferedReader(in_t);
	             
	           //total.txt����f�[�^���������Ɏ擾
                String line_t;
                while ((line_t = br_t.readLine()) != null) {
	            	line_t = line_t.trim();	//�O��̋󔒕����폜
	            	
	                String[] arr_t = line_t.split(":");
	                String term_t = arr_t[0].trim();	//�P��
                	Integer count_t = Integer.valueOf(arr_t[1].trim());	//�p�x

	                term_count_Map_t.put(term_t, count_t);
	             }
	             
                FileReader in = new FileReader(path + "term_" + target_name + ".txt");
	             BufferedReader br = new BufferedReader(in);
	             
	             String line;
	             //�e�P��ɂ���tf�����߂�B���[�v�̓v���W�F�N�g�ɏo�����鑍�P�ꐔ
	             while ((line = br.readLine()) != null) {
	            	 line = line.trim();	//�O��̋󔒕����폜
	            	
	                String[] arr = line.split(":");
	                String term = arr[0].trim();	//�P��
	                Integer count = Integer.valueOf(arr[1].trim());	//�p�x
		            
	               double tf = (double)count / term_count_Map_t.get(term);
	               tfMap.put(term, tf);
	               
	             }
	             
	             br.close();
	             in.close();
	         } catch (IOException e) {
	             System.out.println(e);
	         }
		  
	         // ��r�̂��߂̃N���X���w�肵�� TreeMap �𐶐�
		    TreeMap treeMap = new TreeMap(new DoubleMapComparator(tfMap));
		    // TreeMap �ɑS���̑g���R�s�[(���̂Ƃ��Ƀ\�[�g�����)
		    treeMap.putAll(tfMap);
		    Set termSet = treeMap.keySet();  // �\�[�g����Ă���
			Iterator iterator1 = termSet.iterator();
			while(iterator1.hasNext()) {
			    String key = (String)iterator1.next();
			    Double value = (Double)treeMap.get(key);
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
	
}
