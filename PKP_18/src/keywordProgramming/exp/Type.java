package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import ast.AstUtil;

/**
 * type(�^)��\���N���X
 * @author sayuu
 *
 */
public class Type implements Cloneable{

	private String name;
	List<String> subtypes;//�������g���܂�
	private String DBString;
	
	/**
	 * �R���X�g���N�^
	 * @param typeString �����f�[�^�[�x�[�X[sub_class.txt]�t�@�C����1�s
	 */
	public Type(String typeString){
		setDBString(typeString);
		String s[] = typeString.split(",");
		setName(s[0]);
		subtypes = new ArrayList<String>();
		subtypes.addAll(Arrays.asList(s));
	}
	

	public String toString(){
		return getName();
	}
	
	public String toDBString(){
		return getDBString();
	}
	
	public String toDBString2(){
		return 	"KeywordProgramming.types.add(new Type(\""+ getDBString() +"\"));";
	}
	
	public String getName(){
		return name;
	}

	public String getSimpleName(){
		return AstUtil.getSimpleClassName(getName());
	}
	
	public List<String> getSubTypes(){
		return subtypes;
	}
	
	/*
	 * java.lang.Object�p
	 */
	public void addSubType(String sub){
		subtypes.add(sub);
		
	}
	
	public void addSubTypes(List<String> types){
		//�擪���e�v�f�Ȃ̂ɒ��ӂ���B
		subtypes.addAll(types.subList(1, types.size()));
		List<String> tmp = new ArrayList<String>();
		tmp.addAll(subtypes.subList(1, subtypes.size()));
		//�d����h���ŁA�\�[�g������B
		TreeSet<String> child_set = new TreeSet<String>(tmp);
		
		
		//����Ȃ����B
		subtypes.clear();
		subtypes.add(getName());//�e
		subtypes.addAll(child_set);//�q
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
	        return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Type o = (Type) obj;
		return this.getName().equals(o.getName());
	}
	
	@Override
	public int hashCode(){
		return this.getName().hashCode();
	}

	public Type clone() {  
		try {  
			Type c = (Type)super.clone();
			
//			c.setName(new String(this.name));
//			c.setDBString(new String(this.DBString));
			
            c.subtypes = new ArrayList();  
            for (String ele:this.subtypes) {  
                c.subtypes.add(new String(ele));  
            }     
            
			return c;
		} catch (CloneNotSupportedException e) {  
			return null;  
		}  
	}


	private void setName(String name) {
		this.name = name;
	}


	private String getDBString() {
		return DBString;
	}


	private void setDBString(String dBString) {
		DBString = dBString;
	}  
}
