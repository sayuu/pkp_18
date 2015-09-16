package methodFreqCount;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;

/**
 * Apache Jakarta BCELを用いたクラスファイル(バイトコード)解析サンプル
 * 
 * http://homepage2.nifty.com/igat/igapyon/diary/2006/ig060106.html
 * @author IGA Tosiki
 */
public class BcelReadClassSample {
    /**
     * 解析を行いたいクラスファイル名を指定します。
     */
	
	private static final String CLASS_MODULE = "./bin/BcelReadClassSample.class";

	private static HashMap<String,Integer> map = new HashMap<String,Integer>();
	
    public static final void main(final String[] args) {
    	Test.getFiles();
    	
    	BcelReadClassSample brcs = new BcelReadClassSample();
    	
//    	brcs.process(CLASS_MODULE);
//    	
    	for(File f_name: Test.file_list){
    		brcs.process(f_name.getAbsolutePath());
    	}
    	
    	
    	List<Map.Entry> entries = new ArrayList<Map.Entry>(map.entrySet());
    	Collections.sort(entries, new Comparator(){
    	    public int compare(Object o1, Object o2){
    	        Map.Entry e1 =(Map.Entry)o1;
    	        Map.Entry e2 =(Map.Entry)o2;
    	        return ((Integer)e2.getValue()).compareTo((Integer)e1.getValue());
    	    }
    	});

    	for (Map.Entry entry : entries) {
    	    // entry.getKey() と entry.getValue() を使ってみた。
    		System.out.println(entry.getKey() + " = " + entry.getValue());
    	}
    	
//    	 Iterator it = map.keySet().iterator();
//         while (it.hasNext()) {
//             Object o = it.next();
//             System.out.println(o + " = " + map.get(o));
//         }
    }

    private final void process(String class_file_name) {
        try {
            final JavaClass javaClass = new ClassParser(class_file_name).parse();
//            System.out.println("クラス名:" + javaClass.getClassName());
//            System.out.println("親クラス:" + javaClass.getSuperclassName());

            final org.apache.bcel.classfile.Method[] methods = javaClass
                    .getMethods();
            for (int indexMethod = 0; indexMethod < methods.length; indexMethod++) {
                final Method method = methods[indexMethod];
//                System.out.println("メソッド:" + method.getName());
                //System.out.println(method.getArgumentTypes());
                final Code code = method.getCode();
                if (code == null) {
                    continue;
                }

                /**
                 * メソッドのなかのバイトコードを解析
                 */
                final String result = Utility.codeToString(code.getCode(),
                        javaClass.getConstantPool(), 0, -1, false);
                //System.out.println(result);

                String[] lines = result.split("\n");
                for(String li: lines){
                	String[]spl = li.split("\t");
                  if(spl[0].endsWith("invokespecial") || spl[0].endsWith("invokevirtual") || spl[0].endsWith("getstatic")){
                	  String key = spl[1];
                  		//System.out.println(key);
                	  if(!key.equals(""))
                  		if(map.containsKey(key)){
                  			int count = map.get(key);
                  			map.put(key, ++count);
                  		}else{
                  			map.put(key, 1);
                  		}
                  }
//                  for(String s: spl){
//                  	System.out.println(s);
//                  }
                }
                
                /**
                 * メソッドのなかのバイトコードを解析
                 */
//                final byte[] codes = code.getCode();
//                for (int indexCode = 0; indexCode < codes.length; indexCode++) {
//                    final short opcode = (short) (codes[indexCode] < 0 ? ((short) codes[indexCode]) + 0x100
//                            : (short) codes[indexCode]);
//                    int oplen = Constants.NO_OF_OPERANDS[opcode];
//                    System.out.println(Constants.OPCODE_NAMES[opcode] + code.get);
//
//                    // オペレーション分進めます。
//                    indexCode += oplen;
//                }
                
            }
        } catch (ClassFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    ///////////////////////////////////////////////////////////////////////
    
    /**
     * @param args
     * @throws Exception
     */
    public static void getJarFile() throws Exception {

    	// カレントディレクトリにあるjarファイルを指定
    	File f = new File(System.getProperty("user.dir"), "hello.jar");
    	JarFile jf = new JarFile(f);
    	Manifest m = jf.getManifest(); //マニフェストの取得

    	// jarファイル内のファイルとディレクトリを表示
    	printEntries(jf);

    	// マニフェストの内容を表示
    	printManifestAttributes(m);

    	// jarファイル内のファイルを読み込む
    	printFile(jf, "META-INF/MANIFEST.MF");

    	// マニフェストの属性取得
    	String class_name = getManifestAttribute(m, "JarCall-Class");
    	System.out.println("[JarCall-Class]=[" + class_name + "]");

    	// jarファイル内のクラスを呼び出す
    	//callCalc(f, class_name);
    }
    
    /**
     * jarファイル内のファイルとディレクトリの一覧を表示する
     * 
     * @param jf	jarファイル
     */
    private static void printEntries(JarFile jf) {
    	System.out.println("↓JarEntry");

    	for (Enumeration e = jf.entries(); e.hasMoreElements();) {
    		ZipEntry ze = (ZipEntry) e.nextElement();
    		String dir = ze.isDirectory() ? "[D]" : "[F]";
    		System.out.println(dir + ze.getName());
    	}
    }
    
    /**
     * マニフェストの内容を全て表示する
     * 
     * @param m	マニフェスト
     */
    private static void printManifestAttributes(Manifest m) {
    	System.out.println("↓MainAttributes");

    	Attributes ma = m.getMainAttributes();
    	for (Iterator i = ma.keySet().iterator(); i.hasNext();) {
    		Object key = i.next();
    		String val = (String) ma.get(key);
    		System.out.println("[" + key + "]=[" + val + "]");
    	}
    }
    
    /**
     * マニフェストの属性を取得する
     * 
     * @param m	マニフェスト
     * @param key	キー
     * @return 	値
     */
    private static String getManifestAttribute(Manifest m, String key) {
    	Attributes a = m.getMainAttributes();
    	return a.getValue(key);
    }
    
    /**
     * zipファイル内のファイルの内容を出力する
     * 
     * @param zf	zipファイル
     * @param name	ファイル名
     * @throws Exception
     */
    private static void printFile(ZipFile zf, String name) throws Exception {
    	System.out.println("↓printFile");

    	ZipEntry ze = zf.getEntry(name);
    	InputStream is = zf.getInputStream(ze);

    	// テキストファイルとして読み込む（エラー処理は無視）
    	BufferedReader r = new BufferedReader(new InputStreamReader(is));
    	while (r.ready()) {
    		System.out.println(r.readLine());
    	}
    	r.close();
    	is.close();
    }
}