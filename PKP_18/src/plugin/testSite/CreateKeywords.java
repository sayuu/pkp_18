package plugin.testSite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import keywordProgramming.ExplanationVector;
import keywordProgramming.Function;
import keywordProgramming.Word;

public class CreateKeywords {

	/*
	 * 何も改変しないでそのままのキーワード
	 */
	public static List<Word> setInputKeywords(String keywords){
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(String k:separateKeyword(keywords)){
			Word word = new Word(k, Word.NORMAL);
			newKeywords.add(word);
		}
		return newKeywords;
	}
	
	/*
	 * 出力文字列を入力キーワードに変換する
	 * 
	 * 引数outが
	 *  message.replaceAll(space, comma);
	 *  ならば、
	 *  返り値は
	 *   message replace all space comma
	 *  となる。
	 *  
	 *  ピリオド、カンマ、セミコロン、括弧は取り、空白文字に変換する。
	 *  message　replaceAll　space　 comma　　
	 *  
	 *  replaceAllの部分は、大文字の手前に空白文字を挿入してから、
	 *  大文字を小文字に変換する。＞AstUtil.splitNameを使う。
	 *  
	 */
	public static String output2InputKeyword(String out){
		if(out == null)
			return null;
		//ピリオド、カンマ、セミコロン、括弧は取り、空白文字に変換する。
		//ダブルクオーテーションと<>も取る。
		String rep1 = out.replaceAll("[\\.\\,\\;\\(\\)\\<\\>\"]", " ");
		//大文字の手前に空白文字を挿入してから、大文字を小文字に変換する。
		String rep2 = ast.AstUtil.splitName(rep1, " ");
		//複数の空白文字が連結していた場合１つにする。
		String rep3 = rep2.replaceAll(" +", " ");
		return rep3.trim();
	}

	/*
	 * キーワード分割 
	 */
	public static List<Word> setSeparateKeywords(String keywords, List<String> methodList, List<String> valList){
		
		ArrayList<String> methodKeyword = new ArrayList<String>();
		
		for(String s: methodList){
			
			String ss = output2InputKeyword(s);
			
			for(String sss: separateKeyword(ss))
				methodKeyword.add(sss);
		}
		
		for(String s: valList){
			
			//大文字で始まっているものはStaticのクラス名 String.みたいな。
			if(s != null && s.length() > 0)
			if(Character.isUpperCase(s.charAt(0))){
				String ss = output2InputKeyword(s);
				if(ss == null)
					continue;
				if(ss.equals(""))
					continue;
				
				for(String sss: separateKeyword(ss)){
					methodKeyword.add(sss);
				}
			}
		}
		
		ArrayList<Boolean> flg = new ArrayList<Boolean>();
		for(int i = 0; i < methodKeyword.size(); i++){
			flg.add(false);
		}
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(String k:separateKeyword(keywords)){
			boolean isMethod = false;
			
			for(int i = 0; i < methodKeyword.size(); i++){
				String s = methodKeyword.get(i);
				if(flg.get(i) == false && k.equals(s)){
					//先頭に@を追加する。
					//k = "@" + k;
					isMethod = true;
					flg.set(i, true);
					break;
				}
			}
			
			if(isMethod)
				newKeywords.add(new Word(k.trim(), Word.FUNCTION));
			else 
				newKeywords.add(new Word(k.trim(), Word.VARIABLE));
		}
		
		return newKeywords;
		
	}
	
	/*
	 * キーワードを全て3文字以下にする。
	 */
	public static List<Word> setFirst3Keywords(List<Word> keywords){
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			String new_word = word.getWord();
			
			//3文字より大きければ、3文字にする。
			if(new_word.length() > 3)
				new_word = new_word.substring(0,3);

			newKeywords.add(new Word(new_word.trim(), word.getType()));
		}
			
		return newKeywords;
	}
	
	/*
	 * 子音だけ残して、先頭以外の母音削除する。文字数制限は無し。
	 */
	public static List<Word> setConsonantKeywords(List<Word> keywords){
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			String new_word = "";
				
			new_word = removeVowels(word.getWord());

			newKeywords.add(new Word(new_word.trim(), word.getType()));
		}
		
		
		return newKeywords;
		
	}
	
	/*
	 * 初めの３つの子音を入れる 
	 * 
	 */
//	public static String setFirst3ConsonantKeywords(String keywords){
//		
//		List<String> newKeywords = new ArrayList<String>();
//		
//		for(String word:separateKeyword(keywords)){
//			String new_word = "";
//			
//			boolean at_flg = false;
//			if(word.charAt(0) == '@'){
//				word = word.substring(1);
//				at_flg = true;
//			}
//				
//			new_word = removeVowels(word);
//			
//			if(new_word.length() == 0){//削除して文字数0になった。
//				new_word = word;	//基に戻す
//				
//			}else if(new_word.length() > 3){//削除しても文字数４以上あるとき
//				new_word = new_word.substring(0, 3);
//			}
//			
//			if(at_flg){
//				new_word = "@" + new_word;
//			}
//			newKeywords.add(new_word);
//		}
//		
//		String ret = "";
//		
//		for(String s: newKeywords){
//			ret += s + " ";
//		}
//		
//		return ret.trim();
//		
//	}
	
	/*
	 * 母音を削除する。
	 * (先頭文字は除く。)
	 * 
	 */
	public static String removeVowels(String word){
		String c_word = word.substring(1);
		
		c_word = c_word.replace("a", "");
		c_word = c_word.replace("i", "");
		c_word = c_word.replace("u", "");
		c_word = c_word.replace("e", "");
		c_word = c_word.replace("o", "");
		
		return word.substring(0,1) + c_word;
	}
	
	public static boolean isConsonant(char s){
		if(s =='a' || s =='i' || s =='u' || s =='e' || s =='o' ){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isConsonant(String s){
		if(s.equals("a") || s.equals("i") || s.equals("u") || s.equals("e") || s.equals("o")){
			return false;
		}else{
			return true;
		}
	}
	
	/*
	 * キーワードを空白で分割し、リストに挿入する
	 */
	public static List<String> separateKeyword(String keyword){
		//文字列を小文字化する
		String s_lowerCase = keyword.toLowerCase();
		//keywordに分割
		List<String> input_keywords = Arrays.asList(s_lowerCase.split("[ 　\t]"));
		return input_keywords;
	}
	
	/*
	 * キーワードをランダムに一文字置換する。
	 */
	
	public static List<Word> setReplacedKeywords(List<Word> keywords){
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			Word new_word = new Word(ReplaceWord(word.getWord()), word.getType());
			newKeywords.add(new_word);
		}
	
		return newKeywords;
		
	}
	
	/*
	 * キーワードをランダムに一文字挿入する。
	 */
	
	public static List<Word> setInsertedKeywords(List<Word> keywords){
		
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			Word new_word = new Word(InsertWord(word.getWord()), word.getType());
			newKeywords.add(new_word);
		}
	
		return newKeywords;
		
	}
	
	/*
	 * ランダムに一文字挿入する。
	 * 
	 */
	public static String InsertWord(String word){
		
		//挿入する文字の位置をランダムに選ぶ
		//挿入できる位置は、word.length+1 存在するので、
		//0~word.lengthまでの乱数生成 (0 <= Math.random() < 1)
		// *word.length()+1 とすると、ちょうどword.length()+1は取得できない。
		// int にキャストでword.length()以上の値は切り捨てられる。
		int place = (int)(Math.random()*word.length()+1);
        
		//挿入する文字(アルファベッド)をランダムに選ぶ
		//0~25 までの乱数生成
		int ran = (int)(Math.random()* 26);
		int a;
		char c;//置き換える文字

		//A～Zのアルファベットを文字コードに直すと６５～９０の値になる
		//６５を足して６５～９０にする
        a = 'a' + ran;
        //charに型変換
        c = (char)a;

		StringBuilder sb = new StringBuilder();
		sb.append(word);
		sb.insert(place, c);
		return sb.toString();
	}
	
	/*
	 * ランダムに一文字置換する。
	 * 
	 */
	public static String ReplaceWord(String word){
		
		//置換する文字をランダムに選ぶ
		//0~word.length-1 までの乱数生成
		int place = (int)(Math.random()*word.length());
        
		char pchar = word.charAt(place);
			
		int ran;
		int a;
		char c;//置き換える文字
		
		//以前と同じ文字なら、もう一度乱数を引く。
		while(true){
			//置換する文字(アルファベッド)をランダムに選ぶ
			//0~25 までの乱数生成
			ran = (int)(Math.random()* 26);
			//A～Zのアルファベットを文字コードに直すと６５～９０の値になる
			//６５を足して６５～９０にする
	        a = 'a' + ran;
	        //charに型変換
	        c = (char)a;
	        
	        //違う文字になったら脱出。
			if(pchar != c){
				break;
			}
		}
        
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		sb.setCharAt(place, c);
		return sb.toString();
	}
	
	public static void main(String args[]){
		List<String> methodList = new ArrayList<String>();
		methodList.add("containsAlphabetOrPad");
		
		List<String> vaList = new ArrayList<String>();
		vaList.add("abcde");
		
		
		String keywords = "contains alphabet or pad line separator";
		
		List<Word> word_list = new ArrayList<Word>();
		
		//キーワード分割する場合。
		String flg_separate = "true";

		if(flg_separate.equals("true")){
			Word.flg_separate = "true";
			word_list = CreateKeywords.setSeparateKeywords(keywords, methodList, vaList);
		}else{
			Word.flg_separate = "false";
			word_list = CreateKeywords.setInputKeywords(keywords);
		}

		print(word_list);
		printSimil(word_list);
		
			word_list = CreateKeywords.setConsonantKeywords(word_list);
			print(word_list);printSimil(word_list);
			word_list = CreateKeywords.setFirst3Keywords(word_list);

			print(word_list);printSimil(word_list);
			word_list = CreateKeywords.setReplacedKeywords(word_list);

			print(word_list);printSimil(word_list);
			word_list = CreateKeywords.setInsertedKeywords(word_list);

			print(word_list);printSimil(word_list);
			
			Function f = new Function("org.apache.commons.codec.binary.BinaryCodec,false,static,nonfinal,method,char[],toAsciiChars,to;ascii;chars,byte[]");
//			Function f = new Function(",true,nonstatic,final,localvariable,java.lang.Boolean,useHex,use;hex,");
			for(int i=0; i < word_list.size(); i++){
				Word w = word_list.get(i);
				System.out.print("(" + w.getWord() + "," + w.getType() + "), ");
				if(!ExplanationVector.judgeWordType(word_list.get(i), f)){
					System.out.println("しない");
				}else{
					System.out.println("する");
				}
			}
	}
	
	public static void print(List<Word> word_list){
		for(Word w: word_list){
			System.out.print("(" + w.getWord() + "," + w.getType() + "), ");
		}
		System.out.println();
	}
	
	public static void printSimil(List<Word> word_list){
		for(Word w: word_list){
			for(int i = 0; i < 4; i++){
				String flg = "";
				
				switch(i){
				case 0:
					flg = PreferenceInitializer.COMMON_SUBSEQUENCE_OFF;
					break;
				case 1:
					flg = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1;
					break;
				case 2:
					flg = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2;
					break;
				case 3:
					flg = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3;
					break;
				case 4:
					flg = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4;
					break;
				case 5:
					flg = PreferenceInitializer.COMMON_SUBSEQUENCE_LD;
					break;
				}
				
				double s = ExplanationVector.similarity2(w.getWord(), "contains", flg);
				System.out.println(w.getWord() + "," + flg +","+ s);
			}
		}
		System.out.println();
	}
}
