package termFreqCount;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * �w�肵���t�H���_�̒��̃t�@�C����S�Č������A
 * ���\�b�h�������o���N���X�B
 * 								2011/07/13
 * 
 * �R�����g�́AgetMethodName3����܂��ɔr���B
 * 
 * if �Ɓ@while�Afor�͏����ׂ��B�܂�����ĂȂ��B (�����ł͂Ȃ��Ȃ邪�A�܂��n�̕������ł��������낤�B
 * 	getMethodLine �ς�
 *  getMethodName3 �ς݁@2011/10/21
 * 
 */

public class MethodNameGetter {

	static String filename = "C://Users//sayuu//Desktop//m_name_output.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String path = "/home/sayuu/source/test";
		//String path = "/home/sayuu/source/commons-codec-1.4-src";
		String path = "/home/sayuu/source/Vuze_4604_source";

		getMethodName3(path, true, false);
	}
	//���O�̑啶���̕����̑O�ɕ�����with��}�����A�啶�����������ɕϊ�����
	  //(���ӁFIO, URL, UTF�Ȃǂ����ׂăo���o���ɕ�������Ă��܂��I)
	  public static String splitName(String name,  String with){
		  StringBuffer sb = new StringBuffer(name);
		  for(int i = 0; i < sb.length(); i++){
			  //�啶���i�ʒui�j��������
			  if(Character.isUpperCase(sb.charAt(i))){
				//���̑啶���̑O�ɕ�����with��}��
				if(i >= 1){
					sb.insert(i, with);
					//������with�̒���������i��i�߂�
					i += with.length();
				}
			  }
		  }
		  return sb.toString().toLowerCase();
	  }

		/*
		 * ���\�b�h�s���擾����Btxt��output����B
		 * 
		 * getMethodName3�����������B
		 * 
		 */
		public static void getMethodLine(String path){
			// TODO Auto-generated method stub
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
			 FileSearch search = new FileSearch();

			 //System.out.println("\n���g���qjava�̃t�@�C�����擾");
			 File[] files = search.listFiles(path, "*.java");
		     //printFileList(files);
		     search.clear();
		     
		     
		     File outfile = new File("/home/sayuu/source/m_name_output.txt");
		     try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
				
				//System.out.println("�t�@�C���ԍ��F�R�����g�ԍ��F�R�����g/�R�[�h");
				//pw.println("�t�@�C���ԍ�: �s�ԍ�: ���\�b�h�ԍ�: ���\�b�h��: �s");
			   
			     int line_num = 0;
			     int method_num = 0;
			     int f_num=1;
			     //1�t�@�C�����ǂݍ���Œ��ׂ�
			     for(File f : files){
			    	//1�s���ǂݍ���ł��̍s���R�����g�s���ǂ������ׂ�
			    	 try {
			             FileReader in = new FileReader(f);
			             BufferedReader br = new BufferedReader(in);
			             String line;
			             
			             /*
	           		  * �O���ʂ̑O�ɕ����񂪂���B
	           		  * 
	           		  * .* ���ł���������A�O��ȏ�
	           		  * 
	           		  * [\s���Z�q] �󔒕����Ɖ��Z�q�� 1��
	           		  *
	           		  * [a-zA-Z]+ ������1��ȏ�
	           		  * 
	           		  * (�͂��̂܂܎g���Ɠ��ʂȋL���Ƃ��ĔF������Ă��܂��̂ŁA
	           		  * \\(�Ƃ���B
	           		  * 
	           		  * ���̉��Z�q���ꉞ�S�Ă�\\��O�ɂ��Ă����B
	           		  * 
	           		  * []���̉��Z�q�܂��͋󔒕����̂����̂����ꂩ�ɓ��Ă͂܂�̈Ӗ��B
	           		  * 
	           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
	           		  * 
	           		  * ���o�������p�^�[����()�ł�����B
	           		  * ([a-zA-Z]+)
	           		  * 
	           		  */
			             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
		            		             
			             /*
			              * ���ꂾ��
			              * 1�s�ɕ������\�b�h���݂����Ƃ���1�����Ƃ�Ȃ�����
			              * �ǂ������炢���񂾂낤�B
			              * 
			              * �Ⴆ�΂��ꂾ�ƁA
			              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
			              *  
			              *  getBytesUnchecked
			              *  encodeHexString
			              *  getCharsetName
			              *  
			              *  ���̂R�����\�b�h���B
			              */

			             /*
			              * matcher.matches();
			              * �ł͂Ȃ��āA
			              * matcher.find();
			              * ���g���B
			              * 
			              * matches�͕��͑S�̂�ΏۂƂ��邪�A
			              * find�͕����������ΏۂƂ���B
			              * 
			              * ���͒��Ƀp�^�[���Ƀ}�b�`���镶���񂪕��������ꍇ��
			              * ������������ƕ֗��B
			              * ����ƁA
			              * 
			              * �}�b�`���镔��������݂̂ɑΉ����鐳�K�\�������������Ηǂ�
			              * �̂ŊȒP�ɂȂ�B
			              */
			    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
			    		 
		            	 boolean flg_block_comment = false;	//�u���b�N�R�����g�̃t���O
  	
			             while ((line = br.readLine()) != null) {
			            	 line_num++;
			            	 
			            	//��s�R�����g�𒲂ׂ�B
			            	 line = line.trim();	//�O��̋󔒕����폜
			            	 
			            	 //trim���Ē���0�Ȃ��s�B
			            	 if(line.length() == 0){
			            		 continue;
			            	 }
			            	 
			            	 //if����while���Afor��������
			            	 if(line.startsWith("if") == true || line.startsWith("else if") == true || line.startsWith("else") == true || line.startsWith("while") == true|| line.startsWith("for") == true){
			            		 continue;
			            	 }
			            	 

			            	 //if���̏I���ƁA���\�b�h�錾�΍�A
			            	 if(line.startsWith("}") == true || line.endsWith("{") == true){ 
			            		 continue;
			            	 }
			            	 
			            	 //"="���������炻�̂����낾�������B ������̐擪�������폜
			            	 if(line.contains("=") == true){
			            		 line = line.substring(line.indexOf("=")+1);
			            		 line = line.trim();
			            	 }
			            	 
		         			//System.out.println(line);
		         			//pw.println(line);

			            	 
			            	//�s������R�����g���n�܂��Ă�����̂̂݃`�F�b�N�B����ȊO�̓r������n�܂��Ă����͖ʓ|�Ȃ̂ł��̂܂܁B
		         			
		         			if(flg_block_comment == true){	//�u���b�N�R�����g�̒�
			         			
		         				//System.out.println("�u���b�N�R�����g�@��");
			         			//pw.println("�u���b�N�R�����g�@��");
			         			
			         			//�u���b�N�R�����g�I������
			         			if(line.startsWith("*/")){
				         			//System.out.println("�u���b�N�R�����g�@�I��");
				         			//pw.println("�u���b�N�R�����g�@�I��");
				         			flg_block_comment = false;
				         		}
			         			
			         			continue;
			             	
			         			
		         			}else{	//�u���b�N�R�����g�̊O
		         				
			         			//1�s�R�����g�̔���
		         				if(line.startsWith("//")){
				         			//System.out.println("��s�R�����g");
				         			//pw.println("��s�R�����g");
				         			continue;
				         		}else if(line.startsWith("/*")){	//�u���b�N�R�����g�J�n����
				         			//System.out.println("�u���b�N�R�����g�@�J�n");
				         			//pw.println("�u���b�N�R�����g�@�J�n");
				         			flg_block_comment = true;
				         			continue;
				         		}
		         			}
			            	
			        		 
			            	 
			            	 Matcher matcher = pattern.matcher(line);
		    	        	      	 
			            	 //���̍s�̂ǂ����Ƀ��\�b�h����������A���̍s��\������
			            	 while(matcher.find()){
			            		 method_num++;
				    	         //String matchedString = matcher.group(1);
				    	         //System.out.println(matchedString);
				    	         pw.println(line);

				    			 /*
				    	         if(show_matchedString)
				    	        	 pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
				    	         if(splitted)
				    	        	 for(String s_name: splitName(matchedString, ",").split(",")){
				    	        		 pw.println(s_name);
				    	        	 }
				    	         else
				    	        	 pw.println(matchedString);
				    	         */
				    	      }
				    	      
			             }
			             
			             br.close();
			             in.close();
			         } catch (IOException e) {
			             System.out.println(e);
			         }
			         f_num++;
			     }
			     //System.out.println("java�t�@�C����: " + f_num + ", ���s��: " + line_sum + ", //�R�����g���s��: " + com_sum + ", �R�����g��(%):" + (double)com_sum/line_sum*100);
			     //pw.println("java�t�@�C����: " + f_num + ", ���s��: " + line_num + ", //���\�b�h����: " + method_num);
				
				pw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	  
	  
		/*
		 * ���\�b�h���̗��񂾂��������ꂽtxt�t�@�C�����o�͂���B 
		 * 
		 * splitted=true �̂Ƃ��A�啶���̕����Ŗ��O��P��ɕ�������B> �p�x�v������true�ɂ���B
		 * show_matchedString=true �̂Ƃ��A�}�b�`�����s���o�͂ɏ������ށB > �p�x�v������false�ɂ���B
		 * 
		 * getMethodName2 �̓R�����g�������擾���Ă��܂��̂ŁA
		 * �R�����g������r������悤�ɍ�蒼���B
		 * 
		 */
		public static void getMethodName3(String path, boolean splitted, boolean show_matchedString){
			// TODO Auto-generated method stub
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
			 FileSearch search = new FileSearch();

			 //System.out.println("\n���g���qjava�̃t�@�C�����擾");
			 File[] files = search.listFiles(path, "*.java");
		     //printFileList(files);
		     search.clear();
		     
		     
		     File outfile = new File(filename);
		     try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
				
				//System.out.println("�t�@�C���ԍ��F�R�����g�ԍ��F�R�����g/�R�[�h");
				//pw.println("�t�@�C���ԍ�: �s�ԍ�: ���\�b�h�ԍ�: ���\�b�h��: �s");
			   
			     int line_num = 0;
			     int method_num = 0;
			     int f_num=1;
			     //1�t�@�C�����ǂݍ���Œ��ׂ�
			     for(File f : files){
			    	//1�s���ǂݍ���ł��̍s���R�����g�s���ǂ������ׂ�
			    	 try {
			             FileReader in = new FileReader(f);
			             BufferedReader br = new BufferedReader(in);
			             String line;
			             
			             /*
	           		  * �O���ʂ̑O�ɕ����񂪂���B
	           		  * 
	           		  * .* ���ł���������A�O��ȏ�
	           		  * 
	           		  * [\s���Z�q] �󔒕����Ɖ��Z�q�� 1��
	           		  *
	           		  * [a-zA-Z]+ ������1��ȏ�
	           		  * 
	           		  * (�͂��̂܂܎g���Ɠ��ʂȋL���Ƃ��ĔF������Ă��܂��̂ŁA
	           		  * \\(�Ƃ���B
	           		  * 
	           		  * ���̉��Z�q���ꉞ�S�Ă�\\��O�ɂ��Ă����B
	           		  * 
	           		  * []���̉��Z�q�܂��͋󔒕����̂����̂����ꂩ�ɓ��Ă͂܂�̈Ӗ��B
	           		  * 
	           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
	           		  * 
	           		  * ���o�������p�^�[����()�ł�����B
	           		  * ([a-zA-Z]+)
	           		  * 
	           		  */
			             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
		            		             
			             /*
			              * ���ꂾ��
			              * 1�s�ɕ������\�b�h���݂����Ƃ���1�����Ƃ�Ȃ�����
			              * �ǂ������炢���񂾂낤�B
			              * 
			              * �Ⴆ�΂��ꂾ�ƁA
			              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
			              *  
			              *  getBytesUnchecked
			              *  encodeHexString
			              *  getCharsetName
			              *  
			              *  ���̂R�����\�b�h���B
			              */

			             /*
			              * matcher.matches();
			              * �ł͂Ȃ��āA
			              * matcher.find();
			              * ���g���B
			              * 
			              * matches�͕��͑S�̂�ΏۂƂ��邪�A
			              * find�͕����������ΏۂƂ���B
			              * 
			              * ���͒��Ƀp�^�[���Ƀ}�b�`���镶���񂪕��������ꍇ��
			              * ������������ƕ֗��B
			              * ����ƁA
			              * 
			              * �}�b�`���镔��������݂̂ɑΉ����鐳�K�\�������������Ηǂ�
			              * �̂ŊȒP�ɂȂ�B
			              */
			    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
			    		 
		            	 boolean flg_block_comment = false;	//�u���b�N�R�����g�̃t���O
    	
			             while ((line = br.readLine()) != null) {
			            	 line_num++;
			            	 
			            	//��s�R�����g�𒲂ׂ�B
			            	 line = line.trim();	//�O��̋󔒕����폜
			            	 
			            	 //trim���Ē���0�Ȃ��s�B
			            	 if(line.length() == 0){
			            		 continue;
			            	 }

			            	 //���\�b�h�錾�΍�A
			            	 if(line.endsWith("{") == true){ 
			            		 continue;
			            	 }

			            	 //if����while���Afor��������
			            	 if(line.startsWith("if") == true ){
			            		 line = line.substring(line.indexOf("if")+1);
			            		 line = line.trim();
			            	 }
			            	 
			            	 //if����while���Afor��������
			            	 if(line.startsWith("else if") == true){
			            		 line = line.substring(line.indexOf("else if")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if����while���Afor��������
			            	 if(line.startsWith("}else if") == true){
			            		 line = line.substring(line.indexOf("}else if")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if����while���Afor��������
			            	 if(line.startsWith("} else if") == true){
			            		 line = line.substring(line.indexOf("} else if")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if����while���Afor��������
			            	 if(line.startsWith("else") == true){
			            		 line = line.substring(line.indexOf("else")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if����while���Afor��������
			            	 if(line.startsWith("}else") == true){
			            		 line = line.substring(line.indexOf("}else")+1);
			            		 line = line.trim();
			            	 }
			            	 

			            	 //if����while���Afor��������
			            	 if(line.startsWith("} else") == true){
			            		 line = line.substring(line.indexOf("} else")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if����while���Afor��������
			            	 if(line.startsWith("while") == true){
			            		 line = line.substring(line.indexOf("while")+1);
			            		 line = line.trim();
			            	 }
			            	
			            	 //if����while���Afor��������
			            	 if(line.startsWith("for") == true){
			            		 line = line.substring(line.indexOf("for")+1);
			            		 line = line.trim();
			            	 }
			            	

			            	 
		         			//System.out.println(line);
		         			//pw.println(line);

			            	 
			            	//�s������R�����g���n�܂��Ă�����̂̂݃`�F�b�N�B����ȊO�̓r������n�܂��Ă����͖ʓ|�Ȃ̂ł��̂܂܁B
		         			
		         			if(flg_block_comment == true){	//�u���b�N�R�����g�̒�
			         			
		         				//System.out.println("�u���b�N�R�����g�@��");
			         			//pw.println("�u���b�N�R�����g�@��");
			         			
			         			//�u���b�N�R�����g�I������
			         			if(line.startsWith("*/")){
				         			//System.out.println("�u���b�N�R�����g�@�I��");
				         			//pw.println("�u���b�N�R�����g�@�I��");
				         			flg_block_comment = false;
				         		}
			         			
			         			continue;
			             	
			         			
		         			}else{	//�u���b�N�R�����g�̊O
		         				
			         			//1�s�R�����g�̔���
		         				if(line.startsWith("//")){
				         			//System.out.println("��s�R�����g");
				         			//pw.println("��s�R�����g");
				         			continue;
				         		}else if(line.startsWith("/*")){	//�u���b�N�R�����g�J�n����
				         			//System.out.println("�u���b�N�R�����g�@�J�n");
				         			//pw.println("�u���b�N�R�����g�@�J�n");
				         			flg_block_comment = true;
				         			continue;
				         		}
		         			}
			            	
			        		 
			            	 
			            	 Matcher matcher = pattern.matcher(line);
			            	 
			    			            	 
			            	 //���̍s�̂ǂ����Ƀ��\�b�h����������A���̍s��\������
			            	 while(matcher.find()){
			            		 method_num++;
				    	         String matchedString = matcher.group(1);
				    	         //System.out.println(matchedString);
				    	         
				    	         if(show_matchedString)
				    	        	 pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
				    	         if(splitted)
				    	        	 for(String s_name: splitName(matchedString, ",").split(",")){
				    	        		 pw.println(s_name);
				    	        	 }
				    	         else
				    	        	 pw.println(matchedString);
				    	         
				    	      }
			             }
			             
			             br.close();
			             in.close();
			         } catch (IOException e) {
			             System.out.println(e);
			         }
			         f_num++;
			     }
			     //System.out.println("java�t�@�C����: " + f_num + ", ���s��: " + line_sum + ", //�R�����g���s��: " + com_sum + ", �R�����g��(%):" + (double)com_sum/line_sum*100);
			     //pw.println("java�t�@�C����: " + f_num + ", ���s��: " + line_num + ", //���\�b�h����: " + method_num);
				
				pw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	
	/*
	 * ���\�b�h���̗��񂾂��������ꂽtxt�t�@�C�����o�͂���B 
	 * 
	 * splitted=true �̂Ƃ��A�啶���̕����Ŗ��O��P��ɕ�������B> ���i��true�ɂ���B
	 * show_matchedString=true �̂Ƃ��A�}�b�`�����s���o�͂ɏ������ށB > ���i��false�ɂ���B
	 */
	public static void getMethodName2(boolean splitted, boolean show_matchedString){
		// TODO Auto-generated method stub
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
		String path = "/home/sayuu/source/jruby-1.6.3/src";

		
		 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
		 FileSearch search = new FileSearch();

		 //System.out.println("\n���g���qjava�̃t�@�C�����擾");
		 File[] files = search.listFiles(path, "*.java");
	     //printFileList(files);
	     search.clear();
	     
	     
	     File outfile = new File("/home/sayuu/source/m_name_output.txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			//System.out.println("�t�@�C���ԍ��F�R�����g�ԍ��F�R�����g/�R�[�h");
			//pw.println("�t�@�C���ԍ�: �s�ԍ�: ���\�b�h�ԍ�: ���\�b�h��: �s");
		   
		     int line_num = 0;
		     int method_num = 0;
		     int f_num=1;
		     //1�t�@�C�����ǂݍ���Œ��ׂ�
		     for(File f : files){
		    	//1�s���ǂݍ���ł��̍s���R�����g�s���ǂ������ׂ�
		    	 try {
		             FileReader in = new FileReader(f);
		             BufferedReader br = new BufferedReader(in);
		             String line;
		             
		             /*
           		  * �O���ʂ̑O�ɕ����񂪂���B
           		  * 
           		  * .* ���ł���������A�O��ȏ�
           		  * 
           		  * [\s���Z�q] �󔒕����Ɖ��Z�q�� 1��
           		  *
           		  * [a-zA-Z]+ ������1��ȏ�
           		  * 
           		  * (�͂��̂܂܎g���Ɠ��ʂȋL���Ƃ��ĔF������Ă��܂��̂ŁA
           		  * \\(�Ƃ���B
           		  * 
           		  * ���̉��Z�q���ꉞ�S�Ă�\\��O�ɂ��Ă����B
           		  * 
           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
           		  * 
           		  * ���o�������p�^�[����()�ł�����B
           		  * ([a-zA-Z]+)
           		  * 
           		  */
		             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
	            		             
		             /*
		              * ���ꂾ��
		              * 1�s�ɕ������\�b�h���݂����Ƃ���1�����Ƃ�Ȃ�����
		              * �ǂ������炢���񂾂낤�B
		              * 
		              * �Ⴆ�΂��ꂾ�ƁA
		              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
		              *  
		              *  getBytesUnchecked
		              *  encodeHexString
		              *  getCharsetName
		              *  
		              *  ���̂R�����\�b�h���B
		              */

		             /*
		              * matcher.matches();
		              * �ł͂Ȃ��āA
		              * matcher.find();
		              * ���g���B
		              * 
		              * matches�͕��͑S�̂�ΏۂƂ��邪�A
		              * find�͕����������ΏۂƂ���B
		              * 
		              * ���͒��Ƀp�^�[���Ƀ}�b�`���镶���񂪕��������ꍇ��
		              * ������������ƕ֗��B
		              * ����ƁA
		              * 
		              * �}�b�`���镔��������݂̂ɑΉ����鐳�K�\�������������Ηǂ�
		              * �̂ŊȒP�ɂȂ�B
		              */
		    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
		   		 
		             while ((line = br.readLine()) != null) {
		            	 line_num++;
		            	 Matcher matcher = pattern.matcher(line);
		            	 
		            	 //���̍s�̂ǂ����Ƀ��\�b�h����������A���̍s��\������
		            	 while(matcher.find()){
		            		 method_num++;
			    	         String matchedString = matcher.group(1);
			    	         //System.out.println(matchedString);
			    	         //if(show_matchedString)
			    	         //pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
			    	         if(splitted)
			    	        	 for(String s_name: splitName(matchedString, ",").split(",")){
			    	        		 pw.println(s_name);
			    	        	 }
			    	         else
			    	        	 pw.println(matchedString);
			    	      }
		             }
		             
		             br.close();
		             in.close();
		         } catch (IOException e) {
		             System.out.println(e);
		         }
		         f_num++;
		     }
		     //System.out.println("java�t�@�C����: " + f_num + ", ���s��: " + line_sum + ", //�R�����g���s��: " + com_sum + ", �R�����g��(%):" + (double)com_sum/line_sum*100);
		     //pw.println("java�t�@�C����: " + f_num + ", ���s��: " + line_num + ", //���\�b�h����: " + method_num);
			
			pw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/*
	 * ���Ղ�txt�t�@�C�����o�͂���B 
	 */
	public static void getMethodName(){
		// TODO Auto-generated method stub
		 //String path = "C:\\sourcefiles\\commons-codec-1.4-src";
		String path = "/home/sayuu/source/commons-codec-1.4-src";
		 //String path = "C:\\sourcefiles\\jMemorize-1.3.0\\src";
		 FileSearch search = new FileSearch();

		 //System.out.println("\n���g���qjava�̃t�@�C�����擾");
		 File[] files = search.listFiles(path, "*.java");
	     //printFileList(files);
	     search.clear();
	     
	     
	     File outfile = new File("/home/sayuu/source/output.txt");
	     try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			
			//System.out.println("�t�@�C���ԍ��F�R�����g�ԍ��F�R�����g/�R�[�h");
			pw.println("�t�@�C���ԍ�: �s�ԍ�: ���\�b�h�ԍ�: ���\�b�h��: �s");
		   
		     int line_num = 0;
		     int method_num = 0;
		     int f_num=1;
		     //1�t�@�C�����ǂݍ���Œ��ׂ�
		     for(File f : files){
		    	//1�s���ǂݍ���ł��̍s���R�����g�s���ǂ������ׂ�
		    	 try {
		             FileReader in = new FileReader(f);
		             BufferedReader br = new BufferedReader(in);
		             String line;
		             
		             /*
           		  * �O���ʂ̑O�ɕ����񂪂���B
           		  * 
           		  * .* ���ł���������A�O��ȏ�
           		  * 
           		  * [\s���Z�q] �󔒕����Ɖ��Z�q�� 1��
           		  *
           		  * [a-zA-Z]+ ������1��ȏ�
           		  * 
           		  * (�͂��̂܂܎g���Ɠ��ʂȋL���Ƃ��ĔF������Ă��܂��̂ŁA
           		  * \\(�Ƃ���B
           		  * 
           		  * ���̉��Z�q���ꉞ�S�Ă�\\��O�ɂ��Ă����B
           		  * 
           		  * [\\s\\(=<>\\+\\-\\*\\/\\%]
           		  * 
           		  * ���o�������p�^�[����()�ł�����B
           		  * ([a-zA-Z]+)
           		  * 
           		  */
		             //Pattern pattern = Pattern.compile(".*[\\.\\s\\(=<>\\+\\-\\*\\/\\%]([a-zA-Z]+)\\(.*");
	            		             
		             /*
		              * ���ꂾ��
		              * 1�s�ɕ������\�b�h���݂����Ƃ���1�����Ƃ�Ȃ�����
		              * �ǂ������炢���񂾂낤�B
		              * 
		              * �Ⴆ�΂��ꂾ�ƁA
		              *  return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
		              *  
		              *  getBytesUnchecked
		              *  encodeHexString
		              *  getCharsetName
		              *  
		              *  ���̂R�����\�b�h���B
		              */

		             /*
		              * matcher.matches();
		              * �ł͂Ȃ��āA
		              * matcher.find();
		              * ���g���B
		              * 
		              * matches�͕��͑S�̂�ΏۂƂ��邪�A
		              * find�͕����������ΏۂƂ���B
		              * 
		              * ���͒��Ƀp�^�[���Ƀ}�b�`���镶���񂪕��������ꍇ��
		              * ������������ƕ֗��B
		              * ����ƁA
		              * 
		              * �}�b�`���镔��������݂̂ɑΉ����鐳�K�\�������������Ηǂ�
		              * �̂ŊȒP�ɂȂ�B
		              */
		    		 Pattern pattern = Pattern.compile("[\\.\\s\\(=<>\\+\\-\\*\\/\\%]?([a-zA-Z]+)\\(");   
		   		 
		             while ((line = br.readLine()) != null) {
		            	 line_num++;
		            	 Matcher matcher = pattern.matcher(line);
		            	 
		            	 //���̍s�̂ǂ����Ƀ��\�b�h����������A���̍s��\������
		            	 while(matcher.find()){
		            		 method_num++;
			    	         String matchedString = matcher.group(1);
			    	         //System.out.println(matchedString);
			    	         pw.println(f_num + ": " + line_num + ": " + ": " + method_num + ": " + matchedString + ": " + line);
			    	      }
		             }
		             
		             br.close();
		             in.close();
		         } catch (IOException e) {
		             System.out.println(e);
		         }
		         f_num++;
		     }
		     //System.out.println("java�t�@�C����: " + f_num + ", ���s��: " + line_sum + ", //�R�����g���s��: " + com_sum + ", �R�����g��(%):" + (double)com_sum/line_sum*100);
		     pw.println("java�t�@�C����: " + f_num + ", ���s��: " + line_num + ", //���\�b�h����: " + method_num);
			
			pw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
