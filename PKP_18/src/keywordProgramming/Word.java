package keywordProgramming;
/**
 * ���̓L�[���[�h�N�G����1���\���B
 * @author sayuu
 *
 */
public class Word {
	
	public static String FUNCTION = "FUNCTION"; 
	public static String VARIABLE = "VARIABLE";
	public static String NORMAL = "NORMAL";
	
	public static String OTHER = "OTHER";
	
	
	
	private String word;	//��̕�����
	private String type;	//'@'��'-'�Ȃǂł��̌ꂪ�ǂ̃��x���ƃ}�b�`���邩�w�肷�邱�Ƃ��ł���B
					//���\�b�h���݂̂Ɉ�v����Ƃ� "method",
					//�ϐ����݂̂Ɉ�v����Ƃ�    "variable"
					//�����w�肪������ "normal"
	
	public static String flg_separate;
	
	public Word(String word, String type){
		this.word = word;
		this.type = type;
	}
	
	/*
	 * //���[�h����ON�̎��̂݁A
	 * �擪��"@"�t���Ă��邩�ۂ��ŁA���̌ꂪMETHOD ��������VARIABLE�𔻒肷��B
	 */
//	public Word(String word){
//		this.word = word;
//		this.type = Word.NORMAL;
//		//���[�h����ON�̎��̂݁AMETHOD ��������VARIABLE��I������B
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
	 * ���̓L�[���[�h�𓪕����ŏꍇ����
	 * @return
	 */
//	private void setWordType(){
//		//���������� �@���\�b�h�Ƀ}�b�`@ �ϐ��Ƀ}�b�`-
//		
//		//������@�̏ꍇ�A���\�b�h�Ƀ}�b�`������B
//		if(word.startsWith("@")){
//			this.type = Word.METHOD;
//			this.word = word.substring(1);//�擪�����폜
//		}
//		else{//���\�b�h�ȊO�͂��ׂāAVARIABLE
//			this.type = Word.VARIABLE;
//		}
//	}
}
