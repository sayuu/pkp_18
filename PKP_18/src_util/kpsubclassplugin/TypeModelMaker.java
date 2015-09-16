package kpsubclassplugin;
/**
 * 09/08/28(金)
 *
 SampleAction 内で、
 mainPrintSubClass()を呼び出して使う。
ワークスペースに
"hello"プロジェクトを用意しておく必要がある。
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
	//付録より、『核になるAPI』のみ。
	static final String [] packages = {
//		"java.beans",
		"java.lang",
//		"java.lang.ref",
//		"java.lang.reflect",
//		"java.math",
//		"java.net",
//		"javax.net", //jsse.jarの中
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

	//基本データ型
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

	//引数で与えられたクラスのサブクラスを全てテキストに出力する関数
	//（先頭に自分自身の名前、以下サブクラス名……）という形式
	//"hello"プロジェクトを用意しておく必要がある。
	  public static void printSubClass(String className) {
		  IWorkspace ws = ResourcesPlugin.getWorkspace();
		  IWorkspaceRoot wsroot = ws.getRoot();
		  IProject project = (IProject) wsroot.findMember("KpSubclassPluginTarget");
		  IJavaProject javaProject = JavaCore.create(project);
//System.out.println("4");
		try {
			IType baseType = javaProject.findType(className);
			ITypeHierarchy hierarchy = baseType.newTypeHierarchy(javaProject, null);
			// プロジェクト内を対象にしたサブタイプ群の取得(クラスもインタフェースも)
			IType[] subclasses = hierarchy.getSubtypes(baseType);
			//consoleStream.print(className);//親クラス名
			System.out.print(className);
			printWriter_Sub.print(className);
			for(IType t: subclasses){
				String subclassName = t.getFullyQualifiedName();
				//パターンマッチ：指定パッケージの中にあるクラスのみを取得する。
				for(String p: packages){
					//パターンマッチ：クラス名に'$'が含まれていれば除外
					if(subclassName.indexOf('$') > -1){
						continue;
					}
					//先頭文字が指定パッケージ名で始まり、パッケージ名の次が大文字であるかを見る
					Pattern pattern1 = Pattern.compile("^"+ p + "\\.[A-Z].*");
					Matcher matcher1 = pattern1.matcher(subclassName);
					//パターンマッチ：クラス名に数字が含まれていれば除外
					Pattern pattern2 = Pattern.compile("[^0-9]+");
					Matcher matcher2 = pattern2.matcher(subclassName);
					//パターンマッチ：クラス名がImplで終わる
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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	  }

	  //出力ファイル
	  static PrintWriter printWriter_Sub;	//subclass ファイル用

	  public static void mainPrintSubClass() throws IOException {

		  	//出力ファイル
		  	File file_sub = new File("D:\\kypg_sub_class.txt");
			FileWriter filewriter_sub = new FileWriter(file_sub);
			BufferedWriter bw_sub = new BufferedWriter(filewriter_sub);
			printWriter_Sub = new PrintWriter(bw_sub);

			//基本データ型の出力
			for(String s: basic_type){
				printWriter_Sub.println(s);
			}
//System.out.println("1");
			// TODO 自動生成されたメソッド・スタブ
			//File input_file = new File("C:\\Program Files\\Java\\jre1.6.0_05\\lib\\rt.jar");//家？
			File input_file = new File("C:\\Program Files\\Java\\jre6\\lib\\rt.jar");//家
			JarFile jf = new JarFile(input_file);
//System.out.println("2");

			for(String packageName : packages){
				String packageLocation = packageName.replace(".", "/");
				//パターンマッチ：クラスの先頭文字が大文字で始まり".class"で終わるのをチェック
				Pattern pattern1 = Pattern.compile("^"+ packageLocation + "/[A-Z].*\\.class");
				//パターンマッチ：クラス名に数字が含まれていれば除外
				Pattern pattern2 = Pattern.compile("[^0-9]+");

				//パターンマッチ：クラス名がImplで終わる
				Pattern pattern3 = Pattern.compile(".*Impl\\.class$");

				for (Enumeration e = jf.entries(); e.hasMoreElements();) {
					ZipEntry ze = (ZipEntry) e.nextElement();
					String zipEntryLocation = ze.getName();
					//パターンマッチ：クラス名に'$'が含まれていれば除外
					if(zipEntryLocation.indexOf('$') > -1){
						continue;
					}
					//java.lang.Objectは除外
					if(zipEntryLocation.equals("java/lang/Object.class")){
						continue;
					}
					//java.lang.ApplicationShutdownHooksは除外
					if(zipEntryLocation.equals("java/lang/ApplicationShutdownHooks.class")){
						continue;
					}
					//パターンマッチ：対象：jarファイル
					Matcher matcher1 = pattern1.matcher(zipEntryLocation);
					Matcher matcher2 = pattern2.matcher(zipEntryLocation);
					Matcher matcher3 = pattern3.matcher(zipEntryLocation);
					if(matcher1.matches() && matcher2.matches() && ! matcher3.matches()){
						//className作成
						/*
						 * zipEntryLocation から末尾の".class"を除いた文字列を作成する　
						 * 6は".class"の文字長である。
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
