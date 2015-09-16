package kpsubclassplugin;
/**
 * 09/08/28(��)
 *
 SampleAction ���ŁA
 mainPrintSubClass()���Ăяo���Ďg���B
���[�N�X�y�[�X��
"hello"�v���W�F�N�g��p�ӂ��Ă����K�v������B
*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class TypeModelMaker {
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

	//�����ŗ^����ꂽ�N���X�̃T�u�N���X��S�ăe�L�X�g�ɏo�͂���֐�
	//�i�擪�Ɏ������g�̖��O�A�ȉ��T�u�N���X���c�c�j�Ƃ����`��
	//"hello"�v���W�F�N�g��p�ӂ��Ă����K�v������B
	  public static void printSubClass(String className) {
		  IWorkspace ws = ResourcesPlugin.getWorkspace();
		  IWorkspaceRoot wsroot = ws.getRoot();
		  IProject project = (IProject) wsroot.findMember("KpSubclassPluginTarget");
		  IJavaProject javaProject = JavaCore.create(project);
//System.out.println("4");
		try {
			IType baseType = javaProject.findType(className);
			ITypeHierarchy hierarchy = baseType.newTypeHierarchy(javaProject, null);
			// �v���W�F�N�g����Ώۂɂ����T�u�^�C�v�Q�̎擾(�N���X���C���^�t�F�[�X��)
			IType[] subclasses = hierarchy.getSubtypes(baseType);
			//consoleStream.print(className);//�e�N���X��
			System.out.print(className);
			printWriter_Sub.print(className);
			for(IType t: subclasses){
				String subclassName = t.getFullyQualifiedName();
				//�p�^�[���}�b�`�F�w��p�b�P�[�W�̒��ɂ���N���X�݂̂��擾����B
				for(String p: packages){
					//�p�^�[���}�b�`�F�N���X����'$'���܂܂�Ă���Ώ��O
					if(subclassName.indexOf('$') > -1){
						continue;
					}
					//�擪�������w��p�b�P�[�W���Ŏn�܂�A�p�b�P�[�W���̎����啶���ł��邩������
					Pattern pattern1 = Pattern.compile("^"+ p + "\\.[A-Z].*");
					Matcher matcher1 = pattern1.matcher(subclassName);
					//�p�^�[���}�b�`�F�N���X���ɐ������܂܂�Ă���Ώ��O
					Pattern pattern2 = Pattern.compile("[^0-9]+");
					Matcher matcher2 = pattern2.matcher(subclassName);
					//�p�^�[���}�b�`�F�N���X����Impl�ŏI���
					Pattern pattern3 = Pattern.compile(".*Impl$");
					Matcher matcher3 = pattern3.matcher(subclassName);

					if(matcher1.matches() && matcher2.matches() &&  ! matcher3.matches()){
						//consoleStream.print("," + subclassName);
						System.out.print("," + subclassName);
						printWriter_Sub.print("," + subclassName);
					}
				}
			}
			//consoleStream.println();
			System.out.println();
			printWriter_Sub.println();

		} catch (JavaModelException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	  }

	  //�o�̓t�@�C��
	  static PrintWriter printWriter_Sub;	//subclass �t�@�C���p

	  public static void mainPrintSubClass() throws IOException {

		  	//�o�̓t�@�C��
		  	File file_sub = new File("D:\\kypg_sub_class.txt");
			FileWriter filewriter_sub = new FileWriter(file_sub);
			BufferedWriter bw_sub = new BufferedWriter(filewriter_sub);
			printWriter_Sub = new PrintWriter(bw_sub);

			//��{�f�[�^�^�̏o��
			for(String s: basic_type){
				printWriter_Sub.println(s);
			}
//System.out.println("1");
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			//File input_file = new File("C:\\Program Files\\Java\\jre1.6.0_05\\lib\\rt.jar");//�ƁH
			File input_file = new File("C:\\Program Files\\Java\\jre6\\lib\\rt.jar");//��
			JarFile jf = new JarFile(input_file);
//System.out.println("2");

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
					if(zipEntryLocation.equals("java/lang/Object.class")){
						continue;
					}
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
//System.out.println(className);
						printSubClass(className);
					}
				}
			}
			printWriter_Sub.close();
		}
}
