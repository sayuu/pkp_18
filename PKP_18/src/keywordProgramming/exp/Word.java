package keywordProgramming.exp;
/**
 * 入力キーワードクエリの1語を表す。
 * @author sayuu
 *
 */
public class Word {
	
	public static String METHOD_CONSTRUCTOR = "METHOD_CONSTRUCTOR"; 
	public static String VARIABLE = "VARIABLE";
	public static String NORMAL = "NORMAL";
	
	private String word;	//語の文字列
	private String type;	
					//メソッド名のみに一致するとき "METHOD_CONSTRUCTOR",
					//変数名のみに一致するとき    "VARIABLE"
					//何も指定が無い時 "NORMAL"
	
	public Word(String word){
		this.word = word;
	}
	
	public String getWord(){
		return word;
	}
	
	public void setWord(String word){
		this.word = word;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
}
