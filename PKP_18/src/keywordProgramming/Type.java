package keywordProgramming;

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
	private List<String> subtypes;//�������g���܂�
	private String DBString;
	
	/**
	 * �R���X�g���N�^
	 * @param typeString �����f�[�^�[�x�[�X[sub_class.txt]�t�@�C����1�s
	 */
	public Type(String typeString){
		DBString = typeString;
		String s[] = typeString.split(",");
		name = s[0];
		subtypes = new ArrayList<String>();
		subtypes.addAll(Arrays.asList(s));
	}
	

	public String toString(){
		return name;
	}
	
	public String toDBString(){
		return DBString;
	}
	
	public String toDBString2(){
		return 	"KeywordProgramming.types.add(new Type(\""+ DBString +"\"));";
	}
	
	public String getName(){
		return name;
	}

	public String getSimpleName(){
		return AstUtil.getSimpleClassName(name);
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
		
		//�d����h���ŁA�\�[�g������B
		TreeSet<String> child_set = new TreeSet<String>(subtypes.subList(1, subtypes.size()));
		
		//����Ȃ����B
		subtypes.clear();
		subtypes.add(name);//�e
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
		return this.name.equals(o.name);
	}
	
	@Override
	public int hashCode(){
		return this.name.hashCode();
	}

	public Type clone() {  
		try {  
			Type c = (Type)super.clone();

			c.subtypes = new ArrayList();  
            for (String ele:this.subtypes) {  
                c.subtypes.add(new String(ele));  
            }     
            
			return c;
		} catch (CloneNotSupportedException e) {  
			return null;  
		}  
	}
}
