package methodFreqCount;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/*

 BcelReadClassSample 
 で作成したファイルを
 ＫＰで読む形に変換する。
 
 * 
 */
public class Test2 {
	
	//file_path に解析したい、.classファイルを置く。
	static String file_path = "C:\\Users\\sayuu\\Desktop\\jar\\frequencyAll.txt";
	static List<File> file_list = new ArrayList<File>();
	
	//出力するパッケージ名の指定 (全て出力するときはnull)
	//static String packageName = "java.lang";
	static String packageName = null;
	
	static final String[][] basic_type_simbols = {
		{"Z", "java.lang.Boolean"      },
		{"B", "java.lang.Byte"         },
		{"C", "java.lang.Character"    },
		{"D", "java.lang.Double"       },
		{"F", "java.lang.Float"        },
		{"I", "java.lang.Integer"      },
		{"J", "java.lang.Long"         },
		{"S", "java.lang.Short"        },
		{"V", "java.lang.Void"         }
	};
	
	
	public static final void main(final String[] args) {
		
		FileReader in;
		try {
			in = new FileReader(file_path);
			BufferedReader br = new BufferedReader(in);
			String line;
			
			while ((line = br.readLine()) != null) {
				
				//System.out.println(line);
				
				String[] s1 = line.split("=");
				String freq = s1[1].trim();
				String[] s2 = s1[0].split("[\\(\\)]");
				String fullName = s2[0];
				String params1 = s2[1];
				
				String className = fullName.substring(0, fullName.lastIndexOf('.')).trim();
				String name = fullName.substring(fullName.lastIndexOf('.')+1).trim();
				//コンストラクタ
				if(name.equals("<init>")){
					name = className.substring(className.lastIndexOf('.')+1).trim();
				}
				String return_t = s2[2].replaceAll("\\/", ".").replace(";", "").trim();
				return_t = formatTypeName(return_t);
				
				String[] params2 = params1.split(";");
				
				List<String> params3 = new ArrayList<String>();
				
				for(int i = 0; i< params2.length; i++){
//					System.out.println("ori:" + params2[i]);
					
					params2[i] = params2[i].replaceAll("\\/", ".").trim();
					
					if(params2[i].startsWith("L")){
						params2[i] = params2[i].replaceFirst("L", "");
						params3.add(params2[i]);
					}else if(params2[i].matches("^[ZBCDFIJSV].*")){
						
						String[] p2 = {params2[i]};
						
						//p2[0]に[ZBCDFIJSV]+
						//p2[1]にjava.lang.Stringなど。
						
						if(params2[i].contains("L")){
							p2 = params2[i].split("L");
						}
						
						if(p2[0].matches("^[ZBCDFIJSV]+")){
							//System.out.println(params2[0]);
							for(int j = 0; j < p2[0].length(); j++){
								params3.add(formatTypeName(String.valueOf(p2[0].charAt(j))));
							}
						}
						
						if(p2.length > 1)
							params3.add(p2[1]);
						
					}else if(!params2[i].equals("")){
						//System.out.println(";" + formatTypeName(params2[i]));
						params3.add(formatTypeName(params2[i]));
					}
					
				}
				
				
//				for(String s: params3){
//					System.out.println("end:" + s);
//				}
				
				if(packageName == null){
					print(freq, className, name, return_t, params3);
				}else if(className.startsWith(packageName)){
					print(freq, className, name, return_t, params3);
				}
					
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        

    }


	private static void print(String freq, String className, String name,
			String return_t, List<String> params3) {
		System.out.print(freq + "," + className + "," + name + "," + return_t + ",");
		
		for(String s: params3){
			System.out.print(s + ",");
		}
		System.out.println();
	}
	

	  // 省略文字だけを戻す。
	  // 基本データ型はオブジェクト型に変換しない。
	  //（配列記号"["はそのまま残す。）
	  public static String formatTypeName(String name){
		  boolean flg_arr = false;
		  if(name.startsWith("[")){
			  name = name.substring(1);
			  flg_arr = true;
		  }
		  
		  if(name.startsWith("L"))
			  name = name.replaceFirst("L", "");
			
		  for(int i = 0; i < basic_type_simbols.length; i++){
			  if(name.equals(basic_type_simbols[i][0])){
				name = basic_type_simbols[i][1];
			  }
		  }
		  
		  if(flg_arr)
			  name = "[" + name; 
		  return name;
	  }
	  
	  /*
	   * param 用
	   */
	  public static String formatTypeNameParam(String name){
		  boolean flg_arr = false;
		  if(name.startsWith("[")){
			  name = name.substring(1);
			  flg_arr = true;
		  }
		  
		  if(name.startsWith("L"))
			  name = name.replaceFirst("L", "");
			
		  for(int i = 0; i < basic_type_simbols.length; i++){
			  if(name.equals(basic_type_simbols[i][0])){
				name = basic_type_simbols[i][1];
			  }
		  }
		  
		  if(flg_arr)
			  name = "[" + name; 
		  return name;
	  }

}
