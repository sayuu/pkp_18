package keywordProgramming.exp;
/**
 * ���̓L�[���[�h�N�G����1���\���B
 * @author sayuu
 *
 */
public class Word {
	
	public static String METHOD_CONSTRUCTOR = "METHOD_CONSTRUCTOR"; 
	public static String VARIABLE = "VARIABLE";
	public static String NORMAL = "NORMAL";
	
	private String word;	//��̕�����
	private String type;	
					//���\�b�h���݂̂Ɉ�v����Ƃ� "METHOD_CONSTRUCTOR",
					//�ϐ����݂̂Ɉ�v����Ƃ�    "VARIABLE"
					//�����w�肪������ "NORMAL"
	
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
