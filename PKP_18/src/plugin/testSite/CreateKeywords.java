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
	 * �������ς��Ȃ��ł��̂܂܂̃L�[���[�h
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
	public static String output2InputKeyword(String out){
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

	/*
	 * �L�[���[�h���� 
	 */
	public static List<Word> setSeparateKeywords(String keywords, List<String> methodList, List<String> valList){
		
		ArrayList<String> methodKeyword = new ArrayList<String>();
		
		for(String s: methodList){
			
			String ss = output2InputKeyword(s);
			
			for(String sss: separateKeyword(ss))
				methodKeyword.add(sss);
		}
		
		for(String s: valList){
			
			//�啶���Ŏn�܂��Ă�����̂�Static�̃N���X�� String.�݂����ȁB
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
					//�擪��@��ǉ�����B
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
	 * �L�[���[�h��S��3�����ȉ��ɂ���B
	 */
	public static List<Word> setFirst3Keywords(List<Word> keywords){
		List<Word> newKeywords = new ArrayList<Word>();
		
		for(Word word:keywords){
			String new_word = word.getWord();
			
			//3�������傫����΁A3�����ɂ���B
			if(new_word.length() > 3)
				new_word = new_word.substring(0,3);

			newKeywords.add(new Word(new_word.trim(), word.getType()));
		}
			
		return newKeywords;
	}
	
	/*
	 * �q�������c���āA�擪�ȊO�̕ꉹ�폜����B�����������͖����B
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
	 * ���߂̂R�̎q�������� 
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
//			if(new_word.length() == 0){//�폜���ĕ�����0�ɂȂ����B
//				new_word = word;	//��ɖ߂�
//				
//			}else if(new_word.length() > 3){//�폜���Ă��������S�ȏ゠��Ƃ�
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
	 * �ꉹ���폜����B
	 * (�擪�����͏����B)
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
	 * �L�[���[�h���󔒂ŕ������A���X�g�ɑ}������
	 */
	public static List<String> separateKeyword(String keyword){
		//�������������������
		String s_lowerCase = keyword.toLowerCase();
		//keyword�ɕ���
		List<String> input_keywords = Arrays.asList(s_lowerCase.split("[ �@\t]"));
		return input_keywords;
	}
	
	/*
	 * �L�[���[�h�������_���Ɉꕶ���u������B
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
	 * �L�[���[�h�������_���Ɉꕶ���}������B
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
	 * �����_���Ɉꕶ���}������B
	 * 
	 */
	public static String InsertWord(String word){
		
		//�}�����镶���̈ʒu�������_���ɑI��
		//�}���ł���ʒu�́Aword.length+1 ���݂���̂ŁA
		//0~word.length�܂ł̗������� (0 <= Math.random() < 1)
		// *word.length()+1 �Ƃ���ƁA���傤��word.length()+1�͎擾�ł��Ȃ��B
		// int �ɃL���X�g��word.length()�ȏ�̒l�͐؂�̂Ă���B
		int place = (int)(Math.random()*word.length()+1);
        
		//�}�����镶��(�A���t�@�x�b�h)�������_���ɑI��
		//0~25 �܂ł̗�������
		int ran = (int)(Math.random()* 26);
		int a;
		char c;//�u�������镶��

		//A�`Z�̃A���t�@�x�b�g�𕶎��R�[�h�ɒ����ƂU�T�`�X�O�̒l�ɂȂ�
		//�U�T�𑫂��ĂU�T�`�X�O�ɂ���
        a = 'a' + ran;
        //char�Ɍ^�ϊ�
        c = (char)a;

		StringBuilder sb = new StringBuilder();
		sb.append(word);
		sb.insert(place, c);
		return sb.toString();
	}
	
	/*
	 * �����_���Ɉꕶ���u������B
	 * 
	 */
	public static String ReplaceWord(String word){
		
		//�u�����镶���������_���ɑI��
		//0~word.length-1 �܂ł̗�������
		int place = (int)(Math.random()*word.length());
        
		char pchar = word.charAt(place);
			
		int ran;
		int a;
		char c;//�u�������镶��
		
		//�ȑO�Ɠ��������Ȃ�A������x�����������B
		while(true){
			//�u�����镶��(�A���t�@�x�b�h)�������_���ɑI��
			//0~25 �܂ł̗�������
			ran = (int)(Math.random()* 26);
			//A�`Z�̃A���t�@�x�b�g�𕶎��R�[�h�ɒ����ƂU�T�`�X�O�̒l�ɂȂ�
			//�U�T�𑫂��ĂU�T�`�X�O�ɂ���
	        a = 'a' + ran;
	        //char�Ɍ^�ϊ�
	        c = (char)a;
	        
	        //�Ⴄ�����ɂȂ�����E�o�B
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
		
		//�L�[���[�h��������ꍇ�B
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
					System.out.println("���Ȃ�");
				}else{
					System.out.println("����");
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
