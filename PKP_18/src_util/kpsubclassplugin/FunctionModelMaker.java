package kpsubclassplugin;
/**
 * 09/07/12(��)
*���f���ŁA���\�b�h�ƃt�B�[���h�̕����A
���V�[�o�������̂�Y��Ă����̂Œǉ��BModelMaker

*�z��͂��̂܂܂ł����v�Ȃ̂��ȁH�܂��ق��Ƃ����B

*����q�N���X�Ƃ肠���������B

* $1 $2 ���Ĉ�̂Ȃ�Ȃ񂾁H �����B
�N���X����$���t���Ă�����̂͏��O���邱�Ƃɂ���B

* �C���������ʁAio��lang�����ŁA
�SFunction ���F4539
�SClass ���F193
�ƂȂ����B

*Class�I�����[�̕\�����Ȃ�����B
�쐬�B
ModelMaker�����͂�������ł��Ă��邯�ǁA
����Class�ɂ͔��f����Ă��܂���B
���ӁB�Ƃ������g��Ȃ��񂾂���������Ă������񂾂���ǂ��B
�Ԉ��Ȃ��悤�ɁB

*classes.txt functions.txt ��2���쐬�ł���悤�ɂȂ����B

 * 09/07/02�i�؁j
 *
 * ���ӁF
 * public static String splitName(String name,  String with)
 * �ɂ����āA
 * IO, URL, UTF�Ȃǂ����ׂăo���o���ɕ�������Ă��܂��I
 * �܂������₠�ƂŎ������B
 * �ꕶ�����o���o���ɕ�������Ă��܂�Model�����邪�A
 * �啔���̓t�B�[���h�̂悤�ł���B
 * �p�X���Ă��܂����B
 *
 * �Ƃ肠����
 * Model�͂ł����A�Ƃ������ƂŁB
 *
*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;


public class FunctionModelMaker {
	//�t�^���A�w�j�ɂȂ�API�x�̂݁B
	static final String [] packages = {
//		"java.beans",
		"java.lang",
//		"java.lang.ref",
//		"java.lang.reflect",
//		"java.math",
//		"java.net",
//		"javax.net", //jsse.jar�̒�
//		"java.io",
//		"java.nio",
//		"java.text",
//		"java.util",
//		"java.util.jar",
//		"java.util.logging",
//		"java.util.prefs",
//		"java.util.regex",
//		"java.util.zip",
//		"javax.crypto",
//		"java.security",
//		"javax.security",
//		"javax.xml",
//		"org.w3c.dom",
//		"org.xml.sax",
	};

	//��{�f�[�^�^
	static final String[] basic_type = {
		"void",
		"boolean",
		"char",
		"byte",
		"short",
		"int",
		"long",
		"float",
		"double",
	};
	
	static final String[][] basic_type_simbols = {
		{"boolean$", "java.lang.Boolean"},
		{"byte$", "java.lang.Byte"      },
		{"char$", "java.lang.Character" },
		{"double$", "java.lang.Double"  },
		{"float$", "java.lang.Float"    },
		{"int$", "java.lang.Integer"    },
		{"long$", "java.lang.Long"      },
		{"short$", "java.lang.Short"    },
		{"void$", "java.lang.Void"      },
		{"Z$", "java.lang.Boolean"      },
		{"B$", "java.lang.Byte"         },
		{"C$", "java.lang.Character"    },
		{"D$", "java.lang.Double"       },
		{"F$", "java.lang.Float"        },
		{"I$", "java.lang.Integer"      },
		{"J$", "java.lang.Long"         },
		{"S$", "java.lang.Short"        },
		{"V$", "java.lang.Void"         },
		{"^\\[L", "["                   }
	};
	
	//�C���q��\�����镶����
	public final String MOD_STRING_NONSTATIC_FIELD = "nonstatic_field";
	public final String MOD_STRING_STATIC_FIELD = "static_field";
	public final String MOD_STRING_NONSTATIC_METHOD = "nonstatic_method";
	public final String MOD_STRING_STATIC_METHOD = "static_method";
	public final String MOD_STRING_CONSTRUCTOR = "constructor";

	/*
	 * static �R���X�g���N�^��C#�ɂ͂��邪�Ajava �ɂ͖����B
	 * */


	//enum�ŕ\��
	enum ModString {
		NONSTATIC_FIELD("nonstatic_field"),
		STATIC_FIELD("static_field"),
		NONSTATIC_METHOD("nonstatic_method"),
		STATIC_METHOD("static_method"),
		CONSTRUCTOR("constructor");
	    private final String mode;
	    private ModString(String m) {
	        this.mode = m;
	    }
	    String modName(){return mode;}
	}


	//�o�̓t�@�C��
	static PrintWriter printWriter_Func;	//Function �t�@�C���p
	static PrintWriter printWriter_Clas;	//Class �t�@�C���p

	static List<String> AllClasses = new ArrayList<String>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		//�o�̓t�@�C��
		//Class �t�@�C���p
		File file_clas = new File("D:\\kypg_mdl_clas.txt");
		FileWriter filewriter_clas = new FileWriter(file_clas);
		BufferedWriter bw_clas = new BufferedWriter(filewriter_clas);
		printWriter_Clas = new PrintWriter(bw_clas);

		for(String s: basic_type){
			printWriter_Clas.println(s);
		}

		//Function �t�@�C���p
		File file_func = new File("D:\\kypg_mdl_func.txt");
		FileWriter filewriter_func = new FileWriter(file_func);
		BufferedWriter bw_func = new BufferedWriter(filewriter_func);
		printWriter_Func = new PrintWriter(bw_func);


		// TODO �����������ꂽ���\�b�h�E�X�^�u
		//File input_file = new File("C:\\Program Files\\Java\\jre1.6.0_05\\lib\\rt.jar");//��
		File input_file = new File("C:\\Program Files\\Java\\jre6\\lib\\rt.jar");//�w�Z
		JarFile jf = new JarFile(input_file);

		int numOfAllFunctions = 0;
		int numOfAllClasses = 0;
		for(String packageName : packages){
			String packageLocation = packageName.replace(".", "/");
			//�p�^�[���}�b�`�F�N���X�̐擪�������啶���Ŏn�܂�".class"�ŏI���̂��`�F�b�N
			Pattern pattern1 = Pattern.compile("^"+ packageLocation + "/[A-Z].*\\.class");

			//�p�^�[���}�b�`�F�N���X���ɐ������܂܂�Ă���Ώ��O
			Pattern pattern2 = Pattern.compile("[^0-9]+");

			//�p�^�[���}�b�`�F�N���X����Impl�ŏI���
			Pattern pattern3 = Pattern.compile(".*Impl\\.class$");

			for (Enumeration e = jf.entries(); e.hasMoreElements();) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				String zipEntryLocation = ze.getName();
				//�p�^�[���}�b�`�F�N���X����'$'���܂܂�Ă���Ώ��O
				if(zipEntryLocation.indexOf('$') > -1){
					continue;
				}
				//java.lang.Object�͏��O
//				if(zipEntryLocation.equals("java/lang/Object.class")){
//					continue;
//				}
				//java.lang.ApplicationShutdownHooks�͏��O
				if(zipEntryLocation.equals("java/lang/ApplicationShutdownHooks.class")){
					continue;
				}
				//�p�^�[���}�b�`�F�ΏہFjar�t�@�C��
				Matcher matcher1 = pattern1.matcher(zipEntryLocation);
				Matcher matcher2 = pattern2.matcher(zipEntryLocation);
				Matcher matcher3 = pattern3.matcher(zipEntryLocation);
				if(matcher1.matches() && matcher2.matches() && ! matcher3.matches()){
					//className�쐬
					/*
					 * zipEntryLocation ���疖����".class"����������������쐬����@
					 * 6��".class"�̕������ł���B
					 */
					String className = zipEntryLocation.substring(0, zipEntryLocation.length() - 6);
					className = className.replace("/", ".");

					//�e�L�X�g��Model���o�͂���
					numOfAllFunctions += printModel(className);
					numOfAllClasses++;
				}
			}
		}
		printWriter_Func.close();
		printWriter_Clas.close();
		System.out.println("�SFunction ���F" + numOfAllFunctions);
		System.out.println("�SClass ���F" + numOfAllClasses);
	}

	public static int printModel(String className) {
		//return printModelStatic2(className);
		//static �̎�1�����o���Ȃ��B
		return printModelStatic1(className);
	}
	//�e�L�X�g��Model���o�͂���
	public static int printModelStatic2(String className) {
		int numOfFunctions = -1;
	    try {
	      int i,j;

	      //Classes�t�@�C���ւ̏����o��
	      printWriter_Clas.println(className);

	      //�ȉ��AFunctions�t�@�C���ւ̏����o��

	      // �N���X�I�u�W�F�N�g�̎擾
	      Class cls = Class.forName(className);
	      className = formatTypeName(className);
	      
	      // �t�B�[���h�̕���
	      Field[] fieldList = cls.getFields();
	      for (i=0;i<fieldList.length;i++) {
	        Field fld = fieldList[i];
	        int mod = fld.getModifiers();

	        //public�̂�
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//static�̎��̂�2��

	        	for(int c = 0; c < count; c++){
		        	//�N���X���̏����o��
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//�C���q�̏����o��
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",field,");
		  	      	// �Ԃ�l�^��\��
			        printWriter_Func.print(formatTypeName(fld.getType().getName())+",");
		  	      	//Function���̏����o��
		  	      	printWriter_Func.print(fld.getName()+",");
			        //���x��(�t�B�[���h��)��\��
		  	      	if(c==0)
		  	      		printWriter_Func.print(splitName(fld.getName(), ";"));
		  	      	else	// ���x��(�N���X��;�t�B�[���h��)��\��(static�̂�)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(fld.getName(), ";"));
		  	      	//�t�B�[���h�Ƀp�����[�^�͖�������v��Ȃ��B
//		  	      	//�p�����[�^�[
//	  	      			//���V�[�o���邢��static�̏����N���X
//	  	      			if(c == 0)
//	  	      				printWriter_Func.print(","+className);
			        printWriter_Func.println();
	        	}

	        }

	      }

	      // �R���X�g���N�^�̕���
	      Constructor[] ctorList = cls.getConstructors();
	      for (i=0;i<ctorList.length;i++) {
	        Constructor ct = ctorList[i];
	        int mod = ct.getModifiers();
	        //public�̂�
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	//�����N���X���̏����o��
	  	      	printWriter_Func.print(className+",");
	  	      	//inthisclass = false
	  	      	printWriter_Func.print("false,");
	  	      	//�C���q�̏����o��
	  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",constructor,");
		        //�Ԃ�l�^�i�N���X���j��\��
		        printWriter_Func.print( ct.getDeclaringClass().getName()+",");
		        //Function���̏����o��
	  	      	printWriter_Func.print(ct.getDeclaringClass().getSimpleName()+",");
		        // ���x��(new;�R���X�g���N�^��)��\��
		        printWriter_Func.print( "new;" + splitName(ct.getDeclaringClass().getSimpleName(), ";"));

		        // �����̌^��\��
		        Class[] cparamList = ct.getParameterTypes();
		        for (j=0;j<cparamList.length;j++) {
		          printWriter_Func.print( "," + formatTypeName(cparamList[j].getName()));
		        }
		        printWriter_Func.println("");
	        }
	      }

	      // ���\�b�h�̕���
	      Method[] methList = cls.getMethods();
	      for (i=0;i<methList.length;i++) {
	        Method m = methList[i];
	        int mod = m.getModifiers();

	        //public�̂�
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//static�̎��̂�2��
	        	for(int c = 0; c < count; c++){
		        	//�����N���X���̏����o��
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//�C���q�̏����o��
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",method,");
		  	      	// �Ԃ�l�̌^��\��
			        printWriter_Func.print(formatTypeName(m.getReturnType().getName())+",");
		  	      	//Function���̏����o��
		  	      	printWriter_Func.print(m.getName()+",");
			        // ���x��(���\�b�h��)��\��
		  	      	if(c == 0)
		  	      		printWriter_Func.print(splitName(m.getName(), ";"));
		  	      	else// ���x��(�N���X��;�t�B�[���h��)��\��(static�̂�)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(m.getName(), ";"));
		  	      	//�p�����[�^�[
		  	      		//���V�[�o���邢��static�̏����N���X
		  	      		if(c == 0)
		  	      			printWriter_Func.print(","+className);
				        // �����̌^��\��
				        Class[] mparamList = m.getParameterTypes();
				        for (j=0;j<mparamList.length;j++) {
				          printWriter_Func.print( "," + formatTypeName(mparamList[j].getName()));
				        }
			        printWriter_Func.println("");
	        	}

	        }
	      }
	      // function ��
	      numOfFunctions = fieldList.length + ctorList.length + methList.length;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return numOfFunctions;
	  }
	
	public static int printModelStatic1(String className) {
		int numOfFunctions = -1;
	    try {
	      int i,j;

	      //Classes�t�@�C���ւ̏����o��
	      printWriter_Clas.println(className);

	      //�ȉ��AFunctions�t�@�C���ւ̏����o��

	      // �N���X�I�u�W�F�N�g�̎擾
	      Class cls = Class.forName(className);
	      className = formatTypeName(className);
	      
	      // �t�B�[���h�̕���
	      Field[] fieldList = cls.getFields();
	      for (i=0;i<fieldList.length;i++) {
	        Field fld = fieldList[i];
	        int mod = fld.getModifiers();

	        //public�̂�
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//static�̎��̂�2��

	        	for(int c = 0; c < 1; c++){
		        	//�N���X���̏����o��
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//�C���q�̏����o��
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",field,");
		  	      	// �Ԃ�l�^��\��
			        printWriter_Func.print(formatTypeName(fld.getType().getName())+",");
		  	      	//Function���̏����o��
		  	      	printWriter_Func.print(fld.getName()+",");
			        //���x��(�t�B�[���h��)��\��
		  	      	if(count == 1)
		  	      		printWriter_Func.print(splitName(fld.getName(), ";"));
		  	      	else	// ���x��(�N���X��;�t�B�[���h��)��\��(static�̂�)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(fld.getName(), ";"));
		  	      	//�t�B�[���h�Ƀp�����[�^�͖�������v��Ȃ��B
//		  	      	//�p�����[�^�[
//	  	      			//���V�[�o���邢��static�̏����N���X
//	  	      			if(c == 0)
//	  	      				printWriter_Func.print(","+className);
			        printWriter_Func.println();
	        	}

	        }

	      }

	      // �R���X�g���N�^�̕���
	      Constructor[] ctorList = cls.getConstructors();
	      for (i=0;i<ctorList.length;i++) {
	        Constructor ct = ctorList[i];
	        int mod = ct.getModifiers();
	        //public�̂�
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	//�����N���X���̏����o��
	  	      	printWriter_Func.print(className+",");
	  	      	//inthisclass = false
	  	      	printWriter_Func.print("false,");
	  	      	//�C���q�̏����o��
	  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",constructor,");
		        //�Ԃ�l�^�i�N���X���j��\��
		        printWriter_Func.print( ct.getDeclaringClass().getName()+",");
		        //Function���̏����o��
	  	      	printWriter_Func.print(ct.getDeclaringClass().getSimpleName()+",");
		        // ���x��(new;�R���X�g���N�^��)��\��
		        printWriter_Func.print( "new;" + splitName(ct.getDeclaringClass().getSimpleName(), ";"));

		        // �����̌^��\��
		        Class[] cparamList = ct.getParameterTypes();
		        for (j=0;j<cparamList.length;j++) {
		          printWriter_Func.print( "," + formatTypeName(cparamList[j].getName()));
		        }
		        printWriter_Func.println("");
	        }
	      }

	      // ���\�b�h�̕���
	      Method[] methList = cls.getMethods();
	      for (i=0;i<methList.length;i++) {
	        Method m = methList[i];
	        int mod = m.getModifiers();

	        //public�̂�
	        if(Modifier.isPublic(mod)){
	        	boolean isStatic = Modifier.isStatic(mod);
	        	boolean isFinal = Modifier.isFinal(mod);

	        	int count = 1;
	        	if(isStatic == true)
	        		count = 2;//static�̎��̂�2��
	        	for(int c = 0; c < 1; c++){
		        	//�����N���X���̏����o��
		  	      	printWriter_Func.print(className+",");
		  	      	//inthisclass = false
		  	      	printWriter_Func.print("false,");
		  	      	//�C���q�̏����o��
		  	      	printWriter_Func.print(isStaticString(isStatic)+","+isFinalString(isFinal)+",method,");
		  	      	// �Ԃ�l�̌^��\��
			        printWriter_Func.print(formatTypeName(m.getReturnType().getName())+",");
		  	      	//Function���̏����o��
		  	      	printWriter_Func.print(m.getName()+",");
			        // ���x��(���\�b�h��)��\��
		  	      	if(count == 1)
		  	      		printWriter_Func.print(splitName(m.getName(), ";"));
		  	      	else// ���x��(�N���X��;�t�B�[���h��)��\��(static�̂�)
		  	      		printWriter_Func.print(splitName(cls.getSimpleName(), ";") + ";" + splitName(m.getName(), ";"));
		  	      	//�p�����[�^�[
		  	      		//���V�[�o���邢��static�̏����N���X
		  	      		if(count == 1)
		  	      			printWriter_Func.print(","+className);
				        // �����̌^��\��
				        Class[] mparamList = m.getParameterTypes();
				        for (j=0;j<mparamList.length;j++) {
				          printWriter_Func.print( "," + formatTypeName(mparamList[j].getName()));
				        }
			        printWriter_Func.println("");
	        	}

	        }
	      }
	      // function ��
	      numOfFunctions = fieldList.length + ctorList.length + methList.length;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return numOfFunctions;
	  }
	
	//���O�̑啶���̕����̑O�ɕ�����with��}�����A�啶�����������ɕϊ�����
	//�Ō��1�������啶���̏ꍇ�͐؂�Ȃ��B
	//�啶�����A�����Ă���ꍇ�́A�؂�Ȃ��B
	// "_" �� �؂�B
	public static String splitName(String name,  String with){
		String ret = name;
		// "_" �ŋ�؂�B
		if(name.contains("_")){
			ret = splitName_splitByUnderBar(name, with);
		}else if(isAllUpperCase(name)){
			//Void.TYPE �̂悤�ȑS�đ啶���̎�
			ret = name.toLowerCase().trim();
		}else{
			// �啶���ŋ�؂�B
			ret = splitName_splitByUpperCase(name, with);
		}
		return ret;
	}

	public static String splitName_splitByUnderBar(String name,  String with){
		name = name.replaceAll("_", with);
		return name.toLowerCase().trim();
	}
	
	//�����񂪑S�đ啶�������m���߂�
	public static boolean isAllUpperCase(String name){
		/*
		 * �ꕶ���ł�������������΁Afalse
		 */
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length(); i++){
			//�啶���i�ʒui�j��������
			if(Character.isLowerCase(sb.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	public static String splitName_splitByUpperCase(String name,  String with){
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length() -1; i++){
			//�啶���i�ʒui�j��������
			if(Character.isUpperCase(sb.charAt(i))){
				
				//�����A�����đ啶���Ȃ�A������B
				if(Character.isUpperCase(sb.charAt(i+1))){
					continue;
				}
				//1�O�̕������啶���Ȃ�A������B
				if(i > 0 && Character.isUpperCase(sb.charAt(i-1))){
					continue;
				}
				//���̑啶���̑O�ɕ�����with��}��
				if(i >= 1){
					sb.insert(i, with);
					//������with�̒���������i��i�߂�
					i += with.length();
				}
			}
		}
		return sb.toString().toLowerCase().trim();
	}
	
	  // �ȗ�������߂��B
	  // ��{�f�[�^�^�͑S�ăI�u�W�F�N�g�^�ɕϊ�����B
	  //�i�z��L��"["�͂��̂܂܎c���B�j
//	  public static String formatTypeName(String name){
//		  boolean flg_changed = false;
//		  
//		  for(int i = 0; i < basic_type_simbols.length; i++){
//			  String old = new String(name);
//			  name = replaceName(name, basic_type_simbols[i][0], basic_type_simbols[i][1]);
//			  if(!old.equals(name))
//				  flg_changed = true;
//		  }
//		
//		  if(flg_changed){//�������Ⴆ�΁A�ύX����B
//			  name = "1\"" + name;	//��{�f�[�^�^�̃t���O�𗧂Ă�B 
//		  }else{
//			  name = "0\"" + name; 	//���ĂȂ��B
//		  }
//			  return name;
//	  }
//	  

	  // �ȗ�����������߂��B
	  // ��{�f�[�^�^�̓I�u�W�F�N�g�^�ɕϊ����Ȃ��B
	  //�i�z��L��"["�͂��̂܂܎c���B�j
	  public static String formatTypeName(String name){
		  boolean flg_changed = false;
		  
		  for(int i = 9; i < basic_type_simbols.length; i++){
			  String old = new String(name);
			  name = replaceName(name, basic_type_simbols[i][0], basic_type_simbols[i][1]);
			  if(!old.equals(name))
				  flg_changed = true;
		  }
		  return name;
	  }
	  
	  public static String replaceName(String name, String from, String to){
		  return name.replaceFirst(from, to);
	  }
	  
//	  
//	  public static String formatTypeName(String name){
//		  int len_start = name.length();
//		  
//		// ��{�f�[�^�^�͑S�ăI�u�W�F�N�g�^�ɕϊ�����B
//			//		  B            byte
//		  name = name.replaceFirst("byte$", "java.lang.Byte");
//			//		  C            char
//		  name = name.replaceFirst("char$", "java.lang.Character");
//			//		  D            double
//		  name = name.replaceFirst("double$", "java.lang.Double");
//			//		  F            float
//		  name = name.replaceFirst("float$", "java.lang.Float");
//			//		  I            int
//		  name = name.replaceFirst("int$", "java.lang.Integer");
//			//		  J            long
//		  name = name.replaceFirst("long$", "java.lang.Long");
//			//		  S            short
//		  name = name.replaceFirst("short$", "java.lang.Short");
//			//		  Z            boolean
//		  name = name.replaceFirst("boolean$", "java.lang.Boolean");
//			//		  V            void
//		  name = name.replaceFirst("void$", "java.lang.Void");
//
//		// �ȗ�������߂��B
//			//		  B            byte
//		  name = name.replaceFirst("B$", "java.lang.Byte");
//			//		  C            char
//		  name = name.replaceFirst("C$", "java.lang.Character");
//			//		  D            double
//		  name = name.replaceFirst("D$", "java.lang.Double");
//			//		  F            float
//		  name = name.replaceFirst("F$", "java.lang.Float");
//			//		  I            int
//		  name = name.replaceFirst("I$", "java.lang.Integer");
//			//		  J            long
//		  name = name.replaceFirst("J$", "java.lang.Long");
//		  //		  L�N���X��;   �N���X�܂��̓C���^�t�F�[�X
//		  //�i�Ƃ肠�����P�����z��̂ݍl���j
//		  name = name.replaceFirst("^\\[L", "[");
//			//		  S            short
//		  name = name.replaceFirst("S$", "java.lang.Short");
//			//		  Z            boolean
//		  name = name.replaceFirst("Z$", "java.lang.Boolean");
//			//		  V            void
//		  name = name.replaceFirst("V$", "java.lang.Void");
//		  
//		  if(len_start != name.length()){//�������Ⴆ�΁A�ύX����B
//			  name = "1\"" + name;	//��{�f�[�^�^�̃t���O�𗧂Ă�B 
//		  }else{
//			  name = "0\"" + name; 	//���ĂȂ��B
//		  }
//			  return name;
//	  }
//	  
//	  //�ȗ�������߂��B�i�z��L��"["�͂��̂܂܎c���B�j
//	  public static String formatTypeName(String name){
//			//		  B            byte
//		  name = name.replaceFirst("B$", "byte");
//			//		  C            char
//		  name = name.replaceFirst("C$", "char");
//			//		  D            double
//		  name = name.replaceFirst("D$", "double");
//			//		  F            float
//		  name = name.replaceFirst("F$", "float");
//			//		  I            int
//		  name = name.replaceFirst("I$", "int");
//			//		  J            long
//		  name = name.replaceFirst("J$", "long");
//		  //		  L�N���X��;   �N���X�܂��̓C���^�t�F�[�X
//		  //�i�Ƃ肠�����P�����z��̂ݍl���j
//		  name = name.replaceFirst("^\\[L", "[");
//			//		  S            short
//		  name = name.replaceFirst("S$", "short");
//			//		  Z            boolean
//		  name = name.replaceFirst("Z$", "boolean");
//			//		  V            void
//		  name = name.replaceFirst("V$", "void");
//			  return name;
//	  }

	  public static String isStaticString(boolean isStatic){
		if(isStatic)
      		return "static";
      	else
      		return "nonstatic";
	  }

	  public static String isFinalString(boolean isFinal){
			if(isFinal)
	      		return "final";
	      	else
	      		return "nonfinal";
	  }
	  
	  public static String isPrimitiveString(boolean isPrim){
			if(isPrim)
	      		return "primitive";
	      	else
	      		return "nonprimitive";
	  }
}
