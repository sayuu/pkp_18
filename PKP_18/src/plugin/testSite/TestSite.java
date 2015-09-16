package plugin.testSite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import ast.AstUtil;


import console_test.LevenshteinDistance;

import keywordProgramming.ExplanationVector;
import keywordProgramming.Function;
import keywordProgramming.FunctionTree;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;
import keywordProgramming.Word;

/*
 * 1�̃^�X�N��\���N���X�B
 */
public class TestSite {

	public static final String TestSiteFolder = "./KeywordProgramming/testSite/";
	public static final String LogSiteFolder = "./KeywordProgramming/logSite/";

	private static List<TestSite> logFiles = new LinkedList<TestSite>();	//�I�����C���w�K�p�̃��O�t�@�C��
	
	private String className;//�N���X���i�p�b�P�[�W�����j
	private int offset;
	private int original_offset;	//�ύX�O�B
    private int line_number_start;
    private int line_number_end;
    private int selected_length;
    //�I�𕶎���
    private String selected_string;
    //�I�𕶎���̏���
    private int selected_string_order;
    //�]�܂����Ԃ�l�̌^
  	private String desiredReturnType;
  	//���̓L�[���[�h
  	private String keywords;
  	//���̓L�[���[�h (��ɖ߂��Ƃ��p�j
  	private String keywords_original;
  	//���P�[�V����
  	/*
  	 * Return�� �̒�
	 * If���@�̒�
	 * �錾��
	 * �����
	 * �n�̕�
	 * ���\�b�h�̈����̒�
	 * �ȂǁB
  	 */
  	private String location;
  	
  	//�d����h�����߂�HashSet�ɂ����B
  	//���݃G�f�B�^���ɑ��݂���L���Ȍ^�����郊�X�g
  	private HashSet<String> classesInActiveEditor = new HashSet<String>();
  	//���݃G�f�B�^���ɑ��݂���L���Ȋ֐������郊�X�g
  	private HashSet<String> functionsInActiveEditor = new HashSet<String>();
  	
  	//1��TextSite�ɑΉ�����1�̃e�L�X�g�t�@�C��
  	private File txtFile;
  	
  	//�I�����ꂽ
  	private boolean isSelectedTask = false;
  	
  	//���O�t�@�C�����ۂ�
  	private boolean isLogFile;
  	
  	//�L�[���[�h�v���O���~���O�̏o��(�o�b�`�w�K�̎��Ɏg�p�B)
  	public List<FunctionTree> outputfunctionTrees = null;
  	
  	//�L�[���[�h�v���O���~���O�̏o��(�I�����C���w�K�̎��Ɏg�p�B)
  	private List<OutputCandidateLog> outputLogList = new ArrayList<OutputCandidateLog>();
  	
  	private int version;	//�^�X�N�`���̃o�[�W����
  	/*
  	 * selected_string �ɂ�����J�n�sx
  	 * �ƁA�֐���y �̑g�ݍ��킹,��؂�B
  	 * ��������ꍇ��;��؂�
  	 * ��F
  	 * 
  	 * 0,activeList;9,token
  	 *  
  	 */
  	private String recieverString;	//���V�[�o(����)��1�s�ŕ\�����B
  	private String localValString;	//���[�J���ϐ�(����)��1�s�ŕ\�����B
  	
  	public List<String> methodList = new ArrayList<String>();	//���\�b�h�ꗗ(�R���X�g���N�^���܂�)
//  	public List<String> recieverList = new ArrayList<String>();	//���V�[�o (String.�Ȃ�static�̃N���X�����܂�)
//  	public List<String> localValList = new ArrayList<String>();	//�ϐ�
  	
  	public List<String> methodNameList = new ArrayList<String>();	//���\�b�h���ꗗ(�R���X�g���N�^���܂�, new ���܂�)
  	public List<String> variableNameList = new ArrayList<String>();	//�ϐ����ꗗ(�t�B�[���h���܂�)
  	
  	public static int type_of_shortened_keywords = 0;
  	
  	//1�̃t�@�C��file�͂P��TestSite�ɑΉ��B
  	public TestSite(File file, boolean isLogFile){
  		setTxtFile(file);
  		this.isLogFile = isLogFile;
    	FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String db_str;
			int line_number = 0;
			while ((db_str = br.readLine()) != null) {
				/*
				if(line_number == 0){
					//getInteger(db_str, 1)
					//db_str ��int�^�ɂ��ĕԂ��B
					//db_str�����l�ł͂Ȃ��Ƃ��́A��2������1��Ԃ��B
					version = Integer.getInteger(db_str, 1);
				}
				
				if(version == 1)
					readVersion1(db_str, line_number);
				else if(version == 2)
					readVersion2(db_str, line_number);
				*/
				
				readVersion4(db_str, line_number);
				
		  		line_number++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
		/*
		 * Log�t�@�C���̏ꍇ�A�o�͌��Q��ۑ������t�@�C�� ".*out.txt" ���ǂݍ���
		 */
		if(isLogFile){
			String name = getTxtFile().getName().replace(".txt", "out.txt");
			String path = getTxtFile().getParent() + "/" +name;
			File logFile = new File(path);
			if(logFile.exists()){
				try {
					fr = new FileReader(logFile);
					BufferedReader br = new BufferedReader(fr);

					selected_string_order = Integer.parseInt(br.readLine());//�擪�s�͑I�����̏��ʁB
					
					String log_str;
					while ((log_str = br.readLine()) != null) {
						OutputCandidateLog log = new OutputCandidateLog(log_str, keywords.split("[ �@\t]").length);
						outputLogList.add(log);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
    }
  	
  	public HashSet<String> getClassesInActiveEditor() {
		return classesInActiveEditor;
	}

	public HashSet<String> getFunctionsInActiveEditor() {
		return functionsInActiveEditor;
	}

	public File getTxtFile() {
		return txtFile;
	}

	public void setTxtFile(File txtFile) {
		this.txtFile = txtFile;
	}

	public void setFunctionsInActiveEditor(HashSet<String> functionsInActiveEditor) {
		this.functionsInActiveEditor = functionsInActiveEditor;
	}

	public void setClassesInActiveEditor(HashSet<String> classesInActiveEditor) {
		this.classesInActiveEditor = classesInActiveEditor;
	}

	/*
  	 * �^�X�N�̌`����
  	 * Version1
  	 * �̃^�X�N����t�B�[���h��ǂݍ��ށB
  	 */
	private void readVersion1(String db_str, int line_number) {
		if(line_number == 0)
			className = db_str;
		else if(line_number == 1)
			offset = Integer.parseInt(db_str);
		else if(line_number == 2)
			original_offset = Integer.parseInt(db_str);
		else if(line_number == 3)
			line_number_start = Integer.parseInt(db_str);
		else if(line_number == 4)
			line_number_end = Integer.parseInt(db_str);
		else if(line_number == 5)
			selected_length = Integer.parseInt(db_str);
		else if(line_number == 6)
			selected_string = db_str;
		else if(line_number == 7)
			keywords = db_str;
		else if(line_number == 8)
			desiredReturnType = db_str;
		else if(line_number == 9)
			location = db_str;
		else if(line_number == 10){
		    //class (type)
			String s1[] = db_str.split(";");   // ";"����؂�B
			for(String ss1: s1){
				getClassesInActiveEditor().add(ss1);
			}
		}else{
			//function 1�s���ƁB
		    getFunctionsInActiveEditor().add(db_str);
		}
	}
 	
  	/*
  	 * �^�X�N�̌`����
  	 * Version2
  	 * �̃^�X�N����t�B�[���h��ǂݍ��ށB
  	 */
	private void readVersion2(String db_str, int line_number) {
		//line_number == 0
		// �̎��́Aversion �܂�2
		
		if(line_number == 1)
			className = db_str;
		else if(line_number == 2)
			offset = Integer.parseInt(db_str);
		else if(line_number == 3)
			original_offset = Integer.parseInt(db_str);
		else if(line_number == 4)
			line_number_start = Integer.parseInt(db_str);
		else if(line_number == 5)
			line_number_end = Integer.parseInt(db_str);
		else if(line_number == 6)
			selected_length = Integer.parseInt(db_str);
		else if(line_number == 7)
			selected_string = db_str;
		else if(line_number == 8)
			keywords = db_str;
		else if(line_number == 9)
			desiredReturnType = db_str;
		else if(line_number == 10)
			location = db_str;
		else if(line_number == 11)
			recieverString = db_str;
		else if(line_number == 12)
			localValString = db_str;
		else if(line_number == 13){
		    //class (type)
			String s1[] = db_str.split(";");   // ";"����؂�B
			for(String ss1: s1){
				getClassesInActiveEditor().add(ss1);
			}
		}else{
			//function 1�s���ƁB
		    getFunctionsInActiveEditor().add(db_str);
		}
	}
	
  	/*
  	 * �^�X�N�̌`����
  	 * Version3
  	 * �̃^�X�N����t�B�[���h��ǂݍ��ށB
  	 */
//	private void readVersion3(String db_str, int line_number) {
//		if(line_number == 0)
//			className = db_str;
//		else if(line_number == 1)
//			offset = Integer.parseInt(db_str);
//		else if(line_number == 2)
//			original_offset = Integer.parseInt(db_str);
//		else if(line_number == 3)
//			line_number_start = Integer.parseInt(db_str);
//		else if(line_number == 4)
//			line_number_end = Integer.parseInt(db_str);
//		else if(line_number == 5)
//			selected_length = Integer.parseInt(db_str);
//		else if(line_number == 6)
//			selected_string = db_str;
//		else if(line_number == 7){
//			keywords = db_str;
//			keywords_original = db_str;
//		}else if(line_number == 8)
//			desiredReturnType = db_str;
//		else if(line_number == 9)
//			location = db_str;
//		else if(line_number == 10){
//			recieverString = db_str;
//			if(!recieverString.equals("null") && !recieverString.equals("")){
//				String s1[] = db_str.split(";");   // ";"��؂�
//				for(String ss1: s1){
//					String sss1 = ss1.split(",")[1];//17,encodeSize;29,context;
//					
//					boolean ismethod = false;
//					
//					//���߂��啶���Ȃ�Static���\�b�h�̈ꕔ(Character.is�`��Character)
//					boolean isStatic = Character.isUpperCase(sss1.charAt(0));
//					
//					//�Ōオ()�Ȃ烁�\�b�h�̈ꕔ
//					//(getMd2Digest().digest(data)�@�̂Ƃ��́AgetMd2Digest())
//					//2�d�A���̂Ƃ�������Bmethod().method().method(abc)
//					
//					boolean isMCall = Character.isUpperCase(sss1.charAt(0));
//					
//					
//					
//					if(isStatic == true || isMCall == true)
//						ismethod = true;
//						
//					if(ismethod){
//						methodList.add(sss1);//���\�b�h
//					}else{	
//						recieverList.add(sss1);//���V�[�o
//					}
//				}
//			}
//		}else if(line_number == 11){
//			localValString = db_str;
//			if(!localValString.equals("null") && !localValString.equals("")){
//				String s1[] = db_str.split(";");   // ";"��؂�
//				for(String ss1: s1){
//					String sss1 = ss1.split(",")[1];//17,encodeSize;29,context;
//					localValList.add(sss1);//���V�[�o
//				}
//			}
//		}else if(line_number == 12){
//		    //method��
//			String s1[] = db_str.split(" ");   // " "��؂�
//			for(String ss1: s1){
//				//���߂��啶���Ȃ�R���X�g���N�^
//				boolean iscnst = Character.isUpperCase(ss1.charAt(0));
//				if(iscnst){
//					methodList.add("new");
//				}
//				methodList.add(ss1);
//			}
//		}else if(line_number == 13){
//		    //class (type)
//			String s1[] = db_str.split(";");   // ";"����؂�B
//			for(String ss1: s1){
//				getClassesInActiveEditor().add(ss1);
//			}
//		}else{
//			//function 1�s���ƁB
//		    getFunctionsInActiveEditor().add(db_str);
//		}
//	}
  	/*
  	 * �^�X�N�̌`����
  	 * Version3
  	 * �̃^�X�N����t�B�[���h��ǂݍ��ށB
  	 */
	private void readVersion4(String db_str, int line_number) {
		if(line_number == 0)
			className = db_str;
		else if(line_number == 1)
			offset = Integer.parseInt(db_str);
		else if(line_number == 2)
			original_offset = Integer.parseInt(db_str);
		else if(line_number == 3)
			line_number_start = Integer.parseInt(db_str);
		else if(line_number == 4)
			line_number_end = Integer.parseInt(db_str);
		else if(line_number == 5)
			selected_length = Integer.parseInt(db_str);
		else if(line_number == 6)
			selected_string = db_str;
		else if(line_number == 7){
			keywords = db_str;
			keywords_original = db_str;
		}else if(line_number == 8)
			desiredReturnType = db_str;
		else if(line_number == 9)
			location = db_str;
		else if(line_number == 10)
			recieverString = db_str;
		else if(line_number == 11)
			localValString = db_str;
		else if(line_number == 12){
		    //method��
			String s1[] = db_str.split(" ");   // " "��؂�
			for(String ss1: s1){
				methodList.add(ss1);
			}
		}else if(line_number == 13){
			    //methodName��
				String s1[] = db_str.split(" ");   // " "��؂�
				for(String ss1: s1){
					methodNameList.add(ss1);
				}
		}else if(line_number == 14){
		    //variableName��
			String s1[] = db_str.split(" ");   // " "��؂�
			for(String ss1: s1){
				variableNameList.add(ss1);
			}
		}else if(line_number == 15){
		    //class (type)
			String s1[] = db_str.split(";");   // ";"����؂�B
			for(String ss1: s1){
				getClassesInActiveEditor().add(ss1);
			}
		}else{
			//function 1�s���ƁB
		    getFunctionsInActiveEditor().add(db_str);
		}
	}
  	public static void loadLogFiles(int numOfFiles){
  		String path = TestSite.LogSiteFolder;//���O��ǂݎ��
  		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        
	        for(File file: files){
	        	//�o�͕����񃍃O�t�@�C���͏����B
	        	if(file.getName().matches(".*out\\.txt$") == false){
		        	logFiles.add(new TestSite(file, true));
	        	}
	        }
	    }
  		
	    //���Ԃ̍~���Ƀ\�[�g����B
	    Collections.sort(logFiles, new TestSiteComparator());
	    
//	    for(TestSite site: logFiles){
//	    	System.out.println(site.getSaveTime());
//	    }
	   
  	}
  	
  	/*
  	 * ���O�t�@�C���̒ǉ��B
  	 */
  	public static void addLogFile(TestSite site){
  		//�擪�ɒǉ�����B
  		logFiles.add(0, site);
  	}

  	/*
  	 * ���O�t�@�C���̎擾
  	 * �w�肵�����̂݁A�ŐV�̐擪����B
  	 */
  	public static TestSite[] getLogFiles(int numOfFiles){
  		ArrayList<TestSite> list = new ArrayList<TestSite>();
  		for(int i = 0; i < numOfFiles; i++){
  			list.add(logFiles.get(i));
  		}
  		return (TestSite[]) list.toArray(new TestSite[list.size()]);
  	}
  	
    /**
     * �I���e�L�X�g����L�[���[�h��������������̂ŁA
     * �L�[���[�h�������ɕK�v�Ƃ��Ȃ��B
     * 
     * @param cn	�N���X��
     * @param o		�I�t�Z�b�g
     * @param o_ori	�ύX�O�̃I�t�Z�b�g
     * @param lis	�I���J�n�s
     * @param lie	�I���I���s
     * @param len	�I���e�L�X�g��
     * @param sct	�I���e�L�X�g
     * @param drt	�]�܂����Ԃ�l
     * @param lo	���P�[�V����
     * @param cls	Type�̃��X�g
     * @param fus	Function�̃��X�g
     * @param is_log_file ���O�t�@�C�����H
     */
    public TestSite(String cn, int o, int o_ori, int lis, int lie, int len, String sct, String drt, String lo, List<String> cls, List<String> fus, boolean is_log_file){
    	className = cn;
    	offset = o;
    	original_offset = o_ori;
        line_number_start = lis;
        line_number_end = lie;
        selected_length = len;
        selected_string = sct;
        keywords = output2InputKeyword(selected_string);
        keywords_original = keywords;
        desiredReturnType = drt;
        location = lo;
        setClassesInActiveEditor(new HashSet<String>(cls));
        setFunctionsInActiveEditor(new HashSet<String>(fus)); 
        isLogFile = is_log_file;
        //�t�@�C�����́u�p�b�P�[�W���{�N���X��/currentTimeMillis�v
      	String savefilename = TestSiteFolder + className.replaceAll("[<>]", "___") + "/" + System.currentTimeMillis() + ".txt";
      	setTxtFile(new File(savefilename));
    }

	public String toString(){
    	String s = 
    	"className= " + className +
    	"\noffset= " + offset +
    	"\noriginal_offset= " + original_offset +
        "\nline_number_start= " + line_number_start +
        "\nline_number_end= " + line_number_end +
        "\nselected_length= " + selected_length +
        "\nselected_string= " + selected_string +
        "\nkeywords= " + keywords +
        "\ndesiredReturnType= " + desiredReturnType +
        "\nlocation= " + location +
        "\nrecieverString= " + recieverString +
        "\nlocalValString= " + localValString +
        "\nmethodList= " + methodListString() +
        "\nmethodNameList= " + methodNameListString() +
        "\nvariableNameList= " + variableNameListString() +
        "\nclassesInActiveEditor= " + getClassesInActiveEditor() +
        "\nfunctionsInActiveEditor= " + getFunctionsInActiveEditor();
    	return s;
    }
    
	private String methodListString(){
    	String mlist = "";
    	for(String m:methodList){
    		mlist += m + " ";
    	}
    	return mlist;
	}
	private String methodNameListString(){
    	String mlist = "";
    	for(String m:methodNameList){
    		mlist += m + " ";
    	}
    	return mlist;
	}
	
	private String variableNameListString(){
    	String mlist = "";
    	for(String m:variableNameList){
    		mlist += m + " ";
    	}
    	return mlist;
	}
	

    public String toDBString(){
    	String s =  className + "\n" + offset + "\n" + original_offset + "\n" +
    line_number_start + "\n" + line_number_end + "\n" +
    selected_length + "\n" + selected_string + "\n" +
    keywords + "\n" + desiredReturnType + "\n" +
    location + "\n" + recieverString + "\n" + localValString + "\n";
    	
    	String mlist = "";
    	for(String m:methodList){
    		mlist += m + " ";
    	}
    	s += mlist + "\n";
    	
    	String mNaList = "";
    	for(String m:methodNameList){
    		mNaList += m + " ";
    	}
    	s += mNaList + "\n";
    	
    	String vlist1 = "";
    	for(String m:variableNameList){
    		vlist1 += m + " ";
    	}
    	s += vlist1 + "\n";
    	
    	//16�s��types
        Iterator<String> it_c = getClassesInActiveEditor().iterator();
        while(it_c.hasNext()){
        	String ss = it_c.next();
        	s += ss + ";";
        }
        s += "\n";
        //17�s�ȍ~ functions
        Iterator<String> it_f = getFunctionsInActiveEditor().iterator();
        while(it_f.hasNext()){
        	s += it_f.next() + "\n";
        }
        return s;
    }
    
//    
//    public String toDBString(){
//    	String s =  className + "\n" + offset + "\n" + original_offset + "\n" + line_number_start + "\n" + line_number_end + "\n" +
//    selected_length + "\n" + selected_string + "\n" + keywords + "\n" + desiredReturnType + "\n" + location + "\n";
//    	//11�s��types
//        Iterator<String> it_c = classesInActiveEditor.iterator();
//        while(it_c.hasNext()){
//        	String ss = it_c.next();
//        	s += ss + ";";
//        }
//        s += "\n";
//        //12�s�ȍ~ functions
//        Iterator<String> it_f = functionsInActiveEditor.iterator();
//        while(it_f.hasNext()){
//        	s += it_f.next() + "\n";
//        }
//        return s;
//    }
//    
    
    /*
     * �p�b�P�[�W�����܂ށA�N���X���B
     */
    public String getFullyQualifiedClassName(){
    	return className;
    }
    /*
     * �p�b�P�[�W�����܂ށA�N���X���B
     */
    public void setFullyQualifiedClassName(String n){
    	className = n;
    }
    public String getPackageName(){
    	int dot = className.lastIndexOf('.');
    	if(dot == -1)
    		return className;
    	return className.substring(0, dot);
    }
    public String getClassSingleName(){
    	int dot = className.lastIndexOf('.');
    	return className.substring(dot+1);
    }
	public int getOffset(){
    	return offset;
    }
    public void setOffset(int o){
    	offset = o;
    }
    public int getOriginalOffset(){
    	return original_offset;
    }
    public int getLineNumberStart(){
    	return line_number_start;
    }
    public void setLineNumberStart(int s){
    	line_number_start = s;
    }
    public int getLineNumberEnd(){
    	return line_number_end;
    }
    public void setLineNumberEnd(int e){
    	line_number_end = e;
    }
    public int getSelectedLength(){
    	return selected_length;
    }
    public void setSelectedLength(int l){
    	selected_length = l;
    }
    public String getSelectedString(){
    	return selected_string;
    }
    public void setSelectedString(String s){
    	selected_string = s;
    }
    public String getDesiredReturnType(){
    	return desiredReturnType;
    }
    public void setDesiredReturnType(String d){
    	desiredReturnType = d;
    }
    public String getLocation(){
    	return location;
    }
    public String getKeywords(){
    	return keywords;
    }
    public void setKeywords(String k){
    	keywords = k;
    }
    public int getSelectedStringOrder(){
    	return selected_string_order;
    }
    
    public List<FunctionTree> getOutputFunctionTrees(){
    	return outputfunctionTrees;
    }
    
    public long getSaveTime(){
    	String s = getTxtFile().getName().replace(".txt", "");
    	return Long.parseLong(s);
    }
    
    public void setOutputLogList(List<OutputCandidateLog> outputLogList){
      	this.outputLogList = outputLogList;
    }
	/*
	 * �o�͕��������̓L�[���[�h�ɕϊ�����
	 * 
	 * ����out��
	 *  message.replaceAll(space, comma);
	 *  �Ȃ�΁A
	 *  �Ԃ�l��
	 *   message replace all space comma
	 *  �ƂȂ�B
	 *  
	 *  �s���I�h�A�J���}�A�Z�~�R�����A���ʂ͎��A�󔒕����ɕϊ�����B
	 *  message�@replaceAll�@space�@ comma�@�@
	 *  
	 *  replaceAll�̕����́A�啶���̎�O�ɋ󔒕�����}�����Ă���A
	 *  �啶�����������ɕϊ�����B��AstUtil.splitName���g���B
	 *  
	 */
	public String output2InputKeyword(String out){
		if(out == null)
			return null;
		//�s���I�h�A�J���}�A�Z�~�R�����A���ʂ͎��A�󔒕����ɕϊ�����B
		//�_�u���N�I�[�e�[�V������<>�����B
		String rep1 = out.replaceAll("[\\.\\,\\;\\(\\)\\<\\>\"]", " ");
		//�啶���̎�O�ɋ󔒕�����}�����Ă���A�啶�����������ɕϊ�����B
		String rep2 = ast.AstUtil.splitName(rep1, " ");
		//�����̋󔒕������A�����Ă����ꍇ�P�ɂ���B
		String rep3 = rep2.replaceAll(" +", " ");
		return rep3.trim();
	}
	
	//keyword�̐����J�E���g����B
	private int countKeywords(){
		
		if(keywords == null){
			//�X��keyword�ɕ���
			String []input_keywords = keywords_original.split("[ �@\t]");
			return input_keywords.length;
		}else{
			//�X��keyword�ɕ���
			String []input_keywords = keywords.split("[ �@\t]");
			return input_keywords.length;
		}
	}
	
	public int getNumOfKeywords(){
		return countKeywords();
	}
	
	public List<String> getKeywordList(){
		List list = new ArrayList();
		//�X��keyword�ɕ���
		String [] input_keywords = keywords.split("[ �@\t]");
		for(String s: input_keywords){
			list.add(s);
		}
		return list;
	}
	
	public void setKeywordList(List<String> list){
		String ret = ""; 
				
		for(String s: list){
			ret += s +" ";
		}
		keywords_original = keywords;
		keywords = ret;
	}
	
	public void resetKeyword(){
		keywords = keywords_original;
	}
	
	public int getNumOfLocalFunctions(){
		if(getFunctionsInActiveEditor() == null)
			return 0;
		return getFunctionsInActiveEditor().size();
	}
	
	public int getNumOfLocalTypes(){
		if(getClassesInActiveEditor() == null)
			return 0;
		return getClassesInActiveEditor().size();
	}
	
	public void save(){
		try{
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(getTxtFile())));
			pw.write(toDBString());
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * �L�[���[�h�v���O���~���O�̃A���S���Y�������s����B
	 * �����āA
	 * ���selected_string�ɕҏW�������߂��o�͕�����̏��ʂ��o�͂���B
	 * 
	 */
//	public int runKeywordProgrammingAndGetNearestOutputNumber(int output_size, String state){
//		//�L�[���[�h�v���O���~���O�̎��s
//		FunctionTree[] ft = KeywordProgramming.execute(keywords, desiredReturnType,  new ArrayList<String>(getClassesInActiveEditor()), new ArrayList<String>(getFunctionsInActiveEditor()), state);
//		outputfunctionTrees = (ArrayList<FunctionTree>) Arrays.asList(ft);
//		//���selected_string�ɕҏW�������߂��o�͕�����̏��ʂ𓾂�B
//		return getNearestDistanceOutputNumber(output_size);
//	}

	/*
	 * ���selected_string�ɕҏW�������߂��o�͕�����̏��ʂ𓾂�B
	 */
//	private int getNearestDistanceOutputNumber(int output_size) {
//		
//		int best_tree_order = -1;
//
////		String s = i + ", ";
//		String s = "";
//		for(double d : ExplanationVector.getWeights()){
//			s += d + ", ";
//		}
//		
//		int min_d = 99999;
//
//		if(outputfunctionTrees == null){
//			s += min_d + ", " + ", �񓚂Ȃ� ts == null";
////			System.out.println(i + ", �񓚂Ȃ� ts == null, "+ desiredReturnType);
//		}else{
//
//			FunctionTree best_tree = null;
//
//			//pop-up�ɕ\�����ꂽ���̒��̂����A
//			//output_size �ȓ��̒��ŁA
//			//��ԓ����ɋ߂������̌���T��
//			if(output_size > outputfunctionTrees.size())
//				output_size = outputfunctionTrees.size();//������over���Ă���A�z��T�C�Y�ɂ���B
//			for(int j = 0; j < output_size; j++){
//				FunctionTree t = outputfunctionTrees.get(j);
//				if(t != null){
//					
//					LevenshteinDistance ld = new LevenshteinDistance() ;
//					int d = ld.edit(t.toCompleteMethodString(), selected_string);
//					
//					if(d < min_d){
//						min_d = d;
//						best_tree = t;
//						best_tree_order = j;
//					}
////						System.out.println(j + ", eval=" + t.getEvaluationValue() + ", dist=" + d + ", " + t.toCompleteMethodString());
//				}
//			}
//
//			if(best_tree == null){
//				s += min_d + ", " + best_tree_order + ", " + ", �񓚂Ȃ� best_tree == null";
////				System.out.println(i + ", �񓚂Ȃ� best_tree == null, "+ desiredReturnType);
//			}else{
//				//System.out.println(i + ", " + min_d + ", " + best_tree.toCompleteMethodString());
//				s += min_d + ", " + best_tree_order + ", " + best_tree.toCompleteMethodString() + ", " + best_tree.getEvaluationValue();
////				System.out.println(s);
//				best_tree.setSelectedFlg();	//�I�񂾃t���O�B
//			}
//
//		}
//		System.out.println("=== " + selected_string + " �Ɉ�ԕҏW�������߂������� ===");
//		System.out.println(s);
//		System.out.println("=== " + selected_string + " �Ɉ�ԕҏW�������߂������� ===");
//		
//		return best_tree_order;
//	}


	/*
	 * �L�[���[�h�v���O���~���O�̃A���S���Y�������s����B
	 * �����āA�����̏��ʂ��o�͂���B
	 * �������o�Ȃ���΁A
	 * -1��Ԃ��B
	 */
//	public int runKeywordProgrammingAndOutputNumber(int output_size, String state){
//		FunctionTree[] ft = KeywordProgramming.execute(keywords, desiredReturnType,  new ArrayList<String>(getClassesInActiveEditor()), new ArrayList<String>(getFunctionsInActiveEditor()), state);
//		outputfunctionTrees = (ArrayList<FunctionTree>) Arrays.asList(ft);
//		//�����̏��ʂ��o�͂���B
//		return getAnswerNumber(output_size);
//	}

	//�L�[���[�h�v���O���~���O�̏�����
	public void initKeywordProgramming(){
		outputfunctionTrees = null;
	}
	
	//�L�[���[�h�v���O���~���O�̏I��
	public void closeKeywordProgramming(){
		outputfunctionTrees = null;
	}
	
	/*
	 * �L�[���[�h�v���O���~���O�̃A���S���Y�������s���A
	 * �o�͌��Q��Ԃ�
	 */
	public List<FunctionTree> runKeywordProgramming(String state){
		KeywordProgramming kp = new KeywordProgramming();
		
		//KeywordProgramming.clearStaticFields(false);
		//�܂��N���A����K�v���Ȃ��ȁB
		
		kp.clearStaticFields(false);
//		
//		URL c_fileURL;
//		URL f_fileURL;
//		InputStream c_in = null;
//		InputStream f_in = null;
//		BufferedReader c_r;
//		BufferedReader f_r;
//		
//		try{
//		
//			//�V�B�ǂݍ��ށB
//			if(console_test.ConsoleTest3.IsConsoleTest == false){
//				//classes�t�@�C��
//				Path c_path = new Path(Activator.classesFileName);
//				c_fileURL = FileLocator.find(Activator.bundle, c_path, null);
//				c_in = c_fileURL.openStream();
//				//functions�t�@�C��
//				Path f_path = new Path(Activator.functionsFileName);
//				f_fileURL = FileLocator.find(Activator.bundle, f_path, null);
//				f_in = f_fileURL.openStream();
//				c_r = new BufferedReader(new InputStreamReader(c_in));
//				f_r = new BufferedReader(new InputStreamReader(f_in));		
//			}else{
//				FileReader in = new FileReader(console_test.ConsoleTest3.expFolder + "sub_class.txt");
//		        c_r = new BufferedReader(in);
//		        FileReader in1 = new FileReader(console_test.ConsoleTest3.expFolder + "function.txt");
//				f_r = new BufferedReader(in1);
//			}
//
//			kp.loadFiles(c_r, f_r);
//		
//		//�N���[�Y
//			c_r.close();
//			f_r.close();
//			if(console_test.ConsoleTest3.IsConsoleTest == false){
//				c_in.close();
//				f_in.close();
//			}
//		}catch(IOException e){
//			e.printStackTrace();
//		}
		
		
		//�p�x�ǉ�(�֐��̂ݐ�ɒǉ�)
		//KeywordProgramming.setFreqToFunction(new ArrayList<String>(getFunctionsInActiveEditor()));
		//�v��Ȃ��B���A���̒��ŁA����Ă�B�̂ŁA���ŁAnew ArrayList<String>(getFunctionsInActiveEditor()) ������B
		
		List<Word> word_list = new ArrayList<Word>();
		
		//�L�[���[�h��������ꍇ�B
		String flg_separate = console_test.ConsoleTest3.para.separate_keywords;
		

		
		//�L�[���[�h����
		String flg_short = console_test.ConsoleTest3.para.shortened_input_keywords;
		
		if(console_test.ConsoleTest3.IsConsoleTest == false){
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			boolean flg = store.getBoolean(PreferenceInitializer.SEPARATE_KEYWORDS);
			if(flg)
				flg_separate = "true";
			else
				flg_separate = "false";
			flg_short = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
		}
		
		word_list = CreateKeywords.setSeparateKeywords(keywords, methodNameList, variableNameList);
		
		if(flg_separate.equals("true")){
			Word.flg_separate = "true";
			word_list = CreateKeywords.setSeparateKeywords(keywords, methodNameList, variableNameList);
		}else{
			Word.flg_separate = "false";
			word_list = CreateKeywords.setInputKeywords(keywords);
		}
		
		if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL)){
			word_list = CreateKeywords.setConsonantKeywords(word_list);
		}else if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3)){
			word_list = CreateKeywords.setFirst3Keywords(word_list);
		}else if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE)){
			word_list = CreateKeywords.setReplacedKeywords(word_list);
		}else if(flg_short.equals(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD)){
			word_list = CreateKeywords.setInsertedKeywords(word_list);
		}
		
//		type_of_shortened_keywords = 2;	//con
//		//type_of_shortened_keywords = 1;	//sen3
//		
//		if(flg_short){
//			switch(type_of_shortened_keywords){
//			case 0:
//				keywords = setFirst3ConsonantKeywords();
//			case 1:
//				keywords = setFirst3Keywords();
//			case 2:
//				keywords = setConsonantKeywords();
//			default:
//				keywords = setFirst3ConsonantKeywords();
//			}
//		}
		
		//�L�[���[�h�v���O���~���O�̎��s
		//FunctionTree[] ft =  KeywordProgramming.execute(keywords, desiredReturnType,  new ArrayList<String>(getClassesInActiveEditor()), new ArrayList<String>(), state);
		FunctionTree[] ft = kp.execute(word_list, desiredReturnType, new ArrayList<String>(getClassesInActiveEditor()), new ArrayList<String>(getFunctionsInActiveEditor()), state);
		resetKeyword();
		
		outputfunctionTrees = Arrays.asList(ft);
		return outputfunctionTrees;
	}

	/*
	 * �L�[���[�h���� 
	 */
	public String setSeparateKeywords(){
		
		ArrayList<String> methodKeyword = new ArrayList<String>();
		
		for(String s: methodList){
			
			String ss = output2InputKeyword(s);
			
			for(String sss: separateKeyword(ss))
				methodKeyword.add(sss);
		}
		
		ArrayList<Boolean> flg = new ArrayList<Boolean>();
		for(int i = 0; i < methodKeyword.size(); i++){
			flg.add(false);
		}
		
		String newKeywords = "";
		for(String k:separateKeyword(keywords)){
			for(int i = 0; i < methodKeyword.size(); i++){
				String s = methodKeyword.get(i);
				if(flg.get(i) == false && k.equals(s)){
					//�擪��@��ǉ�����B
					k = "@" + k;
					flg.set(i, true);
					break;
				}
			}
			newKeywords += " " + k;
		}
		
		return newKeywords.trim();
		
	}
	
	/*
	 * �L�[���[�h��S��3�����ȉ��ɂ���B
	 */
	public String setFirst3Keywords(){
		List<String> newKeywords = new ArrayList<String>();
		
		for(String word:separateKeyword(keywords)){
			String new_word = word;
			
			boolean at_flg = false;
			if(word.charAt(0) == '@'){
				new_word = word.substring(1);
				at_flg = true;
			}
			
			//3�������傫����΁A3�����ɂ���B
			if(new_word.length() > 3)
				new_word = new_word.substring(0,3);

			if(at_flg){
				new_word = "@" + new_word;
			}
			newKeywords.add(new_word);
		}
		
		String ret = "";
		
		for(String s: newKeywords){
			ret += s + " ";
		}
		
		return ret.trim();
	}
	
	/*
	 * �q�������c���āA�擪�ȊO�̕ꉹ�폜����B�����������͖����B
	 */
	public String setConsonantKeywords(){
		
		List<String> newKeywords = new ArrayList<String>();
		
		for(String word:separateKeyword(keywords)){
			String new_word = "";
			
			boolean at_flg = false;
			if(word.charAt(0) == '@'){
				word = word.substring(1);
				at_flg = true;
			}
				
			new_word = removeVowels(word);
			
//			if(new_word.length() == 0){//�폜���ĕ�����0�ɂȂ����B
//				new_word = word;	//��ɖ߂�
//			}
			
			if(at_flg){
				new_word = "@" + new_word;
			}
			newKeywords.add(new_word);
		}
		
		String ret = "";
		
		for(String s: newKeywords){
			ret += s + " ";
		}
		
		return ret.trim();
		
	}
	
	/*
	 * ���߂̂R�̎q�������� 
	 * 
	 */
	public String setFirst3ConsonantKeywords(){
		
		List<String> newKeywords = new ArrayList<String>();
		
		for(String word:separateKeyword(keywords)){
			String new_word = "";
			
			boolean at_flg = false;
			if(word.charAt(0) == '@'){
				word = word.substring(1);
				at_flg = true;
			}
				
			new_word = removeVowels(word);
			
			if(new_word.length() == 0){//�폜���ĕ�����0�ɂȂ����B
				new_word = word;	//��ɖ߂�
				
			}else if(new_word.length() > 3){//�폜���Ă��������S�ȏ゠��Ƃ�
				new_word = new_word.substring(0, 3);
			}
			
			if(at_flg){
				new_word = "@" + new_word;
			}
			newKeywords.add(new_word);
		}
		
		String ret = "";
		
		for(String s: newKeywords){
			ret += s + " ";
		}
		
		return ret.trim();
		
	}
	
	/*
	 * �ꉹ���폜����B
	 * (�擪�����͏����B)
	 * 
	 */
	public String removeVowels(String word){
		String c_word = word.substring(1);
		
		c_word = c_word.replace("a", "");
		c_word = c_word.replace("i", "");
		c_word = c_word.replace("u", "");
		c_word = c_word.replace("e", "");
		c_word = c_word.replace("o", "");
		
		return word.substring(0,1) + c_word;
	}
	
	public boolean isConsonant(char s){
		if(s =='a' || s =='i' || s =='u' || s =='e' || s =='o' ){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean isConsonant(String s){
		if(s.equals("a") || s.equals("i") || s.equals("u") || s.equals("e") || s.equals("o")){
			return false;
		}else{
			return true;
		}
	}
	
	/*
	 * �L�[���[�h���󔒂ŕ������A���X�g�ɑ}������
	 */
	public List<String> separateKeyword(String keyword){
		//�������������������
		String s_lowerCase = keyword.toLowerCase();
		//keyword�ɕ���
		List<String> input_keywords = Arrays.asList(s_lowerCase.split("[ �@\t]"));
		return input_keywords;
	}
	
	/*
	 * �]���l�̍Čv�Z
	 */
	public void reCalculateEvaluationValue(){
		if(isLogFile == false){
			for(FunctionTree ft: outputfunctionTrees){
				ft.getEvaluationValue();
			}
		}else{
			for(OutputCandidateLog log: outputLogList){
				log.calculateEvaluationValue();
			}
		}
	}

	/*
	 * �o�͌��̃\�[�g
	 */
	public void sortFunctionTrees(){
		if(isLogFile == false){
			Collections.sort(outputfunctionTrees);
		}else{
			Collections.sort(outputLogList, new OutputCanditateLogComparator());
		}
	}
	
	/*
	 * �����̏��ʂ����߂�B
	 * ���ʂ�0�Ԃ���B
	 * 
	 * �������o�Ȃ���΁A-1��Ԃ��B
	 */
	public int getAnswerNumber(int output_size) {
		
		int best_tree_order = -1;

//		String s = i + ", ";
		String s = "";
		for(double d : ExplanationVector.getWeights()){
			s += d + ", ";
		}
		
		if(isLogFile == false){
			//�o�b�`�����̂Ƃ��B
			if(outputfunctionTrees == null){
				s += "�񓚂Ȃ� ts == null";
			}else{
	
				FunctionTree best_tree = null;
	
				//pop-up�ɕ\�����ꂽ���̒��ɐ��������邩���ׂ�B
				if(output_size > outputfunctionTrees.size())
					output_size = outputfunctionTrees.size();//������over���Ă���A�z��T�C�Y�ɂ���B
				for(int j = 0; j < output_size; j++){
					FunctionTree t = outputfunctionTrees.get(j);
					if(t != null){
						if(t.toCompleteMethodString().equals(selected_string.trim())){
							best_tree = t;
							best_tree_order = j;
							break;
						}
					}
				}
	
				if(best_tree == null){
					s += best_tree_order + ", �񓚂Ȃ� best_tree == null";
				}else{
					s += best_tree_order + ", " + best_tree.toCompleteMethodString() + ", " + best_tree.getEvaluationValue();
					best_tree.setSelectedFlg();	//�I�񂾃t���O�B
				}
	
			}
	//		System.out.println("=== " + selected_string + " ���o���������� ===");
	//		System.out.println(s);
	//		System.out.println("=== " + selected_string + " ���o���������� ===");
		}else{
			//�I�����C�������̂Ƃ��B
			if(outputLogList == null){
				s += "�񓚂Ȃ� ts == null";
			}else{
	
				OutputCandidateLog best_log = null;
	
				//pop-up�ɕ\�����ꂽ���̒��ɐ��������邩���ׂ�B
				if(output_size > outputLogList.size())
					output_size = outputLogList.size();//������over���Ă���A�z��T�C�Y�ɂ���B
				for(int j = 0; j < output_size; j++){
					OutputCandidateLog log = outputLogList.get(j);
					if(log != null){
						if(log.getCompleteMethodString().equals(selected_string.trim())){
							best_log = log;
							best_tree_order = j;
							break;
						}
					}
				}
	
				if(best_log == null){
					s += best_tree_order + ", �񓚂Ȃ� best_tree == null";
				}else{
					s += best_tree_order + ", " + best_log.getCompleteMethodString() + ", " + best_log.getEvaluationValue();
				}
	
			}
		}
		return best_tree_order;
	}

	public boolean isSelectedTask() {
		return isSelectedTask;
	}

	public void setSelectedTask(boolean isSelectedTask) {
		this.isSelectedTask = isSelectedTask;
	}
	
	/*
	 * �e�X�g�T�C�g�̃t�@�C����V�K�쐬
	 */
	public void createNewFile() {
		//�t�H���_���Ȃ���΍쐬����B
		File dir = getTxtFile().getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			getTxtFile().createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(getTxtFile())));
			pw.write(this.toDBString());
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/*
	 * �e�X�g�T�C�g�̃t�@�C����V�K�쐬
	 */
	public void copyAndMoveFileToTestSiteFolder() {
		//�t�@�C�����Ȃ����
		if (!getTxtFile().exists()) {  
		    return;
		}
		String savefilename = TestSiteFolder + className.replaceAll("[<>]", "___") + "/" + getTxtFile().getName();
      	File newFile = new File(savefilename);
      	
		//�t�H���_���Ȃ���΍쐬����B
		File dir = newFile.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		//�t�@�C�����R�s�[����B
		try{
			copyFile(getTxtFile(), newFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/*
	 * �e�X�g�T�C�g���폜����
	 */
	public void deleteFile() {
		//�t�@�C�����Ȃ����
		if (!getTxtFile().exists()) {  
		    return;
		}
		getTxtFile().delete();
	}
	
	/*
	 * �����Ƃ��ăe�X�g�T�C�g�̃t�@�C����V�K�쐬
	 * 
	 * �ۑ�����ꏊ���Ⴄ�����B
	 */
	public void createNewFileAsLog(String selected_string, int selected_length, int selected_string_order, String keywords) {
		this.selected_string = selected_string;
		this.selected_length = selected_length;
		this.selected_string_order = selected_string_order;
		this.keywords = keywords;
		
		//�t�@�C�����́u�p�b�P�[�W���{�N���X��/currentTimeMillis�v
		long millis = System.currentTimeMillis();
      	String savefilename = LogSiteFolder + className + "/" + millis + ".txt";
      	//�o�͕�������ۑ��B
      	String savefilename_o = LogSiteFolder + className + "/" + millis + "out.txt";
      	File  file = new File(savefilename);
      	File  file_o = new File(savefilename_o);
		//�t�H���_���Ȃ���΍쐬����B
		File dir = file.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			file.createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.write(this.toDBString());
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			file_o.createNewFile();
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file_o)));
			pw.write(selected_string_order + "\n");	//�擪�s�͑I���������̏��ʁB
//			for(FunctionTree ft : KeywordProgramming.getOutputFunctionTrees()){
//				pw.write(ft.toLogDBString());
//			} 
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * �t�@�C���ۑ��������擾
	 */
	public String getSaveDate(){
		if(getTxtFile() == null)
			return null;
		else{
			Date date = new Date(getSaveTime());
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			return df.format(date);
		}
	}
	
	//�t�@�C�����R�s�[����B
	 private void copyFile(File in, File out) throws IOException {
	    FileChannel sourceChannel = new FileInputStream(in).getChannel();
	    FileChannel destinationChannel = new FileOutputStream(out).getChannel();
	    sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
	    sourceChannel.close();
	    destinationChannel.close();
	 }
	 
	 /*
	  * ����TestSite��ID��Ԃ��B
	  */
	 public String getId(){
		 return String.valueOf(getSaveTime());
	 }
	 
	 /*
      * ���̓L�[���[�h���i�^�X�N�{DB�j���̊֐��̃��x���ɑS�Ċ܂܂�邩�𒲂ׂ�֐�
      */
     public boolean verifyLabel(){
         List<Function> fList = new ArrayList<Function>(); 
         //DB��f�ǉ�
//         fList.addAll(KeywordProgramming.getFunctions());
        //�^�X�N����f�ǉ�
         Iterator<String> iter = getFunctionsInActiveEditor().iterator();
         while(iter.hasNext()){
             String func = iter.next();
             fList.add(new Function(func));
         }

        //keyword�ɕ���
         List<String> kList = Arrays.asList(keywords.split("[ �@\t]", -1));
         for(String key: kList){
             boolean flg = false;
             for(Function func: fList){
                 for(String label: func.getLabels()){
                     if(key.equals(label)){
                         flg = true;
                     }
                 }
             }
             //����L�[���[�h�ɂ��āA���ꂪ���x���ɏo�����Ȃ������B
             if(flg == false){
                 return false;
             }
         }
         //�S�ẴL�[���[�h�����x���ɏo�������B
         return true;
     }
     
     
	 /*
	  * �I�𕶎���
      * selected_string���֐����ƂɃo���o���ɂ��āA
      * ���ꂼ��̊֐������A�����ƃ^�X�N���̊֐����ɂ��邩���m���߂�֐��B
      * 
      */
     public String verifyLabel2(){
   
         List<Function> fList = new ArrayList<Function>(); 
         //DB��f�ǉ�
//         fList.addAll(KeywordProgramming.getFunctions());
        //�^�X�N����f�ǉ�
         Iterator<String> iter = getFunctionsInActiveEditor().iterator();
         while(iter.hasNext()){
             String func = iter.next();
             if(!func.equals("")){
            	 //System.out.println(getId());
            	 fList.add(new Function(func));
             }
         }
         
         //�N���X��(�p�b�P�[�W�����Ȃ�simple name)
         List<String> tList = new ArrayList<String>(); 

         Iterator<String> iter2 = getClassesInActiveEditor().iterator();
         while(iter2.hasNext()){
        	 String t = iter2.next();
//        	 System.out.println("t: " + t);
        	 String t_arr[] = t.split(",", -1);
//        	 System.out.println("tarr[]: " + t_arr);
        	 for(String tt: t_arr){
        		 if(!tt.equals("")){
		        	 Type type = new Type(tt);
		        	 tList.add(type.getSimpleName());
        		 }
        	 }
//        	 System.out.println(type.getSimpleName());
         }
         
         TreeMap<String, Type> tMap =null; // new TreeMap<String, Type>(KeywordProgramming.getTypes());
         
         Iterator<String> iter3 = tMap.keySet().iterator();
         while(iter3.hasNext()){
        	 String t = iter3.next();
        	 Type type = new Type(t);
        	 tList.add(type.getSimpleName());
        	 //System.out.println(type.getSimpleName());
         }
         
         /*
          * �܂��Aselected_string���֐����ƂɃo���o���ɂ���̂�
          * �ł���̂��H
          * ()��.��,�ŕ�������΂悢���H
          */
       
    	 List<String> kList = Arrays.asList(selected_string.split("[,\\.\\(\\)]", -1));
    	
         for(String key: kList){
        	 if(!key.equals("")){
	             boolean flg = false;
	             String keyName = key;
	             //key���R���X�g���N�^ (new�̎��ɃX�y�[�X)
	             //new ���O���B
            	 if(key.startsWith("new ")){
            		 keyName = key.substring(4); 
            	 }
	             for(Function func: fList){
	            	 String funcName = func.getName();
	            	 
	                 if(keyName.equals(funcName)){
	                     flg = true;
	                 }
	             }
	             //����֐����ɂ��āA���ꂪ�^�X�N���̊֐����ɏo�����Ȃ������B
	             if(flg == false){
	            	 /*
	            	  * ���̊֐������啶������n�܂�Ȃ�A
	            	  * static�Ăяo���̂Ƃ��̃N���X����������Ȃ��B
	            	  */
	            	 if(Character.isUpperCase(key.charAt(0))){
	            		 //System.out.println(key);
	            		 if(tList.contains(key))
	            			 return "static";
	            		 else
	            			 return "static?";
	            	 }
	                 return "false";
	             }
        	 }
         }
         //�S�Ă̊֐������^�X�N���̊֐����ɏo�������B
         return "true";
     }
     
	 /*
	  * �I�𕶎���selected_string
	  * ���\���ł���悤�Ȋ֐����^�X�N���ɑ��݂��邩���m���߂郁�\�b�h�B
      * 
      * ���ł�verify2���I���Ă�����̂Ƃ���B
      * 
      * �܂��Aselected_string�Ɋ܂܂��S�Ă̊֐������擾�B
      * 
      * ���ɁA
      * ���̊֐����ƈ�v����֐��������֐����A
      * �^�X�N������S�Ď擾�B
      * 
      * �Ō�ɁAselected_string�Ɠ����؂̌`�ɂȂ�悤�ɁA
      * �֐���g�ݍ��킹�Ė؂����B
      * 
      * ��v����؂����݂����true
      * �����łȂ����false
      * ��Ԃ��B
      */
     public boolean verifyFunctions(){
   
         List<Function> fList = new ArrayList<Function>(); 
         //DB��f�ǉ�
         //fList.addAll(KeywordProgramming.getFunctions());
        //�^�X�N����f�ǉ�
         Iterator<String> iter = getFunctionsInActiveEditor().iterator();
         while(iter.hasNext()){
             String func = iter.next();
             if(!func.equals(""))
            	 fList.add(new Function(func));
         }
         
         //�N���X��(�p�b�P�[�W�����Ȃ�simple name)
         List<String> tList = new ArrayList<String>(); 

         Iterator<String> iter2 = getClassesInActiveEditor().iterator();
         while(iter2.hasNext()){
        	 String t = iter2.next();
        	 String t_arr[] = t.split(",", -1);
        	 for(String tt: t_arr){
        		 if(!tt.equals("")){
		        	 Type type = new Type(tt);
		        	 tList.add(type.getSimpleName());
        		 }
        	 }
         }
         
         TreeMap<String, Type> tMap = null; //new TreeMap<String, Type>(KeywordProgramming.getTypes());
         
         Iterator<String> iter3 = tMap.keySet().iterator();
         while(iter3.hasNext()){
        	 String t = iter3.next();
        	 Type type = new Type(t);
        	 tList.add(type.getSimpleName());
         }
         
         /*
          * �܂��Aselected_string���֐����ƂɃo���o���ɂ���̂�
          * �ł���̂��H
          * ()��.��,�ŕ�������΂悢���H
          */
       
    	 List<String> kList = Arrays.asList(selected_string.split("[�@,\\.\\(\\)]", -1));
    	 List<String> kList2 = new ArrayList<String>();
    	 for(String k: kList){
    		 kList2.add(k);
    	 }
    	 for(int i = 0; i < kList.size(); i++){
    		 if(!kList.get(i).equals("")){
	             //key���R���X�g���N�^ (new�̎��ɃX�y�[�X)
	             //new ���O���B
            	 if(kList.get(i).startsWith("new ")){
            		 kList2.add(i, kList.get(i).substring(4)); 
            	 }
    		 }
    	 }
    	 
    	 HashMap<Integer, ArrayList<Function>> fName_function_map = new HashMap<Integer, ArrayList<Function>>();
    	 int key_num = 0;
         for(String key: kList2){
        	 if(!key.equals("")){
	             String keyName = key;
	             //key���R���X�g���N�^ (new�̎��ɃX�y�[�X)
	             //new ���O���B
            	 if(key.startsWith("new ")){
            		 keyName = key.substring(4); 
            	 }
            	 
            	 ArrayList<Function> funcList = new ArrayList<Function>();
            	 
	             for(Function func: fList){
	            	 String funcName = func.getName();
	                 if(keyName.equals(funcName)){
	                	 funcList.add(func);
	                 }
	             }
	             //keyName�Ɩ��O����v����֐����Ȃ���΁A�g�ݍ��킹����false�Ɣ�������̂Ŗ��Ȃ��B
	             
	             fName_function_map.put(key_num, funcList);
	             key_num++;
        	 }
         }
         
	         //�֐����P�̂Ƃ�
	         if(key_num == 1){
	        	 try{
		        	 ArrayList<Function> list = fName_function_map.get(0);
		        	 if(list.size() > 0 && list.get(0).getName().equals(kList2.get(0)))
		        		 return true;
	        	 }catch(Exception e){
	            	 e.printStackTrace();
	             }
	        	 return false;
	         }
         
       //�֐��̑g�ݍ��킹�̏����S�ċ��߂�B
 		Combinations pc = new Combinations();
 		ArrayList<ArrayList<Function>> allCombinations = pc.getAllCombinations(fName_function_map);
 		
 		for(int i = 0; i < allCombinations.size(); i++){
			ArrayList<Function> comb = allCombinations.get(i);
			for(int j=0; j< comb.size(); j++){
				Function f1 = comb.get(j);
				if(f1.getParameters() != null)
				for(String p:f1.getParameters()){
					boolean flg = false;
					for(int k=0; k < comb.size(); k++){
						if(j != k){
							Function f2 = comb.get(k);
							if(p.equals(f2.getReturnType())){
								flg = true;
							}
						}
					}
					if(!flg)
						return false;
				}
			}
 		}
         return true;
     }
     
	 
	 /*
      * 
      */
     private List<String> getLocalFunctionNameList(){
         List<Function> fList = new ArrayList<Function>(); 
         List<String> listOfLocalFunctionName = new ArrayList<String>();
         
//         //DB��f�ǉ�
//         fList.addAll(KeywordProgramming.getFunctions());
        //�^�X�N����f�ǉ�
         //���[�J���֐��̖��O���擾�B
         Iterator<String> iter = getFunctionsInActiveEditor().iterator();
         while(iter.hasNext()){
             String func = iter.next();
             Function f = new Function(func);
             fList.add(f);
             if(f.getFunctionType().equals("localvariable")){
        		 listOfLocalFunctionName.add(f.getName());
        	 }
         }

         return listOfLocalFunctionName;
     }
     
     public List<String> getLocalFunctionNameKeywords(){
    	 List<String> list = new ArrayList<String>();
    			 
    	 for(String keyword: this.getKeywordList()){
    		 for(String localFname: this.getLocalFunctionNameList()){
    			 if(keyword.equals(localFname)){
    				 list.add(keyword);
    			 }
    		 }
    		 
    	 }
    	 return list;
     }
     
     /*
      * �^�X�N�̐��𕶎��񒆂́A
      * �ǂ̃L�[���[�h�����V�[�o�A���[�J���ϐ��Ȃ̂����擾����B
      * 
      * 
      */
     
     private ASTNode node;
     private boolean flgGetReciever = false;	//
     private Expression reciever;
     private List<SimpleName> simpleNameList = new ArrayList<SimpleName>();
     
     private List<String> functions = new ArrayList<String>();
     
     /*
      * �����R�[�h�Ђ�\��
      * ASTNode node
      * ����͂��āA
      * recieverString
      * �ƁA
      * localValString
      * �𓾂�B
      */
     public void setASTNode(ASTNode node){
    	 this.node = node;
    	 searchAST(getNodeArguments(node));
    	 
    	 if(reciever != null){
    		 this.recieverString = (reciever.getStartPosition() - this.offset) + "," + reciever.toString();
    		 this.recieverString = this.recieverString.trim();
    	 }
    	 
    	 this.localValString = "";
    	 for(SimpleName sn: simpleNameList){
    		 this.localValString += (sn.getStartPosition() - this.offset) + "," + sn.toString() + ";";
    	 }
    	 this.localValString = this.localValString.trim();
     }
     
     
     /*
      *  ASTNode �̈������擾����B
      *  ���V�[�o������΁A���V�[�o�����B
      *  
      *  node ��MethodInvocation, ClassInstanceCreation
      *  �̗����ɑΉ��B
      */
     private List<Expression> getNodeArguments(ASTNode node) {

 		List<Expression> list = new ArrayList<Expression>();
 		if(node instanceof MethodInvocation){
 			MethodInvocation current = (MethodInvocation)node;
 			
 			methodList.add(current.getName().toString());
 			
 			for(Object e: current.arguments()){
 				if(e instanceof Expression){
 					list.add((Expression)e);
 				}
 			}
 			
 			//System.out.println(current.resolveMethodBinding().getDeclaringClass().getBinaryName());
 			String parentClass = current.resolveMethodBinding().getDeclaringClass().getBinaryName();
 	
 			functions.addAll(AstUtil.iMethodBindingToDbStringStatic1(current.resolveMethodBinding(), parentClass, false, false, false));
 			
 			
 			reciever = current.getExpression();
 			if(reciever != null)
			if(reciever.getNodeType() == ASTNode.FIELD_ACCESS)
				System.out.println("FIELD_ACCESS");

 			//���V�[�o�ƈ����������Ȃ��悤�ɍ폜�B
// 			if(flgGetReciever){
// 				if(reciever instanceof SimpleName
// 					&& Character.isLowerCase(((SimpleName) reciever).getFullyQualifiedName().charAt(0)))	
// 				list.add(current.getExpression());
// 			}
 		}else if(node instanceof ClassInstanceCreation){
 			ClassInstanceCreation current = (ClassInstanceCreation)node;
 			
 			String parentClass = current.resolveConstructorBinding().getDeclaringClass().getBinaryName();
 			methodList.add(AstUtil.getSimpleClassName(parentClass));
 			
 			for(Object e: current.arguments()){
 				if(e instanceof Expression){
 					list.add((Expression)e);
 				}
 			}
 			
 			functions.addAll(AstUtil.iMethodBindingToDbStringStatic1(current.resolveConstructorBinding(), parentClass, false, false, false));
 			
 		}
 		
 		return list;
 	}
     
     /*
      * ���V�[�o�[�����\�b�h�������Ƃ��̏����B
      * 
      * lsScanner.nextLine().trim()
      * ���ꂾ�ƁA���ꂪ���V�[�o�ɂȂ��Ă��܂����A
      * 0,lsScanner.nextLine()
      * ���ꂾ���ɂ������B
      * 0,lsScanner
      * ���\�b�h��
      * nextLine()��ǉ��������B
      * 
      */
     
     private void searchAST(Expression reciever){
    	 
     }
     
     /*
      * ASTNode ���ċA�I�ɒT�����āA
      * �������擾���A���̃��X�g��Ԃ��B
      */
     private void searchAST(List<Expression> list){
 		if(list == null)
 			return;
 		if(list.size() == 0)
 			return;
 		for(Expression e: list){
 			if(e == null)
 				continue;
 			
// 			System.out.println(e.toString());
 			if(e instanceof SimpleName){
 				simpleNameList.add((SimpleName)e);
 			}else{
 				searchAST(getNodeArguments(e));
 			}
 		}
 		return;
 	}
 	
 	public void printSimpleNameList(){
 		for(SimpleName s: simpleNameList){
 			System.out.println(s.toString());
 		}
 		return;
 	}
 	
 	public void printFunctionList(){
 		for(String s: functions){
 			System.out.println(s);
 		}
 		return;
 	}
 	
 	/*
 	 * �I��͈͂Ɋ܂܂��֐����A�w��񐔉�̕p�x�ŏo������֐����ǂ����𒲂ׂ� 
 	 */
 	public boolean isSelectedFunctionWithXtimesFrequency(int x){
// 		for(String s: functions){
// 			if(KeywordProgramming.IsFunctionWithXtimesFrequency(s, x))
// 				return true;
// 		}
 		return false;
 	}
 	
	
 	/*
 	 * ����TestSite�ɂ���֐����A�w��񐔉�̕p�x�ŏo������֐����ǂ����𒲂ׂ� 
 	 */
 	public boolean isFunctionWithXtimesFrequency(int x){
// 		Iterator<String> iter = getFunctionsInActiveEditor().iterator();
//        while(iter.hasNext()){
//            String func = iter.next();
//            if(KeywordProgramming.IsFunctionWithXtimesFrequency(func, x))
// 				return true;
//        }
 		return false;
 	}
 	
 	public void openTextFile(){
 	    try{
 	       FileReader filereader = new FileReader(getTxtFile());

 	       int ch;
 	       while((ch = filereader.read()) != -1){
 	         System.out.print((char)ch);
 	       }

 	       filereader.close();
 	     }catch(FileNotFoundException e){
 	       System.out.println(e);
 	     }catch(IOException e){
 	       System.out.println(e);
 	     }
 	}
}
