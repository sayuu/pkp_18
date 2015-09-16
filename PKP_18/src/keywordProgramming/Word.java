package keywordProgramming;
/**
 * 入力キーワードクエリの1語を表す。
 * @author sayuu
 *
 */
public class Word {
	
	public static String FUNCTION = "FUNCTION"; 
	public static String VARIABLE = "VARIABLE";
	public static String NORMAL = "NORMAL";
	
	public static String OTHER = "OTHER";
	
	
	
	private String word;	//語の文字列
	private String type;	//'@'や'-'などでこの語がどのラベルとマッチするか指定することができる。
					//メソッド名のみに一致するとき "method",
					//変数名のみに一致するとき    "variable"
					//何も指定が無い時 "normal"
	
	public static String flg_separate;
	
	public Word(String word, String type){
		this.word = word;
		this.type = type;
	}
	
	/*
	 * //ワード分割ONの時のみ、
	 * 先頭に"@"付いているか否かで、その語がMETHOD もしくはVARIABLEを判定する。
	 */
//	public Word(String word){
//		this.word = word;
//		this.type = Word.NORMAL;
//		//ワード分割ONの時のみ、METHOD もしくはVARIABLEを選択する。
//		if(flg_separate.equals("true")){
//			setWordType();
//		}
//	}
	
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
	
	/**
	 * 入力キーワードを頭文字で場合分け
	 * @return
	 */
//	private void setWordType(){
//		//頭文字判定 　メソッドにマッチ@ 変数にマッチ-
//		
//		//頭文字@の場合、メソッドにマッチさせる。
//		if(word.startsWith("@")){
//			this.type = Word.METHOD;
//			this.word = word.substring(1);//先頭文字削除
//		}
//		else{//メソッド以外はすべて、VARIABLE
//			this.type = Word.VARIABLE;
//		}
//	}
}
