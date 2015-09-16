package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import ast.AstUtil;

/**
 * type(型)を表すクラス
 * @author sayuu
 *
 */
public class Type implements Cloneable{

	private String name;
	List<String> subtypes;//自分自身も含む
	private String DBString;
	
	/**
	 * コンストラクタ
	 * @param typeString 内部データーベース[sub_class.txt]ファイルの1行
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
	 * java.lang.Object用
	 */
	public void addSubType(String sub){
		subtypes.add(sub);
		
	}
	
	public void addSubTypes(List<String> types){
		//先頭が親要素なのに注意する。
		subtypes.addAll(types.subList(1, types.size()));
		List<String> tmp = new ArrayList<String>();
		tmp.addAll(subtypes.subList(1, subtypes.size()));
		//重複を防いで、ソートもする。
		TreeSet<String> child_set = new TreeSet<String>(tmp);
		
		
		//入れなおす。
		subtypes.clear();
		subtypes.add(getName());//親
		subtypes.addAll(child_set);//子
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
