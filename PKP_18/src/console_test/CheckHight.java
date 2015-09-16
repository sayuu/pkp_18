package console_test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckHight {

	public static void main(String[] args) {
		String s1 = "array.add(src.readLine())";
		String s2 = "Logger.getLogger(getClass().getName())";
		String s3 = "activeListProvider.getActiveList().getBestToken()";
		String s4 = "scoreableList.subList(from,Math.min(to,totalSize))";
		String s5 = "loadXML()";
		String s6 = "new IOException(e.getMessage())";
		
		List<String> list = new ArrayList<String>();
		for(String str: list){
			for(int i=0; i<str.length(); i++){
			int dot_count = 0;
			//パターンマッチ：クラスの先頭文字が大文字で始まり
			Pattern pattern1 = Pattern.compile("^/[A-Z].*");
	
			Matcher matcher1 = pattern1.matcher(str.subSequence(i, i+1));
			
			if(matcher1.matches()){
				System.out.println("static メソッド. dotをカウントしない。");
			}else{
				dot_count++;
			}
			
			/*
				s1.s
				if(s1.charAt(i){
					
				}*/
			}
		}
		
	}
	
	
}
