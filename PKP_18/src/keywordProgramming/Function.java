package keywordProgramming;

import java.util.ArrayList;
import java.util.List;

import ast.AstUtil;


/**
 * 1��Function��\���N���X
 * @author sayuu
 *
 */
public class Function implements Cloneable{

	public static String FIELD = "field"; 
	public static String CONSTRUCTOR = "constructor"; 
	public static String METHOD = "method"; 
	public static String LOCAL_VARIABLE = "localvariable";
	
	//�ȉ��f�[�^�x�[�X�̏��Ԓʂ�B
	private String parent_class;//0, ���[�J���ϐ��̎���AMembers of enclosing class �̎���""
	private boolean isInThisClass;	//���ݕҏW���̃N���X���̊֐����H
	private boolean isStatic;//1,
	private boolean isFinal;//2,
	private String type;//3, [field or constructor or method or localvariable]
	private String return_type;//4
	private String name;//5
	private List<String> labels;//6
	//private String[] parameters;//7�ȍ~, �p�����[�^�Ȃ��̎���null
	private List<String> parameters;//7�ȍ~, �p�����[�^�Ȃ��̎���null
	private String DBString;
	
	private int apiFreq; //���̊֐��̎g�p�p�x(��)
	
	private int wordScore;	//�L�[���[�h�����̃X�R�A
	
	private boolean isDummy;	//�_�~�[���ۂ�
	
	public boolean isDummy() {
		return isDummy;
	}

	public void setIsDummy(boolean d) {
		this.isDummy = d;
	}
	
	public int getWordScore() {
		return wordScore;
	}

	public void setWordScore(int wordScore) {
		this.wordScore = wordScore;
	}

	/**
	 * �R���X�g���N�^
	 * �����͓����f�[�^�x�[�X(function.txt)��1�s���̂܂�
	 * 
	 * @param function_string function.txt��1�s���̂܂�
	 */
	public Function(String function_string){
		DBString = function_string;
		try{
		String s[] = function_string.split(",");
		parent_class = s[0];
		
		isInThisClass = s[1].equals("true");
		isStatic = s[2].equals("static");
		isFinal = s[3].equals("final");
		type = s[4];
		
		return_type = AstUtil.formatTypeName(s[5]);
		name = s[6];
		labels = new ArrayList<String>();
		String[] tmp_labels = s[7].split(";");
		for(String label: tmp_labels){
			label = label.trim();
			if(!label.equals(""))
				labels.add(label);
		}
		
		if(s.length <= 8)//�p�����[�^�Ȃ��B
			return;

		parameters = new ArrayList<String>();
		
		for(int i = 8; i < s.length; i++){
			String param = s[i].trim();
			if(!param.equals(""))
				parameters.add(AstUtil.formatTypeName(param));
		}
		
		}catch(Exception e){
			System.out.println(function_string);
			e.printStackTrace();
		}
	}

	public String toString(){
		String s = "Function[ret= " + return_type+", name= "+name;
		if(parameters != null){
			s += ", param= ";
			for(String p:parameters){
				s += "," + p;
			}
		}
		s += ", parent= " + parent_class + ", label= ";
		for(String l:labels){
			s += "," + l;
		}
		s += "]";
		return s;
	}
	
	public String getParaTypeName(){
		String para_type = getReturnType();
		if(getParameters() != null)
			for(String p: getParameters()){
				para_type += "_" + p;
			}
		return para_type;
	}
	
	public String toDBString(){
		return DBString;
	}
	
//	public String toDBString2(){
//		return 	"KeywordProgramming.functions.add(new Function(\""+ DBString +"\"));";
//	}
	
	public List<String> getLabels(){
		return labels;
	}

	public String getParentClass(){
		return parent_class;
	}

	public String getName(){
		if(isDummy)
			return getParaTypeName();
		else
			return name;
	}

	public String getReturnType(){
		return return_type;
	}

	public List<String> getParameters(){
		return parameters;
	}
	
	public boolean isStatic(){
		return isStatic;
	}

	public boolean isFinal(){
		return isFinal;
	}

	public void setIsInThisClass(boolean isInThisClass){
		this.isInThisClass = isInThisClass;
	}
	
	public boolean isInThisClass(){
		return isInThisClass;
	}
	
	public String getFunctionType(){
		return type;
	}

	public void setFrequency(List<Frequency> freqs){
		for(Frequency fq: freqs){
			if(fq.name == null)
				continue;
//			if(fq.name.contains("addResultListener") && fq.name.equals(name)){
//				System.out.println();
//			}
			
			if(fq.parent_class.equals(parent_class) && fq.name.equals(name) && fq.return_type.equals(return_type)){
				boolean eq_flg1 = false;
				
				if(parameters == null){
					
					if(fq.parameters.size() == 0)
						eq_flg1 = true;
					
				}else if(fq.parameters.size() == parameters.size()){
					eq_flg1 = true;
				}
//					
//					if(isStatic && fq.parameters.size() == parameters.size()){
//					//static
//					 eq_flg1 = true;
//				}else if(fq.parameters.size() == parameters.size() - 1){
//					// not static
//					//���V�[�o���p�����^�Ɋ܂ނ̂ŁA���̕��@�����B
//					eq_flg1 = true;
//				}
				
				if(eq_flg1 == false)
					return;
				
				boolean eq_flg2 = false;
				
				paramFor:
				for(String p1: fq.parameters){
					for(int i = 0; i < this.parameters.size(); i++){
						
						if(i == 0 && !isStatic ){
							//�������͂ʂ����B���V�[�o�Ȃ̂ŁB
							continue;
						}
						if(p1.equals(parameters.get(i))){
							eq_flg2 = true;
							break paramFor;
						}
					}
				}
				
				if(eq_flg2){
					this.apiFreq = fq.apiFreq;
					return;
				}
			}
			
		}
	}
	
	public void setFrequency(int i){
		this.apiFreq = i;
	}
	
	public int getFrequency(){
		return this.apiFreq;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
	        return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Function o = (Function) obj;
		return this.DBString.equals(o.DBString);
	}
	
	@Override
	public int hashCode(){
		return this.DBString.hashCode();
	}
	
	public Function clone() {  
		try {
			Function c = (Function)super.clone();
			c.labels = new ArrayList();
			for (String ele: this.labels){
				c.labels.add(ele);
			}
			if(this.parameters == null){
				c.parameters = null;
			}else{
				c.parameters = new ArrayList();
				for (String ele: this.parameters){
					c.parameters.add(ele);
				}
			}
			return c;
		} catch (CloneNotSupportedException e) {  
			return null;  
		}  
	}
}
