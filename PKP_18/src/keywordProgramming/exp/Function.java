package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ast.AstUtil;


/**
 * 1つのFunctionを表すクラス
 * @author sayuu
 *
 */
public class Function implements Cloneable{

	public static String FIELD = "field"; 
	public static String CONSTRUCTOR = "constructor"; 
	public static String METHOD = "method"; 
	public static String LOCAL_VARIABLE = "localvariable";
	
	//以下データベースの順番通り。
	private String parent_class;//0, ローカル変数の時や、Members of enclosing class の時は""
	private boolean isInThisClass;	//現在編集中のクラス内の関数か？
	private boolean isStatic;//1,
	private boolean isFinal;//2,
	private String type;//3, [field or constructor or method or localvariable]
	private String return_type;//4
	private String name;//5
	List<String> labels;//6
	//private String[] parameters;//7以降, パラメータなしの時はnull
	List<String> parameters;//7以降, パラメータなしの時はnull
	private String DBString;
	
	private int apiFreq; //この関数の使用頻度(回)
	
	private int wordScore;	//キーワードだけのスコア
	
	private boolean isDummy;	//ダミーか否か
	
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
	 * コンストラクタ
	 * 引数は内部データベース(function.txt)の1行そのまま
	 * 
	 * @param function_string function.txtの1行そのまま
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
		
		if(s.length <= 8)//パラメータなし。
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
			if(fq == null)
				continue;
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
//					//レシーバもパラメタに含むので、その分　引く。
//					eq_flg1 = true;
//				}
				
				if(eq_flg1 == false)
					return;
				
				boolean eq_flg2 = false;
				
				paramFor:
				for(String p1: fq.parameters){
					for(int i = 0; i < this.parameters.size(); i++){
						
						if(i == 0 && !isStatic ){
							//第一引数はぬかす。レシーバなので。
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
